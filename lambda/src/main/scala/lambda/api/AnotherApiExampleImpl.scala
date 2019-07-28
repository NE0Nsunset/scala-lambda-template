package lambda.api
import lambda.Movie

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AnotherApiExampleImpl extends AnotherApiExample {
  override def findMovieByName(name: String): Future[Option[Movie]] = {
    println(name.toList)
    Future {
      SimpleMovieDb.movies.find(
        _.title.toLowerCase().contains(name.toLowerCase()))
    }
  }
}

object SimpleMovieDb {
  // Top 5 Top Rated Movies From IMDB
  val movies = Seq(
    Movie(
      "The Shawshank Redemption",
      1994,
      "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
      "https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_UX182_CR0,0,182,268_AL_.jpg"
    ),
    Movie(
      "The Godfather",
      1972,
      "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
      "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY268_CR3,0,182,268_AL_.jpg"
    ),
    Movie(
      "The Godfather: Part II",
      1974,
      "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate.",
      "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY268_CR3,0,182,268_AL_.jpg"
    ),
    Movie(
      "The Dark Knight",
      2008,
      "When the menace known as The Joker emerges from his mysterious past, he wreaks havoc and chaos on the people of Gotham. The Dark Knight must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
      "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_UX182_CR0,0,182,268_AL_.jpg"
    ),
    Movie(
      "12 Angry Men",
      1957,
      "A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence.",
      "A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence."
    )
  )
}
