package lambda

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import lambda.LambdaHandler.autowireApiController
import play.api.libs.json.{JsObject, JsValue, Json}
import akka.http.scaladsl.unmarshalling.Unmarshaller

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.Picklers._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    val scalajsScript = scalajs.html
      .scripts("client",
               name => s"/assets/$name",
               name => getClass.getResource(s"/public/$name") != null)
      .body

    val htmlTemplate =
      s"""| <html>
          |   <head>
          |   </head>
          |   <body>
          |     <div id="lambdaapp"></div>
          |     $scalajsScript
          |   </body>
          |  </html>""".stripMargin

    val route =
      pathSingleSlash { // Frontend entry point
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, htmlTemplate))
        }
      } ~ pathPrefix("assets" / Remaining) { file =>
        // optionally compresses the response with Gzip or Deflate
        // if the client accepts compressed responses
        encodeResponse {
          getFromResource("public/" + file)
        }
      } ~ pathPrefix("api" / Remaining) { path => // Autowired api endpoint
        post {
          decodeRequest {
            entity(as[String]) { str =>
              complete(autowireApiController(path, ujson.read(str)))
            }
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/")
  }
}
