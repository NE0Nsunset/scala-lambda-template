package lambda.blog

import lambda.api.BlogApi
import lambda.models.BlogItem

import scala.concurrent.Future

class BlogApiImpl(blogService: BlogService) extends BlogApi {

  def findByDateAndSlug(year: Int,
                        month: Int,
                        date: Int,
                        slug: String): Future[Option[BlogItem]] =
    blogService.findByDateAndSlug(year, month, date, slug)

  def getNBlogs(n: Int): Future[List[BlogItem]] = blogService.getNBlogs(n)
}
