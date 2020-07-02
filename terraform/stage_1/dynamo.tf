resource "aws_dynamodb_table" "lambda-scala-table" {
  name = "${var.dynamo_db_table}"
  hash_key = "partKey"
  range_key = "rangeKey"

  read_capacity = "${var.dynamodb_table_read_capacity}"
  write_capacity = "${var.dynamodb_table_write_capacity}"


  attribute {
    name = "partKey"
    type = "S"
  }

  attribute {
    name = "rangeKey"
    type = "S"
  }

  ttl {
    attribute_name = "expireDateEpoch"
    enabled = true
  }

  global_secondary_index {
    name               = "rangeIndex"
    hash_key           = "rangeKey"
    write_capacity     = "${var.dynamodb_table_gsi_write_capacity}"
    read_capacity      = "${var.dynamodb_table_gsi_read_capacity}"
    projection_type    = "ALL"
  }

  lifecycle {
    prevent_destroy = false
  }
}