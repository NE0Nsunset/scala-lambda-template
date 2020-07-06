package lambda.pagelevel

import com.thoughtworks.binding.Binding
import lambda.IDEHelpers
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

class EmptyPageComponent extends PageComponent with IDEHelpers {
  @html def render: Binding[Node] = <!-- -->
}
