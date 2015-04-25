import java.time.{Instant, Duration, Clock}
import java.time.temporal.ChronoUnit
import com.jcabi.github.Issue
import com.getbootstrap.no_carrier.github.FancyIssue
import com.getbootstrap.no_carrier.github.util._

package object test_implicits {
  object RicherIssue {
    private val label = "waiting-for-OP"
    private var count = 1
  }
  implicit class RicherIssue(issue: Issue) {
    def fancy(implicit clock: Clock, timeout: Duration): FancyIssue = new FancyIssue(issue = issue, label = RicherIssue.label, timeout = timeout)
    def uniqueComment() {
      issue.comments.post(s"Comment number ${RicherIssue.count}.")
      RicherIssue.count += 1
    }
  }

  implicit class RicherInstant(instant: Instant) {
    def truncatedToSecs: Instant = instant.truncatedTo(ChronoUnit.SECONDS)
  }

  implicit class RicherOptionInstant(optInstant: Option[Instant]) {
    def truncatedToSecs: Option[Instant] = optInstant.map{ _.truncatedToSecs }
  }
}
