package lambda.service

import com.typesafe.config.Config
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeDefinition,
  CreateTableRequest,
  GlobalSecondaryIndex,
  KeySchemaElement,
  KeyType,
  Projection,
  ProjectionType,
  ProvisionedThroughput,
  ScalarAttributeType
}

import scala.concurrent.ExecutionContext.Implicits.global
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

import scala.compat.java8.FutureConverters

/**
  * A client for interacting with DynamoDB
  */
trait DynamoClientT {
  val config: Config
  val awsClient: DynamoDbAsyncClient
  val dynamoHost: String = config.getString("dynamo.host")
  val dynamoPort: String = config.getString("dynamo.port")
  val hostAndPort: String = s"http://$dynamoHost:$dynamoPort"
  val dynamoRegion: String = config.getString("dynamo.region")
  val tableName: String = config.getString("dynamo.tableName")
  val environment: String = config.getString("environment")
}

class DynamoClientImpl(val config: Config) extends DynamoClientT {
  val sdkHttpClient: SdkAsyncHttpClient =
    NettyNioAsyncHttpClient.builder().build()

  val awsClient: DynamoDbAsyncClient =
    DynamoDbAsyncClient
      .builder().httpClient(sdkHttpClient).build()

//  def listTables: Future[ListTablesResponse] = {
//    val listTablesRequest = new ListTablesRequest()
//    FutureConverters.toScala(awsClient.listTables(listTablesRequest))
//  }
}

object DynamoClientImpl {
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
