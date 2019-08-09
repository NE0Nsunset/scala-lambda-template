package lambda.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import lambda.models.ExampleDynamoItem
import scala.concurrent.ExecutionContext

@Singleton
class ExampleItemServiceImpl @Inject()(
    config: Config,
    val client: DynamoClientT)(private implicit val actorSystem: ActorSystem)
    extends DynamoService[ExampleDynamoItem] {
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext =
    actorSystem.dispatcher

  val prefixName: String = "exampleItem"

  def describeTable: String = {
    client.client.describeTable(client.tableName).toString
  }
}
