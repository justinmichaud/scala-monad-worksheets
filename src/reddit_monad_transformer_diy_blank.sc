import Monad._
import MonadTransformer._
import RedditHelpers._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Success, Try}

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

// The heart of this example
// This was hard to write, don't worry if it is hard to read
object MonadTransformer {
  // Monad1[_] just means that we want a type like Option
  // That is, we want Option and not Option[T], a higher-kinded type
  sealed trait OptionT[Monad1[_], A] {
    type AoA[T] = Monad1[Monad1[T]]

    def map[B](f: A=>B)
              (implicit m1: Monad[Monad1, A],
               m2: Monad[Monad1, B])
              : OptionT[Monad1, B] =
      flatMap(x => Lifted(m2.point(f(x))))

    // We use Monad1[A] =:= Monad2[A] to make sure
    // this function is only applied when Monad1 == Monad2
    // This ensures that, when we have a pipeline with the
    // same monad, it is correctly collapsed
    def flatMap[Monad2[_], B](f: A=>OptionT[Monad1, B])
                  (implicit m: Monad[Monad1, A],
                   ev: Monad1[A] =:= Monad2[A])
                  : OptionT[Monad1, B] =
      ???

    // ({ type T[A] = Monad1[Monad2[A]] })#T is just a scary way
    // of writing
    //     type T[A] = Monad1[Monad2[A]]
    // inline. That is, it is the higher-kinded type that has
    // Monad2[_] nested within it.
    def flatMap[Monad2[_], B](f: A=>OptionT[Monad2, B])
                             (implicit m: Monad[Monad1, A])
                             : OptionT[({ type T[A] = Monad1[Monad2[A]] })#T, B] =
      ???


    // Convert our series of transformations into a final nested Monad1
    def run: Monad1[A]
  }

  // Our type to represent a value that has been "lifted" into
  // our wrapper type
  final case class Lifted[Monad1[_], A](m: Monad1[A])
    extends OptionT[Monad1, A] {
    def run = m
  }

  // ***** Add any other case classes you need here
}

// Instead of nesting, we can "transform" each monad here
// into our same kind of wrapper monad, then get the nested
// result by calling "run"
val result2 = for {
  body <- Lifted(makeFrontpageRequest)
  json <- Lifted(Try(parse(body)))
  posts <- Lifted(Try(json \ "data" \ "children"))
  post <- Lifted(Try(posts.children.head))
  postData <- Lifted(Try(post \ "data" \ "title"))
  title <- Lifted(Try(postData.extract[String]))
  post = Post(title)
} yield post

val result3: Future[Try[Post]] = result2.run

println("This code should not block until Await")

Await.result(result3, 10.seconds)