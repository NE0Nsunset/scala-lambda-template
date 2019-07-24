package lambda.api

import lambda.SharedClass

import scala.concurrent.Future

// shared API interface
trait SharedApi {
  def doThing(name: String, description: String): Future[SharedClass]
}
