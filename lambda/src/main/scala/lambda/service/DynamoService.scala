package lambda.service

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.typesafe.config.ConfigFactory
import lambda.models.DynamoItem

trait DynamoService {
  val client: AmazonDynamoDB

  val prefixName: String

  def describeTable: String
}
