import java.time._
import org.specs2.mutable._
import com.getbootstrap.no_carrier.util.RichInstant

class RichInstantSpec extends Specification {
  val oneDay = Duration.ofDays(1)
  val twoDays = Duration.ofDays(2)
  val twoDaysAndOneSec = twoDays.plusSeconds(1)
  val threeDays = Duration.ofDays(3)

  "isBeyondTimeout" should {
    "be false when no time has elapsed" in {
      (Instant.now() isBeyondTimeout twoDays) must beFalse
    }
    "be false when just a bit of time has elapsed" in {
      (Instant.now().minus(oneDay) isBeyondTimeout twoDays) must beFalse
    }
    "be false at the exact instant that the timeout has elapsed" in {
      (Instant.now().minus(twoDays) isBeyondTimeout twoDays) must beFalse
    }
    "be true after the timeout has expired" in {
      (Instant.now().minus(twoDaysAndOneSec) isBeyondTimeout twoDays) must beTrue
      (Instant.now().minus(threeDays) isBeyondTimeout twoDays) must beTrue
    }
  }
}
