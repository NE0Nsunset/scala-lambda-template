package lambda.api

import lambda.models.MovieItem
import lambda.service.MovieService

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class MovieApiWithDynamoImpl(movieService: MovieService)
    extends MovieApiWithDynamo {

  def putMovie(movie: MovieItem): Future[Boolean] = {
    for {
      result <- movieService.put(movie).map(_ => true)
    } yield {
      result
    }
  }

//  def findByName(name: String): Future[Option[MovieItem]] = {
//    movieService.findMoviesByName(name).map(_.headOption)
//  }

  def retrieveAllMovies(): Future[List[MovieItem]] = {
    movieService.findAllMovies()
  }

}
