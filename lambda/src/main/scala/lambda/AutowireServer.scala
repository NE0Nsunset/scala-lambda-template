package lambda

import lambda.api.{SimpleApi, SimpleApiImpl}

import scala.concurrent.ExecutionContext.Implicits.global
import serialization.Picklers._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object AutowireServer
    extends autowire.Server[String,
                            upickle.default.Reader,
                            upickle.default.Writer] {
  def write[Result: upickle.default.Writer](r: Result) =
    upickle.default.write(r)

  def read[Result: upickle.default.Reader](p: String) =
    upickle.default.read[Result](p)
}
