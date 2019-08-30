package lambda.models

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/**
  * Implementors will need to come up with their own strategy
  * for how they define their partKey and rangeKey (aka sortKey)
  * see https://aws.amazon.com/blogs/database/choosing-the-right-dynamodb-partition-key/
  *
  * - partKey does not need to be unique and can group items together
  *
  * - the DynamoDb table defines an additional index on rangeKey
  *   this allows for searches when only the rangeKey is known
  *
  * - partKey and rangeKey combine to form the required composite key
  *
  * - createdAt used as metadata
  *
  * - lastUpdate used as metadata AND for optimistic locking
  */
trait DynamoItem {
  val partKey: String;
  val rangeKey: String;

  val createdAt: String;
  val lastUpdate: String;
  def itemToAttributeMap: java.util.Map[String, AttributeValue]
}
