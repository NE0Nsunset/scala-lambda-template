package lambda.api

import scala.concurrent.Future

trait SimpleApi {
  def twoPlusN(n: Int): Future[(String, String)]
}
