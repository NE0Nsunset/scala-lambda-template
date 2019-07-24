# SCALA LAMBDA BINDING TEMPLATE
Basic structure for a Binding.scala frontend powered by scala -> java AWS Lambda functions.
This project uses Autowire to bind a single AWS Lambda function to automatically absorb all frontend requests and direct
them to their appropriate api calls. Additional frontend requests require no extra setup in AWS.

The project is split into multiple parts. The `client` folder contains the scalajs / binding.scala codebase. 
`Shared` contains the shared objects that are to be used between the frontend and the backend. `lambda` contains the code for the 
AWS lambda function and the handlers to get into those functions. `lambda-offline` depends on the code in `lambda` and is intended for
local development purposes.

A 'fat' jar is created via the sbt-assembly plugin to upload to AWS. This is also the reason for the distinction between lambda-offline and lambda,
to limit the amount of unnecessary dependencies in the AWS jar file.

## Project Structure
    client/                     # Binding.scala / scalajs frontend codebase
    |-- static_resources            # index.html bootstrap and other static resources
    shared/                     # Shared objects between client and server
    lambda/                     # AWS Lambda functions
    lambda-offline/             # Local wiring of above lambda functions for development
    README.md                   # This readme file 

## SBT Commands
- Compile frontend              - `sbt client/fastOptJS` or `sbt client/fullOptJS`
- Compile fat jar for aws       - `sbt lambda/assembly`

## DynamoDB
TODO 
If using the DynamoDB database one can run it locally via https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html.
Note, make sure a AWS default profile exists in ~/.aws/credentials

After following the install guide:
- Start DynamoDB - run `java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory` from the folder created during install

DynamoDB shell can be accessed via http://localhost:8000/shell/

### Setup
TODO

## Local Server
TODO

## Running Tests
TODO

## Terraform deployment
TODO

