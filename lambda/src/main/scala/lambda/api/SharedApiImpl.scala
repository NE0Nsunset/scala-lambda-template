package lambda.api

import lambda.SharedClass
import upickle._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.Picklers._

/**
  * Simple example of an api that takes two strings and creates
  * and returns the case class 'SharedClass'
  */
object SharedApiImpl extends SharedApi {
  override def doThing(name: String,
                       description: String): Future[SharedClass] = {
    Future { SharedClass(name, description) }
  }
}
