package lambda.routing

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.Var
import lambda.{ClientConfig, FrontendApp}
import lambda.pagelevel.{EmptyPageComponent, PageComponent}
import org.scalajs.dom.{Event, window}
import wvlet.airframe._

import scala.scalajs.js.Dynamic

/**
  * A basic single page application router with support for
  * finding a route from location and changing to a route
  */
class SimpleRouter {
  type SimpleRoutePageComponent = SimpleRoute[_ <: PageComponent]

  val clientConfig = bind[ClientConfig]

  // react to changes in browser location
  window.addEventListener("popstate", (event: Event) => {
    routeFromCurrentLocation
  })

  private var routeList: List[SimpleRoutePageComponent] =
    List.empty[SimpleRoutePageComponent]

  val emptyPageComponent = new EmptyPageComponent

  // A map of url token name to value extracted from [[SimpleRoute.pathPattern]]
  var currentRouteKeyValues: List[(String, String)] = Nil

  def findRouteValue(routeKey: String): Option[String] =
    currentRouteKeyValues.find(_._1 == routeKey).map(_._2)

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

  def changeToRouteByName(routeName: String,
                          routeProps: List[(String, String)] = Nil) = {
    println(s"changing route to $routeName with ${routeProps.toString}")
    currentPageComponentOpt.value.foreach(_.onDestroy)
    currentRouteOpt.value = routeList.find(_.routeName.entryName == routeName)

    // TODO cleanup
    val calculatedPath = {
      val x =
        s"/${currentRouteOpt.value
          .map(_.tokenMapToHref(routeProps)).getOrElse("/")}"
      if (x.startsWith("//")) x.tail else x
    }

    window.history.pushState(Dynamic.literal(), "", s"${clientConfig.getStageName}$calculatedPath")
    currentRouteKeyValues = routeProps
    currentPageComponentOpt.value = currentRouteOpt.value
      .map(_.sessionToComponent(FrontendApp.session))
    currentPageComponentOpt.value.foreach(_.onCreate)
  }

  def routeFromCurrentLocation = {
    println("route from current location")
    currentRouteKeyValues = Nil // unset route props
    val href =
      window.location.pathname.replace(clientConfig.getStageName, "")
    val current = routeList.find(_.matches(href))
    if (currentRouteOpt.value != current){
      currentPageComponentOpt.value.foreach(_.onDestroy)
    }
    currentRouteOpt.value = current
    current foreach { x =>
      currentRouteKeyValues = x.routeTokenMap(href)
    }
    currentPageComponentOpt.value = current
      .map(_.sessionToComponent(FrontendApp.session))
    currentPageComponentOpt.value.foreach(_.onCreate)
  }
}

/**
  * Definition of a simple route
  * @param pathPattern the pattern with which to match a route. supports defining tokens by prefixing a value with ':'
  *                     see lambda.routing.Routes for examples
  * @param routeName
  * @param sessionToComponent
  * @tparam Component
  */
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

  def routeTokenMap(href: String) = {
    val tokens =
      pathPattern.split("/").zipWithIndex.filter(_._1.startsWith(":"))
    val hrefTokens = href.split("/")
    tokens.map(x => x._1.tail -> hrefTokens(x._2)).toList
  }

  // reconstructs a URL from tokens
  def tokenMapToHref(tokenMap: List[(String, String)]): String = {
    val patternTokens = pathPatternTokens.zipWithIndex
    patternTokens
      .map(
        {
          case x if x._1.startsWith(":") =>
            tokenMap.find(_._1 == x._1.tail).map(_._2).getOrElse("")
          case y => y._1
        }
      ).mkString("/")
  }

  def matches(href: String) = {
    href.matches(pathToRegex.mkString("/"))
  }
}

/**
  * Mixin to use with PageComponents that are built via airframe session
  */
trait UsesSimpleRouter {
  val simpleRouter: SimpleRouter = bind[SimpleRouter]
}
