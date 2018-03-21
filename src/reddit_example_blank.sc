import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scalaj.http._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Try

implicit val ec: ExecutionContext = ExecutionContext.global
implicit val format = DefaultFormats

final case class Post(title: String, subreddit: String, domain: String)

def makeFrontpageRequest: Future[String] = Future {
  Http("https://www.reddit.com/.json")
    .asString.body
}

def makeFrontpageRequestError: Future[String] = Future {
  "\"data\": {\"children\":[]}"
}

def parsePost(body: JValue): Try[Post] = {
  for {
    postData <- Try(body \ "data")
    post <- Try(postData.extract[Post])
  } yield post
}

def parsePosts(body: String): Try[List[Post]] = {
  for {
    json <- Try(parse(body))
    posts <- Try(json \ "data" \ "children")
    posts <- Try(posts.children)
  } yield posts.flatMap(parsePost(_).toOption)
}

val result = for {
  body <- makeFrontpageRequest
} yield
  for {
    posts <- parsePosts(body)
    post <- Try(posts.head)
  } yield post

println("Should not block until await")

Await.result(result, 10.seconds)
