package lambda

import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExport
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import autowire._
import lambda.pagelevel.PageComponent
import lambda.routing.{RouteName, Routes, SimpleRoute, SimpleRouter}
import lambda.models.Movie
import lambda.serialization.Picklers._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.std.option._
import wvlet.airframe._

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
object FrontendApp extends js.JSApp {
  val design: Design =
    newDesign
      .bind[SimpleRouter].toEagerSingleton
      .bind[ClientConfig].toEagerSingleton
      .bind[AjaxClient].toSingleton
      .bind[Routes].toEagerSingleton

  val session = design.newSession
  val builtSession = session.build[FrontendApp]

  @JSExport
  def main(): Unit = {
    dom.render(document.getElementById("lambdaapp"), builtSession.appLayout)
  }
}

trait FrontendApp {
  private val session = bind[Session]
  lazy val router = bind[SimpleRouter]
  val clientConfig = bind[ClientConfig]
  lazy val ajaxClient = bind[AjaxClient]

  @dom def navBar: Binding[Node] =
    <nav class="blue-grey darken-2" data:role="navigation">
      <div class="nav-wrapper container">
        <a href="/" onclick={e:Event => {e.preventDefault(); router.changeToRouteByName(RouteName.Home.entryName)}} class="brand-logo"><span class="grey-text text-lighten-2">Lambda Scala Project!</span></a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
          <li><a href="/" onclick={e:Event => {e.preventDefault();router.changeToRouteByName(RouteName.Home.entryName)}}>Getting Started</a></li>
          <li><a href="/dynamo-examples" onclick={e:Event => {e.preventDefault();router.changeToRouteByName(RouteName.DynamoExample.entryName)}}>Dynamo Examples</a></li>
        </ul>
      </div>
    </nav>

  @dom def appLayout: Binding[BindingSeq[Node]] = {
    Constants(
      navBar.bind,
      router.currentPageComponentOrEmpty.bind.render.bind
    )
  }

  router.routeFromCurrentLocation
}
