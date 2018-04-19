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

Failure is not an option (its a Try) (as we discssed).

First, let's make a request:

********

(this is a Future[String])

********

Now, we can parse it (once we get it) using map

********

Lets fill in parsePosts. We have a List[JValue], and we need a List[Post]. Remember that yield will wrap our value in Try, because it is really just a call to map.

********

So, we use flatMap. If a post can't be parsed, we just ignore it. We need the toOption because list only works with other collections, not try.

We see that our result is a Future[Try[List[Post]]]

********

Now, let's get the first post. We will use flatMap on our Try[List[Post]] inside to turn it into a Try[Post].

Flatmap needs something that produces a try

********

We have a List[Post]

********

So, we try to get the head of the list

********

This is a Future[Try[Post]], as expected, since we turned our Try[List[Post]] into a Try[Post]

********

Now, remember that nice syntax I showed you earlier. We can use that to make this much easier to understand. This is a Future[String]

********

Now, lets turn our String into a Try[Post]

********

We can see that the for yield block handles wrapping and unwrapping everything for us

********

And so, we built a pipeline! If any of these steps fail, the entire thing will stop. Otherwise, we get our expected result!

4:40 / 1:50

********

# Further Reading

I have prepared two more worksheet examples for you, talking about io and monad transformers if you are interested.

On the github page, I have the slides, blank scala worksheets for you to fill in, and some more resources for you to continue reading.

Thanks!

5:00 / 0:20