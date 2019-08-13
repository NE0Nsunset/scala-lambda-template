package lambda

import com.typesafe.config.{Config, ConfigFactory}

trait Configurable {
  val config: Config
}
