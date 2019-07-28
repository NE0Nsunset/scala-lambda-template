package lambda

trait AWSLogging {
  lazy val debugEnabled = System.getenv("ENABLE_DEBUG") == "true"

  def debugMessage(debugEnabled: Boolean)(message: String) = {
    if (debugEnabled)
      println(message)
  }

  lazy val logMessage: String => Unit = debugMessage(debugEnabled)
}
