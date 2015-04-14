package com.getbootstrap.no_carrier.github

import java.util.EnumMap
import java.time.Instant
import javax.json.JsonObject
import scala.util.{Try,Success}
import scala.collection.JavaConverters._
import com.jcabi.github.{Event=>IssueEvent, Issue, Issues, Search}
import com.jcabi.github.Issue.{Smart=>SmartIssue}
import com.jcabi.github.Event.{Smart=>SmartIssueEvent}
import com.jcabi.github.Comment.{Smart=>SmartComment}
import com.getbootstrap.no_carrier.util._

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
    def smartComments: Iterable[SmartComment] = issue.comments.iterate.asScala.map{ new SmartComment(_) }

    def lastLabelledWithAt(label: String): Option[Instant] = {
      val labellings = issue.smartEvents.filter{ event => event.isLabeled && event.label == Some(label) }
      labellings.map{ _.createdAt.toInstant }.lastOption
    }
  }

  implicit class RichSmartIssueEvent(event: SmartIssueEvent) {
    def isLabeled: Boolean = event.`type` == IssueEvent.LABELED
    def isClosed: Boolean = event.`type` == IssueEvent.CLOSED

    def label: Option[String] = {
      Try {Option[JsonObject](event.json.getJsonObject("label")).map {_.getString("name")}}.recoverWith {
        case _: ClassCastException => Success(None)
      }.get
    }
  }
}
