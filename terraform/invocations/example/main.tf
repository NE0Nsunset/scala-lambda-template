terraform {
  required_version = "~> 0.11.11"

// uncomment to provide a S3 bucket back end
//  backend "s3" {
//    bucket = "S3_BUCKET_NAME"
//    key = "scalalambda-example"
//    region = "us-west-2"
//  }
}

provider "aws" {
  alias  = "us-west-2"
  region = "us-west-2"
}

variable app_uuid {default = "example"}
variable api_title {default = "example"}
variable api_prefix {default = "example"}

variable "aws_region" { default = "us-west-2" }

variable "override_api_base" { default = false}
variable "override_static_base" { default = false}
variable "override_stage_name" { default = false}

locals {
  application_prefix = "${var.api_prefix}/${var.aws_region}/${var.app_uuid}"
  application_identity = "${var.api_prefix}_${var.aws_region}_${var.app_uuid}"
  application-identity = "${var.api_prefix}-${var.aws_region}-${var.app_uuid}"
}

output "api_gateway_url" {value = "${module.scala-lambda-stage-2.api_gateway_url}"}
output "s3_website_url" {value = "${module.scala-lambda-stage-2.s3_website_url}"}
output "static_url" {value = "${module.scala-lambda-stage-2.static_url}"}
