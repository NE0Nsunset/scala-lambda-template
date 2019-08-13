package lambda

import com.thoughtworks.binding.Binding.Var
import lambda.PageLevel.{DynamoExamples, Home, PageComponent}

object SimpleRouter {

  object routes {
    lazy val home = new Home
    lazy val dynamoExamples = new DynamoExamples
  }

  val currentRoute: Var[PageComponent] = Var(routes.home)

  def changeRoute(newRoute: PageComponent) = {
    currentRoute.value = newRoute
  }
}
