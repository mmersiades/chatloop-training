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

Postman is recommended to manually test/learn API. `docs/` folder contains ready-to-use postman collection.

Authorization HTTP header of type Bearer Token is required to be present in each request (ie JWT token). Any such token must be signed
or issued by provider which used secret `security.jwt.mac.secret=` property. To generate tokens for different user names, see class 
JwtTokenGenerator in tests. For local testing, file `test-jwt-tokens.txt` contains pre-generated JWT tokens for 3 virtual users.

Property `security.jwt.mac.secret` in `application-local.properties` contains private key used to verify JWT tokens. Default value is
copied from Monolith for dev/testing purposes. Bearer Token HTTP header is required.   To generate tokens for different user names, see class JwtTokenGenerator in tests. 
In `docs/` you can find sample Postman configuration with preconfigured JWT tokens. TODO will change later to env variable.

Once application is started, GraphQL is available under JWT-secured endpoint `http://localhost:8888/graphql`. Also, there is unsecured
webhooks endpoint which is intended to be called by Contentful under `http://localhost:8888/webhook`.

You may optionally install pgadmin4 tool (postgres admin console) to get familiar with SQL schema & data.
#### In Docker

First `mvn clean package -Dmaven.test.skip=true`, which builds and packages new docker image. You can verify that new image is built via
`docker images`. Then `docker-compose up` to start both microservice as well as postgres database. They expose 8888 web port and 5555 
postgres port.

You can stop & remove ALL containers and networks with `./scripts/docker_reset.sh`. Optional, and use with care.

### Tests

To run all tests from command line execute `mvn clean test`. It all also perform checkstyle verification.

### Profile options:

Default only used for now.

    local           	- when running the server locally
    staging         	- when deploying to staging environment in AWS
    production			- enables several production environment settings

## Deployment

To package project, run `mvn clean package`.

### To AWS

...

## FAQ

...
