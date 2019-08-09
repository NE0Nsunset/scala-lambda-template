package lambda.service

import akka.actor.ActorSystem
import net.codingwell.scalaguice.ScalaModule

class Module(actorSystem: ActorSystem) extends ScalaModule {
  override def configure() = {
    bind[ActorSystem].toInstance(actorSystem)
    bind[DynamoService].to[DynamoServiceImpl]
  }
}
