package com.github.silverest.requester

import com.github.silverest.requester.enums.*
import sttp.client3.Response
import zio.{UIO, ZIO, ZIOAppDefault}

import java.lang.System.currentTimeMillis
import java.util

object RequesterMain extends ZIOAppDefault:
  import RequestType._

  def printThread = s"{${Thread.currentThread().getName}}"


  def concurrentRequests(requester: Requester, endpoint: String,
    reps: Int, token: Option[String] = None): Seq[UIO[String]] =
    (0 to reps).map(_ -> ZIO.succeed{
      val startTime = currentTimeMillis()
      val response = requester.get(endpoint, token = token)
      val endTime = currentTimeMillis()
      // TODO: add a fiber to store the repetitions
      endTime - startTime
    }).map(_._2)

  def oneRequest(requester: Requester, requestType: RequestType,
    endpoint: String, body: String = "") =
    for {
      result <- {
        ZIO.succeed(requester.defineRequest(requestType, endpoint, body))
      }
    } yield result

  // Temporary token extractor to test
  // request with auth and without auth
  extension (response: String)
    def extractToken: String =
      println(s"$response")
      response.split(":")(1)
        .replace("\"", "")
        .replace("}", "")

  // TODO: redo sequencing to make use of with auth and without auth
  def requestWithAuth(req: Requester, endpoint: String,
    loginEndpoint: String, creds: String) =
    oneRequest(req, Post, loginEndpoint, creds).flatMap(
      res => {
        res match
          case res: Response[String] =>
            ZIO.foreachPar(
              concurrentRequests(req, endpoint, 10,
                Some(res.body.extractToken)))(_.debug(printThread).fork)
          case _ => ZIO.fail(res).debug(printThread).fork
      }).exitCode

  def requestWithoutAuth(req: Requester, endpoint: String) =
    ZIO.foreachPar(concurrentRequests(req, endpoint, 10))(_.debug(printThread).fork).exitCode


  // TODO: Create a run function actually lol
  override def run = ???
