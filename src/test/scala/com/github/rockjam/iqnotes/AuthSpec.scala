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

package com.github.rockjam.iqnotes

import akka.http.scaladsl.model.StatusCodes
import com.github.rockjam.iqnotes.http.AuthRoutes
import com.github.rockjam.iqnotes.models._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{ native, DefaultFormats }

class AuthSpec extends SpecBase with Json4sSupport {
  behavior of "authorization"

  it should "grant access token to existing user with correct password" in grantToken()

  it should "not grant access to user with wrong password" in dontGrantWrongPass()

  it should "not grant access to not registered user" in dontGrantNotRegistered()

  it should "not allow to register users with existing username" in dontRegisterSameUsername()

  implicit val serialization = native.Serialization
  implicit val formats       = DefaultFormats

  val authRoutes = AuthRoutes()

  def grantToken(): Unit = {
    val regRequest = UserRegisterRequest("rockjam", "hellofapassword")
    registerUser(regRequest)

    val authRequest = AuthorizeRequest(regRequest.username, regRequest.password)
    Post("/login", authRequest) ~> authRoutes ~> check {
      response.status shouldEqual StatusCodes.OK
      responseAs[AccessToken].accessToken should not be empty
    }
  }

  def dontGrantWrongPass(): Unit = {
    val user = UserRegisterRequest("rockjam", "hellofapassword")
    registerUser(user)

    val authRequest = AuthorizeRequest(user.username, "some wrong password")
    Post("/login", authRequest) ~> authRoutes ~> check {
      response.status shouldEqual StatusCodes.Forbidden
      responseAs[HttpError] shouldEqual HttpErrors.AuthError
    }
  }

  def dontGrantNotRegistered(): Unit = {
    val authRequest = AuthorizeRequest("burglar", "secret")
    Post("/login", authRequest) ~> authRoutes ~> check {
      response.status shouldEqual StatusCodes.Forbidden
      responseAs[HttpError] shouldEqual HttpErrors.AuthError
    }
  }

  def dontRegisterSameUsername(): Unit = {
    val firstReq = UserRegisterRequest("rockjam", "hellofapassword")
    registerUser(firstReq)

    val secondReq = UserRegisterRequest("rockjam", "sercretpassword")
    Post("/register", secondReq) ~> authRoutes ~> check {
      response.status shouldEqual StatusCodes.BadRequest
      responseAs[HttpError] shouldEqual HttpErrors.SameUsername
    }
  }

  private def registerUser(req: UserRegisterRequest): Unit =
    Post("/register", req) ~> authRoutes ~> check {
      response.status shouldEqual StatusCodes.Created
      response.entity.isKnownEmpty() shouldEqual true
    }

}
