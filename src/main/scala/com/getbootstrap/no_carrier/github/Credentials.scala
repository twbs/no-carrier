package com.getbootstrap.no_carrier.github

import com.jcabi.github.{Github, RtGithub}
import com.jcabi.http.wire.RetryWire

case class Credentials(username: String, password: String) {
  private def basicGithub: Github = new RtGithub(username, password)
  def github: Github = new RtGithub(basicGithub.entry.through(classOf[RetryWire])) // FIXME: use RetryCarefulWire once it's available
}
