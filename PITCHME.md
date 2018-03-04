@title[What the hell is a monad?]

# What the hell is a monad?
##### Justin Michaud
###### April ???

---

# What is a Monad?
```
sealed trait Monad {
    def flatMap[A,B](fn: A=>Monad[B]): Monad[B]
    def point[A](a: A): Monad[A]
}
```

---

# The IO Monad

---?code=src/io_blank.sc&title=IO Monad
@[22-30]

---

# Parsing JSON With Monads

---?code=src/reddit_example_blank.sc&title=Reddit Example
@[27-33]

---

# Monad Transformers

---?code=src/reddit_monad_transformer_blank.sc&title=Reddit Example
@[54-63](Left as an exercise)
@[72-83](Left as an exercise)

---

### Where can I learn more?
See the examples at [github.com/justinmichaud/scala-monad-worksheets](https://github.com/justinmichaud/scala-monad-worksheets) for
worksheets, slides, and links

---