/*
 * Copyright 2016 Nikolay Tatarinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rockjam.iqnotes.http

import akka.actor.ActorSystem
import com.github.rockjam.iqnotes.db.{ AccessTokensRepo, UsersRepo }
import com.github.rockjam.iqnotes.models._
import com.github.rockjam.iqnotes.utils.SecurityUtils
import org.json4s.{ DefaultFormats, Formats }
import spray.http.{ HttpEntity, StatusCode, StatusCodes }
import spray.routing.Route
import spray.routing.Directives._
import spray.httpx.Json4sSupport

import scala.concurrent.Future
import scala.util.{ Failure, Success }

// TODO: rename to AuthHttp
final class AuthHandler(implicit system: ActorSystem) extends HttpRoutes with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  import system.dispatcher

  // format: off
  def routes: Route =
    path("login") {
      post {
        entity(as[AuthCredentials]) { auth ⇒
          onComplete(validateAuth(auth)) {
            case Success(Right(res)) ⇒
              complete(res)
            case Success(Left((code, error))) ⇒
              complete(code → error)
            case Failure(err) ⇒
              system.log.error(err, "Failed to execute find user request")
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    } ~
    path("register") {
      post {
        entity(as[RegisterUser]) { reg ⇒
          onComplete(register(reg)) {
            case Success(Right(res)) ⇒
              complete(StatusCodes.Created → HttpEntity.Empty)
            case Success(Left((code, error))) ⇒
              complete(code → error)
            case Failure(err) ⇒
              system.log.error(err, "Failed to create user")
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  // format: on

  // TODO: maybe move to trait AuthHandlers
  private val users  = new UsersRepo
  private val tokens = new AccessTokensRepo

  private def logout(): Future[Unit] = ???

  private def register(reg: RegisterUser): Future[(StatusCode, HttpError) Either Unit] = {
    val user = User(reg.username, SecurityUtils.passwordHash(reg.password))
    for {
      _ <- users.create(user)
    } yield Right(())
  }

  private def validateAuth(
      auth: AuthCredentials): Future[(StatusCode, HttpError) Either AccessToken] =
    for {
      user <- users.findUser(auth.username)
      _ = println(s"=======user: ${user}")
      result <- user.fold(authError) { user ⇒
        if (SecurityUtils.isValidPassword(auth.password, user.passwordHash))
          tokens.generateAccessTokens() map Right.apply
        else
          authError
      }
    } yield result

  private val authError: Future[(StatusCode, HttpError) Either AccessToken] =
    Future.successful(Left(StatusCodes.Forbidden → HttpErrors.AuthError))

}
