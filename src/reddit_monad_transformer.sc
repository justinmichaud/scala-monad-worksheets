import org.json4s.{DefaultFormats, JValue}
import org.json4s.jackson.JsonMethods.parse

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

implicit val format = DefaultFormats
implicit val ec: ExecutionContext = ExecutionContext.global

// Our helpers from before
object RedditHelpers {
  import scala.concurrent.Future
  import scalaj.http.Http

  final case class Post(title: String)

  def getFrontpageJson(): Future[String] = Future {
    Http("https://www.reddit.com/.json")
      .asString.body
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
}
import RedditHelpers._

// A typeclass to represent a "monad". Whenever we want
// to make this work with a new monad, we must simply
// provide an implicit def like those below. To accept one,
// we ask for an implicit Monad[...]
object Monad {
  // Coll[_] just means that we want a higher-kinded type (ex: Option)
  // That is, we want Option and not Option[T]
  sealed trait Monad[Coll[_]] {
    def map[A,B](a: Coll[A], b: A=>B): Coll[B] = flatMap(a, (x: A) => point(b(x)))
    def flatMap[A,B](a: Coll[A], b: A=>Coll[B]): Coll[B]
    def point[A](a: A): Coll[A]
  }

  implicit def futureMonad = new Monad[Future] {
    override def flatMap[A,B](a: Future[A], b: A => Future[B]) = a.flatMap(b)
    override def point[A](a: A) = Future.successful(a)
  }

  implicit def tryMonad = new Monad[Try] {
    override def flatMap[A,B](a: Try[A], b: A => Try[B]) = a.flatMap(b)
    override def point[A](a: A) = Success(a)
  }
}
import Monad._

object MonadTransformer {
  // Creates a new monad that has the characteristics
  // of Try + Monad1
  case class TryT[Monad1[_], A](value: Monad1[Try[A]])
                               (implicit m: Monad[Monad1]) {
    def map[B](f: A=>B): TryT[Monad1, B] =
      flatMap(x => TryT(m.point(Success(f(x)))))

    def flatMap[B](f: A=>TryT[Monad1, B]): TryT[Monad1, B] =
      TryT(m.flatMap[Try[A],Try[B]](value, {
        case Success(v) => f(v).value
        case Failure(e) => m.point(Failure(e))
      }))
  }
}
import MonadTransformer._

// Problem: we want all of our composing building blocks to have
// the flexibility to run at some time in the future (Future), and
// to be fallible (Try).
// We can change our steps to produce a TryT[Future, _] instead, letting
//  them use functionality from both monads

def makeFrontpageRequest(): TryT[Future, String] = {
  TryT[Future, String](getFrontpageJson().map(Success(_)))
}

def parseFrontpagePosts(posts: String): TryT[Future, List[Post]] = {
  TryT(Future.successful(parsePosts(posts)))
}

// Then, TryT makes composition very simple!
val result2: TryT[Future, Post] = for {
  body <- makeFrontpageRequest
  posts <- parseFrontpagePosts(body)
  post <- TryT(Future.successful(Try(posts.head)))
} yield post

val result3: Future[Try[Post]] = result2.value

println("This code should not block until Await")

Await.result(result3, 10.seconds)