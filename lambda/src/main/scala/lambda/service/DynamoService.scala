package lambda.service

import lambda.models.DynamoItem
import play.api.libs.json.{Reads, Writes}

import scala.concurrent.{ExecutionContext, Future}
import lambda.serialization.Serializer._
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeValue,
  GetItemRequest,
  PutItemRequest,
  PutItemResponse,
  ScanRequest,
  ScanResponse
}
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import scala.compat.java8.FutureConverters
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Basic DynamoDB interactions that all Dynamo Services
  * Should extend.
  * @tparam T
  */
trait DynamoService[T <: DynamoItem] {
  val clientHandler: DynamoClientT

  def itemConvert(av: Map[String, AttributeValue]): T

  def scan: Future[ScanResponse] = {
    val scanRequest =
      ScanRequest.builder().tableName(clientHandler.tableName).build()
    FutureConverters.toScala(clientHandler.awsClient.scan(scanRequest))
  }

  def findItemByCompositeKey(partKey: String,
                             rangeKey: String): Future[Option[T]] = {
    val keyMap: java.util.Map[String, AttributeValue] =
      Map("partKey" -> AttributeValue.builder.s(partKey).build(),
          "rangeKey" -> AttributeValue.builder.s(rangeKey).build())

    val getItemRequest = GetItemRequest
      .builder().tableName(clientHandler.tableName).key(keyMap).build()
    FutureConverters.toScala(clientHandler.awsClient.getItem(getItemRequest)) map {
      gir =>
        if (gir.item().nonEmpty)
          Some(itemConvert(gir.item().asScala.toMap))
        else
          None
    }
  }

  def put(t: T): Future[PutItemResponse] = {
    val putItemRequest =
      PutItemRequest
        .builder().tableName(clientHandler.tableName).item(t.itemToAttributeMap).build()
    FutureConverters.toScala(clientHandler.awsClient.putItem(putItemRequest))
  }

  def putIfNotExists(t: T): Future[Option[PutItemResponse]] = {
    findItemByCompositeKey(t.partKey, t.rangeKey) flatMap { exists =>
      if (exists.isDefined) Future { None } else put(t).map(Some(_))
    }
  }
}
