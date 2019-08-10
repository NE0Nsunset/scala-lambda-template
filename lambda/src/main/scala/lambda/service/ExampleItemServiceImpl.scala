package lambda.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import lambda.models.ExampleDynamoItem
import play.api.libs.json.{JsValue, Json}
import lambda.serialization.Serializer._

import scala.concurrent.ExecutionContext

@Singleton
class ExampleItemServiceImpl @Inject()(config: Config,
                                       val clientHandler: DynamoClientT)(
    private implicit val actorSystem: ActorSystem)
    extends DynamoService[ExampleDynamoItem] {
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext =
    actorSystem.dispatcher
  implicit val readsT = lambda.serialization.Serializer.edynamoImplicitReads
  implicit val writesT = lambda.serialization.Serializer.edynamoImplicitWrites

  val prefixName: String = "exampleItem"
}
