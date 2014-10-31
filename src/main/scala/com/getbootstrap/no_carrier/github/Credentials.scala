package com.getbootstrap.no_carrier.github

import org.eclipse.egit.github.core.client.GitHubClient

case class Credentials(username: String, password: String) {
  def client = {
    val c = new GitHubClient()
    c.setCredentials(username, password)
    c
  }
}
