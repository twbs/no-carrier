package com.getbootstrap.no_carrier.util

import scala.util.Try

object IntFromStr {
  def unapply(str: String): Option[Int] = Try{ Integer.parseInt(str) }.toOption
}
