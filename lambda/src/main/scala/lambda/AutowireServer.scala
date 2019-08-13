package lambda

import com.google.inject.Guice
import javax.inject.{Inject, Singleton}
import lambda.LambdaHandler.logMessage
import lambda.api.{
  AnotherApiExample,
  AnotherApiExampleImpl,
  MovieApiWithDynamo,
  MovieApiWithDynamoImpl,
  SharedApi,
  SharedApiImpl
}
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.Picklers._
import lambda.service.{Module, MovieService}
import ujson.Value
import upickle.default._

import scala.concurrent.Future

@Singleton
class AutowireServer @Inject()(movieApiWithDynamo: MovieApiWithDynamo)
    extends autowire.Server[Value,
                            upickle.default.Reader,
                            upickle.default.Writer] {

  def write[Result: upickle.default.Writer](r: Result) =
    upickle.default.writeJs(r)

  def read[Result: upickle.default.Reader](p: Value) = {
    val r = upickle.default.read[Result](p)
    println(r)
    r
  }

  // Bind Api Contracts to their implementations here
  val routeList = List(this.route[SharedApi](SharedApiImpl),
                       this.route[AnotherApiExample](AnotherApiExampleImpl),
                       this.route[MovieApiWithDynamo](movieApiWithDynamo))

  def autowireApiController(path: String,
                            bodyJsonString: Value): Future[String] = {
    val strippedPath = path.replaceAll("^\"|\"$", "") // remove pesky quotes AWS likes to include
    logMessage(strippedPath)
    logMessage(bodyJsonString.toString())
    val autowireRequest = autowire.Core
      .Request(strippedPath.split("/"),
               ujson
                 .read(bodyJsonString)
                 .asInstanceOf[ujson.Obj]
                 .value
                 .toMap)

    val route = this.routeList
      .find(r => r.isDefinedAt(autowireRequest))
      .getOrElse(throw new Exception(s"route not defined for path: $path"))

    val router = route(autowireRequest)

    router.map(ujson.write(_))
  }
}
