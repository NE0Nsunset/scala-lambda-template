package lambda.models

import java.util.Date

case class ExampleDynamoItem(name: String,
                             description: String,
                             createdAtDate: Date,
                             lastUpdateDate: Date)
    extends DynamoItem {
  override val createdAt: String = createdAtDate.toGMTString
  override val lastUpdate: String = lastUpdateDate.toGMTString
  override val partKey: String = name
  override val rangeKey: String = description.hashCode.toString
}
