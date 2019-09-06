package lambda

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import wvlet.airframe._

class ClientConfig {
  private val config = js.Dynamic.global.window.clientConfig
  lazy val backendApiUrl = config.backendApi.asInstanceOf[String]
}

trait UsesClientConfig {
  val clientConfig = bind[ClientConfig]
}
