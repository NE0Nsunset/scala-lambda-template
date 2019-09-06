package lambda.routing

import enumeratum.EnumEntry
import lambda.pagelevel.{DynamoExamples, Home}
import wvlet.airframe._

trait Routes {
  lazy val simpleRouter: SimpleRouter = bind[SimpleRouter]
  val routeList = List(
    SimpleRoute[Home]("/",
                      RouteName.Home,
                      (session: Session) => session.build[Home]),
    SimpleRoute[DynamoExamples](
      "/dynamo-examples",
      RouteName.DynamoExample,
      (session: Session) => session.build[DynamoExamples])
  )

  simpleRouter.addRoutes(routeList)
}

trait RouteName extends EnumEntry
object RouteName {
  case object Home extends RouteName
  case object DynamoExample extends RouteName
}
