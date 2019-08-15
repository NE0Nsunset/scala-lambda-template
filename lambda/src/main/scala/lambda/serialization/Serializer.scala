package lambda.serialization

import lambda.models.{DynamoItem, ExampleDynamoItem, MovieItem}
import play.api.libs.json.{Json, OWrites}
import play.api.libs.json._

object Serializer {
  implicit val movieReads = Json.reads[MovieItem]
  implicit val movieWrites = Json.writes[MovieItem]
  implicit val movieFormat = Json.format[MovieItem]

  // TODO write custom formatter to flatten metadata case class for dynamo
}
