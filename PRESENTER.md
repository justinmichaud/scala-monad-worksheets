# What is a Monad? (Presenter Notes)

[show comic]

In this talk, I want to show you a small example of how you can use monads to make your code simpler.

So what is a monad?

For our purposes, a monad is a thing that has two operators
1) Point, which can wrap up a base value
2) Flatmap, which allows us to chain functions that operate on our base value

Map is not part of the definition of a monad, but given a monad, the implementation here will always work.

THIS IS WHAT A MONAD IS! It is a way to abstract away control flow by piping functions together.

The one takeaway I want you to get from this talk is: if you find yourself with a bunch of nested error checks,
or re-inventing some way to "chain" steps together, try to use an existing monad!

0:50 / 0:50

---

So what do Future, Option, and Try all have in common? The reason they "feel" similar is because they are all monads!

Option abstracts away "nothingness". We only move on to the next step in the pipeline if we have "Some"
Try abstracts away exception handling. We only move on to the next step in the pipeline if we did not throw.
Future abstracts away when a computation is run. We move on some time in the future.

Let's look at an example.

1:20 / 0:30

# Parsing JSON

Your mission, should you choose to accept it, is to produce the title of the top post on reddit using monads.

Failure is not an option (its a Try).

First, let's make a request:

---

```
val result = makeFrontpageRequest
```

(this is a Future[String])

---

```
val result = makeFrontpageRequest.map(parsePosts)
```

(this is a Future[Try[List[Post]]])

First, let's fill in parsePosts. We have a List[JValue], as well as something that takes a JValue and produces a Try[Post]. We need a Try[List[Post]].

---

```
yield posts.flatMap(parsePost(_).toOption)
```

We need toOption because list a Try is not compatible with List's flatMap function

---

Now, let's grab the first post and produce that.

```
val result = makeFrontpageRequest.map(body =>
  parsePosts(body).flatMap(posts =>
    Try(posts.head)
  )
)
```

This is a Future[Try[Post]], as expected. We see that body is a String, and posts is a List[Post], since map/flatMap unwrap the value for us.

---

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