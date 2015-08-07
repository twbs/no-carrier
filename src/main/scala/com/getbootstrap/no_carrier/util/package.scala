package com.getbootstrap.no_carrier

import java.time.{Instant, Duration}
import com.google.common.base.{Optional=>GuavaOptional}

package object util {
  val InstantOrdering = implicitly[Ordering[Instant]]
  val DurationOrdering = implicitly[Ordering[Duration]]

  implicit class RichInstant(instant: Instant) {
    def +(duration: Duration): Instant = instant.plus(duration)
    def -(earlier: Instant): Duration = Duration.between(earlier, instant)
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
