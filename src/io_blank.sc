import scala.io.StdIn

sealed trait IO[A] {
  def map[B](fn: A=>B): IO[B] = ???
  def flatMap[B](fn: A=>IO[B]): IO[B] = ???
  def run: A
}

object IO {
  final case class Bind[A](fn: () => A) extends IO[A] {
    def run = ???
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

println("Nothing gets executed until run is called")
sayHello.run