# Monad Scala Worksheets - Lightning Talk

Each file has an associated _blank file for you to try for yourself. You should fill in the blanks to get the desired result. Suggested order is given below.

You can open these examples as an intellij worksheet file, with the exception of the IO examples which require ammonite.

1) reddit_example - Use Future and Try to request the reddit front page and parse its json
2) io - Implement an IO monad that allows you to compose operations that have side effects in a pure way, and interpret them later.
This is the same idea that haskell uses to accomplish pure IO
3) reddit_monad_transformer - Build a wrapper around Try that lets you stack any other monad on top of it, then use it with Future to combine fetching + parsing into one nice pipeline

# What is a Monad?
```
sealed trait Monad {
    def flatMap[A,B](fn: A=>Monad[B]): Monad[B]
    def point[A](a: A): Monad[A]
}
```

A monad is a thing that has
1) An operator that, for any type A, will wrap it. This is called point above.
2) An operator that acts like flatmap, allowing us to chain together functions

Essentially, it allows us to augment a value by wrapping it up, while still allowing function composition to work as expected.
This allows us to define "pipelines" for data to flow in, and to let a monad abstract away control flow.

What do Future, Option, and Try all have in common? The reason they "feel" similar is because they are all monads[1]!
Future abstracts away when the computation is run, option abstracts away "nothingness," and Try abstracts away exception
handling.

[1] Some people claim that Future and Try are not technically monads when you consider all of the possible side-effects that they allow, but
the debate is very technical, and has little impact on every-day use

# The IO Monad

The IO monad allows pure functions to build a pipeline of impure operations, then send them to an impure interpreter
for execution. This is how Haskell can maintain purity while still allowing the program to do useful things.

See the io example to implement this yourself!

# Monad Transformers

Often (as seen in the reddit example), we need to "stack" monads, like `Future[Try[SomeValue]]`. We can create a
monad transformer that transforms our base monad (in this case, Try) into something that can accept another monad
stacked on top. The transformer handles wrapping and unwrapping everything for us.

See the reddit_monad_transformer example to implement this yourself!

# Where can I learn more?
There are plenty of great blogs and books about this topic. If like me, you are not quite ready to learn Haskell, I recommend
reading this [blog post](https://underscore.io/blog/posts/2015/04/14/free-monads-are-simple.html) about free monads and
interpereters, a generalization of the IO monad we discussed.

You can also check out scalaz or cats, two libraries that provide support for many of these abstractions (although they may be a
bit too far out there for heavy production use)
