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
  front_end_source_hash = "${base64sha256(file(local.frontend_lambda_file))}"

  frontend_lambda_file = "${"${path.root}/../../../client/src/main/js/frontend.js"}"

  static_s3 = "https://${aws_s3_bucket.frontend_ui.bucket_domain_name}"

  default_stage_name = "/${aws_api_gateway_deployment.a.stage_name}"

  static_s3_or_override = "${var.override_static_base == 0 ? local.static_s3 : var.override_static_base}"
  backend_api_or_override = "${var.override_api_base == 0 ? aws_api_gateway_deployment.a.invoke_url : var.override_api_base}"
  stagename_or_override = "${var.override_stage_name == 0 ? local.default_stage_name : var.override_stage_name}"

}