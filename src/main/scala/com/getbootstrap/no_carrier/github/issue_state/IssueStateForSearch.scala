package com.getbootstrap.no_carrier.github.issue_state

sealed trait IssueStateForSearch {
  def codename: String
}

object All extends IssueStateForSearch {
  override def codename = "all"
}

sealed trait IssueState extends IssueStateForSearch

object Open extends IssueState {
  override def codename = "open"
}
object Closed extends IssueState {
  override def codename = "closed"
}
