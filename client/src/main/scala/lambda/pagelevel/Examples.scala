package lambda.pagelevel
import com.thoughtworks.binding.{Binding, FutureBinding}
import org.scalajs.dom.raw.{Event, HTMLFormElement, HTMLInputElement, Node}
import autowire._
import com.thoughtworks.binding.Binding.{Var, Vars}
import lambda.{IDEHelpers, SharedClass, UsesAjaxClient}
import lambda.api.{AnotherApiExample, MovieApiWithDynamo, SharedApi}
import lambda.models.{Movie, MovieItem}
import lambda.serialization.Picklers._
import org.lrng.binding.html
import org.scalajs.dom.document
import scalaz.std.list._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import wvlet.airframe._

import scala.util.{Failure, Success}
import scalaz.std.option._
import scalaz.std.list._

class Examples extends PageComponent with UsesAjaxClient with IDEHelpers {
  val simpleApiFuture: Var[FutureBinding[SharedClass]] = Var(
    FutureBinding(Future { SharedClass("", "") }))
  val movieApiExample: Var[FutureBinding[Option[Movie]]] = Var(
    FutureBinding(Future { None }))
  val moviesLoaded: Vars[MovieItem] = Vars.empty[MovieItem]

  val loadMovies = FutureBinding {
    for {
      movies <- ajaxClient[MovieApiWithDynamo].retrieveAllMovies().call()
    } yield {
      moviesLoaded.value ++= movies
    }
  }

  @html def simpleApiIsLoading: Binding[Boolean] = Binding apply {
    simpleApiFuture.bind.bind match {
      case Some(Success(_)) | None => false
      case None                    => true
      case Some(Failure(_))        => false
    }
  }

  @html def movieApiIsLoading: Binding[Boolean] = Binding apply {
    movieApiExample.bind.bind match {
      case Some(Success(_)) | None => false
      case None                    => true
      case Some(Failure(_))        => false
    }
  }

  def handleSubmit(e: Event) = {
    e.preventDefault()
    val form: HTMLFormElement = e.target.asInstanceOf[HTMLFormElement]
    val title = form.elements(0).asInstanceOf[HTMLInputElement].value
    val year = form.elements(1).asInstanceOf[HTMLInputElement].value
    val description = form.elements(2).asInstanceOf[HTMLInputElement].value
    val imageUrl = form.elements(3).asInstanceOf[HTMLInputElement].value
    val movie = MovieItem.createMovie(title, year.toInt, description, imageUrl)

    ajaxClient[MovieApiWithDynamo].putMovie(movie).call() map {
      if (_)
        moviesLoaded.value += movie
    }
  }

  def sendSimpleApiRequest(): Unit = {
    val n: String =
      document.getElementById("name").asInstanceOf[HTMLInputElement].value
    val d: String = document
      .getElementById("description")
      .asInstanceOf[HTMLInputElement]
      .value
    val f = ajaxClient[SharedApi]
      .doThing(n, d)
      .call()
    simpleApiFuture.value = FutureBinding(f)
  }

  def sendMovieApiExampleRequest(): Unit = {
    val movieTitle: String =
      document.getElementById("movieTitle").asInstanceOf[HTMLInputElement].value
    val f = ajaxClient[AnotherApiExample].findMovieByName(movieTitle).call()
    movieApiExample.value = FutureBinding(f)
  }

  @html def movieForm: Binding[Node] =
    <div class="row">
      <form class="col s12" onsubmit={e: Event => handleSubmit(e)}>
        <div class="row">
          <div class="input-field col s6">
            <input placeholder="" id="title" type="text" class="validate" />
            <label for="first_name">Title</label>
          </div>

          <div class="input-field col s6">
            <input placeholder="" id="year" type="text" class="validate" />
            <label for="first_name">Year</label>
          </div>

          <div class="input-field col s6">
            <input placeholder="" id="description" type="text" class="validate" />
            <label for="first_name">Description</label>
          </div>

          <div class="input-field col s6">
            <input placeholder="" id="thumbnail" type="text" class="validate" />
            <label for="first_name">Image URL</label>
          </div>

        </div>
        <button class="btn waves-effect waves-light" type="submit">
          Submit<i class="material-icons right">send</i>
        </button>
      </form>
    </div>

  @html def simpleExampleRender: Binding[Node] =
    <div class="container">
      <div class="row">
        <div class="col s12 m6">
          <div class="icon-block">
            <h5 class="center">Simple API Example</h5>
            <p class="light">This example takes sends your input to the backend and it sends back an instance of the shared class lambda.SharedClass </p>
            <div class="row">
              <div class="input-field col s6">
                <input id="name" type="text" class="validate" />
                <label for="first_name">Name</label>
              </div>
              <div class="input-field col s6">
                <input id="description" type="text" class="validate" />
                <label for="last_name">Description</label>
              </div>
              <a href="javascript:void(0)" class="btn-large waves-effect waves-light orange" onclick={_:Event => sendSimpleApiRequest()}>Try it!</a>
            </div>
          </div>
        </div>
        <div class="col s12 m6">
          <div class="icon-block">
            {
            if (simpleApiIsLoading.bind)
              <div class="progress">
                <div class="indeterminate"></div>
              </div>
            else
              <!-- -->
            }
            {
            simpleApiFuture.bind.bind match {
              case Some(Success(sharedClass)) => <div>{sharedClass.toString}</div>
              case _ => <div>>{simpleApiFuture.bind.bind.toString}</div>
            }
            }
          </div>
        </div>
      </div>
    </div>

  @html def movieApiExampleRender: Binding[Node] =
    <div class="container">
      <div class="row">

        <div class="col s12 m6">
          <div class="icon-block">
            <h5 class="center">AnotherApiExample</h5>
            <p class="light">
              In this example we rely on hardcoded values for movies in lambda.models.Movie.
              Search input is sent to the backend were it tries to find a matching record that begins with your input.
              Try searching for "Shawshank" or "Godfather".
              Then, take a look at lambda/lambda.api.AnotherApiExampleImpl for other searches you can make
            </p>
            <div class="row">
              <div class="input-field col s6">
                <input id="movieTitle" type="text" class="validate" />
                <label for="movieTitle">Movie Title</label>
              </div>
              <a href="javascript:void(0)" class="btn-large waves-effect waves-light orange" onclick={_:Event => sendMovieApiExampleRequest()}>Search Movie!</a>
            </div>
          </div>
        </div>

        <div class="col s12 m6">
          {
          if (movieApiIsLoading.bind)
            <div class="progress">
              <div class="indeterminate"></div>
            </div>
          else
            <!-- -->
          }
          {
          movieApiExample.bind.bind match {
            case Some(Success(Some(movie))) =>
              <div>
                {movie.title}<br />
                {movie.year.toString}<br />
                <p>{movie.description}</p>
                <p><img src={movie.thumbnail}/></p>
              </div>
            case _ => <div>>{movieApiExample.bind.bind
              .toString}</div>
          }}

        </div>
      </div>
    </div>

  @html def movieApiWithDynamoExample: Binding[Node] = {
    <div class="container">
      <div class="row">
        <div class="col s12">
          <div class="icon-block">
            <h5 class="center">MovieApiWithDynamo</h5>
            <p class="light">Finally, the last example shows the results of retrieving movie items from DynamoDb. The form to submit has been disabled. Movie records should show below.</p>
          </div>
          {
          //movieForm.bind
          <!-- form disable, uncomment in source code to try locally -->
          }
        </div>
      </div>
      <div class="row">
        <div class="col s12">
          {moviesLoaded.map { movie: MovieItem =>
          <div class="col s4">
            <p>{movie.title}</p>
            <p>{movie.year.toString}</p>
            <p>{movie.description}</p>
            <img src={movie.thumbnail} style="width:200px;"></img>
          </div>
        }}
          {if (moviesLoaded.isEmpty.bind)
          <p>No movies loaded in database</p>
        else
          <!-- -->
          }
        </div>
      </div>
    </div>
  }

  @html override def render: Binding[Node] = {
    <div class="section no-pad-bot" id="index-banner">
      {simpleExampleRender.bind}
      <br/><br/>
      {movieApiExampleRender.bind}
      <br/><br/>
      {movieApiWithDynamoExample.bind}
    </div>
  }
}
