package lambda.PageLevel
import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import org.scalajs.dom.raw.{Event, HTMLFormElement, HTMLInputElement, Node}
import autowire._
import com.thoughtworks.binding.Binding.Vars
import lambda.AjaxClient
import lambda.api.MovieApiWithDynamo
import lambda.models.MovieItem
import lambda.serialization.Picklers._
import scalaz.std.list._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DynamoExamples extends PageComponent {

  val moviesLoaded: Vars[MovieItem] = Vars.empty[MovieItem]

  val loadMovies = FutureBinding {
    for {
      movies <- AjaxClient[MovieApiWithDynamo].retrieveAllMovies().call()
    } yield {
      moviesLoaded.value ++= movies
    }
    Future {}
  }

  def handleSubmit(e: Event) = {
    e.preventDefault()
    val form: HTMLFormElement = e.target.asInstanceOf[HTMLFormElement]
    val title = form.elements(0).asInstanceOf[HTMLInputElement].value
    val year = form.elements(1).asInstanceOf[HTMLInputElement].value
    val description = form.elements(2).asInstanceOf[HTMLInputElement].value
    val imageUrl = form.elements(3).asInstanceOf[HTMLInputElement].value
    val movie = MovieItem.createMovie(title, year.toInt, description, imageUrl)

    AjaxClient[MovieApiWithDynamo].putMovie(movie).call() map {
      if (_)
        moviesLoaded.value += movie
    }
  }
  @dom def movieForm: Binding[Node] =
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

  @dom override def render: Binding[Node] = {
    <div class="section no-pad-bot" id="index-banner">
      <div class="container">
          <div class="row">
            <div class="col s12">
              {movieForm.bind}
              </div>
          </div>
        <div class="row">
          <div class="col s12">
            {moviesLoaded.bind.toList map { movie =>
              <div class="col s4">
                <p>{movie.title}</p>
                <p>{movie.year.toString}</p>
                <p>{movie.description}</p>
                <img src={movie.thumbnail} style="width:200px;"></img>
              </div>
            }}
          </div>
        </div>
      </div>
    </div>
  }
}
