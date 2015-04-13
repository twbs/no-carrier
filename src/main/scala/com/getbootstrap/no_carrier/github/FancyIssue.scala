package com.getbootstrap.no_carrier.github

import java.time.{Clock, Instant, Duration}
import com.jcabi.github.Issue
import com.getbootstrap.no_carrier.util._
import com.getbootstrap.no_carrier.github.util._
import InstantOrdering._

class FancyIssue(val issue: Issue, val label: String, val timeout: Duration)(implicit clock: Clock) {
  lazy val lastLabelledAt: Instant = issue.lastLabelledWithAt(label).get
  lazy val lastCommentedOnAt: Instant = issue.smartComments.map{ _.createdAt.toInstant }.max
  lazy val lastClosedAt: Option[Instant] = issue.smartEvents.filter{ _.isClosed }.map{ _.createdAt.toInstant }.maxOption
  lazy val hasSubsequentComment: Boolean = lastLabelledAt < lastCommentedOnAt
  lazy val wasClosedAfterLabelling: Boolean = lastClosedAt match {
    case None => false
    case Some(closedAt) => lastLabelledAt < closedAt
  }
  lazy val isPastDeadline: Boolean = lastLabelledAt isBeyondTimeout timeout
  lazy val opNeverDelivered: Boolean = isPastDeadline && !wasClosedAfterLabelling && !hasSubsequentComment
}
