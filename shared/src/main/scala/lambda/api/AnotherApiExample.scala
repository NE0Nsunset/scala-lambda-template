package lambda.api

import lambda.Movie

import scala.concurrent.Future

trait AnotherApiExample {
  def findMovieByName(name: String): Future[Option[Movie]]
}
