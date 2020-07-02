output "dynamodb_arn" {
  value = "${aws_dynamodb_table.lambda-scala-table.arn}"
}

output "dynamodb_name" {
  value = "${aws_dynamodb_table.lambda-scala-table.name}"
}