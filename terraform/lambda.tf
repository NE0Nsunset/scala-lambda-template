resource "aws_lambda_function" "lambda-autowire-backend" {
  function_name = "${local.application-identity}-autowire-api"
  runtime = "java8"

  filename          = "${var.lambda_payload_filename}"
  source_code_hash  = "${local.lambda_source_hash}"

  handler = "lambda.LambdaHandler::autowireApiHandler"
  role = "${aws_iam_role.lambda_role.arn}"

  memory_size = 1024 // TODO needs tuning, default (128) not enough

  environment {
    variables {
      ENABLE_CORS = "true"
      ENABLE_DEBUG = "true"
    }
  }
  depends_on = [
    "aws_iam_role.lambda_role",
  ]
}