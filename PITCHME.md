@title[What is a monad?]

# What is a monad?
##### Justin Michaud
###### April ???

---

![](https://imgs.xkcd.com/comics/haskell.png)

---

# What is a Monad?
```
sealed trait Monad {
    def flatMap[A,B](fn: A=>Monad[B]): Monad[B]
    def map[B](fn: A=>B): Monad[B] = flatMap(x => point(f(x)))
}

object Monad {
    def point[A](a: A): Monad[A]
}
```

Ex: Option, Try, Future

---

# Option
```scala
val myVal: Option[Int] = Some(5)
val myVal2: Option[Int] = None
myVal.flatMap(x => Some(x + 1))
> res: Option[Int] = Some(6)
myVal2.flatMap(x => Some(x + 1))
> res2: Option[Int] = None
```

---

# Try

```scala
def myFun(): String = throw new RuntimeException()
Try(myFun).map(_ => "Hi!")
> res1: Try[String] = Failure(java.lang.RuntimeException)
Try(5).map(_ => "Hi!")
> res2: Try[String] = Success("Hi!")
```

---

# Future

```scala
implicit val ec = ExecutionContext.global
val res = Future {
    Thread.sleep(5000)
    "My async code"
}
> res: Future[String] = Future(<not completed>)
res
> res: Future[String] = Future(Success(My async code))
```

---?code=src/reddit_example_blank.sc&title=Parsing JSON With Monads
@[23-36](Parse Reddit Front Page)

---?code=src/io_blank.sc&title=More examples
@[22-29](Left as an exercise)

---

### Where can I learn more?
See the examples at [github.com/justinmichaud/scala-monad-worksheets](https://github.com/justinmichaud/scala-monad-worksheets) for
blank scala worksheets, slides, and links
