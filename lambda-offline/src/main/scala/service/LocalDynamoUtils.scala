package service

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import com.typesafe.config.ConfigFactory

object LocalDynamoUtil extends App {
  lazy val c = ConfigFactory.load()

  lazy val tableName: String = c.getString("dynamo.tableName")

  implicit val system = ActorSystem()

  implicit val materializer: Materializer = ActorMaterializer()

//  //val settings = DynamoSettings(system)
//  lazy val awsCreds = new BasicAWSCredentials("access_key_id", "secret_key_id")
//
//  lazy val client: AmazonDynamoDB = {
//    val conf = new EndpointConfiguration("http://localhost:8000", "us-east-1")
//    AmazonDynamoDBClientBuilder
//      .standard()
//      .withEndpointConfiguration(conf)
//      .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//      .build()
//  }
//
//  override def main(args: Array[String]): Unit = {
//    println(getOrCreateTable(tableName).toString)
//  }
//
//  def getOrCreateTable(tableName: String): TableDescription = {
//    try {
//      println(tableName)
//      client.describeTable(tableName).getTable
//    } catch {
//      case e: ResourceNotFoundException => {
//        println(e)
//        println(s"Creating table $tableName")
//        createTable(tableName)
//      }
//    }
//  }
//  def getTable(tableName: String): String = {
//    client.describeTable(tableName).toString
//  }
//
//  def destroyTable(tableName: String): Boolean = {
//    client.deleteTable(tableName).toString.nonEmpty
//  }
//
//  def createTable(tableName: String): TableDescription = {
//    import com.amazonaws.auth.BasicAWSCredentials
//    val partKeyAttribute: AttributeDefinition =
//      new AttributeDefinition("partKey", "S")
//    val rangeKeyAttribute: AttributeDefinition =
//      new AttributeDefinition("rangeKey", "S")
//    val lastUpdateDate: AttributeDefinition =
//      new AttributeDefinition("lastUpdate", "S")
//
//    val partKeySchemaElement = new KeySchemaElement()
//      .withAttributeName(partKeyAttribute.getAttributeName)
//      .withKeyType(KeyType.HASH)
//
//    val rangeKeySchemaElement = new KeySchemaElement()
//      .withAttributeName(rangeKeyAttribute.getAttributeName)
//      .withKeyType(KeyType.RANGE)
//
//    val secondaryIndexKeySchema =
//      new KeySchemaElement()
//        .withAttributeName(rangeKeyAttribute.getAttributeName)
//        .withKeyType(KeyType.HASH)
//
//    val secondaryIndexProjection = new Projection().withProjectionType("ALL")
//    val provisionedThroughput = new ProvisionedThroughput()
//      .withReadCapacityUnits(5l)
//      .withWriteCapacityUnits(5l)
//
//    val globalSecondaryIndex =
//      new GlobalSecondaryIndex()
//        .withIndexName("rangeIndex")
//        .withKeySchema(secondaryIndexKeySchema)
//        .withProjection(secondaryIndexProjection)
//        .withProvisionedThroughput(provisionedThroughput)
//    val createTableRequest = new CreateTableRequest()
//      .withTableName(tableName)
//      .withAttributeDefinitions(partKeyAttribute, rangeKeyAttribute)
//      .withGlobalSecondaryIndexes(globalSecondaryIndex)
//      .withProvisionedThroughput(provisionedThroughput)
//      .withKeySchema(partKeySchemaElement, rangeKeySchemaElement)
//    println(createTableRequest.toString)
//
//    val createResult = client.createTable(createTableRequest)
//
//    createResult.getTableDescription
//  }
}
