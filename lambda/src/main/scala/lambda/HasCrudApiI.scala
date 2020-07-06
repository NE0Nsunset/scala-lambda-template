package lambda

import lambda.controller.AutowireServer
import lambda.models.DynamoItem
import lambda.serialization.DynamoItemBean
import lambda.service.DynamoService
import software.amazon.awssdk.enhanced.dynamodb.Key
import scala.compat.java8.FutureConverters
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait CrudApi[R <: DynamoItem, T <: DynamoItemBean[R]] {
  //type DynamoServiceType = DynamoService[R, T]

  // val service: DynamoServiceType
  def insert(t: R): Future[R]
  //  FutureConverters.toScala(service.table.putItem(t)).map(_ => t)
  //def read(key: Key): Future[T]
  //def update(t: T): Future[T]
  //def delete(t: T): Future[Unit]
}

// TODO add auth checks
trait HasCrudApi[R <: DynamoItem, T <: DynamoItemBean[R]]
    extends CrudApi[R, T] {

  type CrudApiType = CrudApi[R, T]
}
