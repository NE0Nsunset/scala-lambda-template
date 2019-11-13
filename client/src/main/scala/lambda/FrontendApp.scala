package lambda

import wvlet.airframe._
import org.scalajs.dom.document

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.concurrent.ExecutionContext.Implicits.global
import autowire._
import lambda.routing.{RouteName, Routes, SimpleRouter}
import lambda.serialization.Picklers._
import scalaz.std.option._
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{BindingSeq, Constants}
import org.lrng.binding.html
import org.scalajs.dom.raw.{Event, Node}
import com.thoughtworks.binding.bindable._

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
@JSExportTopLevel("FrontendApp")
object FrontendApp {
  val design: Design =
    newDesign
      .bind[ClientConfig].toEagerSingleton
      .bind[SimpleRouter].toSingleton
      .bind[AjaxClient].toSingleton
      .bind[Routes].toEagerSingleton

  lazy val session = design.newSession
  val builtSession = session.build[FrontendApp]

  @JSExport
  def main(): Unit = {
    html.render(document.getElementById("lambda-app").asInstanceOf[Node],
                builtSession.appLayout)
  }
}

trait FrontendApp extends IDEHelpers {
  lazy val simpleRouter = bind[SimpleRouter]
  lazy val clientConfig = bind[ClientConfig]
  lazy val ajaxClient = bind[AjaxClient]

  @html def navBar: Binding[Node] = {
    document.addEventListener("DOMContentLoaded", (e: Event) => {
      var elems = document.querySelectorAll(".sidenav");
      var instances = scalajs.js.Dynamic.global.M.Sidenav.init(elems, {})
    })

    <nav class="blue-grey darken-2" data:role="navigation">
      <div class="nav-wrapper container-wide">
        <div class="row">
          <div class="col s12 m3">
            <a href="/" onclick={e: Event => {
              e.preventDefault(); simpleRouter.changeToRouteByName(RouteName.Home.entryName)
            }} class="brand-logo">
              <span class="grey-text text-lighten-2">Scala Lambda Template</span>
            </a>
            <a href="#" data:data-target="mobile-demo" class="sidenav-trigger">
              <i class="material-icons">menu</i>
            </a>
          </div>
          <div class="col s12 m9">
            <ul class="right hide-on-med-and-down">
              <li>
                <a href="https://bitbucket.org/jkapple/scala-lambda-template/" target="_blank">Repo</a>
              </li>
              <li>
                <a href="/dynamo-examples" onclick={e: Event => {
                  e.preventDefault();
                  simpleRouter.changeToRouteByName(RouteName.DynamoExample.entryName)
                }}>Examples</a>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <ul class="sidenav" id="mobile-demo">
        <li>
          <a href="/" onclick={e: Event => {
            e.preventDefault(); simpleRouter.changeToRouteByName(RouteName.Home.entryName)
          }}>Home</a>
        </li>
        <li>
          <a href="https://bitbucket.org/jkapple/scala-lambda-template/" target="_blank">Repo</a>
        </li>
        <li>
          <a href="/dynamo-examples" onclick={e: Event => {
            e.preventDefault();
            simpleRouter.changeToRouteByName(RouteName.DynamoExample.entryName)
          }}>Examples</a>
        </li>
      </ul>
    </nav>
  }

  @html def footer: Binding[Node] = {
    <footer class="page-footer blue-grey darken-2" style="padding-top:0px;">
      <div class="footer-copyright" onclick={(_:Event) => println("daaaa")}>
        <div class="container">
          © 2019 Josh Kapple
          <a class="grey-text text-lighten-4 right" href="https://www.joshkapple.com" target="_blank">JoshKapple.com</a>
        </div>
      </div>
    </footer>
  }

  @html def appLayout: Binding[BindingSeq[Node]] = Binding apply {
    List(
      navBar,
      simpleRouter.currentPageComponentOrEmpty.bind.render
    ).bindSeq
  }

  simpleRouter.routeFromCurrentLocation
}
