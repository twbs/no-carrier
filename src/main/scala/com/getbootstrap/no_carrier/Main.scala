package com.getbootstrap.no_carrier

import scala.util.{Success,Failure}
import scala.util.Try
import org.eclipse.egit.github.core.RepositoryId
import com.getbootstrap.no_carrier.github.{IssueFilters, IssueLabel, Credentials}
import com.getbootstrap.no_carrier.github.issues_filter.{All=>AllIssues}
import com.getbootstrap.no_carrier.github.issue_state.{All=>OpenOrClosed}
import com.getbootstrap.no_carrier.github.util._


object Main extends App {
  val arguments = args.toSeq
  val argsPort = arguments match {
    case Seq(portStr: String) => {
      Try{ portStr.toInt } match {
        case Failure(_) => {
          System.err.println("USAGE: no-carrier <username> <password> <owner/repo> <label> <duration>")
          System.exit(1)
          None // dead code
        }
        case Success(portNum) => Some(portNum)
      }
    }
    case Seq() => None
  }
  implicit val repoId = RepositoryId.createFromId("twbs/bootstrap")
  val credentials = Credentials("username", "pass")
  val client = credentials.client
  implicit val issueService = client.issuesService
  val labels = Set(new IssueLabel("awaiting reply"))
  val filters = IssueFilters(filter = AllIssues, state = OpenOrClosed, labels = labels)
  val issues = issueService.issuesWhere(repoId, filters)
  for { issue <- issues } {
    for { event <- issue.events } {
      event.getEvent
      "labeled"
      "unlabeled"
    }
  }
}
