// to create a database, uncomment the module below
//module "scala-lambda-dynamo-creation-stage" {
//  source = "../../../terraform/stage_1"
//  dynamo_db_table = "${local.application_identity}-table"
//}

module "scala-lambda-stage-2" {
  source = "../../../terraform/stage_2"

  // to use an existing database, comment out the above module and provide
  // the dynamo db arn and name here
  //dynamodb_arn = "${module.scala-lambda-dynamo-creation-stage.dynamodb_arn}"
  //dynamodb_name = "${module.scala-lambda-dynamo-creation-stage.dynamodb_name}"
  dynamodb_arn = "EXISTING DYNAMODB ARN HERE"
  dynamodb_name = "EXISTING DYNAMODB NAME HERE"
  aws_region = "${var.aws_region}"

  override_api_base = "${var.override_api_base}"
  override_stage_name = "${var.override_stage_name}"
  override_static_base = "${var.override_static_base}"
  application_identity = "${local.application_identity}"
  application-identity = "${local.application-identity}"
  application_prefix = "${local.application_prefix}"
}