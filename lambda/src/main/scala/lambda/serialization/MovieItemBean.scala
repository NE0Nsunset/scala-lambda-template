package lambda.serialization
import scala.beans.BeanProperty
import lambda.models.MovieItem
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

@DynamoDbBean
class MovieItemBean extends DynamoItemBean[MovieItem] {

  @BeanProperty var title: String = _
  @BeanProperty var year: Int = _
  @BeanProperty var description: String = _
  @BeanProperty var thumbnail: String = _

  def apply(): MovieItemBean = new MovieItemBean()

  def toItem: MovieItem = MovieItem(getPartKey, getRangeKey, getCreatedAt, getLastUpdate, getTitle, getYear, getDescription, getThumbnail)

  override def applyFromItem(r: MovieItem): Unit = {
    super.applyFromItem(r)
    setTitle(r.title)
    setYear(r.year)
    setDescription(r.description)
    setThumbnail(r.thumbnail)
  }
}
