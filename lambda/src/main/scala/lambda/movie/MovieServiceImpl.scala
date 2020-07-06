package lambda.movie

import com.typesafe.config.Config
import lambda.BasicConsumer
import lambda.models.MovieItem
import lambda.serialization.DynamoItemConverters._
import lambda.service.{DynamoClientT, DynamoService}
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema

import scala.compat.java8.FutureConverters
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
    val basicConsumer: BasicConsumer[MovieItemBean] =
      new BasicConsumer[MovieItemBean]
    FutureConverters
      .toScala(table.scan().items().subscribe(basicConsumer)).map(_ =>
        basicConsumer.getList)
  }
}
