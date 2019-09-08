package lambda.pagelevel
import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import lambda.{SharedClass, UsesAjaxClient}
import lambda.api.{AnotherApiExample, BlogApi, SharedApi}
import org.scalajs.dom.document
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}
import com.thoughtworks.binding.Binding.Var
import lambda.models.{BlogItem, Movie}
import scala.util.{Failure, Success}
import lambda.serialization.Picklers._
import wvlet.airframe._
import autowire._
import lambda.routing.{RouteName, UsesSimpleRouter}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.std.option._
import scalaz.std.list._

import scala.concurrent.Future

class Home extends PageComponent with UsesAjaxClient with UsesSimpleRouter {
  val simpleApiFuture: Var[Option[FutureBinding[SharedClass]]] = Var(None)
  val movieApiExample: Var[Option[FutureBinding[Option[Movie]]]] = Var(None)

  val latestBlogs: FutureBinding[List[BlogItem]] = FutureBinding {
    ajaxClient[BlogApi].getNBlogs(10).call()
  }

  @dom val isLoading: Binding[Boolean] =
    simpleApiFuture.bind.map(_.bind) match {
      case Some(Some(Success(_))) | None => false
      case Some(None)                    => true
      case Some(Some(Failure(_)))        => false
    }

  @dom val isMovieApiLoading: Binding[Boolean] =
    movieApiExample.bind.map(_.bind) match {
      case Some(Some(Success(_))) | None => false
      case Some(None)                    => true
      case Some(Some(Failure(_)))        => false
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
    simpleApiFuture.value = Some(FutureBinding(f))
  }

  def sendMovieApiExampleRequest(): Unit = {
    val movieTitle: String =
      document.getElementById("movieTitle").asInstanceOf[HTMLInputElement].value
    val f = ajaxClient[AnotherApiExample].findMovieByName(movieTitle).call()
    movieApiExample.value = Some(FutureBinding(f))
  }
  @dom def simpleExampleRender: Binding[Node] =
    <div class="col s12 m4">
      <div class="icon-block">
        <h5 class="center">Simple API Example</h5>
        <p class="light">Click the button to send a request to the lambda backend. On its return, you should the result! (it's shared/lambda.SharedClass)</p>
        {
        if (isLoading.bind)
          <div class="progress">
            <div class="indeterminate"></div>
          </div>
        else
          <!-- -->
        }
        {
        simpleApiFuture.bind.map(_.bind) match {
          case Some(Some(Success(sharedClass))) => <div>{sharedClass.toString}</div>
          case _ => <div>>{simpleApiFuture.bind.map(_.bind).toString}</div>
        }
        }
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

  @dom def movieApiExampleRender: Binding[Node] =
    <div class="col s12 m4">
      <div class="icon-block">
        <h5 class="center">shared/lambda.api.AnotherApiExample</h5>
        <p class="light">
          This time, we'll search for a movie in our simple database.
          Try searching for "Shawshank" or "Godfather".
          Then, take a look at lambda/lambda.api.AnotherApiExampleImpl for other searches you can make
        </p>
        {
        if (isMovieApiLoading.bind)
          <div class="progress">
            <div class="indeterminate"></div>
          </div>
        else
          <!-- -->
        }
        {
        movieApiExample.bind.map(_.bind) match {
          case Some(Some(Success(Some(movie)))) =>
            <div>
              {movie.title}<br />
              {movie.year.toString}<br />
              <p>{movie.description}</p>
              <p><img src={movie.thumbnail}/></p>
            </div>
          case _ => <div>>{movieApiExample.bind.map(_.bind).toString}</div>
        }
        }
        <div class="row">
          <div class="input-field col s6">
            <input id="movieTitle" type="text" class="validate" />
            <label for="movieTitle">Movie Title</label>
          </div>
          <a href="javascript:void(0)" class="btn-large waves-effect waves-light orange" onclick={_:Event => sendMovieApiExampleRequest()}>Search Movie!</a>
        </div>
      </div>
    </div>

  @dom def blogTitle(blogItems: List[BlogItem]): Binding[Node] = {
    <ul>
      {blogItems map { blogItem =>
      <li>
        <a href="" onclick={event: Event => {event.preventDefault(); simpleRouter.changeToRouteByName(RouteName.BlogDetail.entryName, BlogDetail.props(new scalajs.js.Date(blogItem.rangeKey.split("#")(0)), blogItem.slug))  } }>
          {blogItem.title}
        </a>

      </li>
      }}
    </ul>
  }

  @dom def render: Binding[Node] =
    <div class="section no-pad-bot" id="index-banner">
      <div class="container">
        <br /><br />
        <h1 class="header center orange-text">Example API calls</h1>
        <br /><br />
        <p></p>
        <div class="row">
          {simpleExampleRender.bind}
          {movieApiExampleRender.bind}
        </div>
        <div class="row">
          {latestBlogs.bind match {
          case Some(Success(xs @ head :: tail)) => blogTitle(xs).bind
          case Some(Success(Nil)) => <!-- -->
          case _ => <p>loading ...</p>
          }}
        </div>
      </div>
    </div>
}
