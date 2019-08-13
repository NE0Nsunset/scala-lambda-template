package lambda.api

import lambda.models.Movie

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AnotherApiExampleImpl extends AnotherApiExample {
  override def findMovieByName(name: String): Future[Option[Movie]] = {
    Future {
      Movie.SimpleMovieDb.movies
        .find(_.title.toLowerCase().contains(name.toLowerCase()))
    }
  }
}
