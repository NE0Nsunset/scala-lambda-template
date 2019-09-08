package lambda

import java.net.URI

import com.typesafe.config.Config
import lambda.seed.SeedObjects
import lambda.service.DynamoClientT
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeDefinition,
  CreateTableRequest,
  CreateTableResponse,
  DeleteTableRequest,
  DescribeTableRequest,
  GlobalSecondaryIndex,
  KeySchemaElement,
  KeyType,
  ListTablesRequest,
  Projection,
  ProjectionType,
  ProvisionedThroughput,
  ScalarAttributeType
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.compat.java8.FutureConverters
import scala.concurrent.Future

/**
  * Local Dynamo Client with Database level operations
  * @param config
  */
class LocalDynamoClient(val config: Config) extends DynamoClientT {

  val awsClient: DynamoDbAsyncClient = {
    val endpointUri = "http://localhost:8000"

    DynamoDbAsyncClient
      .builder()
      .region(Region.US_WEST_2)
      .endpointOverride(URI.create(endpointUri)).build()
  }

  def createTable: Future[CreateTableResponse] = {
    println("creating table")
    FutureConverters.toScala(
      awsClient.createTable(LocalDynamoClient.tableDefinition(tableName))) map {
      r =>
        println(r)
        r
    }
  }

  def createTableIfNotExists(): Future[Unit] = {
    println(s"checking for $tableName")
    val describeFuture = FutureConverters.toScala(
      awsClient.describeTable(
        DescribeTableRequest.builder().tableName(tableName).build()))

    describeFuture
      .map(_ => {})
      .recover({
        case x: Throwable => {
          createTable.map(_ => {}).recover({ case x: Throwable => println(x) })
        }
      })
  }

  def destroyTable: Future[Boolean] = {
    FutureConverters.toScala(
      awsClient.deleteTable(
        DeleteTableRequest.builder().tableName(tableName).build())) map { ftr =>
      true
    }
  }

  def listTablesFuture[ListTablesResponse] = {
    FutureConverters.toScala(
      awsClient.listTables(ListTablesRequest.builder().build()))
  }
}
object LocalDynamoClient {
  def tableDefinition(tableName: String): CreateTableRequest = {
    val partKeyElement = KeySchemaElement
      .builder().attributeName("partKey").keyType(KeyType.HASH).build()
    val rangeKeyElement = KeySchemaElement
      .builder().attributeName("rangeKey").keyType(KeyType.RANGE).build()

    CreateTableRequest
      .builder()
      .attributeDefinitions(
        AttributeDefinition
          .builder()
          .attributeName(partKeyElement.attributeName())
          .attributeType(ScalarAttributeType.S)
          .build(),
        AttributeDefinition
          .builder()
          .attributeName(rangeKeyElement.attributeName())
          .attributeType(ScalarAttributeType.S)
          .build(),
      )
      .keySchema(
        partKeyElement,
        rangeKeyElement
      )
      .globalSecondaryIndexes(
        GlobalSecondaryIndex
          .builder().keySchema(KeySchemaElement
            .builder().attributeName(rangeKeyElement.attributeName()).keyType(
              KeyType.HASH).build()).indexName("rangeIndex")
          .projection(
            Projection.builder().projectionType(ProjectionType.ALL).build())
          .provisionedThroughput(
            ProvisionedThroughput
              .builder().readCapacityUnits(5L).writeCapacityUnits(5L).build()
          )
          .build()
      )
      .tableName(tableName)
      .provisionedThroughput(ProvisionedThroughput
        .builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
      .build()
  }
}
