package lambda.models
import java.util
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import scala.collection.JavaConverters._
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/**
  *
  * @param partKey
  * @param rangeKey
  * @param createdAt
  * @param lastUpdate
  * @param title - Blog Title
  * @param slug - A short URL friendly title
  * @param shortDescription - Short description
  * @param body
  */
case class BlogItem(partKey: String,
                    rangeKey: String,
                    createdAt: String,
                    lastUpdate: String,
                    title: String,
                    slug: String,
                    shortDescription: String,
                    body: String)
    extends DynamoItem {

  override def itemToAttributeMap: util.Map[String, AttributeValue] = {
    Map(
      "partKey" -> AttributeValue.builder().s(partKey).build(),
      "rangeKey" -> AttributeValue.builder().s(rangeKey).build(),
      "createdAt" -> AttributeValue.builder().s(createdAt).build(),
      "lastUpdate" -> AttributeValue.builder().s(lastUpdate).build(),
      "title" -> AttributeValue.builder().s(title).build(),
      "slug" -> AttributeValue.builder().s(slug).build(),
      "shortDescription" -> AttributeValue
        .builder().s(shortDescription).build(),
      "body" -> AttributeValue.builder().s(body).build(),
    ).asJava
  }
}

object BlogItem {
  val partitionKey = "blog-item"

  def rangeKeyString(date: LocalDate, slug: String) = {
    s"${date.format(ISO_LOCAL_DATE)}#$slug"
  }

  def fromDateTitleString(date: LocalDateTime,
                          title: String,
                          slug: String,
                          shortDescription: String,
                          body: String): BlogItem = {
    BlogItem(
      partitionKey,
      rangeKeyString(date.toLocalDate, slug), // TODO slugify
      date.format(ISO_LOCAL_DATE_TIME),
      date.format(ISO_LOCAL_DATE_TIME),
      title,
      slug,
      shortDescription,
      body
    )
  }

  def fromAttributeMap(av: Map[String, AttributeValue]): BlogItem = {
    BlogItem(
      av("partKey").s(),
      av("rangeKey").s(),
      av("createdAt").s(),
      av("lastUpdate").s(),
      av("title").s(),
      av("slug").s(),
      av("shortDescription").s(),
      av("body").s()
    )
  }
}
