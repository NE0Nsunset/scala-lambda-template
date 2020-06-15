package lambda.service

import com.typesafe.config.Config
import lambda.BasicConsumer
import lambda.models.{DynamoItem, MovieItem}
import lambda.serialization.{BlogItemBean, MovieItemBean}
import lambda.serialization.Serializer._
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import software.amazon.awssdk.services.dynamodb.model.{AttributeValue, QueryRequest}

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.DynamoItemConverters._
/**
  * An example DynamoService for storing movies
  */
trait MovieService extends DynamoService[MovieItem, MovieItemBean] {
  //def findMoviesByName(name: String): Future[List[MovieItem]]
  def findAllMovies(): Future[List[MovieItem]]
}

class MovieServiceImpl(config: Config, val clientHandler: DynamoClientT)
    extends MovieService {

  val tableSchema: BeanTableSchema[MovieItemBean] =
    TableSchema.fromBean(classOf[MovieItemBean])

  def findAllMovies(): Future[List[MovieItem]] = {
    val basicConsumer: BasicConsumer[MovieItemBean] = new BasicConsumer[MovieItemBean]
    FutureConverters.toScala(table.scan().items().subscribe(basicConsumer)).map(_ => basicConsumer.getList)
  }
}
