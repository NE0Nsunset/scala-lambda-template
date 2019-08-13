variable "aws_region" {default = "us-west-2"}
variable "app_version" {default = "v0.1.0"}
variable "app_uuid" {
  default = false
}

variable "s3_utility_user_arn" {default = "arn:aws:iam::016854806273:user/test_user"} // change to you AWS user arn

variable "lambda_payload_filename" {
  default = "../lambda/target/scala-2.12/lambda-assembly-1.0.jar"
}

variable "dynamodb_table_name" {default = "scala-lambda-table" }

variable "dynamodb_table_read_capacity" {default = 10}

variable "dynamodb_table_write_capacity" {default = 10}

variable "dynamodb_table_gsi_read_capacity" {default = 10}

variable "dynamodb_table_gsi_write_capacity" {default = 10}