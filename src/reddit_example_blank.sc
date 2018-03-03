import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scalaj.http._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Try

implicit val ec: ExecutionContext = ExecutionContext.global
implicit val format = DefaultFormats

final case class Post(title: String)

def makeFrontpageRequest: Future[String] = Future {
  Http("https://www.reddit.com/.json")
    .asString.body
}

// Produce the a post on the front page
// Tip: json \ "key" \ "key2") will extract {"key": {"key2": val}}
//     or throw otherwise
// Tip: json.children will produce a list of json values if json
//     is an array, or throw otherwise
// Tip: json.extract[Type] will attempt to turn the json into Type
// Tip: Try(expr) will produce Success if expr runs without throwing,
//     or a Failure(exception) if it throws
def parsePost(body: String): Try[Post] = {
  for {
    json <- Try(parse(body))
    ??? <- ???
    title <- ???
  } yield Post(title)
}

val result: Future[Try[Post]] = ???

println("Should not block until await")

Await.result(result, 10.seconds)
