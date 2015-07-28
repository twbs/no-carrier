package com.getbootstrap.no_carrier.github

import com.jcabi.github.{Github, RtGithub}
import com.getbootstrap.no_carrier.http.{UserAgent, SuperWire}

case class Credentials(username: String, password: String) {
  private def basicGithub: Github = new RtGithub(username, password)
  def github(threshold: Int)(implicit userAgent: UserAgent): Github = new RtGithub(basicGithub.entry.through(classOf[SuperWire], userAgent, new Integer(threshold)))
  override def toString: String = s"Credentials(username = ${username}, password = <SECRET>)"
}
