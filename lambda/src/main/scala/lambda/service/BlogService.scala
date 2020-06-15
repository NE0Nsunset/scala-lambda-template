package lambda.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import com.typesafe.config.Config
import lambda.BasicConsumer
import lambda.models.BlogItem
import lambda.serialization.BlogItemBean
import lambda.serialization.Serializer._
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import software.amazon.awssdk.enhanced.dynamodb.{Key, TableSchema}
import software.amazon.awssdk.enhanced.dynamodb.model.{
  QueryConditional,
  QueryEnhancedRequest
}
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.compat.java8.FutureConverters
import scala.concurrent.Future
import lambda.serialization.DynamoItemConverters._


trait BlogService extends DynamoService[BlogItem, BlogItemBean] {
  val partitionKey = BlogItem.partitionKey
  val tableSchema: BeanTableSchema[BlogItemBean] =
    TableSchema.fromBean(classOf[BlogItemBean])

  def findByDateAndSlug(year: Int,
                        month: Int,
                        date: Int,
                        slug: String): Future[Option[BlogItem]]

  def getNBlogs(n: Int,
                lastEvaluatedKey: Map[String, String] =
                Map.empty[String, String]): Future[List[BlogItem]]

}

class BlogServiceImpl(config: Config, val clientHandler: DynamoClientT)
  extends BlogService {

  def findByDateAndSlug(year: Int,
                        month: Int,
                        date: Int,
                        slug: String): Future[Option[BlogItem]] = {
    val localDate = LocalDate.of(year, month, date)

    findItemByCompositeKey(partKey = partitionKey,
      rangeKey =
        s"${localDate.format(ISO_LOCAL_DATE)}#$slug")
  }

  def getNBlogs(n: Int,
                lastEvaluatedKey: Map[String, String] =
                Map.empty[String, String]): Future[List[BlogItem]] = {

    val consumer: BasicConsumer[BlogItemBean] = new BasicConsumer[BlogItemBean]

    val queryEnhancedRequest =
      QueryEnhancedRequest
        .builder()
        .queryConditional(QueryConditional.keyEqualTo(
          Key.builder().partitionValue(partitionKey).build()))
        .limit(n)
        .build()

    val filterRequest =
      table.query(queryEnhancedRequest).items().subscribe(consumer)

    FutureConverters
      .toScala(filterRequest).recover({
      case e: Exception => {
        println(e.getMessage)
        Nil
      }
    }).map(_ => consumer.getList)

  }
}
