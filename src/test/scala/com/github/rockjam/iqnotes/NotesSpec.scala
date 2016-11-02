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
import com.github.rockjam.iqnotes.http.{ AuthRoutes, NotesRoutes }
import com.github.rockjam.iqnotes.models._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{ native, DefaultFormats }

class NotesSpec extends SpecBase with Json4sSupport {

  behavior of "notes create"

  it should "allow authorized user to create note" in createNoteAuthorized

  it should "not allow unauthorized user to create note" in createNoteUnauthorized

  it should "not create note with missing field(title or body)" in createNoteIncomplete

  behavior of "notes list"

  it should "list all notes to authrized user" in listNotesAuthorized

  implicit val serialization = native.Serialization
  implicit val formats       = DefaultFormats

  val authRoutes  = AuthRoutes()
  val notesRoutes = NotesRoutes()

  def createNoteAuthorized(): Unit = {
    val token = getAuthToken(UserRegisterRequest("rockjam", "hellofapassword"))
    val note = NoteDataRequest(
      Some("Note title"),
      Some("Note content, a lot of it")
    )

    Put(s"/note?access_token=${token}", note) ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.OK
      responseAs[NoteId]._id should not be empty
    }
  }

  def createNoteUnauthorized(): Unit = {
    val note = NoteDataRequest(
      Some("Note title"),
      Some("Note content, a lot of it")
    )

    // forbid when no access token
    Put(s"/note", note) ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.Forbidden
      responseAs[HttpError] shouldEqual HttpErrors.AuthError
    }

    // forbid when no access token is wrong
    val wrongToken = "123"
    Put(s"/note?access_token=${wrongToken}", note) ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.Forbidden
      responseAs[HttpError] shouldEqual HttpErrors.AuthError
    }
  }

  def createNoteIncomplete(): Unit = {
    val token = getAuthToken(UserRegisterRequest("rockjam", "hellofapassword"))

    val noBody = NoteDataRequest(Some("Note title"), None)
    Put(s"/note?access_token=${token}", noBody) ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.BadRequest
      responseAs[HttpError] shouldEqual HttpErrors.BadRequest
    }

    val noTitle = NoteDataRequest(None, Some("like a lot of content"))
    Put(s"/note?access_token=${token}", noTitle) ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.BadRequest
      responseAs[HttpError] shouldEqual HttpErrors.BadRequest
    }
  }

  private def listNotesAuthorized(): Unit = {}

  private def getAuthToken(req: UserRegisterRequest): String = {
    Post("/register", req) ~> authRoutes ~> check {
      response.status shouldEqual StatusCodes.Created
      response.entity.isKnownEmpty() shouldEqual true
    }

    val authRequest = AuthorizeRequest(req.username, req.password)
    Post("/login", authRequest) ~> authRoutes ~> check {
      response.status shouldEqual StatusCodes.OK
      val token = responseAs[AccessToken]
      token.accessToken should not be empty
      token.accessToken
    }
  }

}
