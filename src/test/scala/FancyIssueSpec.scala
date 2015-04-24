import java.time.{Clock, Instant}
import scala.collection.JavaConverters._
import org.specs2.mutable._
import com.jcabi.github.mock.MkGithub
import com.getbootstrap.no_carrier.github.util._
import test_implicits._

class FancyIssueSpec extends Specification {
  implicit val clock = Clock.systemUTC
  val github = new MkGithub("mock_gh")
  val repo = github.repos.create("foobar")
  val label = "waiting-for-OP"
  val twoSecsInMs = 2000
  def newIssue() = repo.issues.create("Title here", "Description here")
  def delay() {
    Thread.sleep(twoSecsInMs)
  }

  "lastCommentedOnAt" should {
    "give the only value when there is exactly one comment" in {
      val issue = newIssue()
      val comment = issue.comments.post("First comment").smart
      issue.fancy.lastCommentedOnAt mustEqual Some(comment.createdAt.toInstant)
    }
    "give the maximum when there are several comments" in {
      val issue = newIssue()
      issue.comments.post("First comment")
      delay()
      issue.comments.post("Second comment")
      delay()
      val lastComment = issue.comments.post("Third comment").smart
      issue.fancy.lastCommentedOnAt mustEqual Some(lastComment.createdAt.toInstant)
    }
  }

  "lastLabelledAt" should {
    "give the maximum when there have been several labellings" in {
      val issue = newIssue()
      issue.labels.add(label)
      issue.labels.remove(label)
      delay()
      issue.labels.add(label)
      issue.labels.remove(label)
      delay()
      val before = clock.instant()
      issue.labels.add(label)
      val after = clock.instant()
      issue.fancy.lastLabelledAt must beGreaterThanOrEqualTo(before.truncatedToSecs)
      issue.fancy.lastLabelledAt must beLessThanOrEqualTo(after.truncatedToSecs)
    }
  }

  "lastClosedAt" should {
    "be None when the issue is open" in {
      val issue = newIssue()
      issue.fancy.lastClosedAt must beNone
    }
    "be the closure instant when the issue has been closed once" in {
      val issue = newIssue()
      val before = clock.instant()
      issue.smart.close()
      val after = clock.instant()
      issue.fancy.lastClosedAt must beSome(greaterThanOrEqualTo(before.truncatedToSecs))
      issue.fancy.lastClosedAt must beSome(lessThanOrEqualTo(after.truncatedToSecs))
    }
    "be the last closure instant when the issue has been closed and reopened multiple times" in {
      val issue = newIssue()
      val smartIssue = issue.smart
      smartIssue.close()
      smartIssue.open()
      delay()
      smartIssue.close()
      smartIssue.open()
      delay()
      val before = clock.instant()
      smartIssue.close()
      val after = clock.instant()
      smartIssue.open()
      issue.fancy.lastClosedAt must beSome(greaterThanOrEqualTo(before.truncatedToSecs))
      issue.fancy.lastClosedAt must beSome(lessThanOrEqualTo(after.truncatedToSecs))
    }
  }

  "wasClosedAfterLabelling" should {
    "be false when the issue has never been closed" in {
      val issue = newIssue()
      issue.fancy.wasClosedAfterLabelling must beFalse
      issue.labels.add(label)
      issue.fancy.wasClosedAfterLabelling must beFalse
    }
    "be false when the issue was closed prior to the labelling" in {
      val issue = newIssue()
      issue.smart.close()
      issue.smart.open()
      delay()
      issue.labels.add(label)
      issue.fancy.wasClosedAfterLabelling must beFalse
    }
    "be true when the issue was closed after the labelling" in {
      val issue = newIssue()
      issue.labels.add(label)
      delay()
      issue.smart.close()
      issue.smart.open()
      issue.fancy.wasClosedAfterLabelling must beTrue
    }
    "be correct when the issue has been labelled, closed, and reopened multiple times" in {
      val issue = newIssue()
      // FIXME
    }
  }
}
