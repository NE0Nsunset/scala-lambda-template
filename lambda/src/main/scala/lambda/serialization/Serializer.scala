package lambda.serialization

import lambda.models.{BlogItem, DynamoItem, MovieItem}
import play.api.libs.json.{Json, OWrites}
import play.api.libs.json._
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.collection.JavaConverters._

object Serializer {
  implicit val movieReads = Json.reads[MovieItem]
  implicit val movieWrites = Json.writes[MovieItem]
  implicit val movieFormat = Json.format[MovieItem]

  implicit val blogReads = Json.reads[BlogItem]
  implicit val blogWrites = Json.writes[BlogItem]
  implicit val blogFormat = Json.format[BlogItem]
}
