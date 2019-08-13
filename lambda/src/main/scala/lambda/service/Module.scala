package lambda.service

import akka.actor.ActorSystem
import com.typesafe.config.Config
import lambda.api.{MovieApiWithDynamo, MovieApiWithDynamoImpl}
import lambda.models.{ExampleDynamoItem, MovieItem}
import net.codingwell.scalaguice.ScalaModule

class Module(actorSystem: ActorSystem, config: Config) extends ScalaModule {

  override def configure() = {
    bind[Config].toInstance(config)
    bind[ActorSystem].toInstance(actorSystem)
    bind[DynamoClientT].to[DynamoClientImpl]
    bind[MovieService].to[MovieServiceImpl]
    bind[MovieApiWithDynamo].to[MovieApiWithDynamoImpl]
  }
}
