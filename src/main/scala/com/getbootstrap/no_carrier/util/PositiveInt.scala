package com.getbootstrap.no_carrier.util

object PositiveInt {
  def unapply(int: Int): Option[Int] = if (int > 0) Some(int) else None
}
