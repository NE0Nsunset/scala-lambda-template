package lambda.controller

import lambda.AWSLogging
import ujson.Value
import scala.concurrent.ExecutionContext.Implicits.global
import ujson.Value
import upickle.default._
import scala.concurrent.Future

class AutowireServer(awsLogging: AWSLogging)
    extends autowire.Server[Value,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  def write[Result: upickle.default.Writer](r: Result) =
    upickle.default.writeJs(r)

  def read[Result: upickle.default.Reader](p: Value) = {
    val r = upickle.default.read[Result](p)
    r
  }
}
