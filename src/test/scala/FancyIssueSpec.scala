import java.time.{Clock,Duration}
import org.specs2.mutable._
import com.jcabi.github.Issue
import com.jcabi.github.mock.MkGithub
import com.getbootstrap.no_carrier.github.util._
import test_implicits._

class FancyIssueSpec extends Specification {
  implicit val clock = Clock.systemUTC
  val github = new MkGithub("mock_gh")
  val repo = github.repos.create("foobar")
  val label = "waiting-for-OP"
  val twoSecsInMs = Duration.ofSeconds(2).toMillis
  val tinyDuration = Duration.ofSeconds(2)
  val tinyDelayMs = tinyDuration.toMillis
  val mediumTimeout = Duration.ofSeconds(4)
  val longDuration = Duration.ofSeconds(8)
  val longDelayMs = longDuration.toMillis
  val twoDays = Duration.ofDays(2)
  def newIssue() = repo.issues.create("Title here", "Description here")
  def longDelay() {
    Thread.sleep(longDelayMs)
  }
  def delay() {
    Thread.sleep(twoSecsInMs)
  }
  def tinyDelay() {
    Thread.sleep(tinyDelayMs)
  }

  "lastCommentedOnAt" should {
    implicit val timeout = twoDays

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
    implicit val timeout = twoDays

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
      issue.fancy.lastLabelledAt must beSome(greaterThanOrEqualTo(before.truncatedToSecs))
      issue.fancy.lastLabelledAt must beSome(lessThanOrEqualTo(after.truncatedToSecs))
    }
  }

  "lastClosedAt" should {
    implicit val timeout = twoDays

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
    implicit val timeout = twoDays

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
  }

  def addLabel(issue: Issue) {
    issue.labels.add(label)
    tinyDelay()
  }
  def unlabel(issue: Issue) {
    issue.labels.remove(label)
    tinyDelay()
  }
  def close(issue: Issue) {
    issue.smart.close()
    tinyDelay()
  }
  def reopen(issue: Issue) {
    issue.smart.open()
    tinyDelay()
  }
  def commentOn(issue: Issue) {
    issue.uniqueComment()
    tinyDelay()
  }

  "opNeverDelivered" should {
    implicit val timeout = mediumTimeout

    "work correctly in a fairly exhaustive example" in {
      var issue = newIssue()
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      issue.smart.open()
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      issue.smart.open()
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      issue.smart.open()
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      // same as previous but without any delay
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      // same as previous but without any delay
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      // same as previous but without any delay
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      issue.labels.add(label)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      // same as previous but without any delay
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse

      issue = newIssue()
      close(issue)
      issue.fancy.opNeverDelivered must beFalse
      reopen(issue)
      issue.fancy.opNeverDelivered must beFalse
      addLabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      longDelay()
      issue.fancy.opNeverDelivered must beTrue
      unlabel(issue)
      issue.fancy.opNeverDelivered must beFalse
      commentOn(issue)
      issue.fancy.opNeverDelivered must beFalse

      ok
    }
  }
}
