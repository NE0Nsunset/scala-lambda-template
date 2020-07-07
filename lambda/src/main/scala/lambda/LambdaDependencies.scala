package lambda

import com.typesafe.config.ConfigFactory
import lambda.api.{AnotherApiExample, AnotherApiExampleImpl, BlogApi, SharedApi, SharedApiImpl}
import lambda.blog.BlogModule
import lambda.controller.{AutowireController, AutowireServer}
import lambda.movie.MovieModule
import lambda.service.DynamoClientT

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

trait Module {
  def autowireRoutesDef: List[AutowireServer#Router] = Nil
}

/**
  * This trait represents the basic dependencies that Lambda handler
  * needs
  */
trait LambdaDependencies extends Module with BlogModule with MovieModule {
  import lambda.serialization.Picklers._

  // Implement depending on local webserver or AWS
  val dynamoClient: DynamoClientT

  val config = ConfigFactory.load()

  lazy val awsLoggingImpl: AWSLogging = new AWSLogging {}

  lazy val autowireServer: AutowireServer =
    new AutowireServer(awsLoggingImpl)

  override def autowireRoutesDef: List[AutowireServer#Router] = super.autowireRoutesDef :::
    List(
      autowireServer.route[SharedApi](SharedApiImpl),
      autowireServer.route[AnotherApiExample](AnotherApiExampleImpl),
    )

  lazy val autowireController: AutowireController =
    new AutowireController(awsLoggingImpl, autowireRoutesDef)
}
