package lambda.blog

import com.typesafe.config.Config
import lambda.{CrudApi, Module}
import lambda.api.BlogApi
import lambda.controller.AutowireServer
import lambda.models.BlogItem
import lambda.service.DynamoClientT

import scala.compat.java8.FutureConverters
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import lambda.serialization.DynamoItemConverters._

trait BlogModule extends Module {
  import lambda.serialization.Picklers._
  def config: Config
  val dynamoClient: DynamoClientT
  val autowireServer: AutowireServer

  lazy val blogService = new BlogServiceImpl(config, dynamoClient)
  lazy val blogApi = new BlogApiImpl(blogService)

  trait BlogModuleCrudApi {
    def insert(t: BlogItem): Future[BlogItem]
  }

  lazy val crudApiImpl = new BlogModuleCrudApi {
    def insert(t: BlogItem): Future[BlogItem] =
      FutureConverters.toScala(blogService.table.putItem(t)).map(_ => t)
  }

  override def autowireRoutesDef: List[AutowireServer#Router] = {
    super.autowireRoutesDef ::: List(
      autowireServer.route[BlogApi](blogApi),
      autowireServer.route[BlogModuleCrudApi](crudApiImpl))
  }
}
