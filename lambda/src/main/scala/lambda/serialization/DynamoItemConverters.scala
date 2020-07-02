package lambda.serialization

import lambda.models.{BlogItem, DynamoItem, MovieItem}

object DynamoItemConverters {
  implicit def dynamoItembean2DynamoItem[T <: DynamoItem](
      dib: DynamoItemBean[T]): T = dib.toItem

  implicit def blogItem2BlogItemBean(bi: BlogItem): BlogItemBean = {
    val b = new BlogItemBean
    b.applyFromItem(bi)
    b
  }

  implicit def movieItem2MovieItembean(mi: MovieItem): MovieItemBean = {
    val mib = new MovieItemBean
    mib.applyFromItem(mi)
    mib
  }

  implicit def convListDIB2DI[S <: DynamoItemBean[_], T <: DynamoItem](
      input: List[S])(implicit c: S => T): List[T] =
    input map c

  implicit def convListDI2DIB[S <: DynamoItem, T <: DynamoItemBean[_]](
      input: List[S])(implicit c: S => T): List[T] =
    input map c
}
