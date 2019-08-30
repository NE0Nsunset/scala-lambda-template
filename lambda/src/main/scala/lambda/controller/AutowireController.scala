package lambda.controller

import lambda.AWSLogging
import ujson.Value
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import ujson.Value
import upickle.default._
import scala.concurrent.Future

class AutowireController(awsLogging: AWSLogging,
                         val routeList: List[AutowireServer#Router]) {

  def autowireApiController(path: String,
                            bodyJsonString: Value): Future[String] = {
    println(routeList.toString)
    val strippedPath = path.replaceAll("^\"|\"$", "") // remove pesky quotes AWS likes to include
    awsLogging.logMessage(strippedPath)
    awsLogging.logMessage(bodyJsonString.toString())
    val autowireRequest = autowire.Core
      .Request(strippedPath.split("/"),
               ujson
                 .read(bodyJsonString)
                 .asInstanceOf[ujson.Obj]
                 .value
                 .toMap)

    val route = this.routeList
      .find(r => r.isDefinedAt(autowireRequest))
      .getOrElse(throw new Exception(
        s"route not defined for path: $path, ${autowireRequest.args} ${autowireRequest.path}"))

    val router = route(autowireRequest)

    router.map(ujson.write(_))
  }
}
