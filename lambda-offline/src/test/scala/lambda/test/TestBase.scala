package lambda.test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import lambda.models.MovieItem
import lambda.service.{DynamoService, Module, MovieService, MovieServiceImpl}
import org.scalatest.AsyncFunSpec

class TestBase extends AsyncFunSpec {
  val config = ConfigFactory.load("test")

  val tableName = config.getString("dynamo.tableName")

  implicit val actorSystem = ActorSystem("my-system")
  val injector = Guice.createInjector(new Module(actorSystem, config))
  implicit val materializer = ActorMaterializer()

  // needed for the future flatMap/onComplete in the end
  //implicit val executionContext = actorSystem.dispatcher
  val movieDynamoService: MovieService =
    injector.getInstance(classOf[MovieServiceImpl])

}
