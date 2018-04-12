# Monad Scala Worksheets - Lightning Talk
[Slides](https://gitpitch.com/justinmichaud/scala-monad-worksheets/master?grs=github&t=black)

Each file has an associated _blank file for you to try for yourself. You should fill in the blanks to get the desired result. Suggested order is given below.

You can open these examples as an intellij worksheet file, with the exception of the IO examples which require ammonite for input.

1) reddit_example - Use Future and Try to request the reddit front page and parse its json
2) io - Implement an IO monad that allows you to compose operations that have side effects in a pure way, and interpret them later.
This is the same idea that haskell uses to accomplish pure IO
3) reddit_monad_transformer - Build a wrapper around Try that lets you stack any other monad on top of it, then use it with Future to combine fetching + parsing into one nice pipeline

# What is a Monad?
```
sealed trait Monad {
    def flatMap[A,B](fn: A=>Monad[B]): Monad[B]
    def map[B](fn: A=>B): Monad[B] = flatMap(x => point(f(x)))
}

object Monad {
    def point[A](a: A): Monad[A]
}
```

That depends who you ask. Answers range from "monads are like burritos[1]" to "monads are monoids in the category of endofunctors[2]".

For our purposes, a monad is a thing that has
1) An operator that, for any type A, will wrap it. This is called point above.
2) An operator that acts like flatmap, allowing us to chain/compose together functions

Essentially, it allows us to augment a value by wrapping it up, while still allowing function composition to work as expected.
This allows us to define "pipelines" for data to flow in, and to let a monad abstract away control flow.

What do Future, Option, and Try all have in common? The reason they "feel" similar is because they are all monads[3]!
Option abstracts away "nothingness," Try abstracts away exception handling, and Future abstracts away when the computation is run.

[1] https://blog.plover.com/prog/burritos.html
[2] https://blog.merovius.de/2018/01/08/monads-are-just-monoids.html
[3] Some people claim that Future and Try are not technically monads when you consider all of the possible side-effects that they allow, but
the debate is very technical, and has little impact on every-day use

# The IO Monad

The IO monad allows pure functions to build a pipeline of impure operations, then send them to an impure interpreter
for execution. An impure function is one that has side effects; That is, calling it multiple times with the same parameters may
produce a different result. The IO monad allows Haskell programs to remain completely pure, while still allowing the program to do useful things.
We decouple the program from its side effects.

See the io example to implement this yourself!

# Monad Transformers

Often (as seen in the reddit example), we need to "stack" monads, like `Future[Try[SomeValue]]`. We can create a
monad transformer that transforms our base monad (in this case, Try) into something that can accept another monad
stacked on top. The transformer handles wrapping and unwrapping everything for us.

See the reddit_monad_transformer example to implement this yourself!

# Where can I learn more?
There are plenty of great blogs and books about this topic. If like me, you are not quite ready to learn Haskell, I recommend
reading starting off with these blog posts.

- https://underscore.io/blog/posts/2015/04/28/monadic-io-laziness-makes-you-free.html
- https://underscore.io/blog/posts/2015/04/14/free-monads-are-simple.html

You can also check out scalaz or cats, two libraries that provide support for many of these abstractions (although they may be a
bit too far out there for heavy production use)

Finally, some resources that seemed interesting (but I will admit I haven't read through yet) about category theory:

- "Monads for functional programming", by Philip Wadler: http://homepages.inf.ed.ac.uk/wadler/papers/marktoberdorf/baastad.pdf
- "Seven Sketches in Compositionality: An Invitation to Applied Category Theory": https://johncarlosbaez.wordpress.com/2018/03/26/seven-sketches-in-compositionality/
- http://nikgrozev.com/2016/03/14/functional-programming-and-category-theory-part-1-categories-and-functors/