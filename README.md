# Chatloop Training

## Development

### Running the App in IDE

Developers can start application in IDE by running ChatloopTrainingMain main spring boot class.  Create run configuration in IDE 
which uses ChatloopTrainingMain as main class and add VM option `-Dspring.profiles.active=local` to use `application-local.properties`.

By default it will use real Contenful service, so internet connection is required. Contentful parameters, such as space/env IDs etc, 
can be changed in `application-*.properties`. 

Once application is started, GraphQL endpoint is available under `http://localhost:8080/graphql`. Bearer Token HTTP header is required. 
For local testing, file `test-jwt-tokens.txt` contains pre-generated tokens for 3 virtual users. To generate tokens for different user
names, see class JwtTokenGenerator in tests. In `docs/` you can find sample Postman configuration with preconfigured JWT tokens.

Embedded H2 database is used by default, and it will keep its data in project root directory. H2 web console can be accessed via
`http://localhost:8080/h2-console`, password is `sa`. JDBC URL in console should point to `jdbc:h2:file:./h2data`. This ensures
that database state is preserved between runs.

You can connect to `http://localhost:8080/graphiql` to access GraphiQL console. Ready to use sample queries which use IDs present in 
remote Contentful store can be found in `docs/graphql-queries.txt`. Just copy/paste them one by one, in order, into GraphiQL web console.
To be obsoleted soon by Postman, which has better support for JWT tokens.

You may optionally install pgadmin4 tool (postgres admin console), similar purpose-wise to H2 console. After installation, it's
available at `http://127.0.0.1:43535/browser/`. TODO

#### In Docker

TODO

### Tests

To run all tests from command line execute `mvn test`.

### Profile options:

Default only used for now.

    local           	- when running the server locally
    staging         	- when deploying to staging environment in AWS
    production			- enables several production environment settings

## Deployment

To package project, run ``mvn clean package`.

### To AWS

TODO

## FAQ

TODO
