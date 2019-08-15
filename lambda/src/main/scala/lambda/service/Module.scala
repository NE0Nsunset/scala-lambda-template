package lambda.service

import akka.actor.ActorSystem
import com.typesafe.config.Config
import lambda.AWSLogging
import lambda.api.{MovieApiWithDynamo, MovieApiWithDynamoImpl}
import net.codingwell.scalaguice.ScalaModule

class Module(actorSystem: ActorSystem, config: Config, awsLogging: AWSLogging)
    extends ScalaModule {

  override def configure() = {
    bind[AWSLogging].toInstance(awsLogging)
    bind[Config].toInstance(config)
    bind[ActorSystem].toInstance(actorSystem)
    bind[DynamoClientT].to[DynamoClientImpl]
    bind[MovieService].to[MovieServiceImpl]
    bind[MovieApiWithDynamo].to[MovieApiWithDynamoImpl]
  }
}
