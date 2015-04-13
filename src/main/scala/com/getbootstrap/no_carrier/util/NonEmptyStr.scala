package com.getbootstrap.no_carrier.util

object NonEmptyStr {
  def unapply(str: String): Option[String] = if (str.nonEmpty) Some(str) else None
}
