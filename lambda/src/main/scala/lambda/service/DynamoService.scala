package lambda.service

import akka.stream.ActorMaterializer
import akka.stream.alpakka.dynamodb.AwsOp.GetItem
import akka.stream.alpakka.dynamodb.DynamoAttributes
import akka.stream.alpakka.dynamodb.scaladsl.DynamoDb
import akka.stream.scaladsl.Sink
import com.amazonaws.services.dynamodbv2.document.{Item, ItemUtils}
import com.amazonaws.services.dynamodbv2.model.{
  AttributeValue,
  GetItemRequest,
  GetItemResult,
  PutItemRequest,
  PutItemResult,
  ScanRequest,
  ScanResult
}
import lambda.models.DynamoItem
import play.api.libs.json.{JsValue, Json, OWrites, Reads, Writes}

import scala.concurrent.{ExecutionContext, Future}
import lambda.serialization.Serializer._

import scala.collection.JavaConversions._

trait DynamoService[T <: DynamoItem] {
  val prefixName: String
  val clientHandler: DynamoClientT
  implicit val executionContext: ExecutionContext
  implicit val materializer: ActorMaterializer
  implicit val readsT: Reads[T]
  implicit val writesT: Writes[T]

  def attributeValues(t: T): java.util.Map[String, AttributeValue] = {
    val item = Item.fromJSON(Json.stringify(Json.toJson(t)))
    ItemUtils.toAttributeValues(item)
  }

  def describeTable: String = {
    clientHandler.awsClient.describeTable(clientHandler.tableName).toString
  }

  def scan: Future[ScanResult] = {
    val source = DynamoDb
      .source(new ScanRequest().withTableName(clientHandler.tableName))
      .withAttributes(DynamoAttributes.client(clientHandler.alpakkaClient))
    source.runWith(Sink.head)
  }

  def findItemByCompositeKey(partKey: String,
                             rangeKey: String): Future[Option[T]] = {
    val keyMap: java.util.Map[String, AttributeValue] =
      Map("partKey" -> new AttributeValue(partKey),
          "rangeKey" -> new AttributeValue(rangeKey))

    val getItemRequest = new GetItemRequest()
      .withTableName(clientHandler.tableName)
      .withKey(keyMap)
    val source =
      DynamoDb
        .source(
          new GetItemRequest()
            .withTableName(clientHandler.tableName)
            .withKey(keyMap))
        .withAttributes(DynamoAttributes.client(clientHandler.alpakkaClient))

    source.runWith(Sink.head) map { r =>
      if (Option(r.getItem).nonEmpty)
        Some(Json.parse(ItemUtils.toItem(r.getItem).toJSON).as[T])
      else
        None
    }
  }

  def findItemByRangeKey(rangeKey: String) = {}

  def put(t: T): Future[PutItemResult] = {
    val attributeMap = attributeValues(t)
    val source = DynamoDb
      .source(
        new PutItemRequest()
          .withTableName(clientHandler.tableName)
          .withItem(attributeMap))
      .withAttributes(DynamoAttributes.client(clientHandler.alpakkaClient))
    source.runWith(Sink.head)
  }
}
