package lambda.seed

import java.time.LocalDateTime

import lambda.models.BlogItem

object SeedObjects {
  val blogEntryDate = LocalDateTime.of(2019, 4, 7, 10, 0)
  val blogEntry =
    BlogItem.fromDateTitleString(blogEntryDate,
                                 "Test Blog",
                                 "test-blog",
                                 "a short description",
                                 "blog body")

  val seeds: List[BlogItem] = List(blogEntry)
}
