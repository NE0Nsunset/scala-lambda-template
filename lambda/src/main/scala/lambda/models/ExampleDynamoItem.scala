package lambda.models

import java.util.Date

case class ExampleDynamoItem(partKey: String,
                             rangeKey: String,
                             createdAt: String,
                             lastUpdate: String,
                             // ^^ DynamoItem required fields ^^//
                             name: String,
                             description: String)
    extends DynamoItem {}

object ExampleDynamoItem {
  val prefix: String = "ExampleItem#"
}
