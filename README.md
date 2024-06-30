# Chatloop User Training

## Overview

This app is a graphql microservice that provides information about user progress through the Chatloop training programs.

The content and structure (Program -> Course -> Module -> ModuleMedia and ModuleTask) are defined and hosted on a
third-party CDN service called Contentful.

This microservice provides a webhook endpoint for Contentful to let the microservice know when the training
content/structure changes.

This microservice takes the ids of the Programs, Courses and Modules provided by Contentful and associates user progress
data with these ids.

This data is then available for the front-end to request via a graphql endpoint.

## Spring Profiles

This app can run with 4 spring profiles.
These profiles are set by the SPRING_PROFILES_ACTIVE environment variable.

|      Profile       |                                        Purpose                                        | 
|:------------------:|:-------------------------------------------------------------------------------------:|
|   local Profile    |            For local development on your computer, no mock data by default            |  
|    e2e Profile     | For local and CI/CD end-to-end testing. Includes mock data and db reset functionality |    
|  staging Profile   |                                     For app demos                                     |            
| production Profile |                                       For reals                                       |                    

## Running the app

There numerous ways the app is/can be run:

- locally, using your IDE run configuration
- locally, using the docker-compose file
- locally, using chatloop-network (for local e2e testing or local front-end development)
- cloud on-demand, using chatloop-network (for CI/CD e2e testing)
- cloud server, using AWS Elastic Beanstalk (production)

More details on this elsewhere in this readme.

## Environment Variables

This app needs the following environment variables. For those marked 'secret', you'll need to ask someone for them.

|        Env Var         |               Purpose                |           local            |         e2e local          |           e2e on-demand           |                staging                |          production           |
|:----------------------:|:------------------------------------:|:--------------------------:|:--------------------------:|:---------------------------------:|:-------------------------------------:|:-----------------------------:|
| SPRING_PROFILES_ACTIVE |       Specify Spring profiles        |           local            |            e2e             |                e2e                |                staging                |          production           |
|      RDS_HOSTNAME      |              DB config               |         localhost          |         localhost          |          chatloop-e2e-db          |          chatloop-staging-db          |                               |
|        RDS_PORT        |              DB config               |            5555            |            5555            |               5432                |                 5432                  |                               |
|      RDS_DB_NAME       |              DB config               |            cut             |            cut             |  chatloop_user_training_postgres  |    chatloop_user_training_postgres    |                               |
|      RDS_USERNAME      |              DB config               |            cut             |            cut             |           neonkingkong            |             neonkingkong              |                               |
|      RDS_PASSWORD      |              DB config               |            cut             |            cut             |             [secret]              |               [secret]                |           [secret]            |
|  CFUL_DELIVERY_TOKEN   |     Make requests to Contentful      |          [secret]          |          [secret]          |             [secret]              |               [secret]                |           [secret]            |
|      CFUL_ENV_ID       |     Make requests to Contentful      |           master           |           master           |              master               |                master                 |            master             |
|     CFUL_SPACE_ID      |     Make requests to Contentful      |        zjf0mnbplhc5        |        zjf0mnbplhc5        |           zjf0mnbplhc5            |             zjf0mnbplhc5              |         zjf0mnbplhc5          |
|     JWT_SECRET_KEY     |  Authenticate requests from clients  |          [secret]          |          [secret]          |             [secret]              |               [secret]                |           [secret]            |
| MONOLITH_REST_ENDPOINT |  Make requests to chatloop-monolith  | http://localhost:8080/rest | http://localhost:8080/rest | https://e2e-api.chatloop.org/rest | https://staging-api.chatloop.org/rest | https://api.chatloop.org/rest |
|   AWS_ACCESS_KEY_ID    | Get secrets from AWS Secrets Manager |          [secret]          |          [secret]          |                 ?                 |                   ?                   |               ?               |
| AWS_SECRET_ACCESS_KEY  | Get secrets from AWS Secrets Manager |          [secret]          |          [secret]          |                 ?                 |                   ?                   |               ?               |
|                        |                                      |                            |                            |                                   |                                       |                               |

Note that the app also fetches a JWT from AWS Secrets Manager. It is used for making requests to `chatloop-monolith`.

## Database

## Development

### Running the app

#### Running the App in IDE

For app development, it is recommended to run the app in the IDE, rather than in a Docker container (see Local
Deployment Test below).

Most of the parameters that may need to be changed are in `application.properties`, including postgres, contentful, web
server port,
JWT secret. For local development we always use local spring profile, where we also treat `security.jwt.mac.secret` bit
differently
from rest of configuration options, and it should be provided as environment variable as
per `application-local.properties` when running
via main method.

By default, real, remote Contentful service is used, so internet connection is required.

Also, an instance of a postgres database is required. If needed, default docker container running postgres can be
started via `mvn docker:start`.
`mvn docker:stop` can be used to remove existing instance. Booted postgres instance will export port 5555 to localhost,
so that it does
not conflict with any pre-existing postgres instances which may be using default port 5432.

Developers can start application in IDE by running ChatloopUserTrainingMain main spring boot class. Create a run
configuration in your IDE
which uses the above class as main class and add VM option `-Dspring.profiles.active=local` to
use `application-local.properties`.
Also, you'll need to put the JWT secret key for the local environment into the run configuration as an environment
variable.

IDEA run configurations for staging and production profiles require all environment variables listed in their
respective `application-*.properties`
file.

Postman is recommended to manually test/learn API. `docs/` folder contains ready-to-use postman collection.

Authorization HTTP header of type Bearer Token is required to be present in each request (ie JWT token). Any such token
must be signed
or issued by the provider who used secret `security.jwt.mac.secret=` property. To generate tokens for different user
names, see class
JwtTokenGenerator in tests. For local testing, file `test-jwt-tokens.txt` contains pre-generated JWT tokens for 3
virtual users.

Once application is started, GraphQL is available under JWT-secured endpoint `http://localhost:8888/graphql`. Also,
there is unsecured
webhooks endpoint which is intended to be called by Contentful under `http://localhost:8888/webhook`.

In case you want to test integration with the Monolith (nkk), you need to send a valid Bearer token in each request.
There \
are two alternative ways to set the token:

- generate a JWT in the monolith by running `ServiceTokenGenerator.java` in the monolith and manually setting
  the `JWT_MONOLITH_REST` \
  environment variable in your run config to that token, or
- installing the `aws-cli` on your local computer and not setting the `JWT_MONOLITH_REST` envionment variable, in
  which \
  case the microservice on startup will fetch a token from `Secrets Manager` in AWS

#### Running the app using Docker (docker-compose.yaml)

[//]: # (TODO)

#### Running the app using `chatloop-network`

[//]: # (TODO: &#40;for local e2e testing or local front-end development&#41;)

### Tests

Tests require postgres database, which can be started with `mvn docker:start`. We intentionally always use postgres for
more realistic
testing rather than using embedded H2 database.

To run all tests from command line execute `mvn clean test`. It will also perform checkstyle verification.

### Profile options:

Default only used for now.

    local               - generally used locally
    staging             - when deploying to staging environment in AWS
    production          - enables several production environment settings
    e2e                 - same as local, but loads test data

## Deployment

### Local Deployment Test

Before deploying remotely, full stack docker deploy should be tested locally. `.env` file contains all environment
variables, some of which
need to be changed, such as Contentful parameters or JWT secret if they are different from default.

First ensure all our docker containers are down (our app, our postgres and our pgadmin), and app is not running in IDE
either, to avoid
port conflicts.

Next `mvn clean package -Dmaven.test.skip=true`, which builds and packages new docker image of app. You can verify that
new image is
built via `docker images`.

Next, execute `docker-compose --env-file ./.env up`. This will boot docker containers for our app, postgres and pgadmin.

They expose the 8888 web port, the 5555 postgres port and 29000 pgadmin port (`http://localhost:29000/browser/`), all of
which should be accessible
on the host machine. To shut down all containers, pressing Ctrl+C should do the trick.

You can stop & remove ALL containers and networks with `./scripts/docker_reset.sh`. Optional, and use with care.

If there are port conflict related errors or warnings, it probably means some prior container was not shut down (
properly) and
clean it first before retrying. Or, if unable to connect type of errors are seen, then some containers are not started.

### Manual AWS Deployment

Lists the steps which are needed to perform a deployment to AWS.

### Prerequisites

The following tools should be installed on your machine.

#### Terraform

To deploy infrastructure and service, you will use Terraform 0.12.

It can be manually installed by downloading the latest 0.12.x release here:
https://releases.hashicorp.com/terraform/

As an alternative, you may also use a package manager for you OS.

###### MacOS

`brew install terraform@0.12`

###### Windows

`choco install terraform --version 0.12.29 --allow-downgrade`

To check which version you are running, execute: `terraform -v`

##### Python 3

Make sure you have python 3 installed on your machine as well.
see https://www.python.org/downloads/

##### AWS CLI

Install AWS CLI version 2 on you machine.
see https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html

After AWS CLI is installed, you should set up your AWS credentials using
profiles. In your user directory, after you installed the AWS CLI, there should
be a hidden folder for configuration and credentials: `~/.aws/`

First log in to AWS console and get a key pair for your user:
https://signin.aws.amazon.com/console

In the top right corner, expand the dropdown menu under your account name and select `My Security Credentials`.
You are now heading over to Amazons IAM service. In the center of the screen, expand the item "Access keys (access key
ID and secret access key)"
and click the "Create New Access Key" button. Note: you can only have 2 key pairs at the same time.

A key pair contains two values. The "AWS Access Key" and the "AWS Secret Access Key". The "AWS Secret Access Key" will
only be shown once, after it is created.
So make sure to download the csv file containing the key pair or copy paste the key pair into a text editor for now.

It is time to finish your AWS CLI configuration. Let's assume the profile we want to create is called `chatloop`
Open or create `~/.aws/config` in a text editor and add this:

```
[default]
region=us-west-1

[profile chatloop]
region=ap-southeast-2
```

Next, open or create `~/.aws/credentials` in a text editor and use the key pair which you created earlier:

```
[chatloop]
aws_access_key_id=<your key>
aws_secret_access_key=<your secret>
```

Finally, to test the AWS CLI, open a terminal and run:
`aws s3 ls` which should list your existing S3 buckets.

#### Creating the auxiliary AWS infrastructure

In the subfolder `infrastructure/terraform` are a bunch of subfolders for the environments `staging-user-training`
and `production-user-training`:

* `infrastructure/terraform/modules` - defines the template for AWS resources which need to exist in every environment (
  database etc)
* `infrastructure/terraform/staging-user-training` - invokes the template with the parameters to create
  the `staging-user-training` environment
* `infrastructure/terraform/production-user-training` - invokes the template with the parameters to create
  the `production-user-training` environment

The next step has to be done once before using Terraform to initialize any of the environments.

Log in to the AWS console and select the correct region in the upper right. Go to the "Secrets Manager" service, click "
Store new scret", click "Other type of secrets", switch to the Plaintext tab and
drop the DB password for your environments database (plain, no JSON):

```
superSecr3tPassw0rd // don't use this
```

The name of the new secret should be `chatloop-<environment name>-db-password`. Leave all other options to their default
values
and click "Next" a couple of times and finally "Store" to create the secret.

Now it's time to execute Terraform. Change into one of the subfolders above and run the commands
below (`staging-user-training` as an example).

```bash
cd infrastructure/terraform/staging-user-training
terraform init
terraform apply
```

Review the changes and - if everything looks fine - confirm by typing `yes` and hit ENTER. Note the database hostname
output at the end.

#### Packaging

To package the project, run `mvn clean package -Dmaven.test.skip=true`. This will generate a package
called `chatloop-user-training-elastic-beanstalk.zip`.

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

These 5 env variables need also be set manually

* RDS_HOSTNAME - output when Terraform finished creating the environments database
* RDS_PORT - 5432
* RDS_DB_NAME - ebdb
* RDS_USERNAME - neonkingkong
* RDS_PASSWORD - to be found in AWS Secrets Manager

To create an Elastic Beanstalk environment via the AWS web console:

1. Set the platform to Docker.
2. Upload `chatloop-user-training-elastic-beanstalk.zip` from local machine and navigate to the configuration page,
   rather than creating the environment at this stage.
3. Set the environment variables in the "Software" panel.
4. In the "Capacity" panel, select "load balancer" over "single instance".
5. In the "load balancer" panel, set up a listener for `https`

- Port: 443
- Protocol: HTTPS
- SSL certificate: choose the chatloop.org one
- SSL policy: ELBSecurityPolicy-2016-08
- Default process: default

6. Also in the "load balancer" panel, add a process for the health checks:

- Name: `default`
- Port: `80`
- Protocol: `HTTP`
- Health check path: `/actuator/health`
- Stickiness: `disabled`

7. In the "Rolling updates" panel, select "Rolling with additional batches".
8. In the "Security" panel, assign an SSH keypair, so you can SSH into the EC2 container if you need to.
9. Leave the "Database" panel blank.

When load balancer is configured, environment URL should be used to connect to the app, rather than instance DNS name.

### Pipeline AWS Deployment

To deploy to AWS via the BitBucket pipeline:

1. Merge a PR into the master branch. This will trigger the BitBucket pipeline to run the test suite, and then build the
   app.

2. If the build is successful, the BitBucket pipeline will automatically deploy the microservice to
   the `staging-user-training`
   Elastic Beanstalk staging
   environment ([staging-user-training.chatloop.org](https://staging-user-training.chatloop.org))

3. To deploy to the `production-user-training` Elastic Beanstalk production
   environment ([user-training.chatloop.org](https://user-training.chatloop.org)),
   go to the BitBucket UI
   at [the pipeline Deployments page](https://bitbucket.org/thoughtastronaut/chatloop-training/addon/pipelines/deployments),
   open the relevant pipeline (probably the most recent one), and click the DEPLOY button.

#### Troubleshooting

In test setups, create your own public/private key in AWS and enable it during instance configuration. For prod use
whatever prod key pair is generally used.

Above is test JWT secret which works with our test users (tester etc). The real monolith's key should generally be used.

To troubleshoot remote docker deployments, follow below instructions. Sudo is required for most commands once ssh-ed:

- Download your public/private key and save it to `~/.ssh/` directory. Exec `chmod 400 YouKey.pem`. This key should be
  enabled on the instance
  during environment creation process.
- `ssh -v -i "YouKey.pem" ec2-user@PublicDNSAddress-southeast-2.compute.amazonaws.com`. Once ssh-ed, `sudo docker ps -a`
  etc can be used to interact with Docker.
- `/opt/elasticbeanstalk/deployment/env.list` contains env variables that will be injected into docker container, which
  can be also checked
  with `./bin/get-config environment`.
- The file `deployments/app_source_bundle` is actually the beanstalk ZIP file, just rename it, and unpackage it if you
  need to check configuration.
  You can also rebuild image remotely with `sudo docker image build .`.
- Check if microservice container has connectivity to RDS via `ssh -vv PublicDNSAddress.rds.amazonaws.com -p 5432`. It
  should print the message:
  `Connection closed by remote host`.

`sudo docker tag 12fa899eaca4 cut/cutms`
`sudo docker run --name cut -d cut/cutms`
`sudo docker run --env-file ./env.list --name cut –d cut/cutms`

Or use beanstalk CLI client.

## Migration

See `docs/migration/migration-guide.txt`.

## Misc

To run test coverage, execute `mvn clean verify`. Test report should be
under `chatloop-training/target/jacoco-report/index.html` which can be
opened in the browser. Long term wise, as more functionality is being migrated from Monolith to this or other
microservices, I would suggest considering
creating automated REST/GQL test suite in Postman (or other similar tools), which would cover UI, Contentful and backend
microservices, and whose
tests would be conversational ie model exactly the flow of business use cases.

When interacting with Maven, tests can be skipped with `-Dmaven.test.skip=true` flag.

Start docker container of application manually. Handy when troubleshooting beanstalk setup. Shows list of all env
variables required.

`docker run --name chatloop-user-training -e RDS_HOSTNAME=db -e RDS_PORT=5432 -e RDS_DB_NAME=cut -e RDS_USERNAME=cut -e RDS_PASSWORD=cut -e JWT_SECRET_KEY= -e SPRING_PROFILES_ACTIVE=staging -p 8888:8080 -d neonkingkong/chatloop-user-training`

