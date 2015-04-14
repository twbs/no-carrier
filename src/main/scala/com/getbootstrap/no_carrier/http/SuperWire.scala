package com.getbootstrap.no_carrier.http

import java.util.{Collection=>JavaCollection}
import java.util.Map.{Entry=>MapEntry}
import java.io.InputStream
import com.jcabi.http._
import com.jcabi.http.wire.RetryWire

case class SuperWire(private val wire: Wire, userAgent: UserAgent) extends Wire {
  // FIXME: use RetryCarefulWire once it's available
  private val wrappedWire = UserAgentWire(wire = new RetryWire(wire), userAgent = userAgent)

  @Override
  def send(
    request: Request,
    home: String,
    method: String,
    headers: JavaCollection[MapEntry[String, String]],
    content: InputStream
  ): Response = wrappedWire.send(request, home, method, headers, content)
}
