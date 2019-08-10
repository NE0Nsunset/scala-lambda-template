package lambda.serialization

import lambda.models.{DynamoItem, ExampleDynamoItem}
import play.api.libs.json.{Json, OWrites}
import play.api.libs.json._

object Serializer {
  implicit val edynamoImplicitWrites: OWrites[ExampleDynamoItem] =
    Json.writes[ExampleDynamoItem]
  implicit val edynamoImplicitReads = Json.reads[ExampleDynamoItem]

  implicit val exampleDynamoItemFormat = Json.format[ExampleDynamoItem]

  // TODO write custom formatter for metadata case class
}
