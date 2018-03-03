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

def parsePost(body: String): Try[Post] = {
  for {
    json <- Try(parse(body))
    posts <- Try(json \ "data" \ "children")
    post <- Try(posts.children.head)
    postData <- Try(post \ "data" \ "title")
    title <- Try(postData.extract[String])
  } yield Post(title)
}

val result: Future[Try[Post]] = for {
  body <- makeFrontpageRequest
} yield {
  for {
    incident <- parsePost(body)
  } yield incident
}

println("Should not block until await")

Await.result(result, 10.seconds)
