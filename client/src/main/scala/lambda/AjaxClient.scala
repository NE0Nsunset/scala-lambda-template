package lambda

import autowire._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import ujson.Js
import upickle._

/**
  * An autowire ajax client that maps API contracts from shared/api as method calls
  * e.g. Client[SharedApi].doThing("name here", "description here")
  */
object Client
    extends autowire.Client[ujson.Value,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  // TODO setup config
  val backendUrl: String =
    "http://localhost:8080/api/"

  override def doCall(req: Request): Future[ujson.Value] = {
    Ajax
      .post(
        url = backendUrl + req.path
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
