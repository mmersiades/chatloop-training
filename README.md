# Chatloop Training

## Development

### Running the App in IDE

Most of the parameters that may need to be changed are in `application.properties`, including postgres, contentful, web server port,
JWT secret.

By default real, remote Contenful service is used, so internet connection is required. 

Also, instance of postgres database is required. If needed, default docker container running postgres can be started via `mvn docker:start`.
`mvn docker:stop` can be used to remove existing instance. Booted postgres instance will export port 5555 to localhost, so that it does
not conflict with any pre-existing postgres instances which may be using default port 5432. 

Developers can start application in IDE by running ChatloopUserTrainingMain main spring boot class.  Create run configuration in IDE 
which uses above class as main class and add VM option `-Dspring.profiles.active=local` to use `application-local.properties`. 

Also, IDEA run configuration needs environment variable `JWT_SECRET_KEY=`. Property `security.jwt.mac.secret` in `application-test.properties` 
contains default Monolith's private key used to verify test JWT tokens which can be used as default.

Postman is recommended to manually test/learn API. `docs/` folder contains ready-to-use postman collection.

Authorization HTTP header of type Bearer Token is required to be present in each request (ie JWT token). Any such token must be signed
or issued by provider which used secret `security.jwt.mac.secret=` property. To generate tokens for different user names, see class 
JwtTokenGenerator in tests. For local testing, file `test-jwt-tokens.txt` contains pre-generated JWT tokens for 3 virtual users.

Once application is started, GraphQL is available under JWT-secured endpoint `http://localhost:8888/graphql`. Also, there is unsecured
webhooks endpoint which is intended to be called by Contentful under `http://localhost:8888/webhook`.

### Running the App in Docker

First `mvn clean package -Dmaven.test.skip=true`, which builds and packages new docker image. You can verify that new image is built via
`docker images`. 

Then `docker-compose up` to start microservice, postgres database and pgadmin contrainers. They expose 8888 web port, 5555 
postgres port and 29000 pgadmin port (`http://localhost:29000/browser/`), all of which should be accessible on the host machine. To shutdown all containers, pressing
Ctrl+C should do the trick.

File `.env` contains environment variable `JWT_SECRET_KEY` which is used by docker-compose to propagate it to containers.
Default value of above variable is Monolith's test JWT secret. Change it as needed.

You can stop & remove ALL containers and networks with `./scripts/docker_reset.sh`. Optional, and use with care.

If there are port conflict related errors or warnings, it probably means some prior containter was not shutdown (properly) and
clean it first before retrying.

### Tests

Tests require postgres database, which can be started with `mvn docker:start`. We intentionally always use postgres for more realistic
testing rather then using embedded H2 database. 

To run all tests from command line execute `mvn clean test`. It all also perform checkstyle verification.

### Profile options:

Default only used for now.

    local               - generally used locally
    staging             - when deploying to staging environment in AWS
    production          - enables several production environment settings

## Deployment

To package project, run `mvn clean package`.

### To AWS

...

## FAQ

...

## Misc

Start docker container of application manually. Handy when troubleshooting beanstalk setup. Shows list of all env
variables required.

`docker run --name chatloop-user-training -e RDS_HOSTNAME=db -e RDS_PORT=5432 -e RDS_DB_NAME=cut -e RDS_USERNAME=cut -e RDS_PASSWORD=cut -e JWT_SECRET_KEY= -e SPRING_PROFILES_ACTIVE=staging -p 8888:8080 -d neonkingkong/chatloop-user-training`

