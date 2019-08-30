package lambda

import java.io.{InputStream, OutputStream}
import play.api.libs.json.{JsObject, Json}
import autowire._
import lambda.LambdaHandler.config
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import lambda.serialization.Picklers._
import lambda.service.DynamoClientImpl
import scala.concurrent.ExecutionContext.Implicits.global

trait LambdaDynamoClient {
  val dynamoClient = new DynamoClientImpl(config)
}

object LambdaHandler extends LambdaDependencies with LambdaDynamoClient {

  lazy val corsEnabled = System.getenv("ENABLE_CORS") == "true"

  lazy val corsHeaders: JsObject = Json.obj(
    "access-control-allow-headers" -> "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
    "access-control-allow-methods" -> "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT",
    "access-control-allow-origin" -> "*"
  )

  /*
   * Exists to test jars like they would be run from AWS
   */
  def main(args: Array[String]): Unit = {
    println(autowireController.routeList.toString)
  }

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
    */
  def autowireApiHandler(input: InputStream, output: OutputStream): Unit = {
    val s = scala.io.Source.fromInputStream(input).mkString
    val jsonstr = ujson.read(s)

    val path = jsonstr("path").toString.replaceFirst("(/)?api/", "")
    val bodyString = StringContext treatEscapes jsonstr("body")
      .toString()
      .replaceAll("^\"|\"$", "")

    awsLoggingImpl.logMessage(path.toString)
    awsLoggingImpl.logMessage(bodyString)

    val bodyJson = ujson.read(bodyString)
    val autowireFuture = autowireController.autowireApiController(
      path,
      bodyJson) map { s =>
      val r: String =
        response(s, 200, if (corsEnabled) corsHeaders else Json.obj())
          .toString()
      output.write(r.getBytes("UTF-8"))
    }

    Await.result(autowireFuture, Duration.create(60, "seconds"))
  }
}
