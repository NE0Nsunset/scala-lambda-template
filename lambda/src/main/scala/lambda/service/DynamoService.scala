package lambda.service

import akka.stream.ActorMaterializer
import akka.stream.alpakka.dynamodb.DynamoAttributes
import akka.stream.alpakka.dynamodb.scaladsl.DynamoDb
import akka.stream.scaladsl.Sink
import com.amazonaws.services.dynamodbv2.model.{
  AttributeValue,
  PutItemRequest,
  ScanRequest
}
import lambda.models.DynamoItem
import collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}

trait DynamoService[T <: DynamoItem] {
  val prefixName: String
  def describeTable: String
  val client: DynamoClientT
  implicit val executionContext: ExecutionContext
  implicit val materializer: ActorMaterializer

  def scan = {
    val source = DynamoDb
      .source(new ScanRequest().withTableName(client.tableName))
      .withAttributes(DynamoAttributes.client(client.alpakkaClient))
    source.runWith(Sink.head)
  }

  def put(t: T) = {
    val testMap: java.util.Map[String, AttributeValue] =
      Map[String, AttributeValue](
        ("partKey", new AttributeValue("asdasa")),
        ("rangeKey", new AttributeValue("rangeadsdas")))
    val source = DynamoDb
      .source(
        new PutItemRequest().withTableName(client.tableName).withItem(testMap))
      .withAttributes(DynamoAttributes.client(client.alpakkaClient))
    source.runWith(Sink.head)
  }
}
