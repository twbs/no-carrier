import java.time.{Instant, Duration, Clock}
import java.time.temporal.ChronoUnit
import com.jcabi.github.Issue
import com.getbootstrap.no_carrier.github.FancyIssue

package object test_implicits {
  object RicherIssue {
    private val label = "waiting-for-OP"
    private val timeout = Duration.ofDays(2)
  }
  implicit class RicherIssue(issue: Issue) {
    def fancy(implicit clock: Clock): FancyIssue = new FancyIssue(issue = issue, label = RicherIssue.label, timeout = RicherIssue.timeout)
  }

  implicit class RicherInstant(instant: Instant) {
    def truncatedToSecs: Instant = instant.truncatedTo(ChronoUnit.SECONDS)
  }

  implicit class RicherOptionInstant(optInstant: Option[Instant]) {
    def truncatedToSecs: Option[Instant] = optInstant.map{ _.truncatedToSecs }
  }
}
