import java.time._
import org.specs2.mutable._
import com.getbootstrap.no_carrier.util.RichInstant
import com.getbootstrap.no_carrier.util.DurationOrdering._

class RichInstantSpec extends Specification {
  private implicit class RicherInstant(instant: Instant) {
    def isBeyondTimeout(timeout: Duration)(implicit clk: Clock): Boolean = (clk.instant() - instant) > timeout
  }

  val utc = ZoneId.of("UTC")
  val pseudoNow = ZonedDateTime.of(LocalDateTime.of(2015, 3, 17, 1, 2), utc).toInstant
  implicit val clock = StoppedClock(pseudoNow)
  val oneDay = Duration.ofDays(1)
  val twoDays = Duration.ofDays(2)
  val twoDaysAndOneSec = twoDays.plusSeconds(1)
  val threeDays = Duration.ofDays(3)

  "isBeyondTimeout" should {
    "be false when no time has elapsed" in {
      (pseudoNow isBeyondTimeout twoDays) must beFalse
    }
    "be false when just a bit of time has elapsed" in {
      (pseudoNow.minus(oneDay) isBeyondTimeout twoDays) must beFalse
    }
    "be false at the exact instant that the timeout has elapsed" in {
      (pseudoNow.minus(twoDays) isBeyondTimeout twoDays) must beFalse
    }
    "be true after the timeout has expired" in {
      (pseudoNow.minus(twoDaysAndOneSec) isBeyondTimeout twoDays) must beTrue
      (pseudoNow.minus(threeDays) isBeyondTimeout twoDays) must beTrue
    }
  }
}

case class StoppedClock(atInstant: Instant, timeZone: ZoneId = ZoneId.of("UTC")) extends Clock {
  @Override
  def instant: Instant = atInstant
  @Override
  val getZone: ZoneId = timeZone
  @Override
  def withZone(zone: ZoneId) = StoppedClock(atInstant, zone)
}
