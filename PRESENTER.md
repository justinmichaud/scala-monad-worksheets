# What is a Monad? (Presenter Notes)

That depends who you ask. Answers range from "monads are like burritos[1]" to "monads are monoids in the category of endofunctors[2]".

For our purposes, a monad is a thing that has
1) An operator that, for any type A, will wrap it. This is called point on the slide.
2) An operator that acts like flatmap, allowing us to chain/compose together functions on our wrapped values

Essentially, it allows us to augment a value by wrapping it up, while still allowing function composition to work as expected.
This allows us to define "pipelines" for data to flow in, and to let a monad abstract away control flow.

Keep map in the back of your mind. It is not part of the definition of a monad, but given a monad, the implementation of map is the same.

What do Future, Option, and Try all have in common? The reason they "feel" similar is because they are all monads!

Option abstracts away "nothingness"
Try abstracts away exception handling
Future abstracts away when a computation is run

1:45

# Parsing JSON

Your mission, should you choose to accept it, is to produce the title of the top post on reddit.

First, let's make a request:

---

val result = makeFrontpageRequest

(this is a Future[String])
---

val result = makeFrontpageRequest.map(body => parsePosts(body))

(this is a Future[Try[List[Post]]])

First, let's fill in parsePosts. We have a List[JValue], as well as something that takes a JValue and produces a Try[Post]. We need a Try[List[Post]], so let's do a bit of type-driven development.

---

yield posts.flatMap(parsePost(_).toOption)

We need toOption because list a Try is not compatible with List's flatMap function

---

Let us write this using scala's shorthand, called a "for comprehension":

val result = for {
  body <- makeFrontpageRequest
} yield
  for {
    posts <- parsePosts(body)
  } yield posts

---

Now, let's grab the first post and produce that

val result = for {
  body <- makeFrontpageRequest
} yield
  for {
    posts <- parsePosts(body)
    post <- Try(posts.head)
  } yield post

Which is a Future[Try[Post]] as expected

Remember that this for syntax is still just calling map and flatMap under the hood.

val result = makeFrontpageRequest.map(body =>
  parsePosts(body).flatMap(posts =>
    Try(posts.head)
  )
)

3:00

# The IO Monad

The IO monad allows pure functions to build a pipeline of impure operations, then send them to an impure interpreter
for execution. An impure function is one that has side effects; That is, calling it multiple times with the same parameters may
produce a different result. The IO monad allows Haskell programs to remain completely pure, while still allowing the program to do useful things.

You can check out this example on the github page for this talk.

# There's more

I prepared one more example for you to check out if you are interested. It involves something called "monad transformers," which let you combine characteristics of different monads.

That is all!

0:15