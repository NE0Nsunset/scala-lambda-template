# SCALA LAMBDA BINDING TEMPLATE
Basic structure for a Binding.scala frontend powered by scala -> java AWS Lambda functions.
This project uses Autowire to bind a single AWS Lambda function to automatically absorb all frontend requests and direct
them to their appropriate api calls. Additional frontend request endpoints would not require additional setup in AWS beyond the first lambda.

The project is split into multiple parts. The `client` folder contains the scalajs / binding.scala codebase. 
`shared` contains the shared objects that are to be used between the frontend and the backend. `lambda` contains the code for the 
AWS lambda function and the handlers to get into those functions. `lambda-offline` depends on the code in `lambda` and is intended for
local development purposes.

A 'fat' jar is created via the sbt-assembly plugin to upload to AWS. This is also the reason for the distinction between lambda-offline and lambda,
to limit the amount of unnecessary dependencies in the AWS jar file.

Terraform is used to manage and deploy a basic infrastructure to AWS.

## Project Structure
    client/                     # Binding.scala / scalajs frontend codebase
    shared/                     # Shared objects between client and server
    lambda/                     # AWS Lambda functions
    lambda-offline/             # Local wiring of above lambda functions into Akka HTTP server for local development
    | -- src/main/public          # static files
    terraform                   # Basic Terraform configuration files, see terraform section below
    README.md                   # This readme file 

## SBT Commands
- Compile frontend              - `sbt client/fastOptJS` or `sbt client/fullOptJS` 
                                  (local is wired for fastOpt while deploy uses fullOpt)
- Compile fat jar for aws       - `sbt lambda/assembly`
- Run local development server  - Inside sbt console run `~offline/reStart` 
                                  This starts a continuous compile, meaning any changes in client or lambda folders will recompile and restart the webserver

## DynamoDB
TODO write simple DynamoDB service example
TODO write starter DynamoDB terraform config
If using the DynamoDB database one can run it locally via https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html.
Note, make sure a AWS default profile exists in ~/.aws/credentials

After following the install guide:
- Start DynamoDB - run `java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory` from the folder created during install

DynamoDB shell can be accessed via http://localhost:8000/shell/

## Local Server
Akka HTTP is used for developing locally. Run it in sbt via `~offline/reStart`. While running, changes to code in client,shared or lambda will cause 
SBT to re-compile and re-start the web server. The http server is hosted at `http://localhost:9000`

## Running Tests
TODO

## Terraform deployment
The ./terraform folder contains a basic deploy infrastructure. It is namespaced, so all assets should share a common prefix. 

This project depends on terraform 11. Version 12 will not work. Install the Terraform CLI through brew via `brew install @terraform0.11`. 
You'll also need to install AWS CLI and setup credentials, see https://docs.aws.amazon.com/singlesignon/latest/userguide/howtogetcredentials.html
Terraform depends on ~/.aws/credentials being present and the `AWS_PROFILE` environment variable to connect. 

- After installing Terraform and AWS CLI, cd into the ./terraform config folder, `cd terraform`
- run the SBT commands to compile the frontend, `sbt client/fullOptJS` and backend, `sbt lambda/assembly`. Terraform will upload the target output files.
- run `terraform init` to install the required modules
- run `terraform plan` to build the infrastructure plan, this gives an opportunity to see what will be built
- run `terraform apply` to deploy the plan to aws

What gets deployed?
- API Gateway
- Lambda(s)
- S3 bucket for index.html and frontend static files
- Iam roles

When terraform finishes the output will contain a url for the backend api and frontend. Copy/paste the frontend url into a browser to see your deployment.

# Cleanup
To remove all assets created during deploy run `terraform destroy`. Please note, this starter template stores terraform state locally in the ./terraform/ folder.
You will need this state to perform changes or remove the assets. If the state files are erased, Terraform will essentially start from scratch. 

