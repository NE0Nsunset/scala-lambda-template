package lambda

import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.document
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import com.thoughtworks.binding.Binding.{BindingSeq, Constants}
import org.scalajs.dom.raw.{Event, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import autowire._
import lambda.api.{SharedApi, SimpleApi}
import lambda.serialization.Picklers

/**
  * Entrypoint for scala.binding / scalajs frontend
  */
object LambdaApp extends js.JSApp {
  def sendIt() = {
    val f = Client[SimpleApi].twoPlusN(1).call()
    f.map(x => println(x))
  }

  def sendIt2() = {
    val f = Client[SharedApi]
      .doThing(SharedClass("name here", "description here"))
      .call()
    f.map(x => println(x))
  }

  @dom def button: Binding[Node] =
    <div>
      <button onclick={e: Event => sendIt()}>Simple Api</button>
      <button onclick={e: Event => sendIt2()}>Shared Api</button>
    </div>

  @dom def navBar: Binding[Node] =
    <nav class="blue-grey darken-2" data:role="navigation">
      <div class="nav-wrapper container">
        <a href="#" class="brand-logo"><span class="grey-text text-lighten-2">Lambda!</span></a>
        <ul id="nav-mobile" class="right hide-on-med-and-down"></ul>
      </div>
      {button.bind}
    </nav>

  @dom def appLayout: Binding[BindingSeq[Node]] = {
    Constants(navBar.bind)
  }

  @JSExport
  def main(): Unit = {
    dom.render(document.getElementById("lambdaapp"), appLayout)
  }
}
