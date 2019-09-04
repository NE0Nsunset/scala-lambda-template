package lambda.Routing

import com.thoughtworks.binding.Binding.Var
import enumeratum.EnumEntry
import lambda.FrontendApp
import lambda.PageLevel.{DynamoExamples, Home, PageComponent}
import org.scalajs.dom.window
import wvlet.airframe._

class SimpleRouter {
  val routeList = List(
    SimpleRoute[Home]("/",
                      RouteName.Home,
                      (session: Session) => session.build[Home]),
    SimpleRoute[DynamoExamples](
      "/dynamo-examples",
      RouteName.DynamoExample,
      (session: Session) => session.build[DynamoExamples])
  )

  val currentRouteOpt = Var[Option[SimpleRoute[_ <: PageComponent]]](None)
  lazy val currentPageComponent =
    Var[PageComponent](routeList.head.sessionToComponent(FrontendApp.session))

  def matchingRouteByName(routeName: String) = {
    routeList.find(_.routeName.entryName == routeName)
  }

  def changeRouteByName(routeName: String) = {
    currentRouteOpt.value = routeList.find(_.routeName.entryName == routeName)
    currentPageComponent.value = currentRouteOpt.value
      .map(_.sessionToComponent(FrontendApp.session)).get
  }
}

case class SimpleRoute[Component <: PageComponent](
    pathPattern: String,
    routeName: RouteName,
    sessionToComponent: Session => PageComponent) {
  val tokenRegex = "[^/?]+"
  val pathPatternTokens = pathPattern.split("/")

  lazy val pathToRegex: Array[String] = {
    pathPatternTokens map {
      case pathToken if pathToken.startsWith(":") => tokenRegex
      case pathToken                              => pathToken
    }
  }

  def matches(href: String) = {
    href.matches(pathToRegex.mkString("/"))
  }

}

object SimpleRoute {
  def fromCurrentLocation(
      router: SimpleRouter): Option[SimpleRoute[_ <: PageComponent]] = {
    router.routeList.find(_.matches(window.location.pathname))
  }
}

trait RouteName extends EnumEntry
object RouteName {
  case object Home extends RouteName
  case object DynamoExample extends RouteName
}
