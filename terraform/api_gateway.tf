resource "aws_lambda_permission" "invoke-scala-api" {
  statement_id = "AllowInvokeScalaLambdaApi"
  action = "lambda:InvokeFunction"
  principal = "apigateway.amazonaws.com"
  function_name = "${aws_lambda_function.lambda-autowire-backend.arn}"
  source_arn = "${aws_api_gateway_deployment.a.execution_arn}/*/*"
}

data "template_file" "open_api_yaml_template" {
  template = "${file("${path.module}/templates/openapi.v3.yaml")}"
  vars = {
    lambda_api_backend_arn = "${aws_lambda_function.lambda-autowire-backend.invoke_arn}"
    frontend_arn = "${aws_lambda_function.front-end.invoke_arn}"
    aws_region="${var.aws_region}"
    iam_role_arn="${aws_iam_role.api_gatewayrole.arn}"
    api_title="${local.api_title}"
    app_version="${var.app_version}"
  }
}

resource "aws_api_gateway_rest_api" "rest_api" {
  body = "${data.template_file.open_api_yaml_template.rendered}"
  name = "${local.application_identity}_gateway"
  description = "${local.api_description}"

  endpoint_configuration {
    types = ["REGIONAL"]
  }
}

resource "aws_api_gateway_deployment" "a" {
  rest_api_id = "${aws_api_gateway_rest_api.rest_api.id}"
  stage_name = "a"

  stage_description = "${md5(file("${path.module}/templates/openapi.v3.yaml"))}" // actually trigger deploy on api definition change
}