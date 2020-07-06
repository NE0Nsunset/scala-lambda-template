package lambda.test

import com.typesafe.config.ConfigFactory
import lambda.{AWSLogging, LocalDynamoClient}
import lambda.api.MovieApiWithDynamo
import lambda.controller.AutowireServer
import lambda.movie.{MovieApiWithDynamoImpl, MovieService, MovieServiceImpl}
import org.scalatest.AsyncTestSuite

import scala.concurrent.ExecutionContext.Implicits.global

class TestBase extends AsyncTestSuite {
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
    new AutowireServer(awsLogging)

  val movieDynamoService: MovieService = movieService

}
