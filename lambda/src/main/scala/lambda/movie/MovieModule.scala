package lambda.movie

import com.typesafe.config.Config
import lambda.Module
import lambda.api.MovieApiWithDynamo
import lambda.controller.AutowireServer
import lambda.service.DynamoClientT

import scala.concurrent.ExecutionContext.Implicits.global

trait MovieModule extends Module {
  import lambda.serialization.Picklers._

  def config: Config
  val dynamoClient: DynamoClientT
  val autowireServer: AutowireServer

  lazy val movieService: MovieService =
    new MovieServiceImpl(config, dynamoClient)

  lazy val movieApiWithDynamo: MovieApiWithDynamo =
    new MovieApiWithDynamoImpl(movieService)

  override def autowireRoutesDef: List[AutowireServer#Router] = {
    super.autowireRoutesDef ::: List(
      autowireServer.route[MovieApiWithDynamo](movieApiWithDynamo))
  }
}
