package lambda

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import wvlet.airframe._

class ClientConfig {
  private val config = js.Dynamic.global.window.clientConfig

  def getBackendApiUrl = config.backendApi.asInstanceOf[String]

  def getStaticUrl = config.staticUrl.asInstanceOf[String] + "/"

  def getStageName: String = config.apiStage.asInstanceOf[String]
}

trait UsesClientConfig {
  val clientConfig = bind[ClientConfig]
}
