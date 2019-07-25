package lambda

import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExport
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import org.scalajs.dom.raw.{Event, HTMLInputElement, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import autowire._
import lambda.api.{SharedApi, SimpleApi}
import lambda.serialization.Picklers._
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.std.option._

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
object FrontendApp extends js.JSApp {

  val simpleApiFuture: Var[Option[FutureBinding[SharedClass]]] = Var(None)

  @dom val isLoading: Binding[Boolean] =
    simpleApiFuture.bind.map(_.bind) match {
      case Some(Some(Success(_))) | None => false
      case Some(None)                    => true
      case Some(Some(Failure(_)))        => false
    }

  def sendIt(): Unit = {
    val n: String =
      document.getElementById("name").asInstanceOf[HTMLInputElement].value
    val d: String = document
      .getElementById("description")
      .asInstanceOf[HTMLInputElement]
      .value
    val f = Client[SharedApi]
      .doThing(n, d)
      .call()
    simpleApiFuture.value = Some(FutureBinding(f))
  }

  @dom def navBar: Binding[Node] =
    <nav class="blue-grey darken-2" data:role="navigation">
      <div class="nav-wrapper container">
        <a href="#" class="brand-logo"><span class="grey-text text-lighten-2">Lambda Scala Project!</span></a>
        <ul id="nav-mobile" class="right hide-on-med-and-down"></ul>
      </div>
    </nav>

  @dom def render: Binding[Node] =
    <div class="section no-pad-bot" id="index-banner">
      <div class="container">
        <br /><br />
        <h1 class="header center orange-text">Example API calls</h1>
        <br /><br />
        <div class="row">
          <div class="col s12 m4">
            <div class="icon-block">
              <h2 class="center light-blue-text"><i class="material-icons">flash_on</i></h2>
              <h5 class="center">Simple API Example</h5>
              <p class="light">!Click the button to send a request to the lambda backend. On it's return, you should see an alert with the result!  </p>
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
                case Some(Some(Success(sharedClass))) => <div>{sharedClass.display}</div>
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
                  </div>
              <a href="javascript:void(0)" class="btn-large waves-effect waves-light orange" onclick={_:Event => sendIt()}>Try it!</a>
            </div>
          </div>
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
