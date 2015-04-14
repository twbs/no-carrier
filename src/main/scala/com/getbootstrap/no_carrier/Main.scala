package com.getbootstrap.no_carrier

import java.time.{Clock, Duration}
import scala.util.{Success, Failure}
import scala.util.Try
import com.jcabi.github.{Github, Issue}
import com.jcabi.github.Coordinates.{Simple=>RepoId}
import com.typesafe.scalalogging.StrictLogging
import com.getbootstrap.no_carrier.util._
import com.getbootstrap.no_carrier.github.{Credentials, FancyIssue}
import com.getbootstrap.no_carrier.github.util._

case class Arguments(
  github: Github,
  repoId: RepoId,
  label: String,
  timeout: Duration
)

object Main extends App with StrictLogging {
  val enabled = false
  implicit val clock = Clock.systemUTC
  val arguments = (args.toSeq match {
    case Seq(username, password, RepositoryId(repoId), NonEmptyStr(label), IntFromStr(PositiveInt(dayCount))) => {
      Some(Arguments(
        Credentials(username = username, password = password).github,
        repoId = repoId,
        label = label,
        timeout = java.time.Duration.ofDays(dayCount)
      ))
    }
    case _ => {
      System.err.println("USAGE: no-carrier <username> <password> <owner/repo> <label> <days>")
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
        s"""This issue is being automatically closed since the original poster (or another relevant commenter) hasn't responded to the question or request made to them ${timeout.toDays} days ago.
           |We are therefore assuming that the user has lost interest in this issue or was able to resolve their problem on their own.
           |If the user does later end up responding, a team member will be happy to reopen this issue.
           |After a long period of further inactivity, this issue may get automatically locked.
           |""".stripMargin
      issue.smart.createdAt.toInstant
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
