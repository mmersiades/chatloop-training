# Chatloop User Training

## Overview
This app is a graphql microservice that provides information about user progress through the Chatloop training programs.

The content and structure (Program -> Course -> Module -> ModuleMedia and ModuleTask) are defined and hosted on a third-party CDN service called Contentful.

This microservice provides a webhook endpoint for Contentful to let the microservice know when the training content/structure changes.

This microservice takes the ids of the Programs, Courses and Modules provided by Contentful and associates user progress data with these ids.

This data is then available for the front-end to request via a graphql endpoint.

## Development

### Running the App in IDE

For app development, it is recommended to run teh app in the IDE, rather than in a Docker container (see Local Deployment Test below).

Most of the parameters that may need to be changed are in `application.properties`, including postgres, contentful, web server port,
JWT secret. For local development we always use local spring profile.

By default, real, remote Contentful service is used, so internet connection is required. 

Also, an instance of a postgres database is required. If needed, default docker container running postgres can be started via `mvn docker:start`.
`mvn docker:stop` can be used to remove existing instance. Booted postgres instance will export port 5555 to localhost, so that it does
not conflict with any pre-existing postgres instances which may be using default port 5432. 

Developers can start application in IDE by running ChatloopUserTrainingMain main spring boot class. Create a run configuration in your IDE 
which uses the above class as main class and add VM option `-Dspring.profiles.active=local` to use `application-local.properties`. 
Also, you'll need to put the JWT secret key for the local environment into the run configuration as an environment variable.

IDEA run configurations for staging and production profiles require all environment variables listed in their respective `application-*.properties`
file.

Postman is recommended to manually test/learn API. `docs/` folder contains ready-to-use postman collection.

Authorization HTTP header of type Bearer Token is required to be present in each request (ie JWT token). Any such token must be signed
or issued by the provider who used secret `security.jwt.mac.secret=` property. To generate tokens for different user names, see class 
JwtTokenGenerator in tests. For local testing, file `test-jwt-tokens.txt` contains pre-generated JWT tokens for 3 virtual users.

Once application is started, GraphQL is available under JWT-secured endpoint `http://localhost:8888/graphql`. Also, there is unsecured
webhooks endpoint which is intended to be called by Contentful under `http://localhost:8888/webhook`.

### Tests

Tests require postgres database, which can be started with `mvn docker:start`. We intentionally always use postgres for more realistic
testing rather than using embedded H2 database. 

To run all tests from command line execute `mvn clean test`. It will also perform checkstyle verification.

### Profile options:

Default only used for now.

    local               - generally used locally
    staging             - when deploying to staging environment in AWS
    production          - enables several production environment settings

## Deployment

### Local Deployment Test

Before deploying remotely, full stack docker deploy should be tested locally. `.env` file contains all environment variables, some of which
need to be changed, such as Contentful parameters or JWT secret if they are different from default.

First ensure all our docker containers are down (our app, our postgres and our pgadmin), and app is not running in IDE either, to avoid 
port conflicts.

Next `mvn clean package -Dmaven.test.skip=true`, which builds and packages new docker image of app. You can verify that new image is 
built via `docker images`. 

Next, execute `docker-compose --env-file ./.env up`. This will boot docker containers for our app, postgres and pgadmin.

They expose the 8888 web port, the 5555 postgres port and 29000 pgadmin port (`http://localhost:29000/browser/`), all of which should be accessible 
on the host machine. To shut down all containers, pressing Ctrl+C should do the trick.

You can stop & remove ALL containers and networks with `./scripts/docker_reset.sh`. Optional, and use with care.

If there are port conflict related errors or warnings, it probably means some prior container was not shut down (properly) and
clean it first before retrying. Or, if unable to connect type of errors are seen, then some containers are not started.

### Manual AWS Deployment

To package project, run `mvn clean package -Dmaven.test.skip=true`. This will generate a package called `chatloop-user-training-elastic-beanstalk.zip`.

Environment variables that generally may need to be changed/provided for remote/AWS deployment are:

* CFUL_SPACE_ID
* CFUL_ENV_ID
* CFUL_DELIVERY_TOKEN
* JWT_SECRET_KEY
* SPRING_PROFILES_ACTIVE

Following additional environment variables should be provided for microservice instance:
* AWS_ACCESS_KEY_ID
* AWS_SECRET_ACCESS_KEY

These 7 variables can be manually added via the Elastic Beanstalk environment configuration web interface.

The 5 env variables below are auto-injected by AWS itself.

* RDS_HOSTNAME
* RDS_PORT
* RDS_DB_NAME
* RDS_USERNAME
* RDS_PASSWORD

To create an Elastic Beanstalk environment via the AWS web console:

1. Set the platform to Docker.
2. Upload `chatloop-user-training-elastic-beanstalk.zip` from local machine and navigate to the configuration page,
rather than creating the environment at this stage.
3. Set the environment variables in the "Software" panel.
4. In the "Capacity" panel, select "load balancer" over "single instance".
5. In the load balancer panel, set up a listener for `https`

  - Port: 443
  - Protocol: HTTPS
  - SSL certificate: choose the chatloop.org one
  - SSL policy: ELBSecurityPolicy-2016-08
  - Default process: default
  
6. In the "Rolling updates" panel, select "Rolling with additional batches".
7. In the "Security" panel, assign an SSH keypair, so you can SSH into the EC2 container if you need to.
8. In the "Database" panel, configure postgres RDB database (its config params will be auto-injected into the container via RDS* env variables).

If you are using load balancer, health check path to be used should be set to `/actuator/health`, ie our app exposes endpoint
such as `http://localhost:8888/actuator/health`. Thus, AWS LB Listener is HTTP/80 pointing to Process HTTP/80, HTTP code 200 and
health check path `/actuator/health`. This path can be set via the EC2 web console, on the "Target groups" page.

When load balancer is configured, environment URL should be used to connect to the app, rather than instance DNS name.

### Pipeline AWS Deployment

To deploy to AWS via the BitBucket pipeline:

1. Merge a PR into the master branch. This will trigger the BitBucket pipeline to run the test suite, and then build the app.

2. If the build is successful, the BitBucket pipeline will automatically deploy the microservice to the `staging-user-training` 
Elastic Beanstalk staging environment ([staging-user-training.chatloop.org](https://staging-user-training.chatloop.org))

3. To deploy to the `production-user-training` Elastic Beanstalk production environment ([user-training.chatloop.org](https://user-training.chatloop.org)),
go to the BitBucket UI at [the pipeline Deployments page](https://bitbucket.org/thoughtastronaut/chatloop-training/addon/pipelines/deployments),
open the relevant pipeline (probably the most recent one), and click the DEPLOY button.

#### Troubleshooting

In test setups, create your own public/private key in AWS and enable it during instance configuration. For prod use whatever prod key pair is generally used.

Above is test JWT secret which works with our test users (tester etc). The real monolith's key should generally be used.

To troubleshoot remote docker deployments, follow below instructions. Sudo is required for most commands once ssh-ed:

- Download your public/private key and save it to `~/.ssh/` directory. Exec `chmod 400 YouKey.pem`. This key should be enabled on the instance
during environment creation process.
- `ssh -v -i "YouKey.pem" ec2-user@PublicDNSAddress-southeast-2.compute.amazonaws.com`. Once ssh-ed, `sudo docker ps -a` etc can be used to interact with Docker.
- `/opt/elasticbeanstalk/deployment/env.list` contains env variables that will be injected into docker container, which can be also checked
with `./bin/get-config environment`.
- The file `deployments/app_source_bundle` is actually the beanstalk ZIP file, just rename it, and unpackage it if you need to check configuration.
You can also rebuild image remotely with `sudo docker image build .`.
- Check if microservice container has connectivity to RDS via `ssh -vv PublicDNSAddress.rds.amazonaws.com -p 5432`. It should print the message:
`Connection closed by remote host`.

`sudo docker tag 12fa899eaca4 cut/cutms`
`sudo docker run --name cut -d cut/cutms`
`sudo docker run --env-file ./env.list --name cut –d cut/cutms`

Or use beanstalk CLI client.

## Misc

When interacting with Maven, tests can be skipped with `-Dmaven.test.skip=true` flag.

Start docker container of application manually. Handy when troubleshooting beanstalk setup. Shows list of all env
variables required.

`docker run --name chatloop-user-training -e RDS_HOSTNAME=db -e RDS_PORT=5432 -e RDS_DB_NAME=cut -e RDS_USERNAME=cut -e RDS_PASSWORD=cut -e JWT_SECRET_KEY= -e SPRING_PROFILES_ACTIVE=staging -p 8888:8080 -d neonkingkong/chatloop-user-training`

