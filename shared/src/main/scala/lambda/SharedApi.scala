package lambda

// shared API interface
trait SharedApi {
  def doThing(sharedClass: SharedClass): (String, String)
}
