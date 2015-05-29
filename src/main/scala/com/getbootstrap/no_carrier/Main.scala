package com.getbootstrap.no_carrier

import java.time.{Clock, Duration}
import scala.util.{Success, Failure, Properties}
import scala.util.Try
import com.jcabi.github.{Github, Issue}
import com.jcabi.github.Coordinates.{Simple=>RepoId}
import com.typesafe.scalalogging.StrictLogging
import com.getbootstrap.no_carrier.github.{Credentials, FancyIssue}
import com.getbootstrap.no_carrier.github.util._
import com.getbootstrap.no_carrier.http.UserAgent
import com.getbootstrap.no_carrier.util._


case class Arguments(
  github: Github,
  repoId: RepoId,
  label: String,
  timeout: Duration
)

object Main extends App with StrictLogging {
  val enabled = false
  implicit val clock = Clock.systemUTC
  implicit val userAgent = new UserAgent("NoCarrier/0.1 (https://github.com/twbs/no-carrier)")
  val rateLimitThreshold = 10
  val username = Env.requiredEnv("GITHUB_USERNAME")
  val password = Env.requiredEnv("GITHUB_PASSWORD")
  val arguments = (args.toSeq match {
    case Seq(RepositoryId(repoId), NonEmptyStr(label), IntFromStr(PositiveInt(dayCount))) => {
      Some(Arguments(
        Credentials(username = username, password = username).github(rateLimitThreshold),
        repoId = repoId,
        label = label,
        timeout = java.time.Duration.ofDays(dayCount)
      ))
    }
    case Seq(usernameStr, passwordStr, RepositoryId(repoId), NonEmptyStr(label), IntFromStr(PositiveInt(dayCount))) => {
      Some(Arguments(
        Credentials(username = usernameStr, password = passwordStr).github(rateLimitThreshold),
        repoId = repoId,
        label = label,
        timeout = java.time.Duration.ofDays(dayCount)
      ))
    }
    case _ => {
      System.err.println("USAGE: no-carrier <owner/repo> <label> <days>")
      System.exit(1)
      None // dead code
    }
  }).get

  main(arguments)

  def main(args: Arguments) {
    logger.info("Started session.")
    val repo = args.github.repos.get(args.repoId)

    val waitingOnOp = repo.issues.openWithLabel(args.label)
    val opNeverDelivered = waitingOnOp.filter{ issue => new FancyIssue(issue = issue, label = args.label, timeout = args.timeout).opNeverDelivered }
    val totalClosed = opNeverDelivered.map { issue =>
      if (closeOut(issue, args.timeout)) 1 else 0
    }.sum
    logger.info(s"Closed ${totalClosed} issues.")
    logger.info("Session complete; exiting.")
  }

  def closeOut(issue: Issue, timeout: Duration): Boolean = {
    logger.info(s"OP never delivered on issue #${issue.number}. Going to close it out.")
    if (enabled) {
      val explanatoryComment =
        s"""Hey there!
           |
           |We're automatically closing this issue since the original poster (or another commenter) hasn't yet responded to the question or request made to them ${timeout.toDays} days ago. We therefore assume that the user has lost interest or resolved the problem on their own. Closed issues that remain inactive for a long period may get automatically locked.
           |
           |Don't worry though; if this is in error, let us know with a comment and we'll be happy to reopen the issue.
           |
           |Thanks!""".stripMargin

      val attempt = Try{ issue.comments.post(explanatoryComment) }.flatMap{ comment => {
        logger.info(s"Posted comment #${comment.number}")
        Try{ issue.smart.close() }
      }}
      attempt match {
        case Success(_) => logger.info(s"Closed issue #${issue.number}")
        case Failure(exc) => logger.error(s"Error when trying to close out issue #${issue.number}", exc)
      }
      attempt.isSuccess
    }
    else {
      false
    }
  }
}
