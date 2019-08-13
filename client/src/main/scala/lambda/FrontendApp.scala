package lambda

import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExport
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import autowire._
import lambda.models.Movie
import lambda.serialization.Picklers._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.std.option._

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
object FrontendApp extends js.JSApp {
  lazy val router = SimpleRouter
  val simpleApiFuture: Var[Option[FutureBinding[SharedClass]]] = Var(None)
  val movieApiExample: Var[Option[FutureBinding[Option[Movie]]]] = Var(None)
  val clientConfig = new ClientConfig

  @dom def navBar: Binding[Node] =
    <nav class="blue-grey darken-2" data:role="navigation">
      <div class="nav-wrapper container">
        <a href="#" class="brand-logo"><span class="grey-text text-lighten-2">Lambda Scala Project!</span></a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
          <li><a href="javascript:void(0);" onclick={_:Event => router.changeRoute(router.routes.home)}>Getting Started</a></li>
          <li><a href="javascript:void(0);" onclick={_:Event => router.changeRoute(router.routes.dynamoExamples)}>Dynamo Examples</a></li>
        </ul>
      </div>
    </nav>

  @dom def div: Binding[Node] = <div><h1>HELLO</h1></div>

  @dom def appLayout: Binding[BindingSeq[Node]] = {
    Constants(navBar.bind, router.currentRoute.bind.render.bind)
  }

  @JSExport
  def main(): Unit = {
    dom.render(document.getElementById("lambdaapp"), appLayout)
  }
}
