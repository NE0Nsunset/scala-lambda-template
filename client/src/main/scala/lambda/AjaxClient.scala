package lambda

import autowire._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import ujson.Value
import upickle._

/**
  * An autowire ajax client that maps API contracts from shared/api as method calls
  * e.g. Client[SharedApi].doThing("name here", "description here")
  */
object AjaxClient
    extends autowire.Client[ujson.Value,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  val backendUrl: String = FrontendApp.clientConfig.backendApiUrl

  override def doCall(req: Request): Future[ujson.Value] = {
    Ajax
      .post(
        url = backendUrl + req.path
          .mkString("/"),
        data = upickle.default.writeJs(req.args).toString(),
        headers = Map("Content-Type" -> "application/json")
      )
      .map(x => ujson.read(x.responseText))
  }

  def write[Result: upickle.default.Writer](r: Result) =
    upickle.default.writeJs(r)
  def read[Result: upickle.default.Reader](p: Value) = {
    upickle.default.read[Result](p)
  }
}
