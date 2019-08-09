package lambda.service

import akka.actor.ActorSystem
import com.typesafe.config.Config
import lambda.models.ExampleDynamoItem
import net.codingwell.scalaguice.ScalaModule

class Module(actorSystem: ActorSystem, config: Config) extends ScalaModule {
  override def configure() = {
    bind[Config].toInstance(config)
    bind[ActorSystem].toInstance(actorSystem)
    bind[DynamoClientT].to[DynamoClientImpl]
    bind[DynamoService[ExampleDynamoItem]].to[ExampleItemServiceImpl]
  }
}
