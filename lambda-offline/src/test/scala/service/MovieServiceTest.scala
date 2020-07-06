package service

import lambda.models.{Movie, MovieItem}
import org.scalatest.BeforeAndAfterEach
import lambda.test.{TestBase, TestObjects}
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import lambda.serialization.DynamoItemConverters._

class MovieServiceTest extends TestBase with BeforeAndAfterEach {
  override def beforeEach(): Unit = {
    super.beforeEach()
    Await.result(dynamoClient.createTableIfNotExists(),
                 Duration.create(60, "seconds"))
  }

  override def afterEach(): Unit = {
    super.afterEach()
    Await.result(dynamoClient.destroyTable, Duration.create(60, "seconds"))
  }

//  describe("MovieService Tests") {
//
//    it("Should connect to dynamo") {
//      FutureConverters.toScala(
//        dynamoClient.awsClient
//          .listTables(ListTablesRequest.builder().build)) map { f =>
//        println(f.toString)
//        assert(true)
//      }
//    }
//
//    it("Should be able to insert and retrieve an item") {
//      val movieItem = MovieItem.fromMovie(Movie.SimpleMovieDb.movies.head)
//
//      for {
//        result <- movieDynamoService.put(movieItem)
//        getResult <- movieDynamoService.findItemByCompositeKey(
//          movieItem.partKey,
//          movieItem.rangeKey)
//      } yield {
//        println(getResult.toString)
//        println(result.toString)
//        assert(true)
//      }
//    }

//    it("Should return none when no item exists for query") {
//      for {
//        getResult <- movieDynamoService.findItemByCompositeKey(
//          "bogus key",
//          "another bogus key")
//      } yield {
//        assert(getResult.isEmpty)
//      }
//    }

//    it("Should find movies by name") {
//      val movieItem1 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(0))
//      val movieItem2 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(1))
//      val movieItem3 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(2))
//      val movieItem4 = MovieItem.fromMovie(Movie.SimpleMovieDb.movies(3))
//      for {
//        _ <- movieDynamoService.put(movieItem1)
//        _ <- movieDynamoService.put(movieItem2)
//        _ <- movieDynamoService.put(movieItem3)
//        _ <- movieDynamoService.put(movieItem4)
//        result <- movieDynamoService.scan
//        searchResults <- movieDynamoService.findMoviesByName("The Godfather")
//      } yield {
//        println(result.toString)
//        assert(searchResults.length == 2)
//      }
//    }

//  }
}
