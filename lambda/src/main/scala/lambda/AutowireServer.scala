package lambda

import lambda.api.{
  AnotherApiExample,
  AnotherApiExampleImpl,
  SharedApi,
  SharedApiImpl,
  SimpleApi,
  SimpleApiImpl
}

import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.Picklers._
import ujson.Value
import upickle.default._

object AutowireServer
    extends autowire.Server[Value,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  def write[Result: upickle.default.Writer](r: Result) =
    upickle.default.writeJs(r)

  def read[Result: upickle.default.Reader](p: Value) = {
    val r = upickle.default.read[Result](p)
    println(r)
    r
  }

  // Bind Api Contracts to their implementations here
  val routeList = List(
    AutowireServer.route[SimpleApi](SimpleApiImpl),
    AutowireServer.route[SharedApi](SharedApiImpl),
    AutowireServer.route[AnotherApiExample](AnotherApiExampleImpl)
  )
}
