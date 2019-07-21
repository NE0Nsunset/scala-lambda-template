package lambda

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.Context
import lambda.api.{SimpleApi, SimpleApiImpl}
import play.api.libs.json.{JsObject, JsValue, Json}
import ujson.Value
import upickle._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val path = "lambda/api/SimpleApi/twoPlusN"
    val jsonString = ujson.read(Json.obj("n" -> "1").toString())
    Await.result(autowireApiController(path, jsonString),
                 Duration.create(10, "seconds"))
  }

  def testLambda(input: InputStream,
                 output: OutputStream,
                 context: Context): Unit = {
    val s = scala.io.Source.fromInputStream(input).mkString
    val json: JsValue = Json.parse(s)
    val path = (json \ "path").get

    val body: JsObject = Json.obj("path" -> path,
                                  "input stream" -> s,
                                  "context" -> context.toString)
    val response: JsObject =
      Json.obj(
        "isBase64Encoded" -> false,
        "statusCode" -> 200,
        "body" -> body.toString,
      )

    output.write(response.toString.getBytes("UTF-8"))
  }

  def autowireApiHandler(input: InputStream,
                         output: OutputStream,
                         context: Context): Unit = {
    def response(v: String): JsObject =
      Json.obj(
        "isBase64Encoded" -> false,
        "statusCode" -> 200,
        "body" -> v,
        "headers" -> Json.obj(
          "access-control-allow-headers" -> "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
          "access-control-allow-methods" -> "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT",
          "access-control-allow-origin" -> "*",
          "test-header" -> v
        )
      )
    val s = scala.io.Source.fromInputStream(input).mkString
    val jsonstr = ujson.read(s)
    println(jsonstr("path")).toString
    println(jsonstr("path").toString.replaceFirst("(/)?api/", ""))
    val path = jsonstr("path").toString.replaceFirst("(/)?api/", "")
    val bodyString = StringContext treatEscapes jsonstr("body").toString()
    val body = bodyString.substring(1, bodyString.length - 1)
    val bodyJson = ujson.read(body)

    println(path)
    println(bodyJson.toString())

    val autowireFuture = autowireApiController(path, bodyJson) map { s =>
      val r: JsObject = response(s)
      output.write(r.toString().getBytes("UTF-8"))
    }
    Await.result(autowireFuture, Duration.create(60, "seconds"))
  }

  def autowireApiController(path: String,
                            bodyJsonString: Value): Future[String] = {
    val bodyJson = ujson.read(Json.obj("n" -> "1").toString())

    val routeList = List(
      AutowireServer.route[SimpleApi](SimpleApiImpl)
    )

    println(bodyJson)
    println(path)
    def response(v: String): JsObject =
      Json.obj(
        "isBase64Encoded" -> false,
        "statusCode" -> 200,
        "body" -> v,
        "headers" -> Json.obj(
          "access-control-allow-headers" -> "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
          "access-control-allow-methods" -> "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT",
          "access-control-allow-origin" -> "*"
        )
      )

    val autowireRequest = autowire.Core
      .Request(path.split("/"),
               upickle.default.read[Map[String, String]](bodyJson))
    println(autowireRequest)
    println(routeList)

    val route = routeList
      .find(r => r.isDefinedAt(autowireRequest))
      .getOrElse(throw new Exception(s"route not defined for path: $path"))

    println(route.toString())
    println(route.isDefinedAt(autowireRequest))

    val router = route(autowireRequest)

    router.map(r => println(response(r).toString()))
    router
  }
}
