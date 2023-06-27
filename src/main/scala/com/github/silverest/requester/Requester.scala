package com.github.silverest.requester

import com.github.silverest.requester.*
import com.github.silverest.requester.enums.*
import sttp.client3.*
import sttp.client3.circe.*
import sttp.model.Uri.Segment

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

  def get(path: Path, body: Option[String] = None,
          token: Option[String] = None)
          (responseType: ResponseType = ResponseType.STRING) = 
    basicRequest
      .get(endpointUrl.addPathSegments(path.toSegment))
      .response(responseType.get)
      .send(backend)

  def post(path: Path, body: Option[String] = None,
           token: Option[String] = None)
          (responseType: ResponseType = ResponseType.STRING) =
    basicRequest
      .post(endpointUrl.addPathSegments(path.toSegment))
      .response(responseType.get)
      .send(backend)

  def put(path: Path, body: Option[String] = None)
         (responseType: ResponseType = ResponseType.STRING) =
    basicRequest
      .put(endpointUrl.addPathSegments(path.toSegment))
      .response(responseType.get)
      .send(backend)

  def patch(path: Path, body: Option[String] = None)
         (responseType: ResponseType = ResponseType.STRING) =
    basicRequest
      .patch(endpointUrl.addPathSegments(path.toSegment))
      .response(responseType.get)
      .send(backend)

  def delete(path: Path, body: Option[String] = None)
            (responseType: ResponseType = ResponseType.STRING) = 
    basicRequest
      .delete(endpointUrl.addPathSegments(path.toSegment))
      .response(responseType.get)
      .send(backend)

  def defineRequest(requestType: RequestType, endpoint: String, body: Option[String] = None) =
    import RequestType._
    requestType match
      case Get => this.get(endpoint, body)
      case Post => this.post(endpoint, body)
      case Put => this.put(endpoint, body)
      case Delete => this.delete(endpoint, body)
