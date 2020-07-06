package lambda

import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.BindingSeq
import org.lrng.binding.html.NodeBinding
import org.scalajs.dom.raw.Node

import scala.xml.{Comment, Elem, NodeBuffer}

trait IDEHelpers {
  implicit def makeIntellijHappy(e: Elem): Binding[Node] = ???
  implicit def makeIntellijHappy(e: Comment): Binding[Node] = ???
  implicit def makeIntellijHappy(e: List[Elem]): List[NodeBinding[Node]] = ???
  implicit def makeIntellijHappy(
      e: BindingSeq[Elem]): Binding[BindingSeq[Node]] = ???
  implicit def makeIntellijHappy(o: Binding[Object]): Binding[Node] = ???
  implicit def makeIntellijHappy(b: NodeBuffer): BindingSeq[Node] = ???
}
