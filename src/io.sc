import scala.io.StdIn

// Run with ammonite (amm io.sc)
// Further reading + inspiration: https://underscore.io/blog/posts/2015/04/28/monadic-io-laziness-makes-you-free.html

sealed trait IO[A] {
  def map[B](fn: A=>B): IO[B] = IO.Compose[A, B](this, x => IO.Bind(() => fn(x)))
  def flatMap[B](fn: A=>IO[B]): IO[B] = IO.Compose(this, fn)
  def run: A
}

object IO {
  final case class Bind[A](fn: () => A) extends IO[A] {
    def run = fn()
  }
  final case class Compose[A, B](head: IO[A], transform: A=>IO[B]) extends IO[B] {
    def run = transform(head.run).run
  }
  def puts(msg: String): IO[Unit] = Bind(() => println(msg))
  def gets: IO[String] = Bind(StdIn.readLine)
}

val sayHello = for {
  _ <- IO.puts("What is your first name?")
  fn <- IO.gets
  _ <- IO.puts("What is your last name?")
  ln <- IO.gets
  name = s"$fn $ln"
  _ <- IO.puts(s"Hello $name!")
} yield name

val sayHelloDesugared =
  IO.puts("*What is your first name?")
    .flatMap(_ => IO.gets)
    .flatMap(fn =>
      IO.puts("*What is your last name?")
        .flatMap(_ => IO.gets)
        .flatMap(ln => {
          val name = s"$fn $ln"
          IO.puts(s"*Hello $name!")
            .map(_ => name)
        })
    )

println("Nothing gets executed until run is called")
sayHello.run
println("----")
sayHelloDesugared.run