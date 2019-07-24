package lambda

import java.nio.ByteBuffer
import autowire._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import serialization.Picklers._
import ujson.Js

/**
  * An autowire ajax client that maps API contracts from shared/api as method calls
  * e.g. Client[SharedApi].doThing(SharedClass("name here", "description here"))
  */
object Client
    extends autowire.Client[ujson.Value,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  // TODO setup config
  val backendUrl: String =
    "https://btn2p0qqs2.execute-api.us-west-1.amazonaws.com/a/api/"

  override def doCall(req: Request): Future[ujson.Value] = {
    Ajax
      .post(
        url = +req.path
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
