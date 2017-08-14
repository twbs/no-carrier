package com.getbootstrap.no_carrier.github.util

import com.jcabi.github.Coordinates.{Simple=>RepoId}

object RepositoryId {
  private val OwnerSlashRepo = "([a-zA-Z0-9_-]+)/([a-zA-Z0-9._-]+)".r

  def unapply(ownerRepo: String): Option[RepoId] = {
    ownerRepo match {
      case OwnerSlashRepo(owner, repo) => Some(new RepoId(owner, repo))
      case _ => None
    }
  }
}
