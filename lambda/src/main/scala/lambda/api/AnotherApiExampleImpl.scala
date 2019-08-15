package lambda.api

import lambda.models.Movie

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Another api example that retrieves movie information
  * from an object stored server side.
  */
object AnotherApiExampleImpl extends AnotherApiExample {
  override def findMovieByName(name: String): Future[Option[Movie]] = {
    Future {
      Movie.SimpleMovieDb.movies
        .find(_.title.toLowerCase().contains(name.toLowerCase()))
    }
  }
}
