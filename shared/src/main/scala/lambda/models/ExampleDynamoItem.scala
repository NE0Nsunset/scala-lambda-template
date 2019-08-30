//package lambda.models
//
//import java.sql.Date
//
//case class ExampleDynamoItem(partKey: String,
//                             rangeKey: String,
//                             createdAt: String,
//                             lastUpdate: String,
//                             // ^^ DynamoItem required fields ^^//
//                             name: String,
//                             description: String)
//    extends DynamoItem {}
//
//object ExampleDynamoItem {
//  // Simple partition key that groups all ExampleItems together as a collection within the table
//  // Your usage pattens may require something different
//  val defaultPartKey: String = "ExampleItem"
//
//  // For more complex usage patterns, this could be used as a sort key that prefixes the range
//  // to filter by within the collection
//  // partKey and rangeKey combined must be unique
//  val rangePrefix: String = ""
//
//  // Factory that automatically applies our defaulPartKey and rangePrefix
//  def apply(rangeKey: String,
//            createdAt: Date,
//            lastUpdate: Date,
//            name: String,
//            description: String): ExampleDynamoItem = {
//    new ExampleDynamoItem(defaultPartKey,
//                          rangePrefix + rangeKey,
//                          createdAt.toLocaleString,
//                          lastUpdate.toLocaleString,
//                          name,
//                          description)
//  }
//}
