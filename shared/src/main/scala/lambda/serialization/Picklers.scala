package serialization

import boopickle._
import lambda.SharedClass

object Picklers
    extends Base
    with BasicImplicitPicklers
    with TransformPicklers
    with TuplePicklers
    with MaterializePicklerFallback {
  import boopickle.Default._

  implicit val playerPickler = generatePickler[SharedClass]

}
