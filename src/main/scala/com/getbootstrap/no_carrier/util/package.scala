package com.getbootstrap.no_carrier

import java.time.{Clock, Instant, Duration}
import com.google.common.base.{Optional=>GuavaOptional}

package object util {
  val InstantOrdering = implicitly[Ordering[Instant]]

  implicit class RichInstant(instant: Instant) {
    import InstantOrdering._

    def +(duration: Duration): Instant = instant.plus(duration)
    def isBeyondTimeout(timeout: Duration)(implicit clock: Clock): Boolean = {
      val now = Instant.now(clock)
      val deadline = instant + timeout
      deadline < now
    }
  }

  implicit class GoogleToScalaOptional[T](option: GuavaOptional[T]) {
    def toOption: Option[T] = {
      if (option.isPresent) {
        Some(option.get())
      }
      else {
        None
      }
    }
  }

  implicit class LoggerName(name: String) {
    import ch.qos.logback.classic.Logger
    import org.slf4j.LoggerFactory

    def logger: Logger = LoggerFactory.getLogger(name).asInstanceOf[Logger]
  }
}
