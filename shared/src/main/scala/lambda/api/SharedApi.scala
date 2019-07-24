package lambda.api

import lambda.SharedClass

import scala.concurrent.Future

// shared API interface
trait SharedApi {
  def doThing(sharedClass: SharedClass): Future[(String, String)]
}
