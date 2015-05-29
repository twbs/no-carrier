package com.getbootstrap.no_carrier.util

class MissingEnvVar(msg: String) extends IllegalArgumentException(msg)

object EnvVars {
  def getRequired(key: String): String = {
    sys.env.get(key) match {
      case Some(v) => v
      case None => throw new IllegalArgumentException(s"Missing required environment variable $$${key} !")
    }
  }
}
