package lambda

import java.nio.ByteBuffer

import autowire._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import serialization.Picklers._
import ujson.Js

object Client
    extends autowire.Client[ujson.Value,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  override def doCall(req: Request): Future[ujson.Value] = {
    Ajax
      .post(
        url = "https://btn2p0qqs2.execute-api.us-west-1.amazonaws.com/a/api/" + req.path
          .mkString("/"),
        data = upickle.default.write(req.args),
        headers = Map("Content-Type" -> "application/json")
      )
      .map(_.responseText)
  }

  def write[Result: upickle.default.Writer](r: Result) =
    upickle.default.write(r)
  def read[Result: upickle.default.Reader](p: ujson.Value) =
    upickle.default.read[Result](p)
}
