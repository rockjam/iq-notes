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

package com.github.rockjam.iqnotes.logic

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ StatusCode, StatusCodes }
import com.github.rockjam.iqnotes.db.{ AccessTokensCollection, UsersCollection }
import com.github.rockjam.iqnotes.models._
import com.github.rockjam.iqnotes.utils.SecurityUtils

import scala.concurrent.Future

trait AuthLogic {

  implicit val system: ActorSystem
  import system.dispatcher

  private lazy val users  = new UsersCollection
  private lazy val tokens = new AccessTokensCollection

//  // TODO: implement logout with token deletion
//  private def logout(): Future[Unit] = ???

  protected def register(reg: UserRegisterRequest): Future[(StatusCode, HttpError) Either Unit] =
    for {
      exists <- users.exists(reg.username)
      user = User(reg.username, SecurityUtils.passwordHash(reg.password))
      result <- if (exists) sameUsernameError else users.create(user) map Right.apply
    } yield result

  protected def validateAuth(
      auth: AuthorizeRequest): Future[(StatusCode, HttpError) Either AccessToken] =
    for {
      user <- users.find(auth.username)
      result <- user.fold(authError) { user ⇒
        if (SecurityUtils.isValidPassword(auth.password, user.passwordHash))
          tokens.generateAccessTokens() map Right.apply
        else
          authError
      }
    } yield result

  private val sameUsernameError =
    Future.successful(Left(StatusCodes.BadRequest → HttpErrors.SameUsername))

  private val authError: Future[(StatusCode, HttpError) Either AccessToken] =
    Future.successful(Left(StatusCodes.Forbidden → HttpErrors.AuthError))

}
