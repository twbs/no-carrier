package com.getbootstrap.no_carrier.github

import java.util.EnumMap
import java.time.Instant
import scala.util.Try
import scala.collection.JavaConverters._
import com.jcabi.github.{Event => IssueEvent, _}
import com.jcabi.github.Issue.{Smart=>SmartIssue}
import com.jcabi.github.Event.{Smart=>SmartIssueEvent}
import com.jcabi.github.Comment.{Smart=>SmartComment}
import com.jcabi.github.Repos.RepoCreate
import com.getbootstrap.no_carrier.util.GoogleToScalaOptional

package object util {
  implicit class RichIssues(issues: Issues) {
    private def openWithLabelQuery(label: String) = {
      val params = new EnumMap[Issues.Qualifier, String](classOf[Issues.Qualifier])
      params.put(Issues.Qualifier.STATE, issue_state.Open.codename)
      params.put(Issues.Qualifier.LABELS, label)
      params
    }
    def openWithLabel(label: String): Iterable[Issue] = issues.search(Issues.Sort.UPDATED, Search.Order.ASC, openWithLabelQuery(label)).asScala
  }

  implicit class RichIssue(issue: Issue) {
    def smart: SmartIssue = new SmartIssue(issue)
    def smartEvents: Iterable[SmartIssueEvent] = issue.events.asScala.map{ new SmartIssueEvent(_) }
    def commentsIterable: Iterable[Comment] = issue.comments.iterate.asScala

    def lastLabelledWithAt(label: String): Option[Instant] = {
      val labellings = issue.smartEvents.filter{ event => event.labelOption.exists{ _.name == label } }
      labellings.lastOption.map{ _.createdAt.toInstant }
    }
  }

  implicit class RichSmartIssue(issue: SmartIssue) {
    def lastClosure: Option[IssueEvent] = latestEventOption(IssueEvent.CLOSED)
    def lastReopening: Option[IssueEvent] = latestEventOption(IssueEvent.REOPENED)
    def latestEventOption(eventType: String): Option[IssueEvent] = {
      Try{ Some(issue.latestEvent(eventType)) }.recover{
        case _:IllegalStateException => None
      }.get
    }
  }

  implicit class RichIssueEvent(event: IssueEvent) {
    def smart: SmartIssueEvent = new SmartIssueEvent(event)
  }

  implicit class RichSmartIssueEvent(event: SmartIssueEvent) {
    def labelOption: Option[Label] = event.label.toOption
  }

  implicit class RichComment(comment: Comment) {
    def smart: SmartComment = new SmartComment(comment)
  }

  implicit class RichRepos(repos: Repos) {
    def create(name: String): Repo = {
      repos.create(new RepoCreate(name, true))
    }
  }

  implicit class RichIssueLabels(labels: IssueLabels) {
    def smart: IssueLabels.Smart = new IssueLabels.Smart(labels)
    def add(label: String) {
      val singleton = new java.util.LinkedList[String]()
      singleton.add(label)
      labels.add(singleton)
    }
  }
}
