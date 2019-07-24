package lambda.serialization

import lambda.SharedClass
import upickle.default.{ReadWriter => RW, macroRW}

object Picklers {
  implicit val sharedClassPickler: RW[SharedClass] = macroRW
}
