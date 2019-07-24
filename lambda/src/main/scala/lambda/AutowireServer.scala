package lambda

import lambda.api.{SharedApi, SharedApiImpl, SimpleApi, SimpleApiImpl}
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.Picklers._

object AutowireServer
    extends autowire.Server[String,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  def write[Result: upickle.default.Writer](r: Result) =
    upickle.default.write(r)

  def read[Result: upickle.default.Reader](p: String) =
    upickle.default.read[Result](p)

  // Bind Api Contracts to their implementations here
  val routeList = List(
    AutowireServer.route[SimpleApi](SimpleApiImpl),
    AutowireServer.route[SharedApi](SharedApiImpl)
  )
}
