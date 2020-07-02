# SCALA LAMBDA BINDING TEMPLATE
Basic structure for a [Binding.scala](https://github.com/ThoughtWorksInc/Binding.scala), [org.lrng.binding](https://github.com/GlasslabGames/html.scala) frontend powered by scala -> java AWS Lambda functions.
This project uses [Autowire](https://github.com/lihaoyi/autowire) to bind a single AWS Lambda function to automatically absorb all frontend requests and direct
them to their appropriate api calls. Additional frontend request endpoints would not require additional setup in AWS beyond the first lambda.

The project is split into multiple parts. The `client` folder contains the scalajs / binding.scala codebase. 
`shared` contains the shared objects that are to be used between the frontend and the backend. `lambda` contains the code for the 
AWS lambda function and the handlers to get into those functions. `lambda-offline` depends on the code in `lambda` and is intended for
local development purposes.

A 'fat' jar is created via the sbt-assembly plugin to upload to AWS. This is also the reason for the distinction between lambda-offline and lambda,
to limit the amount of unnecessary dependencies in the AWS jar file.

[Terraform](https://www.terraform.io/docs/index.html) is used to manage and deploy a basic infrastructure to AWS.

## Project Structure
    client/                     # Binding.scala / scalajs frontend codebase
    | -- scala/                   # scalajs source code
    | -- js/                      # AWS frontend lambda source
    docker/                     # Docker files 
    shared/                     # Shared objects between client and server
    lambda/                     # AWS Lambda functions
    lambda-offline/             # Local wiring of above lambda functions into Akka HTTP server for local development
    | -- src/main/public          # static files
    terraform                   # Basic Terraform configuration files, see terraform section below
    |  -- invocatations/          # directory that houses different terraform instances
    |       -- example/             # an example invocation, run `terraform init`, `terraform apply`, etc, from here 
    README.md                   # This readme file 

## SBT Commands
After starting sbt from the project root, use the following commands to:
- Compile frontend and backend  - `buildTask`
- Compile frontend              - `client/fastOptJS` or `client/fullOptJS` 
                                  (local is wired for fastOpt while Terraform deploy uses fullOpt)
- Compile backend               - `sbt lambda/assembly`
- Run local development server  - `~lambdaOffline/reStart` 
                                  This starts a continuous compile, meaning any changes in project folders will recompile and restart the webserver

## DynamoDB
This template comes with a terraform configuration for a simple DynamoDB table. A basic example of a Dynamo Service exists in lambda.service.MovieServiceImpl.

Use the included docker/docker-compose.yml to start a local dockerized version of DynamoDB by running `docker-compose up` from the docker folder.

OR

One can install it via https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html.
After following the install guide:
- Start DynamoDB - run `java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory` from the folder created during install

Note, make sure a AWS default profile exists in ~/.aws/credentials

DynamoDB shell can be accessed via http://localhost:8000/shell/

## Local Server
Akka HTTP is used for developing locally. Run it in SBT via `~reStart`. While running, changes to code in sub project folders will cause 
SBT to re-compile and re-start the web server. The http server is hosted at `http://localhost:9090`

## Running Tests
TODO

## Terraform deployment
The ./terraform folder contains a basic deploy infrastructure. It is namespaced, so all assets should share a common prefix.

An example invocation is located at ./terraform/invocations/example. CD into this directory before running `terraform init`, `terraform apply`, etc. 

This project depends on terraform 11. Version 12 will not work. Install the Terraform CLI through brew via `brew install @terraform0.11`. 
You'll also need to install AWS CLI and setup credentials, see https://docs.aws.amazon.com/singlesignon/latest/userguide/howtogetcredentials.html
Terraform depends on ~/.aws/credentials being present and the `AWS_PROFILE` environment variable to connect. 

### Deploy
- run the SBT command to compile the frontend and backend from the root directory, `sbt buildTask`.
- After installing Terraform and AWS CLI, cd into the ./terraform/invocations/example folder, `cd terraform/invocations/example`
- run `terraform init` from the invocations/example/ folder to install the required modules
- run `terraform plan` from the invocations/example/ folder to build the infrastructure plan. This also gives an opportunity to see what will be built
- run `terraform apply` from the invocations/example/ folder to deploy the plan to AWS

#### Deploy Phase 2
After deploying, one can create a custom api gateway domain and configure the app to use said domain and eliminate the stage prefix. Once a domain is setup, 
simple edit `invocations/example/main.tf` and change `override_api_base` to the domain name (including proto) and change `override_stage_name` from false to empty string.
Then run `terraform apply` again. 

#####
Run S3 sync to copy static assets to the S3 bucket
- cd into `lambda-offline/src/main/public`
- run `aws s3 sync . s3://<bucket_name from terraform static_url output>`

### What gets deployed?
- API Gateway
- Lambda(s)
- S3 bucket for index.html and frontend static files
- Iam roles
- Dynamo table

When terraform finishes, the output will contain a url for the backend api and frontend. Copy/paste the frontend url into a browser to see your deployment.

### Cleanup
To remove all assets created during deploy run `terraform destroy` from the invocations/example folder. Please note, this starter template stores terraform state locally.
You will need this state to perform changes or remove the assets. If the state files are erased, Terraform will essentially start from scratch. 

