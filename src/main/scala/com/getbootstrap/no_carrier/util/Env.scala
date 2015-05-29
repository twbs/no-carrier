package com.getbootstrap.no_carrier.util

class MissingEnvVar(msg: String) extends RuntimeException(msg)


object Env {
  def optionalEnv(key: String, default: Option[Any] = None): Option[Any] = {
    default match {
      case None    => None
      case default => sys.env.get(key).orElse(default)
    }
  }

  def requiredEnv(key: String) = {
    sys.env.get(key) match {
      case Some(v) => v
      case None    => throw(
        new MissingEnvVar(s"$key")
      )
    }
  }

}
