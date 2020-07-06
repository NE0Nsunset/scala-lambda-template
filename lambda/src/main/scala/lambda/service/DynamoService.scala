package lambda.service

import lambda.BasicConsumer
import lambda.models.DynamoItem
import lambda.serialization.DynamoItemBean
import scala.concurrent.{ExecutionContext, Future}
import lambda.serialization.Serializer._
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.DynamoItemConverters._

/**
  * Basic DynamoDB interactions that all Dynamo Services
  * Should extend.
  */
trait DynamoService[R <: DynamoItem, T <: DynamoItemBean[R]] {
  val clientHandler: DynamoClientT
  val tableSchema: BeanTableSchema[T]

  lazy val table = clientHandler.enhancedAsyncClient
    .table[T](clientHandler.config.getString("dynamo.tableName"), tableSchema)

  def findItemByCompositeKey(partKey: String,
                             rangeKey: String): Future[Option[R]] = {
    val key: Key =
      Key.builder().partitionValue(partKey).sortValue(rangeKey).build()

    val filterRequest = table.getItem(key)

    FutureConverters
      .toScala(filterRequest).map(x => Some(x.toItem)).recover {
        case e: Exception => {
          println(e.getMessage)
          None
        }
      }
  }

  def put(t: T): Future[Unit] = {
    FutureConverters.toScala(table.putItem(t)).map(_ => ())
  }

  def putIfNotExists(t: T): Future[Unit] = {
    findItemByCompositeKey(t.partKey, t.rangeKey) flatMap { exists =>
      if (exists.isDefined) Future { None } else put(t).map(Some(_))
    }
  }

  def scan(limit: Int): Future[List[T]] = {
    val consumer = new BasicConsumer[T]

    FutureConverters
      .toScala(table.scan().items().limit(limit).subscribe(consumer)).map(_ =>
        consumer.getList)
  }
}
