package lambda.serialization

import lambda.models.BlogItem
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

import scala.beans.BeanProperty

@DynamoDbBean
class BlogItemBean extends DynamoItemBean[BlogItem] {
  @BeanProperty var title: String = _
  @BeanProperty var slug: String = _
  @BeanProperty var shortDescription: String = _
  @BeanProperty var body: String = _

  def apply(): BlogItemBean = new BlogItemBean()

  def toItem: BlogItem =
    BlogItem(getPartKey,
             getRangeKey,
             getCreatedAt,
             getLastUpdate,
             getTitle,
             getSlug,
             getShortDescription,
             getBody)

  override def applyFromItem(r: BlogItem): Unit = {
    super.applyFromItem(r)
    setTitle(r.title)
    setSlug(r.slug)
    setShortDescription((r.shortDescription))
    setBody(r.body)
  }
}
