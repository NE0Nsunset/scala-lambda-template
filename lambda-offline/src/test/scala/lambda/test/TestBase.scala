package lambda.test

import com.typesafe.config.ConfigFactory
import lambda.{AWSLogging, AutowireServer, LocalDynamoClient}
import lambda.api.{MovieApiWithDynamo, MovieApiWithDynamoImpl}
import lambda.models.MovieItem
import lambda.service.{
  DynamoClientImpl,
  DynamoClientT,
  DynamoService,
  MovieService,
  MovieServiceImpl
}
import org.scalatest.AsyncFunSpec
import scala.concurrent.ExecutionContext.Implicits.global

class TestBase extends AsyncFunSpec {
  val config = ConfigFactory.load("test")

  val tableName = config.getString("dynamo.tableName")
  val awsLogging = new AWSLogging {}
  lazy val dynamoClient: LocalDynamoClient =
    new LocalDynamoClient(config)
  lazy val movieService: MovieService =
    new MovieServiceImpl(config, dynamoClient)
  lazy val movieApiWithDynamo: MovieApiWithDynamo = new MovieApiWithDynamoImpl(
    movieService)
  lazy val autowireServer: AutowireServer =
    new AutowireServer(movieApiWithDynamo, awsLogging)

  // needed for the future flatMap/onComplete in the end
  //implicit val executionContext = actorSystem.dispatcher
  val movieDynamoService: MovieService = movieService

}
