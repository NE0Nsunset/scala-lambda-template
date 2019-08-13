package lambda.api

import lambda.models.{ExampleDynamoItem, MovieItem}

import scala.concurrent.Future

trait MovieApiWithDynamo {
  def putMovie(movie: MovieItem): Future[Boolean]
  def findByName(name: String): Future[Option[MovieItem]]
  def retrieveAllMovies(): Future[List[MovieItem]]
}
