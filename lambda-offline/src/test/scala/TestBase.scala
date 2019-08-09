import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import lambda.models.ExampleDynamoItem
import lambda.service.{DynamoService, ExampleItemServiceImpl, Module}
import org.scalatest.{AsyncFunSpec, FunSuite}

class TestBase extends AsyncFunSpec {
  val config = ConfigFactory.load("test")

  val tableName = config.getString("dynamo.tableName")

  implicit val actorSystem = ActorSystem("my-system")
  val injector = Guice.createInjector(new Module(actorSystem, config))
  implicit val materializer = ActorMaterializer()

  // needed for the future flatMap/onComplete in the end
  //implicit val executionContext = actorSystem.dispatcher
  val exampleDynamoService: DynamoService[ExampleDynamoItem] =
    injector.getInstance(classOf[ExampleItemServiceImpl])

}
