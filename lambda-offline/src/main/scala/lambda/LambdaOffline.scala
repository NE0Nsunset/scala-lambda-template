package lambda

import lambda.LambdaHandler.autowireApiController
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.Picklers._

//TODO wire up lambdas to simple http server
object LambdaOffline extends App {

  override def main(args: Array[String]): Unit = {
    super.main(args)
    val path1 = "lambda/api/SimpleApi/twoPlusN"
    val path2 = "lambda/api/SharedApi/doThing"
    val x1 = """{"n":1}"""
    val x2 = """{"name":"name","description":"desc"}"""
    val jsonString1 = ujson.read(x1)
    val jsonString2 = ujson.read(x2)
    println(jsonString1)
    println(jsonString2)
    println(
      Await.result(autowireApiController(path1, jsonString1),
                   Duration.create(10, "seconds")))
    println(
      Await.result(autowireApiController(path2, jsonString2),
                   Duration.create(10, "seconds")))
  }
}
