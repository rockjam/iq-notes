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

  it should "list all notes to authorized user" in listNotesAuthorized

  it should "not allow unathorized user to list notes" in listNotesUnauthorized

  behavior of "get note"

  it should "return existing note to authorized user" in getNoteAuthorized

  it should "respond with NotFound when note does not exist" in getNoteNotFound

  it should "not allow unathorized user to get note" in getNoteUnauthorized

  implicit val serialization = native.Serialization
  implicit val formats       = DefaultFormats

  private val authRoutes  = AuthRoutes()
  private val notesRoutes = NotesRoutes()

  private val defaultUserRequest = UserRegisterRequest("rockjam", "hellofapassword")
  private val defaultNoteRequest = NoteDataRequest(
    Some("Note title"),
    Some("Note content, a lot of it")
  )

  def createNoteAuthorized(): Unit = {
    val token = getAuthToken(defaultUserRequest)
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
    val token = getAuthToken(defaultUserRequest)

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

  def listNotesAuthorized(): Unit = {
    val token = getAuthToken(defaultUserRequest)

    val noteIds = createNotes(5, token) map (_._id)

    Get(s"/note?access_token=${token}") ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.OK
      val notes = responseAs[List[Note]]
      notes map (_._id) should contain theSameElementsAs noteIds
    }
  }

  def listNotesUnauthorized(): Unit = {
    val token = getAuthToken(defaultUserRequest)

    val noteIds = createNotes(5, token) map (_._id)

    // somebody tries to list notes without access token
    Get(s"/note") ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.Forbidden
      responseAs[HttpError] shouldEqual HttpErrors.AuthError
    }
  }

  def getNoteAuthorized(): Unit = {
    val token = getAuthToken(defaultUserRequest)

    val noteId = createNote(defaultNoteRequest, token)._id

    Get(s"/note/${noteId}?access_token=${token}") ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.OK
      val note = responseAs[Note]
      note._id shouldEqual noteId
      note.title shouldEqual defaultNoteRequest.title.get
      note.body shouldEqual defaultNoteRequest.body.get
    }
  }

  def getNoteNotFound(): Unit = {
    val token = getAuthToken(defaultUserRequest)

    val nonExistingNoteId = "123"
    Get(s"/note/${nonExistingNoteId}?access_token=${token}") ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.NotFound
      responseAs[HttpError] shouldEqual HttpErrors.NoteNotFound
    }
  }

  def getNoteUnauthorized(): Unit = {
    val token = getAuthToken(defaultUserRequest)

    val noteId = createNote(defaultNoteRequest, token)._id

    // somebody tries to get note without access token
    Get(s"/note/${noteId}") ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.Forbidden
      responseAs[HttpError] shouldEqual HttpErrors.AuthError
    }
  }

  private def createNotes(number: Int, accessToken: String): IndexedSeq[NoteId] =
    1 to number map { i ⇒
      createNote(
        NoteDataRequest(
          Some(s"note number ${i}"),
          Some(s"Content of note number ${i}")
        ),
        accessToken)
    }

  private def createNote(note: NoteDataRequest, accessToken: String): NoteId =
    Put(s"/note?access_token=${accessToken}", note) ~> notesRoutes ~> check {
      response.status shouldEqual StatusCodes.OK
      val noteId = responseAs[NoteId]
      noteId._id should not be empty
      noteId
    }

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
