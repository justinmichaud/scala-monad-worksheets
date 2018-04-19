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
List(1,2,3,4).flatMap(x => if (x%2==0) List(x) else List())
> res0: List[Int] = List(2, 4)
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
def doStepOne(): Option[Int]
def doStepTwo(input: Int): Option[Boolean]

val res = doStepOne.flatMap(doStepTwo)
> res: Option[Boolean] = ...
```

---

# Pipelines

```scala
val res = for {
    resOne <- doStepOne()
    resTwo <- doStepTwo(resOne)
} yield resTwo
> res: Option[Boolean] = ...
```

---?image=img/1.png&size=auto 100%
---?image=img/2.png&size=auto 100%
---?image=img/3.png&size=auto 100%
---?image=img/4.png&size=auto 100%
---?image=img/5.png&size=auto 100%
---?image=img/6.png&size=auto 100%
---?image=img/7.png&size=auto 100%
---?image=img/8.png&size=auto 100%
---?image=img/9.png&size=auto 100%
---?image=img/10.png&size=auto 100%
---?image=img/11.png&size=auto 100%
---?image=img/12.png&size=auto 100%
---?image=img/13.png&size=auto 100%
---?image=img/14.png&size=auto 100%

---

### Where can I learn more?
See the examples at [github.com/justinmichaud/scala-monad-worksheets](https://github.com/justinmichaud/scala-monad-worksheets) for
blank scala worksheets, slides, and links

Other examples to try yourself: IO monad, Monad transformers
