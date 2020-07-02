// A layer of node_module dependencies
data "archive_file" "api_lambda_dependency_layer" {
  type = "zip"
  source_dir = "${"${var.project-root}/lambda/target/scala-2.12/aws_layer/"}"

  output_path = "./files/api_dependency_layer.zip"
}

data "archive_file" "frontend_zip" {
  type = "zip"
  source_file = "${local.frontend_lambda_file}"

  output_path = "./files/frontend.zip"
}