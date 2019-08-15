package lambda

import java.io.{InputStream, OutputStream}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.amazonaws.services.lambda.runtime.Context
import play.api.libs.json.{JsObject, Json}
import autowire._
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import lambda.serialization.Picklers._
import lambda.service.Module
import com.google.inject._

object LambdaHandler {
  lazy val config = ConfigFactory.load()
  implicit val actorSystem = ActorSystem("my-system")
  lazy val awsLogging = new AWSLogging {}
  lazy val injector =
    Guice.createInjector(new Module(actorSystem, config, awsLogging))
  implicit val materializer = ActorMaterializer()(actorSystem)

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = actorSystem.dispatcher

  lazy val corsEnabled = System.getenv("ENABLE_CORS") == "true"

  lazy val corsHeaders: JsObject = Json.obj(
    "access-control-allow-headers" -> "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
    "access-control-allow-methods" -> "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT",
    "access-control-allow-origin" -> "*"
  )

  lazy val autowireServer: AutowireServer =
    injector.getInstance(classOf[AutowireServer])

  /*
   * Exists to test jars like they would be run from AWS
   */
  def main(args: Array[String]): Unit = {
    println(autowireServer.routeList.toString)
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

    awsLogging.logMessage(path.toString)
    awsLogging.logMessage(bodyString)

    val bodyJson = ujson.read(bodyString)
    val autowireFuture = autowireServer.autowireApiController(path, bodyJson) map {
      s =>
        val r: String =
          response(s, 200, if (corsEnabled) corsHeaders else Json.obj())
            .toString()
        output.write(r.getBytes("UTF-8"))
    }

    Await.result(autowireFuture, Duration.create(60, "seconds"))
  }
}
