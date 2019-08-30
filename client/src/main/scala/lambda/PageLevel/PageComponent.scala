package lambda.PageLevel

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.Node

trait PageComponent {
  def render: Binding[Node]
  def reinit: Unit = Unit
}
