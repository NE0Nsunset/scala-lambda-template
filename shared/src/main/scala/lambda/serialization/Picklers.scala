package lambda.serialization

import lambda.{Movie, SharedClass}
import upickle.default.{macroRW, ReadWriter => RW}

object Picklers {
  implicit val sharedClassPickler: RW[SharedClass] = macroRW
  implicit val moviePickler: RW[Movie] = macroRW
}
