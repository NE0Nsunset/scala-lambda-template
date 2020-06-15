package lambda.service

import com.typesafe.config.Config
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

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

  lazy val enhancedAsyncClient: DynamoDbEnhancedAsyncClient =
    DynamoDbEnhancedAsyncClient
      .builder().dynamoDbClient(awsClient).build()
}

class DynamoClientImpl(val config: Config) extends DynamoClientT {
  val sdkHttpClient: SdkAsyncHttpClient =
    NettyNioAsyncHttpClient.builder().build()

  val awsClient: DynamoDbAsyncClient =
    DynamoDbAsyncClient
      .builder().httpClient(sdkHttpClient).build()
}
