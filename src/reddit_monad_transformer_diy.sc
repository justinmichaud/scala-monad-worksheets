import Monad._
import MonadTransformer._
import RedditHelpers._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Success, Try}

implicit val ec: ExecutionContext = ExecutionContext.global

// Our helpers from before
object RedditHelpers {
  import org.json4s.DefaultFormats
  import org.json4s.jackson.JsonMethods.parse

  import scala.concurrent.Future
  import scala.util.Try
  import scalaj.http.Http

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
}

// A typeclass to represent a "monad". Whenever we want
// to make this work with a new monad, we must simply
// provide an implicit def like those below. To accept one,
// we ask for an implicit Monad[...]
object Monad {
  sealed trait Monad[Coll[_], A] {
    def map[B](a: Coll[A], b: A=>B): Coll[B]
    def flatMap[B](a: Coll[A], b: A=>Coll[B]): Coll[B]
    def point(a: A): Coll[A]
  }

  implicit def futureMonad[A] = new Monad[Future, A] {
    override def map[B](a: Future[A], b: A => B) = a.map(b)
    override def flatMap[B](a: Future[A], b: A => Future[B]) = a.flatMap(b)
    override def point(a: A) = Future.successful(a)
  }

  implicit def tryMonad[A] = new Monad[Try, A] {
    override def map[B](a: Try[A], b: A => B) = a.map(b)
    override def flatMap[B](a: Try[A], b: A => Try[B]) = a.flatMap(b)
    override def point(a: A) = Success(a)
  }
}

object MonadTransformer {
  // Scalaz has a similar type called OptionT, although
  // we will implement a simpler version here
  // See scalaz example
  sealed trait OptionT[Monad1[_], A] {
    type AoA[T] = Monad1[Monad1[T]]

    def map[B](f: A=>B)
              (implicit m: Monad[Monad1, A])
    : OptionT[Monad1, B] =
      CompositionMap[Monad1, A, B](this, f)

    def flatMap[Monad2[_], B](f: A=>OptionT[Monad2, B])
                             (implicit m: Monad[Monad1, A])
    : OptionT[({ type T[A] = Monad1[Monad2[A]] })#T, B] =
      Composition[Monad1, A, Monad2, B](this, f)

    def run: Monad1[A]
  }

  final case class Lifted[Monad1[_], A](m: Monad1[A])
    extends OptionT[Monad1, A] {
    def run = m
  }

  final case class Composition[Monad1[_], A, Monad2[_], B](head: OptionT[Monad1, A], fn: A => OptionT[Monad2, B])
                                                          (implicit val mapper: Monad[Monad1, A])
    extends OptionT[({ type T[A] = Monad1[Monad2[A]] })#T, B] {
    def run = mapper.map(head.run, x => fn(x).run)
  }

  final case class CompositionMap[Monad1[_], A, B](head: OptionT[Monad1, A], fn: A => B)
                                                  (implicit val mapper: Monad[Monad1, A])
    extends OptionT[Monad1, B] {
    def run = mapper.map(head.run, fn)
  }
}

// Instead of nesting, we can "transform" each monad here
// into our same kind of wrapper monad, then get the nested
// result by calling "run"
type FutureTry[A] = Future[Try[A]]
val result2: OptionT[FutureTry, Post] = for {
  body <- Lifted(makeFrontpageRequest)
  incident <- Lifted(parsePost(body))
} yield incident

val result3: Future[Try[Post]] = result2.run

println("This code should not block until Await")

Await.result(result3, 10.seconds)