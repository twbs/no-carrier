package com.getbootstrap.no_carrier.github

import java.time.{Clock, Instant, Duration}
import com.jcabi.github.Issue
import com.getbootstrap.no_carrier.util._
import com.getbootstrap.no_carrier.github.util._
import InstantOrdering._

class FancyIssue(val issue: Issue, val label: String, val timeout: Duration)(implicit clock: Clock) {
  lazy val lastLabelledAt: Option[Instant] = issue.lastLabelledWithAt(label)
  lazy val lastCommentedOnAt: Option[Instant] = issue.commentsIterable.lastOption.map{ _.smart.createdAt.toInstant }
  lazy val lastClosedAt: Option[Instant] = issue.smart.lastClosure.map{ _.smart.createdAt.toInstant }
  lazy val lastReopenedAt: Option[Instant] = issue.smart.lastReopening.map{ _.smart.createdAt.toInstant }
  lazy val hasSubsequentComment: Boolean = (lastLabelledAt, lastCommentedOnAt) match {
    case (Some(labeledAt), Some(commentedAt)) => labeledAt < commentedAt
    case _ => false
  }
  lazy val wasClosedAfterLabelling: Boolean = (lastLabelledAt, lastClosedAt) match {
    case (Some(labeledAt), Some(closedAt)) => labeledAt < closedAt
    case _ => false
  }
  lazy val wasReopenedAfterLabelling: Boolean = (lastLabelledAt, lastReopenedAt) match {
    case (Some(labeledAt), Some(reopenedAt)) => labeledAt < reopenedAt
    case _ => false
  }
  lazy val opennessChangedAfterLabelling: Boolean = wasClosedAfterLabelling || wasReopenedAfterLabelling
  lazy val isPastDeadline: Boolean = lastLabelledAt.exists{ _ isBeyondTimeout timeout }
  lazy val opNeverDelivered: Boolean = {
    val res = issue.smart.isOpen && issue.labels.smart.contains(label) && isPastDeadline && !opennessChangedAfterLabelling && !hasSubsequentComment
    print(".")
    res
  }
}
