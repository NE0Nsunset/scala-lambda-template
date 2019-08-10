import lambda.models.ExampleDynamoItem

object TestObjects {
  val exampleDynamoItem1 = ExampleDynamoItem(
    "somerandompartkey",
    "arandomrangekey",
    new java.util.Date().toLocaleString,
    new java.util.Date().toLocaleString,
    "A name field with stuff",
    "A description field with stuff"
  )
}
