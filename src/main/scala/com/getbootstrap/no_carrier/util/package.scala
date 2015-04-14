package com.getbootstrap.no_carrier

import java.time.{Clock, Instant, Duration}

package object util {
  val InstantOrdering = implicitly[Ordering[Instant]]

  implicit class RichInstant(instant: Instant) {
    import InstantOrdering._

    def +(duration: Duration): Instant = instant.plus(duration)
    def isBeyondTimeout(timeout: Duration)(implicit clock: Clock): Boolean = {
      val now = Instant.now(clock)
      val deadline = (instant + timeout)
      deadline < now
    }
  }
}
