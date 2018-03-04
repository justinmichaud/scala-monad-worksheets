import Monad._
import MonadTransformer._
import RedditHelpers._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

implicit val format = DefaultFormats
implicit val ec: ExecutionContext = ExecutionContext.global

// Our helper from before
object RedditHelpers {
  import scala.concurrent.Future
  import scalaj.http.Http

  final case class Post(title: String)

  def makeFrontpageRequest: Future[String] = Future {
    Http("https://www.reddit.com/.json")
      .asString.body
  }
}

// A typeclass to represent a "monad". Whenever we want
// to make this work with a new monad, we must simply
// provide an implicit def like those below. To accept one,
// we ask for an implicit Monad[...]
object Monad {
  // Coll[_] just means that we want a higher-kinded type (ex: Option)
  // That is, we want Option and not Option[T]
  sealed trait Monad[Coll[_]] {
    def map[A,B](a: Coll[A], b: A=>B): Coll[B]
    def flatMap[A,B](a: Coll[A], b: A=>Coll[B]): Coll[B]
    def point[A](a: A): Coll[A]
  }

  implicit def futureMonad = new Monad[Future] {
    override def map[A,B](a: Future[A], b: A => B) = a.map(b)
    override def flatMap[A,B](a: Future[A], b: A => Future[B]) = a.flatMap(b)
    override def point[A](a: A) = Future.successful(a)
  }

  implicit def tryMonad = new Monad[Try] {
    override def map[A,B](a: Try[A], b: A => B) = a.map(b)
    override def flatMap[A,B](a: Try[A], b: A => Try[B]) = a.flatMap(b)
    override def point[A](a: A) = Success(a)
  }
}

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

def wrapInFutureTryT[A](v: => A) =
  MonadTransformer.TryT(Future.successful(Try(v)))

def fromA[Monad1[_], A](v: Monad1[A])(implicit m1: Monad.Monad[Monad1]) =
  MonadTransformer.TryT[Monad1, A](m1.map[A,Try[A]](v, x => Success(x)))

// TryT lets us stack another monad onto Try. We can
// convert all of our values into a Future[Try[A]],
// and let TryT handle the unwrapping for us!
val result2: TryT[Future, Post] = for {
  body <- fromA(makeFrontpageRequest)
  json <- wrapInFutureTryT(parse(body))
  posts <- wrapInFutureTryT(json \ "data" \ "children")
  post <- wrapInFutureTryT(posts.children.head)
  postData <- wrapInFutureTryT(post \ "data" \ "title")
  title <- wrapInFutureTryT(postData.extract[String])
  post = Post(title)
} yield post

val result3: Future[Try[Post]] = result2.value

println("This code should not block until Await")

Await.result(result3, 10.seconds)