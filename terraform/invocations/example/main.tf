terraform {
  required_version = "~> 0.11.11"
}

provider "aws" {
  alias  = "us-west-2"
  region = "us-west-2"
}

output "s3_website_url" {
  value = "${module.scala-lambda.s3_website_url}"
}

output "api_gateway_url" {
  value = "${module.scala-lambda.api_gateway_url}"
}

output "static url" {
  value = "${module.scala-lambda.static_url}"
}
