package lambda.api

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import lambda.models.MovieItem
import lambda.service.MovieService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MovieApiWithDynamoImpl @Inject()(movieService: MovieService)(
    private implicit val actorSystem: ActorSystem)
    extends MovieApiWithDynamo {
  implicit val executionContext: ExecutionContext =
    actorSystem.dispatcher

  def putMovie(movie: MovieItem): Future[Boolean] = {
    for {
      movieExists <- findByName(movie.title)
      result <- {
        if (movieExists.isDefined) {
          Future { false }
        } else {
          movieService.put(movie).map(_ => true)
        }
      }
    } yield {
      result
    }
  }

  def findByName(name: String): Future[Option[MovieItem]] = {
    movieService.findMoviesByName(name).map(_.headOption)
  }

  def retrieveAllMovies(): Future[List[MovieItem]] = {
    movieService.findAllMovies()
  }

}
