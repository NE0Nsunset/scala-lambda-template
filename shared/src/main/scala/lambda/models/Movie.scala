package lambda.models

import java.util.Date
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import scala.collection.JavaConverters._

case class Movie(title: String,
                 year: Int,
                 description: String,
                 thumbnail: String)
object Movie {
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
        "https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0LTg0MjAtYTJlMjFhNGUxZDFmXkEyXkFqcGdeQXVyNjc1NTYyMjg@._V1_UX182_CR0,0,182,268_AL_.jpg"
      ),
      Movie(
        "Forrest Gump",
        1994,
        "The presidencies of Kennedy and Johnson, the events of Vietnam, Watergate, and other history unfold through the perspective of an Alabama man with an IQ of 75.",
        "https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_UY268_CR1,0,182,268_AL_.jpg"
      )
    )
  }
}

case class MovieItem(partKey: String,
                     rangeKey: String,
                     createdAt: String,
                     lastUpdate: String,
                     title: String,
                     year: Int,
                     description: String,
                     thumbnail: String)
    extends DynamoItem {
  def itemToAttributeMap: java.util.Map[String, AttributeValue] = {
    Map(
      "partKey" -> AttributeValue.builder().s(partKey).build(),
      "rangeKey" -> AttributeValue.builder().s(rangeKey).build(),
      "createdAt" -> AttributeValue.builder().s(createdAt).build(),
      "lastUpdate" -> AttributeValue.builder().s(lastUpdate).build(),
      "title" -> AttributeValue.builder().s(title).build(),
      "year" -> AttributeValue.builder().n(year.toString).build(),
      "description" -> AttributeValue.builder().s(description).build(),
      "thumbnail" -> AttributeValue.builder().s(thumbnail).build(),
    ).asJava
  }
}

object MovieItem {
  val defaultPartKey: String = "movie"

  def createMovie(title: String,
                  year: Int,
                  description: String,
                  thumbnail: String): MovieItem = {
    val date = new Date().toLocaleString
    MovieItem(defaultPartKey,
              s"$year#$title", // allow us to sort by year with our range key
              date,
              date,
              title,
              year,
              description,
              thumbnail)
  }

  def fromMovie(movie: Movie) = {
    MovieItem(
      "movie",
      s"${movie.year}#${movie.title}", // allow us to sort by year with our range key
      new Date().toLocaleString,
      new Date().toLocaleString,
      movie.title,
      movie.year,
      movie.description,
      movie.thumbnail
    )
  }

  def fromAttributeMap(av: Map[String, AttributeValue]): MovieItem = {
    MovieItem(
      av("partKey").s(),
      av("rangeKey").s(),
      av("createdAt").s(),
      av("lastUpdate").s(),
      av("title").s(),
      av("year").n().toInt,
      av("description").s(),
      av("thumbnail").s(),
    )
  }
}
