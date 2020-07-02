package lambda.pagelevel

import com.thoughtworks.binding.Binding
import org.scalajs.dom.document
import org.scalajs.dom.raw.Node

trait PageComponent {
  def render: Binding[Node]
  def onCreate: Unit = {

    var instance = scalajs.js.Dynamic.global.M.AutoInit()
  }

  def onDestroy: Unit = {
    var elem = document.querySelector("#mobile-demo")
    var instance = scalajs.js.Dynamic.global.M.Sidenav.getInstance(elem)
    instance.close()
    instance.destroy()
  }
}
