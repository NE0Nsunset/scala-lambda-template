package lambda.seed

import java.time.LocalDateTime

import lambda.models.{BlogItem, DynamoItem, Movie, MovieItem}

object SeedObjects {
  val blogEntryDate = LocalDateTime.of(2019, 4, 7, 10, 0)
  val blogEntry =
    BlogItem.fromDateTitleString(blogEntryDate,
                                 "Test Blog",
                                 "test-blog",
                                 "a short description",
                                 "blog body")

  val movieItem1 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(0))
  val movieItem2 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(2))
  val movieItem3 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(3))
  val movieItem4 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(4))
  val seeds: List[DynamoItem] =
    List(blogEntry, movieItem1, movieItem2, movieItem3, movieItem4)
}
