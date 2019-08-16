package lambda.test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import lambda.{AWSLogging, AutowireServer}
import lambda.LambdaHandler.{actorSystem, awsLogging, config}
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

class TestBase extends AsyncFunSpec {
  val config = ConfigFactory.load("test")

  val tableName = config.getString("dynamo.tableName")
  val awsLogging = new AWSLogging {}
  implicit val actorSystem = ActorSystem("my-system")
  lazy val dynamoClient: DynamoClientT =
    new DynamoClientImpl(config)(actorSystem)
  lazy val movieService: MovieService =
    new MovieServiceImpl(config, dynamoClient)
  lazy val movieApiWithDynamo: MovieApiWithDynamo = new MovieApiWithDynamoImpl(
    movieService)
  lazy val autowireServer: AutowireServer =
    new AutowireServer(movieApiWithDynamo, awsLogging)
  implicit val materializer = ActorMaterializer()

  // needed for the future flatMap/onComplete in the end
  //implicit val executionContext = actorSystem.dispatcher
  val movieDynamoService: MovieService = movieService

}
