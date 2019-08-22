package lambda

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import play.api.libs.json.{JsObject, JsValue, Json}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration
import lambda.serialization.Picklers._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import lambda.models.{DynamoItem}
import lambda.service.{
  DynamoClientImpl,
  DynamoClientT,
  DynamoService,
  MovieService,
  MovieServiceImpl
}
import scala.io.StdIn
import com.typesafe.config.ConfigFactory
import lambda.LambdaHandler.{awsLogging, config}
import lambda.api.{MovieApiWithDynamo, MovieApiWithDynamoImpl}

object WebServer extends App {
  implicit val actorSystem = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()(actorSystem)
  implicit val executionContext = actorSystem.dispatcher

  val config = ConfigFactory.load()

  val host: String = config.getString("akka-http.host")
  val port: Int = config.getInt("akka-http.port")

  lazy val awsLogging = new AWSLogging {}
  lazy val dynamoClient: DynamoClientT =
    new LocalDynamoClient(config)
  lazy val movieService: MovieService =
    new MovieServiceImpl(config, dynamoClient)
  lazy val movieApiWithDynamo: MovieApiWithDynamo =
    new MovieApiWithDynamoImpl(movieService)
  lazy val autowireServer: AutowireServer =
    new AutowireServer(movieApiWithDynamo, awsLogging)

  val scalajsScript = scalajs.html
    .scripts("client",
             name => s"/assets/$name",
             name => getClass.getResource(s"/public/$name") != null)
    .body

  val clientConfig =
    Json.obj("backendApi" -> s"http://$host:${port.toString}/api/").toString()

  val htmlTemplate =
    s"""| <html>
          |   <head>
          |     <!--Import Google Icon Font-->
          |     <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
          |     <!--Import materialize.css-->
          |     <link type="text/css" rel="stylesheet" href="static/css/materialize.min.css"  media="screen,projection"/>
          |     <!--Let browser know website is optimized for mobile-->
          |      <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
          |   </head>
          |   <body>
          |     <div id="lambdaapp"></div>
          |     <script type="text/javascript">
          |       window.clientConfig = $clientConfig
          |     </script>
          |     $scalajsScript
          |     <script type="text/javascript" src="static/js/materialize.min.js"></script>
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
            complete(
              autowireServer
                .autowireApiController(path, ujson.read(str))
                .map(x => {
                  println(x)
                  x
                }))
          }
        }
      }
    } ~ pathPrefix("static") {
      getFromResourceDirectory("public")
    }
  val bindingFuture = Http().bindAndHandle(route, host, port)

  println(
    s"Server online at http://$host:${port.toString}\n Press return to stop")
  //    StdIn.readLine() // let it run until user presses return
  //    bindingFuture
  //      .flatMap(_.unbind()) // trigger unbinding from the port
  //      .onComplete(_ => actorSystem.terminate()) // and shutdown when done
}
