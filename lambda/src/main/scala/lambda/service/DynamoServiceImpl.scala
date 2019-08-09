package lambda.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.typesafe.config.ConfigFactory
import javax.inject.{Inject, Singleton}
import lambda.models.ExampleDynamoItem

import scala.concurrent.ExecutionContext

@Singleton
class DynamoServiceImpl @Inject()()(
    private implicit val actorSystem: ActorSystem)
    extends DynamoService {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContext =
    actorSystem.dispatcher

  lazy val c = ConfigFactory.load()
  val awsAccessKey = c.getString("dynamo.credentials.access-key-id")
  val awsSecretKey = c.getString("dynamo.credentials.secret-key-id")
  val dynamoHost = c.getString("dynamo.host")
  val dynamoPort = c.getString("dynamo.port")
  val hostAndPort = s"http://$dynamoHost:$dynamoPort"
  val dynamoRegion = c.getString("dynamo.region")
  val tableName: String = c.getString("dynamo.tableName")

  val prefixName: String = "exampleItem"

  lazy val awsCreds = new BasicAWSCredentials(awsAccessKey, awsSecretKey)
  val conf = new EndpointConfiguration(hostAndPort, dynamoRegion)

  val client = {
    AmazonDynamoDBClientBuilder
      .standard()
      .withEndpointConfiguration(conf)
      .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
      .build()
  }

  def describeTable: String = {
    client.describeTable(tableName).toString
  }
}
