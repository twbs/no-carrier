package com.getbootstrap.no_carrier

import java.time.{Duration, Instant}

package object util {
  val InstantOrdering = implicitly[Ordering[Instant]]

  implicit class RichInstant(instant: Instant) {
    import InstantOrdering._

    def +(duration: Duration) = instant.plus(duration)
    def isBeyondTimeout(timeout: Duration): Boolean = (instant + timeout) < Instant.now()
  }

  implicit class RichTraversableOnce[T](trav: TraversableOnce[T]) {
    def maxOption(implicit cmp: Ordering[T]): Option[T] = {
      if (trav.isEmpty) None else Some(trav.max)
    }
  }
}
