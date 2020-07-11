package lambda

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import play.api.libs.json.Json
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import lambda.models.{BlogItem, MovieItem}
import lambda.seed.SeedObjects
import lambda.serialization.DynamoItemConverters._

trait LocalDependencies {
  this: LambdaDependencies =>
  override val config = ConfigFactory.load("local")

  implicit val actorSystem = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()(actorSystem)
  implicit val executionContext = actorSystem.dispatcher

  val dynamoClient = new LocalDynamoClient(config)
}

object WebServer extends App with LambdaDependencies with LocalDependencies {

  val host: String = config.getString("akka-http.host")
  val port: Int = config.getInt("akka-http.port")

  val scalajsScript = scalajs.html
    .scripts("client",
             name => s"/assets/$name",
             name => getClass.getResource(s"/public/$name") != null)
    .body

  val clientConfig =
    Json
      .obj("backendApi" -> s"http://$host:${port.toString}/api/",
           "apiStage" -> "",
           "staticUrl" -> "").toString()

  val htmlTemplate =
    s"""| <html>
          |   <head>
          |     <!--Import Google Icon Font-->
          |     <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
          |     <!--Import materialize.css-->
          |     <link type="text/css" rel="stylesheet" href="/static/css/materialize.min.css"  media="screen,projection"/>
          |     <link type="text/css" rel="stylesheet" href="/static/css/main.css"  media="screen,projection"/>
          |     <!--Let browser know website is optimized for mobile-->
          |      <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
          |   </head>
          |   <body>
          |     <main id="lambda-app"></main>
          |     <footer id="lambda-app-footer"></footer>
          |     <script type="text/javascript">
          |       window.clientConfig = $clientConfig
          |     </script>
          |     <script type="text/javascript" src="/static/js/materialize.min.js"></script>
          |     $scalajsScript
          |   </body>
          |  </html>""".stripMargin

  val route =
    pathPrefix("assets" / Remaining) { file =>
      // optionally compresses the response with Gzip or Deflate
      // if the client accepts compressed responses
      encodeResponse {
        getFromResource("public/" + file)
      }
    } ~ pathPrefix("api" / Remaining) { path => // Autowired api endpoint
      post {
        decodeRequest {
          entity(as[String]) { str =>
            complete(
              autowireController
                .autowireApiController(path, ujson.read(str))
                .map(x => {
                  x
                }))
          }
        }
      }
    } ~ pathPrefix("static") {
      getFromResourceDirectory("public")
    } ~ path(Remaining) { _ => // Frontend entry point
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, htmlTemplate))
      }
    }
  val bindingFuture = Http().bindAndHandle(route, host, port)

  dynamoClient
    .createTableIfNotExists().map(
      _ =>
        // Seed local database only if items do not exist and table is created
        SeedObjects.seeds map {
          case blogItem: BlogItem   => blogService.putIfNotExists(blogItem)
          case movieItem: MovieItem => movieService.putIfNotExists(movieItem)
      })

  println(
    s"Server online at http://$host:${port.toString}\n Press return to stop")
  //    StdIn.readLine() // let it run until user presses return
  //    bindingFuture
  //      .flatMap(_.unbind()) // trigger unbinding from the port
  //      .onComplete(_ => actorSystem.terminate()) // and shutdown when done
}
