package lambda.api

import lambda.models.BlogItem

import scala.concurrent.Future

trait BlogApi {
  def findByDateAndSlug(year: Int,
                        month: Int,
                        day: Int,
                        slug: String): Future[Option[BlogItem]]
  def getNBlogs(n: Int): Future[List[BlogItem]]
}
