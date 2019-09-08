package lambda.service

import com.typesafe.config.Config
import lambda.models.{DynamoItem, MovieItem}
import lambda.serialization.Serializer._
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeValue,
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
