package lambda.routing

import enumeratum.EnumEntry
import lambda.pagelevel.{BlogDetail, Examples, Home}
import wvlet.airframe._

trait Routes {
  lazy val simpleRouter: SimpleRouter = bind[SimpleRouter]
  val routeList = List(
    SimpleRoute[Home]("/",
                      RouteName.Home,
      (session: Session) => session.withChildSession(newDesign.bind[Home].toSingleton){cs => cs.build[Home]}),
    SimpleRoute[Examples]("/dynamo-examples",
                          RouteName.DynamoExample,
                          (session: Session) => session.withChildSession(newDesign.bind[Examples].toSingleton){cs => cs.build[Examples]}),
    SimpleRoute[BlogDetail]("/blog/:year/:month/:date/:slug",
                            RouteName.BlogDetail,
                            (session: Session) => session.withChildSession(newDesign.bind[BlogDetail].toSingleton){cs => cs.build[BlogDetail]})
  )

  simpleRouter.addRoutes(routeList)
}

trait RouteName extends EnumEntry
object RouteName {
  case object Home extends RouteName
  case object DynamoExample extends RouteName
  case object BlogDetail extends RouteName
}
