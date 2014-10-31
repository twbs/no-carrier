package com.getbootstrap.no_carrier.github

import scala.collection.JavaConverters._
import org.eclipse.egit.github.core.{RepositoryId, Issue}
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.IssueService

package object util {
  implicit class RichClient(client: GitHubClient) {
    def issuesService = new IssueService(client)
  }
  implicit class RichIssueService(issueService: IssueService) {
    private def pageIssues(repo: RepositoryId, filters: IssueFilters) = issueService.pageIssues(repo, filters.asFilterData.asJava)
    def issuesWhere(repo: RepositoryId, filters: IssueFilters): Iterator[Issue] = {
      val pageIter = pageIssues(repo, filters)
      val issuesIter = pageIter.iterator().asScala.flatten
      issuesIter
    }
    private def eventPages(repo: RepositoryId, issueNum: IssueNumber) = issueService.pageIssueEvents(repo.getOwner, repo.getName, issueNum.number)
    def eventsFor(repo: RepositoryId, issueNum: IssueNumber) = issueService.eventPages(repo, issueNum).iterator().asScala.flatten
  }
  implicit class RichIssue(issue: Issue) {
    def number: IssueNumber = new IssueNumber(issue.getNumber)
    def events(implicit issueService: IssueService, repo: RepositoryId) = issueService.eventsFor(repo, issue.number)
  }
}
