data "aws_iam_policy_document" "lambda" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = [
        "lambda.amazonaws.com"
      ]
    }

    actions = ["sts:AssumeRole"]
  }
}

data "aws_iam_policy_document" "api_gateway" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = [
        "lambda.amazonaws.com",
        "apigateway.amazonaws.com"
      ]
    }

    actions = ["sts:AssumeRole"]
  }
}

data "template_file" "gateway_policy" {
  template = "${file("${path.module}/templates/api_gateway_policy.json")}"
}

data "template_file" "dynamo_policy" {
  template = "${file("${path.module}/templates/lambda_policy.json")}"

  vars {
    dynamodb_table_arn = "${aws_dynamodb_table.lambda-scala-table.arn}"
  }
}

resource "aws_iam_role" "lambda_role" {
  assume_role_policy = "${data.aws_iam_policy_document.lambda.json}"
  name = "${local.application-identity}-lambdaRole"
}

resource "aws_iam_role" "api_gatewayrole" {
  assume_role_policy = "${data.aws_iam_policy_document.api_gateway.json}"
  name = "${local.application-identity}-apiGatewayRole"
}

resource "aws_iam_policy" "lambda_dynamo" {
  policy = "${data.template_file.dynamo_policy.rendered}"
  name = "${local.application-identity}_lambda_dynamo"
  path = "/${local.application_prefix}/"
}

resource "aws_iam_policy" "api_gateway" {
  policy = "${data.template_file.gateway_policy.rendered}"
  name = "${local.application-identity}_api_gateway"
  path = "/${local.application_prefix}/"

}

resource "aws_iam_policy_attachment" "api-gateway" {
  name = "${local.application-identity}-api-gateway-attach-role"
  roles = ["${aws_iam_role.api_gatewayrole.name}"]
  policy_arn = "${aws_iam_policy.api_gateway.arn}"
}

resource "aws_iam_policy_attachment" "lambda" {
  name = "${local.application-identity}-lambda-attach-role"
  roles = ["${aws_iam_role.lambda_role.name}"]
  policy_arn = "${aws_iam_policy.lambda_dynamo.arn}"
}