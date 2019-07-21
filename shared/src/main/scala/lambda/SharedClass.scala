package lambda

case class SharedClass(name: String, description: String) {
  def display: String = {
    name + " " + description
  }
}
