package lambda

import com.typesafe.config.ConfigFactory
import lambda.api.{
  AnotherApiExample,
  AnotherApiExampleImpl,
  MovieApiWithDynamo,
  MovieApiWithDynamoImpl,
  SharedApi,
  SharedApiImpl
}
import lambda.controller.{AutowireController, AutowireServer}
import lambda.service.{DynamoClientT, MovieService, MovieServiceImpl}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This trait represents the basic dependencies that Lambda handler
  * needs
  */
trait LambdaDependencies {
  import lambda.serialization.Picklers._

  // Implement depending on local webserver or AWS
  val dynamoClient: DynamoClientT

  val config = ConfigFactory.load("local")

  lazy val awsLoggingImpl: AWSLogging = new AWSLogging {}

  lazy val movieService: MovieService =
    new MovieServiceImpl(config, dynamoClient)

  lazy val movieApiWithDynamo: MovieApiWithDynamo =
    new MovieApiWithDynamoImpl(movieService)

  lazy val autowireServer: AutowireServer =
    new AutowireServer(awsLoggingImpl)

  lazy val autowireController: AutowireController =
    new AutowireController(awsLoggingImpl, autowireRoutes)

  /**
    * Bind API contracts to their implementations here for Autowire
    */
  val autowireRoutes = List(
    autowireServer.route[SharedApi](SharedApiImpl),
    autowireServer.route[AnotherApiExample](AnotherApiExampleImpl),
    autowireServer.route[MovieApiWithDynamo](movieApiWithDynamo)
  )
}
