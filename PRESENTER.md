# What is a Monad? (Presenter Notes)

[show comic]

I am going to show you a few examples of monads in scala. Don't worry about understanding everything now, you will have
to play with the code yourself for this to make any sense.

So what is a monad?

For our purposes, a monad is a thing that has two operators
1) Point, which can wrap up any type A
2) Flatmap, which allows us to create pipelines of functions operating on our data

THIS IS WHAT A MONAD IS! It is a way to abstract away control flow by making pipelines.
If you find yourself with a bunch of nested conditions, or re-inventing some way to "chain" steps together, STOP AND USE A MONAD!

What do Future, Option, and Try all have in common? The reason they "feel" similar is because they are all monads!

Option abstracts away "nothingness". We only move on to the next step in the pipeline if we have "Some"
Try abstracts away exception handling. We only move on to the next step in the pipeline if we did not throw.
Future abstracts away when a computation is run. We move on some time in the future.

Let's look at an example.

1:10

# Parsing JSON

Your mission, should you choose to accept it, is to produce the title of the top post on reddit using monads.

Failure is not an option (its a Try).

First, let's make a request:

---

val result = makeFrontpageRequest

(this is a Future[String])
---

val result = makeFrontpageRequest.map(parsePosts)

(this is a Future[Try[List[Post]]])

First, let's fill in parsePosts. We have a List[JValue], as well as something that takes a JValue and produces a Try[Post]. We need a Try[List[Post]], so let's do a bit of type-driven development.

---

yield posts.flatMap(parsePost(_).toOption)

We need toOption because list a Try is not compatible with List's flatMap function

---

Now, let's grab the first post and produce that

val result = makeFrontpageRequest.map(body =>
  parsePosts(body).flatMap(posts =>
    Try(posts.head)
  )
)

This is a Future[Try[Post]], as expected.

---

This is a bit unweildy, so let us write this using scala's shorthand, called a "for comprehension":

val result = for {
  body <- makeFrontpageRequest
} yield
  for {
    posts <- parsePosts(body)
    post <- Try(posts.head)
  } yield post

Which is a Future[Try[Post]] as expected

Remember that this for syntax can be reduced to only calls to flatmap. This syntax lets us see that this is just a pipeline for data.

3:40

# Further Reading

I have prepared two more worksheet examples for you, talking about io and monad transformers if you are interested.

On the github page, I have the slides, scala worksheets and some more resources for you to continue reading.

Thanks!

0:15