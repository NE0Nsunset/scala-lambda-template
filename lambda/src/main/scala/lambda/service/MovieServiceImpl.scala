package lambda.service

import com.typesafe.config.Config
import lambda.models.{DynamoItem, MovieItem}
import play.api.libs.json.{JsValue, Json}
import lambda.serialization.Serializer._
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeValue,
  PutItemRequest,
  PutItemResponse,
  QueryRequest
}

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * An example DynamoService for storing movies
  */
trait MovieService extends DynamoService[MovieItem] {
  //def findMoviesByName(name: String): Future[List[MovieItem]]
  def findAllMovies(): Future[List[MovieItem]]
}

class MovieServiceImpl(config: Config, val clientHandler: DynamoClientT)
    extends MovieService {
  implicit val readsT = movieReads
  implicit val writesT = movieWrites

//  // TODO cleanup query generation and execution
//  override def findMoviesByName(name: String): Future[List[MovieItem]] = {
//    val attributeValues = Map(
//      ":partKeyVal" -> new AttributeValue(MovieItem.defaultPartKey),
//      ":nameVal" -> new AttributeValue(name)).asJava
//    val expressionAttributeNames = Map("#name" -> "title").asJava
//
//    val queryRequest = new QueryRequest(clientHandler.tableName)
//      .withExpressionAttributeValues(attributeValues)
//      .withExpressionAttributeNames(expressionAttributeNames)
//      .withKeyConditionExpression("partKey = :partKeyVal")
//      .withFilterExpression("contains(#name, :nameVal)")
//
//    clientHandler.awsClient.queryAsync(queryRequest)
//    val source = DynamoDb
//      .source(queryRequest)
//      .withAttributes(DynamoAttributes.client(clientHandler.alpakkaClient))
//
//    source.runWith(Sink.head) map { result =>
//      {
//        result.getItems.asScala
//          .map(i => Json.parse(ItemUtils.toItem(i).toJSON).as[MovieItem])
//          .toList
//      }
//    }
//  }
//
  def findAllMovies(): Future[List[MovieItem]] = {

    val attributeValues = Map(
      ":partKeyVal" -> AttributeValue.builder
        .s(MovieItem.defaultPartKey).build()).asJava

    val queryRequest = QueryRequest
      .builder().tableName(clientHandler.tableName).expressionAttributeValues(
        attributeValues).keyConditionExpression("partKey = :partKeyVal").build()

    FutureConverters.toScala(clientHandler.awsClient.query(queryRequest)) map {
      qr =>
        qr.items().asScala.map(x => itemConvert(x.asScala.toMap)).toList
    }
  }

  def itemConvert(av: Map[String, AttributeValue]): MovieItem = {
    MovieItem.fromAttributeMap(av)
  }
}
