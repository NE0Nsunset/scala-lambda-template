package lambda.models

trait DynamoItem {
  val partKey: String;
  val rangeKey: String;

  val createdAt: String;
  val lastUpdate: String;
}
