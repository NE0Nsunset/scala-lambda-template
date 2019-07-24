package lambda

import lambda.LambdaHandler.autowireApiController
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

//TODO wire up lambdas to simple http server
object LambdaOffline extends App {

  override def main(args: Array[String]): Unit = {
    super.main(args)
    val path = "lambda/api/SimpleApi/twoPlusN"
    val jsonString = ujson.read(Json.obj("n" -> "1").toString())
    println(
      Await.result(autowireApiController(path, jsonString),
                   Duration.create(10, "seconds")))
  }
}
