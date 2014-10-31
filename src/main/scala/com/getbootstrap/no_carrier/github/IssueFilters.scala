package com.getbootstrap.no_carrier.github

import com.getbootstrap.no_carrier.github.issue_state.IssueStateForSearch
import com.getbootstrap.no_carrier.github.issues_filter.IssuesFilter

case class IssueFilters(filter: IssuesFilter, state: IssueStateForSearch, labels: Set[IssueLabel]) {
  def asFilterData: Map[String, String] = Map(
    "filter" -> filter.codename,
    "state" -> state.codename,
    "labels" -> labels.map{ label => label.name }.mkString(",")
  )
}
