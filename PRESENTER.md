# What is a Monad? (Presenter Notes)

That depends who you ask. Answers range from "monads are like burritos[1]" to "monads are monoids in the category of endofunctors[2]".

For our purposes, a monad is a thing that has
1) An operator that, for any type A, will wrap it. This is called point on the slide.
2) An operator that acts like flatmap, allowing us to chain/compose together functions on our wrapped values

Essentially, it allows us to augment a value by wrapping it up, while still allowing function composition to work as expected.
This allows us to define "pipelines" for data to flow in, and to let a monad abstract away control flow.

Keep map in the back of your mind. It is not part of the definition of a monad, but given a monad, you get map for free.

What do Future, Option, and Try all have in common? The reason they "feel" similar is because they are all monads!

Option abstracts away "nothingness"
Try abstracts away exception handling
Future abstracts away when a computation is run

1:45

# Parsing JSON

Your mission, should you choose to accept it, is to produce the title of the top post on reddit using monads.

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

I have prepared two more worksheet examples for you. The first example, the IO monad, talks about how to cleanly compose together operations with side effects.  Next, "monad transformers" let you combine and compose characteristics of different monads easily.

On the github page, I have the slides, worksheets and some more resources for you to continue reading.

0:25