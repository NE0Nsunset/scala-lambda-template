locals {
  api_title = "scala-lambda-template"
  api_description = "API for scala-lambda-template"

  lambda_source_hash = "${base64sha256(file(local.lambda_payload_filename))}"
  front_end_source_hash = "${base64sha256(file(local.frontend_lambda_file))}"

  lambda_payload_filename = "${"${var.project-root}/lambda/target/scala-2.13/lambda-assembly-1.0.jar"}"

  frontend_lambda_file = "${"${var.project-root}/client/src/main/js/handler.js"}"

  static_s3 = "https://${aws_s3_bucket.frontend_ui.bucket_domain_name}"

  default_stage_name = "/${aws_api_gateway_deployment.a.stage_name}"

  static_s3_or_override = "${var.override_static_base == 0 ? local.static_s3 : var.override_static_base}"
  backend_api_or_override = "${var.override_api_base == 0 ? aws_api_gateway_deployment.a.invoke_url : var.override_api_base}"
  stagename_or_override = "${var.override_stage_name == 0 ? local.default_stage_name : var.override_stage_name}"

}