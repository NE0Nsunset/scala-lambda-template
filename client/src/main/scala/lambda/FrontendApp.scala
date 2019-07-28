package lambda

import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExport
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import autowire._
import lambda.api.{AnotherApiExample, SharedApi}
import lambda.serialization.Picklers._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.std.option._

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
object FrontendApp extends js.JSApp {

  val simpleApiFuture: Var[Option[FutureBinding[SharedClass]]] = Var(None)
  val movieApiExample: Var[Option[FutureBinding[Option[Movie]]]] = Var(None)
  val clientConfig = new ClientConfig

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
    val f = AjaxClient[SharedApi]
      .doThing(n, d)
      .call()
    simpleApiFuture.value = Some(FutureBinding(f))
  }

  def sendMovieApiExampleRequest(): Unit = {
    val movieTitle: String =
      document.getElementById("movieTitle").asInstanceOf[HTMLInputElement].value
    val f = AjaxClient[AnotherApiExample].findMovieByName(movieTitle).call()
    movieApiExample.value = Some(FutureBinding(f))
  }

  @dom def navBar: Binding[Node] =
    <nav class="blue-grey darken-2" data:role="navigation">
      <div class="nav-wrapper container">
        <a href="#" class="brand-logo"><span class="grey-text text-lighten-2">Lambda Scala Project!</span></a>
        <ul id="nav-mobile" class="right hide-on-med-and-down"></ul>
      </div>
    </nav>

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
      </div>
    </div>

  @dom def appLayout: Binding[BindingSeq[Node]] = {
    Constants(navBar.bind, render.bind)
  }

  @JSExport
  def main(): Unit = {
    dom.render(document.getElementById("lambdaapp"), appLayout)
  }
}
