import Monad._
import MonadTransformer._
import RedditHelpers._
import org.json4s.{DefaultFormats, JValue}
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
  // Creates a new monad that has the characteristics of Try + Monad1
  final case class TryT[Monad1[_], A](base: Monad1[Try[A]]) {
    def map[B](f: A=>B)
              (implicit m: Monad[Monad1])
    : TryT[Monad1, B] =
      flatMap(x => TryT(m.point(Success(f(x)))))


    def flatMap[B](f: A=>TryT[Monad1, B])
                  (implicit m: Monad[Monad1])
    : TryT[Monad1, B] =
      TryT(m.flatMap[Try[A],Try[B]](base, {
        case Success(v) => f(v).base
        case Failure(e) => m.point(Failure(e))
      }))
  }

  object TryT {
    def fromTry[Monad1[_], A](v: Try[A])(implicit m1: Monad[Monad1]): TryT[Monad1, A] = TryT(m1.point(v))
    def fromA[Monad1[_], A](v: Monad1[A])(implicit m1: Monad[Monad1]): TryT[Monad1, A] = TryT(m1.map[A,Try[A]](v, x => Success(x)))
  }
}

// TryT will unwrap our final value for us
val result2: TryT[Future, Post] = for {
  body <- TryT.fromA(makeFrontpageRequest)
  json <- TryT.fromTry[Future, JValue](Try(parse(body)))
  posts <- TryT.fromTry[Future, JValue](Try(json \ "data" \ "children"))
  post <- TryT.fromTry[Future, JValue](Try(posts.children.head))
  postData <- TryT.fromTry[Future, JValue](Try(post \ "data" \ "title"))
  title <- TryT.fromTry[Future, String](Try(postData.extract[String]))
  post = Post(title)
} yield post


println("This code should not block until Await")

Await.result(result2.base, 10.seconds)