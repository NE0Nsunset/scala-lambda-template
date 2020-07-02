variable "aws_region" {}
variable "app_version" {default = "v0.1.0"}

variable "override_api_base" { default = false}
variable "override_static_base" { default = false}
variable "override_stage_name" { default = false}

variable "dynamodb_name" {}
variable "dynamodb_arn" {}

variable "application_prefix" {}
variable "application_identity" {}
variable "application-identity" {}

variable "project-root" {}