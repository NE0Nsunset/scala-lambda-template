package lambda.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import com.typesafe.config.Config
import lambda.models.BlogItem
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeValue,
  QueryRequest
}
import lambda.serialization.Serializer._
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.compat.java8.FutureConverters
import scala.concurrent.Future

trait BlogService extends DynamoService[BlogItem] {

  val partitionKey = BlogItem.partitionKey

  def findByDateAndSlug(year: Int,
                        month: Int,
                        date: Int,
                        slug: String): Future[Option[BlogItem]]

  def getNBlogs(n: Int): Future[List[BlogItem]]
}

class BlogServiceImpl(config: Config, val clientHandler: DynamoClientT)
    extends BlogService {

  def findByDateAndSlug(year: Int,
                        month: Int,
                        date: Int,
                        slug: String): Future[Option[BlogItem]] = {
    val localDate = LocalDate.of(year, month, date)
    val attributeValues: java.util.Map[String, AttributeValue] = Map(
      ":partKeyVal" -> AttributeValue.builder
        .s(partitionKey).build(),
      ":rangeKeyVal" -> AttributeValue
        .builder().s(s"${localDate.format(ISO_LOCAL_DATE)}#$slug").build()
    ).asJava

    val queryRequest = QueryRequest
      .builder().tableName(clientHandler.tableName).expressionAttributeValues(
        attributeValues).keyConditionExpression(
        "partKey = :partKeyVal AND rangeKey = :rangeKeyVal").build()

    FutureConverters.toScala(clientHandler.awsClient.query(queryRequest)) map {
      qr =>
        qr.items().asScala.map(x => itemConvert(x.asScala.toMap)).toList.headOption
    }
  }

  def itemConvert(av: Map[String, AttributeValue]): BlogItem = {
    BlogItem.fromAttributeMap(av)
  }

  def getNBlogs(n: Int): Future[List[BlogItem]] = {
    val attributeValues: java.util.Map[String, AttributeValue] = Map(
      ":partKeyVal" -> AttributeValue.builder
        .s(partitionKey).build()
    ).asJava

    val queryRequest = QueryRequest
      .builder().tableName(clientHandler.tableName).expressionAttributeValues(
        attributeValues).keyConditionExpression("partKey = :partKeyVal").limit(
        n).scanIndexForward(true).build()

    FutureConverters.toScala(clientHandler.awsClient.query(queryRequest)) map {
      qr =>
        qr.items().asScala.map(x => itemConvert(x.asScala.toMap)).toList
    }
  }
}
