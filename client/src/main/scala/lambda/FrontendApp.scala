package lambda

import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import org.scalajs.dom.document

import scala.scalajs.js.annotation.JSExport
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import autowire._
import lambda.PageLevel.PageComponent
import lambda.Routing.{RouteName, SimpleRoute, SimpleRouter}
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
        <a href="#" class="brand-logo"><span class="grey-text text-lighten-2">Lambda Scala Project!</span></a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
          <li><a href="javascript:void(0);" onclick={_:Event => {router.changeRouteByName(RouteName.Home.entryName)}}>Getting Started</a></li>
          <li><a href="javascript:void(0);" onclick={_:Event => {router.changeRouteByName(RouteName.DynamoExample.entryName)}}>Dynamo Examples</a></li>
        </ul>
      </div>
    </nav>

  @dom def appLayout: Binding[BindingSeq[Node]] = {
    lazy val current: SimpleRoute[_ <: PageComponent] =
      router.currentRouteOpt.bind.getOrElse(router.routeList.head)
    Constants(
      navBar.bind,
      router.currentPageComponent.bind.render.bind
    )
  }

}
