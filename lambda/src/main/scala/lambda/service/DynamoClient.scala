package lambda.service

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.dynamodb.{
  DynamoAttributes,
  DynamoClient,
  DynamoSettings
}
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.model.{
  AttributeDefinition,
  CreateTableRequest,
  GlobalSecondaryIndex,
  KeySchemaElement,
  KeyType,
  ListTablesRequest,
  ListTablesResult,
  Projection,
  ProvisionedThroughput,
  TableDescription
}
import com.amazonaws.services.dynamodbv2.{
  AmazonDynamoDB,
  AmazonDynamoDBAsync,
  AmazonDynamoDBAsyncClientBuilder
}
import akka.stream.alpakka.dynamodb.AwsOp._
import akka.stream.alpakka.dynamodb.scaladsl._
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

trait DynamoClientT {
  val awsClient: AmazonDynamoDBAsync
  val alpakkaClient: DynamoClient
  val tableName: String

  def createTableIfNotExists: Unit
  def createTable: TableDescription
  def destroyTable: Boolean
  def listTables: Future[ListTablesResult]
}

@Singleton
class DynamoClientImpl @Inject()(config: Config)(
    private implicit val actorSystem: ActorSystem)
    extends DynamoClientT {

  val awsAccessKey: String =
    config.getString("dynamo.credentials.access-key-id")
  val awsSecretKey: String =
    config.getString("dynamo.credentials.secret-key-id")
  val dynamoHost: String = config.getString("dynamo.host")
  val dynamoPort: String = config.getString("dynamo.port")
  val hostAndPort: String = s"http://$dynamoHost:$dynamoPort"
  val dynamoRegion: String = config.getString("dynamo.region")
  val tableName: String = config.getString("dynamo.tableName")
  val environment: String = config.getString("environment")

  println(config.getString("environment"))

  lazy val awsCreds = new BasicAWSCredentials(awsAccessKey, awsSecretKey)
  val conf = new EndpointConfiguration(hostAndPort, dynamoRegion)

  implicit val materializer = ActorMaterializer()

  val awsClient: AmazonDynamoDBAsync = {
    if (environment == "aws")
      AmazonDynamoDBAsyncClientBuilder
        .defaultClient()
    else
      AmazonDynamoDBAsyncClientBuilder
        .standard()
        .withEndpointConfiguration(conf)
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build()
  }

  val settings = DynamoSettings(actorSystem)
    .withTls(false)
    .withRegion(dynamoRegion)
    .withHost(dynamoHost)
    .withPort(dynamoPort.toInt)
    .withCredentialsProvider(new AWSStaticCredentialsProvider(awsCreds))
  val alpakkaClient = {
    if (environment == "aws")
      DynamoClient(DynamoSettings(actorSystem))
    else
      DynamoClient(settings)
  }

  def createTableIfNotExists(): Unit = {
    try {
      awsClient.describeTable(tableName)
    } catch {
      case e: Exception => {
        createTable
      }
    }
  }

  def createTable: TableDescription = {
    awsClient
      .createTable(DynamoClientImpl.tableDefinition(tableName))
      .getTableDescription
  }

  def destroyTable: Boolean = {
    awsClient.deleteTable(tableName).toString.nonEmpty
  }

  def listTables = {
    val source: Source[ListTablesResult, NotUsed] =
      DynamoDb
        .source(new ListTablesRequest())
        .withAttributes(DynamoAttributes.client(alpakkaClient))
    source.runWith(Sink.head)
  }

  createTableIfNotExists()
}

object DynamoClientImpl {
  def tableDefinition(tableName: String): CreateTableRequest = {
    import com.amazonaws.auth.BasicAWSCredentials
    val partKeyAttribute: AttributeDefinition =
      new AttributeDefinition("partKey", "S")
    val rangeKeyAttribute: AttributeDefinition =
      new AttributeDefinition("rangeKey", "S")
    val lastUpdateDate: AttributeDefinition =
      new AttributeDefinition("lastUpdate", "S")

    val partKeySchemaElement = new KeySchemaElement()
      .withAttributeName(partKeyAttribute.getAttributeName)
      .withKeyType(KeyType.HASH)

    val rangeKeySchemaElement = new KeySchemaElement()
      .withAttributeName(rangeKeyAttribute.getAttributeName)
      .withKeyType(KeyType.RANGE)

    val secondaryIndexKeySchema =
      new KeySchemaElement()
        .withAttributeName(rangeKeyAttribute.getAttributeName)
        .withKeyType(KeyType.HASH)

    val secondaryIndexProjection = new Projection().withProjectionType("ALL")
    val provisionedThroughput = new ProvisionedThroughput()
      .withReadCapacityUnits(5l)
      .withWriteCapacityUnits(5l)

    val globalSecondaryIndex =
      new GlobalSecondaryIndex()
        .withIndexName("rangeIndex")
        .withKeySchema(secondaryIndexKeySchema)
        .withProjection(secondaryIndexProjection)
        .withProvisionedThroughput(provisionedThroughput)
    new CreateTableRequest()
      .withTableName(tableName)
      .withAttributeDefinitions(partKeyAttribute, rangeKeyAttribute)
      .withGlobalSecondaryIndexes(globalSecondaryIndex)
      .withProvisionedThroughput(provisionedThroughput)
      .withKeySchema(partKeySchemaElement, rangeKeySchemaElement)
  }
}
