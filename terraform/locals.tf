locals {
  api_title = "Scala lambda functions"

  api_description = "Scala lambda / binding.scala / autowire template"

  api_prefix = "scala-lambda"

  app_uuid = "${var.app_uuid == 0 ? random_string.app_uuid.result : var.app_uuid}"

  application_prefix = "scala-lambda/${var.aws_region}/${local.app_uuid}"
  application_identity = "scala_lambda_${var.aws_region}_${local.app_uuid}"
  application-identity = "scala-lambda-${var.aws_region}-${local.app_uuid}"

  dynamo_db_table = "${local.application-identity}-${var.dynamodb_table_name}"

  tags = {
    Environment = "${var.app_uuid}"
    Project     = "${local.api_title}"
    Region      = "${var.aws_region}"
  }

  lambda_source_hash = "${base64sha256(file(var.lambda_payload_filename))}"
}