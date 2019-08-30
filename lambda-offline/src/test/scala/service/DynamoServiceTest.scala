package service

import lambda.models.{DynamoItem, MovieItem}
import lambda.serialization.Serializer.{movieReads, movieWrites}
import lambda.service.{DynamoClientT, DynamoService}
import org.scalatest.BeforeAndAfterEach
import lambda.test.{TestBase, TestObjects}
import play.api.libs.json.{Json, Reads, Writes}
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.Await
import scala.concurrent.duration.Duration

//class DynamoServiceTest extends TestBase with BeforeAndAfterEach {
//  val dynamoService = new DynamoService[MovieItem] {
//    implicit val readsT: Reads[MovieItem] = Json.reads[MovieItem]
//    implicit val writesT: Writes[MovieItem] = Json.writes[MovieItem]
//
//    val clientHandler: DynamoClientT = dynamoClient
//  }
//
//  override def beforeEach(): Unit = {
//    super.beforeEach()
//    Await.result(dynamoClient.createTableIfNotExists(),
//                 Duration.create(60, "seconds"))
//  }
//
//  override def afterEach(): Unit = {
//    super.afterEach()
//    dynamoClient.destroyTable
//  }
//
//  it("Should connect to dynamo") {
//    FutureConverters.toScala(
//      dynamoClient.awsClient
//        .listTables(ListTablesRequest.builder().build)) map { f =>
//      println(f.toString)
//      assert(true)
//    }
//  }
//}
