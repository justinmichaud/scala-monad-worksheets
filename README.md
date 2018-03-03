# Monad Worksheets (Scala)

Each file has an associated _blank file for you to try for yourself. Types are given. Suggested order is given below.

Except when specified in the file, you can open these in an intellij worksheet file. The IO examples will require ammonite or the scala shell

1) reddit_example - Use Future and Option to request the reddit front page and parse its json
2) io - Implement an IO monad that allows you to compose operations that have side effects in a pure way, and interpret them later.
This is the same idea that haskell uses to accomplish pure IO
3) reddit_monad_transformer - Use ScalaZ to combine different monads together into one pipeline
4) reddit_monad_transformer_diy - Build your own monad typeclass, then use it to build your own monad transformer!