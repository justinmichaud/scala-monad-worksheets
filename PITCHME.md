@title[What is a monad?]

# What is a monad?
##### Justin Michaud
###### April 24, 2018

---

![](https://imgs.xkcd.com/comics/haskell.png)

---

# What is a Monad?
```scala
sealed trait Monad {
    def flatMap[A,B](fn: A=>Monad[B]): Monad[B]
    def map[B](fn: A=>B): Monad[B] = flatMap(x => point(f(x)))
}

object Monad {
    def point[A](a: A): Monad[A]
}
```

Ex: List, Option, Try, Future

---

# Point
```scala
def point[A](a: A): Monad[A]

// ex:
Some(1)
> res0: Option[Int] = Some(1)
```

---

# FlatMap
```scala
def flatMap[A,B](fn: A=>Monad[B]): Monad[B]

// ex:
List(1,2,3).flatMap(x => List(x, x*10))
> res0: List[Int] = List(1, 10, 2, 20, 3, 30)
```

---

# Map
```scala
def map[B](fn: A=>B): Monad[B] = flatMap(x => point(f(x)))

// ex:
List(1,2,3).flatMap(x => List(5*x))
List(1,2,3).map(_ * 5)
> res0: List[Int] = List(5, 10, 15)
```

---

# Option
```scala
val myVal: Option[Int] = Some(5)
myVal.flatMap(x => Some(x + 1))
> res: Option[Int] = Some(6)
```

---

# Option
```scala
val myVal2: Option[Int] = None
myVal2.flatMap(x => Some(x + 1))
> res2: Option[Int] = None
```

---

# Try

```scala
Try(5).map(_ => "Hi!")
> res1: Try[String] = Success("Hi!")
```

---

# Try

```scala
def myFun(): String = throw new RuntimeException()
Try(myFun).map(_ => "Hi!")
> res1: Try[String] = Failure(java.lang.RuntimeException)
```

---

# Future

```scala
val res = Future {
    Thread.sleep(5000)
    "My async code"
}
> res: Future[String] = Future(<not completed>)
```

---

# Future

```scala
val res = Future {
    Thread.sleep(5000)
    "My async code"
}
> res: Future[String] = Future(<not completed>)
res
> res: Future[String] = Future(Success(My async code))
```

---

# Pipelines

```scala
def head2d[A](list: List[List[A]]) =
    list.headOption.flatMap(headRow =>
        headRow.headOption
    )

head2d(List()) // None

head2d(List(List(1,2)))
> res1: Option[Int] = Some(1)
```

---

# Pipelines

```scala
def head2d[A](list: List[List[A]]) =
    for {
        headRow <- list.headOption
        head <- headRow.headOption
    } yield head
```

---?code=src/reddit_example_blank.sc&title=Parsing JSON With Monads
@[23-36](Parse Reddit Front Page)

### Where can I learn more?
See the examples at [github.com/justinmichaud/scala-monad-worksheets](https://github.com/justinmichaud/scala-monad-worksheets) for
blank scala worksheets, slides, and links

Other examples to try yourself: IO monad, Monad transformers
