# Chatloop Training

## Development

### Running the App in IDE

Developers can start application in IDE by running ChatloopTrainingMain main spring boot class.  Create run configuration in IDE 
which uses ChatloopTrainingMain as main class and add VM option `-Dspring.profiles.active=local` to use `application-local.properties`.

By default it will use real Contenful service, so internet connection is required. Contentful parameters, such as space/env IDs etc, 
can be changed in `application-*.properties`. 

Once application is started, you can connect to `http://localhost:8080/graphiql` to access GraphQL console. Ready to use sample
queries which use IDs present in remote Contentful store can be found in `docs/graphql-queries.txt`. Just copy/paste them one by
one, in order, into GraphiQL web console.

Embedded H2 database is used by default, and it will keep its data in project root directory. H2 web console can be accessed via
`http://localhost:8080/h2-console`, password is `sa`. JDBC URL in console should point to `jdbc:h2:file:./h2data`. This ensures
that database state is preserved between runs.

#### In Docker

TODO

### Tests

To run all tests from command line execute `mvn clean package`.

### Profile options:

Default only used for now.

    local           	- when running the server locally
    staging         	- when deploying to staging environment in AWS
    production			- enables several production environment settings

## Deployment

### To AWS

TODO

## FAQ

TODO
