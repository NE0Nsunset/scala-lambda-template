package service

import lambda.models.{ExampleDynamoItem, Movie, MovieItem}
import org.scalatest.BeforeAndAfterEach
import lambda.test.{TestBase, TestObjects}

import scala.collection.JavaConverters._

class MovieServiceTest extends TestBase with BeforeAndAfterEach {
  override def beforeEach(): Unit = {
    super.beforeEach()
    movieDynamoService.clientHandler.createTableIfNotExists
  }

  override def afterEach(): Unit = {
    super.afterEach()
    movieDynamoService.clientHandler.destroyTable
  }

  describe("ExampleDynamoItemService Tests") {
    it("Can describe its table") {
      assert(movieDynamoService.describeTable.nonEmpty)
    }

    it("Should use the alpakaka connector to list tables") {
      movieDynamoService.clientHandler.listTables map { r =>
        val tableNames = r.getTableNames.asScala
        assert(tableNames.contains(tableName))
      }
    }

    it("Should use alpakka to scan database") {
      movieDynamoService.scan map { r =>
        val items = r.getItems.asScala
        println(items)
        assert(true)
      }
    }

    it("Should be able to insert and retrieve an item") {
      val movieItem = MovieItem.fromMovie(Movie.SimpleMovieDb.movies.head)
      for {
        _ <- movieDynamoService.put(movieItem)
        getResult <- movieDynamoService.findItemByCompositeKey(
          movieItem.partKey,
          movieItem.rangeKey)
      } yield {
        assert(getResult.isInstanceOf[Option[MovieItem]])
        assert(getResult.map(_.title).contains(movieItem.title))
      }
    }

    it("Should return none when no item exists for query") {
      for {
        getResult <- movieDynamoService.findItemByCompositeKey(
          "bogus key",
          "another bogus key")
      } yield {
        assert(getResult.isEmpty)
      }
    }

    it("Should find movies by name") {
      val movieItem1 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(0))
      val movieItem2 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(1))
      val movieItem3 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(2))
      val movieItem4 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(3))
      for {
        _ <- movieDynamoService.put(movieItem1)
        _ <- movieDynamoService.put(movieItem2)
        _ <- movieDynamoService.put(movieItem3)
        _ <- movieDynamoService.put(movieItem4)
        result <- movieDynamoService.scan
        searchResults <- movieDynamoService.findMoviesByName("The Godfather")
      } yield {
        println(result.toString)
        assert(searchResults.length == 2)
      }
    }

  }
}
