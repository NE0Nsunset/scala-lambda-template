module "scala-lambda" {
  source = "../../../terraform"

  aws_region  = "us-west-2"
  app_version = "v0.2.0"

  providers = {                                 #explicit declaration
    aws             = "aws.us-west-2"
  }

  override_api_base = false
  override_static_base = false
  override_stage_name = false
}