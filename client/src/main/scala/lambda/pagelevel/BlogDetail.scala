package lambda.pagelevel

import java.time.LocalDate
import java.util.Date

import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import lambda.UsesAjaxClient
import lambda.api.BlogApi
import lambda.routing.UsesSimpleRouter
import autowire._
import lambda.serialization.Picklers._
import org.scalajs.dom.raw.Node
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import wvlet.airframe._

class BlogDetail
    extends PageComponent
    with UsesSimpleRouter
    with UsesAjaxClient {

  val year =
    simpleRouter.currentRouteProps.find(_._1 == "year").map(_._2.toInt).get
  val month =
    simpleRouter.currentRouteProps.find(_._1 == "month").map(_._2.toInt).get
  val date =
    simpleRouter.currentRouteProps.find(_._1 == "date").map(_._2.toInt).get
  val slug =
    simpleRouter.currentRouteProps.find(_._1 == "slug").map(_._2.toString).get

  val loadedBlog =
    FutureBinding {
      ajaxClient[BlogApi].findByDateAndSlug(year, month, date, slug).call()
    }

  @dom def render: Binding[Node] = {
    <div class="section no-pad-bot" id="index-banner">
      {simpleRouter.currentRouteProps.toString}
      {loadedBlog.bind.toString}
    </div>
  }
}

object BlogDetail {

  def props(date: scalajs.js.Date, slug: String): List[(String, String)] = {
    List("year" -> date.getFullYear().toString,
         "month" -> (date.getMonth + 1).toString,
         "date" -> (date.getDate + 1).toString,
         "slug" -> slug)
  }
}
