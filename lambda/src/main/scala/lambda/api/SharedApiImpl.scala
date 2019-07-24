package lambda.api

import lambda.SharedClass
import upickle._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lambda.serialization.Picklers._

object SharedApiImpl extends SharedApi {
  override def doThing(sharedClass: SharedClass) = {
    Future { (sharedClass.description, sharedClass.name) }
  }
}
