package com.mayreh

package object thankyou {

  implicit class RichString(val self: String) extends AnyVal {

    def prepend(str: String): String = if (self.startsWith(str)) self else str + self

    def append(str: String): String = if (self.endsWith(str)) self else self + str

    def trimPrefix(prefix: String): String = if (self.startsWith(prefix)) self.drop(prefix.length) else self

    def trimSuffix(suffix: String): String = if (self.endsWith(suffix)) self.dropRight(suffix.length) else self
  }

  implicit class RichSeq[A](val self: Seq[A]) extends AnyVal {

    def distinctBy[B](f: A => B): Seq[A] = {
      self.groupBy(f).flatMap { case (_, g) => g.headOption }(collection.breakOut)
    }
  }
}
