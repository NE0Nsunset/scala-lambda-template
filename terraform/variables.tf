variable "aws_region" {default = "us-west-2"}
variable "app_version" {}
variable "app_uuid" {
  default = false
}

variable "lambda_payload_filename" {
  default = "../../../lambda/target/scala-2.12/lambda-assembly-1.0.jar"
}

variable "dynamodb_table_name" {default = "scala-lambda-table" }

variable "dynamodb_table_read_capacity" {default = 2}

variable "dynamodb_table_write_capacity" {default = 2}

variable "dynamodb_table_gsi_read_capacity" {default = 2}

variable "dynamodb_table_gsi_write_capacity" {default = 2}

variable "override_api_base" { default = false}
variable "override_static_base" { default = false}
variable "override_stage_name" { default = false}