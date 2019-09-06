package lambda.routing

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Var
import lambda.FrontendApp
import lambda.pagelevel.{EmptyPageComponent, Home, PageComponent}
import org.scalajs.dom.window
import wvlet.airframe._

import scala.scalajs.js.Dynamic

class SimpleRouter {
  type SimpleRoutePageComponent = SimpleRoute[_ <: PageComponent]

  private var routeList: List[SimpleRoutePageComponent] =
    List.empty[SimpleRoutePageComponent]

  val emptyPageComponent = new EmptyPageComponent

  val currentRouteOpt = Var[Option[SimpleRoutePageComponent]](None)

  lazy val currentPageComponentOpt: Var[Option[PageComponent]] = Var(None)

  val currentPageComponentOrEmpty: Binding[PageComponent] =
    Binding { currentPageComponentOpt.bind.getOrElse(emptyPageComponent) }

  def addRoutes(routes: List[SimpleRoutePageComponent]) = {
    routeList ++= routes
  }

  def findRouteByName(routeName: String) = {
    routeList.find(_.routeName.entryName == routeName)
  }

  def changeRouteByName(routeName: String) = {
    currentRouteOpt.value = routeList.find(_.routeName.entryName == routeName)
    currentPageComponentOpt.value = currentRouteOpt.value
      .map(_.sessionToComponent(FrontendApp.session))
    window.history.pushState(
      Dynamic.literal(),
      "",
      s"/${currentRouteOpt.value.map(_.pathPattern).getOrElse("/").tail}")
  }

  def routeFromCurrentLocation = {
    val current = routeList.find(_.matches(window.location.pathname))
    currentRouteOpt.value = current
    currentPageComponentOpt.value = current
      .map(_.sessionToComponent(FrontendApp.session))
  }
}

case class SimpleRoute[Component <: PageComponent](
    pathPattern: String,
    routeName: RouteName,
    sessionToComponent: Session => PageComponent) {
  val tokenRegex = "[^/?]+"
  val pathPatternTokens = pathPattern.split("/")

  lazy val pathToRegex: Array[String] = {
    if (pathPatternTokens.isEmpty) {
      Array("/")
    } else
      pathPatternTokens map {
        case pathToken if pathToken.startsWith(":") => tokenRegex
        case pathToken                              => pathToken
      }
  }

  def matches(href: String) = {
    href.matches(pathToRegex.mkString("/"))
  }
}
