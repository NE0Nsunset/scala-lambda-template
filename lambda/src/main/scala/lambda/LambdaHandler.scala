package lambda

import java.io.{InputStream, OutputStream}
import com.amazonaws.services.lambda.runtime.Context
import play.api.libs.json.{JsObject, Json}
import ujson.Value
import upickle._
import autowire._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import lambda.serialization.Picklers._

object LambdaHandler extends App with AWSLogging {

  lazy val corsEnabled = System.getenv("ENABLE_CORS") == "true"

  lazy val corsHeaders: JsObject = Json.obj(
    "access-control-allow-headers" -> "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
    "access-control-allow-methods" -> "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT",
    "access-control-allow-origin" -> "*"
  )

  def response(v: String,
               statusCode: Int = 200,
               headers: JsObject = Json.obj()): JsObject = {
    Json.obj(
      "isBase64Encoded" -> false,
      "statusCode" -> statusCode,
      "body" -> v,
      "headers" -> headers
    )
  }

  /**
    * Used with a single AWS lambda configured via AWS Api Gateway to consume all sub-paths of /api
    * @param input
    * @param output
    * @param context
    */
  def autowireApiHandler(input: InputStream,
                         output: OutputStream,
                         context: Context): Unit = {

    val s = scala.io.Source.fromInputStream(input).mkString
    val jsonstr = ujson.read(s)

    val path = jsonstr("path").toString.replaceFirst("(/)?api/", "")
    val bodyString = StringContext treatEscapes jsonstr("body")
      .toString()
      .replaceAll("^\"|\"$", "")

    logMessage(path.toString)
    logMessage(bodyString)

    val bodyJson = ujson.read(bodyString)
    val autowireFuture = autowireApiController(path, bodyJson) map { s =>
      val r: String =
        response(s, 200, if (corsEnabled) corsHeaders else Json.obj())
          .toString()
      output.write(r.getBytes("UTF-8"))
    }

    Await.result(autowireFuture, Duration.create(60, "seconds"))
  }

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

    val route = AutowireServer.routeList
      .find(r => r.isDefinedAt(autowireRequest))
      .getOrElse(throw new Exception(s"route not defined for path: $path"))

    val router = route(autowireRequest)

    router.map(ujson.write(_))
  }
}
