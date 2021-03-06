package lambda.pagelevel

import com.thoughtworks.binding.{Binding, FutureBinding}
import lambda.{IDEHelpers, UsesAjaxClient}
import lambda.api.BlogApi
import lambda.routing.UsesSimpleRouter
import autowire._
import lambda.serialization.Picklers._
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import wvlet.airframe._

class BlogDetail
    extends PageComponent
    with UsesSimpleRouter
    with UsesAjaxClient
    with IDEHelpers {

  val year =
    simpleRouter.findRouteValue("year").get.toInt
  val month =
    simpleRouter.findRouteValue("month").get.toInt
  val date =
    simpleRouter.findRouteValue("date").get.toInt
  val slug =
    simpleRouter.findRouteValue("slug").get

  val loadedBlog =
    FutureBinding {
      ajaxClient[BlogApi].findByDateAndSlug(year, month, date, slug).call()
    }

  @html def render: Binding[Node] = {
    <div class="section no-pad-bot" id="index-banner">
      {simpleRouter.currentRouteKeyValues.toString}
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
