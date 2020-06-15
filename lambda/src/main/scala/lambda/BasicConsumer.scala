package lambda

import java.util.function.Consumer

import scala.collection.mutable.ListBuffer

class BasicConsumer[T] extends Consumer[T] {
  val listBuffer: ListBuffer[T] = ListBuffer[T]()

  override def accept(t: T): Unit = listBuffer += t

  def getList = listBuffer.toList
}
