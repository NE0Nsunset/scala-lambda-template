package lambda.api

import lambda.{SharedApi, SharedClass}

object SharedApiImpl extends SharedApi {
  override def doThing(sharedClass: SharedClass): (String, String) =
    (sharedClass.description, "yes")
}
