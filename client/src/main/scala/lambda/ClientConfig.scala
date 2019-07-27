package lambda

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

class ClientConfig {
  private val config = js.Dynamic.global.window.clientConfig
  lazy val backendApiUrl = config.backendApi.asInstanceOf[String]
}
