variable "dynamo_db_table" {default = "scala-lambda-table" }

variable "dynamodb_table_read_capacity" {default = 10}

variable "dynamodb_table_write_capacity" {default = 10}

variable "dynamodb_table_gsi_read_capacity" {default = 10}

variable "dynamodb_table_gsi_write_capacity" {default = 10}