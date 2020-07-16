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

import scala.scalajs.js.Date

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
object FrontendApp {
  val design: Design =
    newDesign
      .bind[ClientConfig].toEagerSingleton
      .bind[SimpleRouter].toSingleton
      .bind[AjaxClient].toSingleton
      .bind[Routes].toEagerSingleton

  lazy val session = design.newSession
  val builtSession = session.build[FrontendApp]

  def main(args: Array[String]): Unit = {
    println("heellll")
    html.render(document.getElementById("lambda-app").asInstanceOf[Node],
                builtSession.appLayout)
    html.render(document.getElementById("lambda-app-footer").asInstanceOf[Node],
                builtSession.footer)
  }
}

class FrontendApp extends IDEHelpers {
  lazy val simpleRouter = bind[SimpleRouter]
  lazy val clientConfig = bind[ClientConfig]
  lazy val ajaxClient = bind[AjaxClient]

  document.addEventListener("DOMContentLoaded", (e: Event) => {
    var instances = scalajs.js.Dynamic.global.window.M.AutoInit()
  })

  @html def navBar: Binding[Node] =
    Binding apply {
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
    }.bind

  @html def footer: Binding[Node] = {
    val year = new Date(Date.now()).getFullYear()
    <footer class="page-footer blue-grey darken-2" style="padding-top:0px;">
      <div class="footer-copyright">
        <div class="container">
          © {year.toString} Josh Kapple
          <a class="grey-text text-lighten-4 right" href="https://www.joshkapple.com" target="_blank">JoshKapple.com</a>
        </div>
      </div>
    </footer>
  }

  @html def appLayout: Binding[BindingSeq[Node]] = Binding apply {
    Constants(
      navBar,
      simpleRouter.currentPageComponentOrEmpty.bind.render
    ).bind.map(_.bind)
  }

  simpleRouter.routeFromCurrentLocation
}
