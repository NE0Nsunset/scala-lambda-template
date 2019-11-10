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
import scala.scalajs.js

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
object FrontendApp extends js.JSApp {
  val design: Design =
    newDesign
      .bind[ClientConfig].toEagerSingleton
      .bind[SimpleRouter].toSingleton
      .bind[AjaxClient].toSingleton
      .bind[Routes].toEagerSingleton

  val session = design.newSession
  val builtSession = session.build[FrontendApp]

  @JSExport
  def main(): Unit = {
    dom.render(document.getElementById("lambda-app"), builtSession.appLayout)
    dom.render(document.getElementById("lambda-app-footer"),
               builtSession.footer)
  }
}

trait FrontendApp {
  private val session = bind[Session]
  lazy val simpleRouter = bind[SimpleRouter]
  val clientConfig = bind[ClientConfig]
  lazy val ajaxClient = bind[AjaxClient]

  @dom def navBar: Binding[Node] = {
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

  @dom def footer: Binding[Node] = {
    <footer class="page-footer blue-grey darken-2" style="padding-top:0px;">
      <div class="footer-copyright">
        <div class="container">
          © 2019 Josh Kapple
          <a class="grey-text text-lighten-4 right" href="https://www.joshkapple.com" target="_blank">JoshKapple.com</a>
        </div>
      </div>
    </footer>
  }

  @dom def appLayout: Binding[BindingSeq[Node]] = {
    Constants(
      navBar.bind,
      simpleRouter.currentPageComponentOrEmpty.bind.render.bind
    )
  }

  simpleRouter.routeFromCurrentLocation
}
