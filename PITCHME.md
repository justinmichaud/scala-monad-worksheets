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

```
def myFun(): String = throw new RuntimeException()
Try(myFun)
> res1: Try[String] = Failure(java.lang.RuntimeException)
```

---

# Future

```
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

---?code=src/io_blank.sc&title=The IO Monad
@[22-29]

---?code=src/reddit_monad_transformer_blank.sc&title=Monad Transformers (Teaser)
@[65-75](Left as an exercise)
@[92-103](Left as an exercise)

---

### Where can I learn more?
See the examples at [github.com/justinmichaud/scala-monad-worksheets](https://github.com/justinmichaud/scala-monad-worksheets) for
worksheets, slides, and links
