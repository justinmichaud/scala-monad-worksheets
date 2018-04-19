# What is a Monad? (Presenter Notes)

[show comic]

In this talk, I want to show you how you can use monads to make your code simpler.

So what is a monad?

For our purposes, a monad is a thing that has two operators

********

1) Point, which can wrap up a base value into our monad

********

2) Flatmap, which allows us to chain functions that operate on our base value

We have our monad, wrapping some base type A. We take a function going from A to our Monad wrapping type B. Then, we produce a monad wrapping type B.

For example, say we have a list. We take a function that takes the base type of the list, and produces a new list. Then, we can produce a new list.

********

Map is not part of the definition of a monad, but given a monad, the implementation here will always work.
We can see how it works with list here

********

So what do Future, Option, and Try all have in common? The reason they "feel" similar is because they are all monads!

Option abstracts away "nothingness". We only move on to the next step in the pipeline if we have "Some"

********

Otherwise, we just produce None

********

Try abstracts away exception handling. We only move on to the next step in the pipeline if we did not throw.

********

Otherwise, we fail

********

Future abstracts away when a computation is run. We move on some time in the future.

********

...

********

THIS IS WHAT A MONAD IS! It is a way to abstract away control flow by piping functions together.

We can take a bunch of functions producing monads, and build a pipeline. If one fails, the rest do not get executed.

********

Scala has some nice syntax to show that this is really a pipeline. Under the hood, this is just flatmap

********

Now, for an example

Let's look at an example.

2:40 / 2:40
********

Your mission, should you choose to accept it, is to produce the title of the top post on reddit using monads.

Failure is not an option (its a Try).

First, let's make a request:

```
val result = makeFrontpageRequest
```

(this is a Future[String])

## 2

```
val result = makeFrontpageRequest.map(parsePosts)
```

(this is a Future[Try[List[Post]]])

First, let's fill in parsePosts. We have a List[JValue], as well as something that takes a JValue and produces a Try[Post]. We need a Try[List[Post]].

## 3

```
yield posts.flatMap(parsePost(_).toOption)
```

We need toOption because list a Try is not compatible with List's flatMap function

## 4

Now, let's grab the first post and produce that.

```
val result = makeFrontpageRequest.map(body =>
  parsePosts(body).flatMap(posts =>
    Try(posts.head)
  )
)
```

This is a Future[Try[Post]], as expected. We see that body is a String, and posts is a List[Post], since map/flatMap unwrap the value for us.

## 5

This is a bit unwieldy, so let us write this using scala's shorthand, called a "for comprehension":

```
val result = for {
  body <- makeFrontpageRequest
} yield
  for {
    posts <- parsePosts(body)
    post <- Try(posts.head)
  } yield post
```

Which is a Future[Try[Post]] as expected

Remember that this for syntax can be reduced to only calls to flatmap. This syntax lets us see that this is just a pipeline for data.

4:40 / 3:10

# Further Reading

I have prepared two more worksheet examples for you, talking about io and monad transformers if you are interested.

On the github page, I have the slides, scala worksheets and some more resources for you to continue reading.

Thanks!

5:00 / 0:20