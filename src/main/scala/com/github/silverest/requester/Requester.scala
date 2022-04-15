package com.github.silverest.requester

import com.github.silverest.requester._
import com.github.silverest.requester.enums._
import sttp.client3.*
import sttp.client3.circe.*
import sttp.model.Uri.Segment
import zio.*
import zio.ZIO.debug

import java.lang.System.currentTimeMillis

case class Requester(endpoint: String):
  private val backend = HttpURLConnectionBackend()
  val endpointUrl = uri"$endpoint"

  type Path = String

  extension (path: Path)
    def toSegment: Seq[Segment] =
      path.split("/").toSeq.filter(_.nonEmpty).map(Segment(_, identity))

  extension (request: Request[Either[String, String], Any])
    def maybeAuth(token: Option[String]) =
      token.fold(request)(t => request.auth.bearer(t))

  def get(path: Path,
          responseType: ResponseType = ResponseType.String,
          token: Option[String] = None): Response[String] =
    basicRequest.get(endpointUrl.addPathSegments(path.toSegment))
      .maybeAuth(token)
      .response(asStringAlways)
      .send(backend)

  def post(path: Path, body: String): Response[String]  =
    basicRequest
      .post(endpointUrl.addPathSegments(path.toSegment))
      .contentType("application/json")
      .body(body)
      .response(asStringAlways)
      .send(backend)

  def put(path: Path, body: String): Response[String] =
    basicRequest
      .put(endpointUrl.addPathSegments(path.toSegment))
      .body(body)
      .response(asStringAlways)
      .send(backend)

  def delete(path: Path): Response[String]  =
    basicRequest
      .delete(endpointUrl.addPathSegments(path.toSegment))
      .response(asStringAlways)
      .send(backend)

  def close: URIO[Any, Unit] =
    ZIO.succeed(backend.close()).debug("Closing backend")

  def defineRequest(requestType: RequestType, endpoint: String, body: String = ""): Object =
    import RequestType._
    requestType match
      case Get => this.get(endpoint)
      case Post => this.post(endpoint, body)
      case Put => this.put(endpoint, body)
      case Delete => this.delete(endpoint)
