package lambda.api

import upickle._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object SimpleApiImpl extends SimpleApi {
  def twoPlusN(n: Int): Future[(String, String)] = {
    Future { ((2 + n).toString, "yes") }
  }
}
