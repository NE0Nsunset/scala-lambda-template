resource "aws_lambda_layer_version" "base" {
  layer_name        = "scala-lambda-dependency-layer"
  filename          = "${data.archive_file.api_lambda_dependency_layer.output_path}"
  source_code_hash  = "${data.archive_file.api_lambda_dependency_layer.output_base64sha256}"

  compatible_runtimes = ["java8"]
}

resource "aws_lambda_function" "lambda-autowire-backend" {
  function_name = "${local.application-identity}-autowire-api"
  runtime = "java8"

  filename          = "${var.lambda_payload_filename}"
  source_code_hash  = "${local.lambda_source_hash}"

  handler = "lambda.LambdaHandler::autowireApiHandler"
  role = "${aws_iam_role.lambda_role.arn}"

  memory_size = 2000 // At least 1536 for Java runtimes signitificantly improves cold start time

  timeout = 30 // TODO determine ways to speed up cold starts

  layers = ["${aws_lambda_layer_version.base.arn}"]

  environment {
    variables {
      ENABLE_CORS = "true"
      ENABLE_DEBUG = "true"
      ENV = "aws"
      TABLE_NAME = "${aws_dynamodb_table.lambda-scala-table.name}"
    }
  }
  depends_on = [
    "aws_iam_role.lambda_role",
  ]
}

resource "aws_lambda_function" "hello-world" {
  function_name = "${local.application-identity}-hello-world"
  runtime = "java8"

  filename          = "${var.lambda_payload_filename}"
  source_code_hash  = "${local.lambda_source_hash}"

  handler = "lambda.LambdaHandler::helloWorld"
  role = "${aws_iam_role.lambda_role.arn}"

  memory_size = 2000 // At least 1536 for Java runtimes signitificantly improves cold start time

  timeout = 30 // TODO determine ways to speed up cold starts

  layers = ["${aws_lambda_layer_version.base.arn}"]

  environment {
    variables {
      ENABLE_CORS = "true"
      ENABLE_DEBUG = "true"
      ENV = "aws"
      TABLE_NAME = "${aws_dynamodb_table.lambda-scala-table.name}"
    }
  }
  depends_on = [
    "aws_iam_role.lambda_role",
  ]
}