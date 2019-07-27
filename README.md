# SCALA LAMBDA BINDING TEMPLATE
Basic structure for a Binding.scala frontend powered by scala -> java AWS Lambda functions.
This project uses Autowire to bind a single AWS Lambda function to automatically absorb all frontend requests and direct
them to their appropriate api calls. Additional frontend request endpoints would not require additional setup in AWS beyond the first lambda.

The project is split into multiple parts. The `client` folder contains the scalajs / binding.scala codebase. 
`Shared` contains the shared objects that are to be used between the frontend and the backend. `lambda` contains the code for the 
AWS lambda function and the handlers to get into those functions. `lambda-offline` depends on the code in `lambda` and is intended for
local development purposes.

A 'fat' jar is created via the sbt-assembly plugin to upload to AWS. This is also the reason for the distinction between lambda-offline and lambda,
to limit the amount of unnecessary dependencies in the AWS jar file.

## Project Structure
    client/                     # Binding.scala / scalajs frontend codebase
    shared/                     # Shared objects between client and server
    lambda/                     # AWS Lambda functions
    lambda-offline/             # Local wiring of above lambda functions for development
    | -- src/main/public          # static files
    terraform                   # Base terraform configuration files, see terraform section below
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
The terraform folder contains a basic deploy infrastructure. It is namespaced, so all assets should share a common prefix. 

This project depends on terraform 11. Version 12 will not work. The terraform cli is available through brew as `brew install @terraform0.11`. 
You'll also need to install AWS cli and setup credentials, see https://docs.aws.amazon.com/singlesignon/latest/userguide/howtogetcredentials.html
Terraform depends on ~/.aws/credentials being present and the `AWS_PROFILE` environment variable to connect. 

- After installing terraform and aws cli, cd into the terraform config folder `cd terraform`
- run `terraform init` to install the required modules
- run `terraform plan` to build the infrastructure plan, this gives an opportunity to see what will be built
- run `terraform apply` to deploy the plan to aws

What gets deployed?
- api gateway
- lambda(s)
- s3 bucket for static files and static files
- roles

To cleanup your deployment, run `terraform destroy` to remove every asset `apply` created in AWS.

TODO clunky deployment
The steps above will deploy the backend api and s3 bucket that hosts the static resources like index.html and client-opt.js. 
At the moment I haven't worked out how to inject the backend api url from the deploy to the static files. To resolve this, after terraform apply'ing, 
copy the `api_gateway_url` from the output and paste it into lambda-offline/src/main/pubic/index.html in the json value for `backendApi`. 
Run `terraform apply` again and it will deploy just the updated index.html with the correct backend api url.