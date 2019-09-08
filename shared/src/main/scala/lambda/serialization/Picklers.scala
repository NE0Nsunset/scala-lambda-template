package lambda.serialization

import lambda.SharedClass
import lambda.models.{BlogItem, Movie, MovieItem}
import upickle.default.{macroRW, ReadWriter => RW}

object Picklers {
  implicit val sharedClassPickler: RW[SharedClass] = macroRW
  implicit val moviePickler: RW[Movie] = macroRW
  implicit val movieItemPickler: RW[MovieItem] = macroRW
  implicit val blogItemPickler: RW[BlogItem] = macroRW
}
