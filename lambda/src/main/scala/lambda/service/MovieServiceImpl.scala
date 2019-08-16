package lambda.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.dynamodb.DynamoAttributes
import akka.stream.alpakka.dynamodb.scaladsl.DynamoDb
import akka.stream.scaladsl.Sink
import com.amazonaws.services.dynamodbv2.document.{ItemUtils, KeyAttribute}
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.model.{
  AttributeValue,
  GetItemRequest,
  QueryRequest
}
import com.typesafe.config.Config
import lambda.models.{ExampleDynamoItem, MovieItem}
import play.api.libs.json.{JsValue, Json}
import lambda.serialization.Serializer._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  * An example DynamoService for storing movies
  */
trait MovieService extends DynamoService[MovieItem] {
  def findMoviesByName(name: String): Future[List[MovieItem]]
  def findAllMovies(): Future[List[MovieItem]]
}

class MovieServiceImpl(config: Config, val clientHandler: DynamoClientT)(
    private implicit val actorSystem: ActorSystem)
    extends MovieService {
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext =
    actorSystem.dispatcher
  implicit val readsT = movieReads
  implicit val writesT = movieWrites

  // TODO cleanup query generation and execution
  override def findMoviesByName(name: String): Future[List[MovieItem]] = {
    val attributeValues = Map(
      ":partKeyVal" -> new AttributeValue(MovieItem.defaultPartKey),
      ":nameVal" -> new AttributeValue(name)).asJava
    val expressionAttributeNames = Map("#name" -> "title").asJava

    val queryRequest = new QueryRequest(clientHandler.tableName)
      .withExpressionAttributeValues(attributeValues)
      .withExpressionAttributeNames(expressionAttributeNames)
      .withKeyConditionExpression("partKey = :partKeyVal")
      .withFilterExpression("contains(#name, :nameVal)")

    val source = DynamoDb
      .source(queryRequest)
      .withAttributes(DynamoAttributes.client(clientHandler.alpakkaClient))

    source.runWith(Sink.head) map { result =>
      {
        result.getItems.asScala
          .map(i => Json.parse(ItemUtils.toItem(i).toJSON).as[MovieItem])
          .toList
      }
    }
  }

  override def findAllMovies(): Future[List[MovieItem]] = {
    val attributeValues = Map(
      ":partKeyVal" -> new AttributeValue(MovieItem.defaultPartKey)).asJava

    val queryRequest = new QueryRequest(clientHandler.tableName)
      .withExpressionAttributeValues(attributeValues)
      .withKeyConditionExpression("partKey = :partKeyVal")

    val source = DynamoDb
      .source(queryRequest)
      .withAttributes(DynamoAttributes.client(clientHandler.alpakkaClient))

    source.runWith(Sink.head) map { result =>
      {
        result.getItems.asScala
          .map(i => Json.parse(ItemUtils.toItem(i).toJSON).as[MovieItem])
          .toList
      }
    }
  }
}
