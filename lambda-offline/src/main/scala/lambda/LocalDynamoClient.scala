package lambda

import java.net.URI

import com.typesafe.config.Config
import lambda.service.{DynamoClientImpl, DynamoClientT}
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.{
  CreateTableResponse,
  DeleteTableRequest,
  DescribeTableRequest,
  ListTablesRequest,
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
    FutureConverters.toScala(
      awsClient.createTable(DynamoClientImpl.tableDefinition(tableName))) map {
      r =>
        println(r)
        r
    }
  }

  def createTableIfNotExists(): Future[Unit] = {

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

  createTableIfNotExists()
}
