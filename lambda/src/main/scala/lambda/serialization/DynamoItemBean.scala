package lambda.serialization

import lambda.models.DynamoItem
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.{DynamoDbBean, DynamoDbPartitionKey, DynamoDbSecondaryPartitionKey, DynamoDbSortKey}

import scala.beans.BeanProperty

@DynamoDbBean
abstract class DynamoItemBean[R <: DynamoItem] {
  var partKey: String = _
  var rangeKey: String = _

  @BeanProperty var createdAt: String = _
  @BeanProperty var lastUpdate: String = _

  @DynamoDbPartitionKey
  def getPartKey = this.partKey
  def setPartKey(id: String) = this.partKey = id

  @DynamoDbSortKey
  @DynamoDbSecondaryPartitionKey(indexNames = Array("rangekeyIndex"))
  def getRangeKey = this.rangeKey
  def setRangeKey(r: String) = this.rangeKey = r

  /**
    * Extend to
    */
  def applyFromItem(r: R): Unit = {
    setPartKey(r.partKey)
    setRangeKey(r.rangeKey)
    setCreatedAt(r.createdAt)
    setLastUpdate(r.lastUpdate)
  }

  def toItem: R
}
