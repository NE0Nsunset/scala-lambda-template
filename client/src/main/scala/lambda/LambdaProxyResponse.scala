package lambda

case class LambdaProxyResponse(isBase64Encoded: Boolean,
                               statusCode: Int,
                               headers: Map[String, String],
                               multiValueHeaders: Map[String, List[String]]) {}
