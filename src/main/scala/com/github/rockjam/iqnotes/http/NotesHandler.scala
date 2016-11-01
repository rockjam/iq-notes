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
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.{ BadRequest, NoContent, NotFound }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ AuthorizationFailedRejection, Directive0, Route }
import com.github.rockjam.iqnotes.db.{ AccessTokensCollection, NotesCollection }
import com.github.rockjam.iqnotes.models._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{ native, DefaultFormats }

import scala.concurrent.Future

final class NotesHandler(implicit system: ActorSystem) extends HttpRoutes with Json4sSupport {

  import system.dispatcher

  implicit val serialization = native.Serialization
  implicit val formats       = DefaultFormats

  // format: off
  def routes: Route =
    authorizeUser {
      pathPrefix("note" / Segment) { noteId ⇒
        // √ get note by id
        get {
          onSuccess(findNote(noteId)) {
            case Right(note) ⇒ complete(note)
            case Left(err) ⇒ complete(err)
          }
        } ~
        // √ update note by id
        post {
          entity(as[NoteDataRequest]) { req ⇒
            onSuccess(updateNote(noteId, req)) {
              case Right(_) ⇒ complete(NoContent)
              case Left(err) ⇒ complete(err)
            }
          }
        } ~
        // √ delete note by id
        delete {
          onSuccess(deleteNote(noteId)) {
            complete(NoContent)
          }
        }
      } ~
      pathPrefix("note") {
        // √ create new note
        put {
          entity(as[NoteDataRequest]) { req ⇒
            onSuccess(createNote(req)) {
              case Right(noteId) ⇒ complete(noteId)
              case Left(err) ⇒ complete(err)
            }
          }
        } ~
        // get list of all notes
        // TODO: add pagination. requires some sort key in note(timestamp for example)
        get {
          parameter("page".?) { page ⇒
            onSuccess(listNotes()) { notes ⇒
              complete(notes)
            }
          }
        }
      }
    }
  // format: on

//  TODO: handle AuthorizationFailedRejection
  private def authorizeUser: Directive0 =
    parameters("access_token".?) flatMap {
      case Some(accessToken) ⇒ authorizeAsync(tokenExists(accessToken))
      case None              ⇒ reject(AuthorizationFailedRejection)
    }

  // TODO: move to separate trait
  private val tokens = new AccessTokensCollection
  private val notes  = new NotesCollection

  def tokenExists(token: String): Future[Boolean] = tokens.exists(token)

  // TODO: add pagination
  def listNotes(): Future[List[Note]] = notes.findAll()

  def createNote(data: NoteDataRequest): Future[(StatusCode, HttpError) Either NoteId] = {
    val optData = for {
      title <- data.title
      body  <- data.body
    } yield title → body

    optData map {
      case (title, body) ⇒
        notes.create(title, body) map Right.apply
    } getOrElse Future.successful(Left(BadRequest → HttpErrors.BadRequest))
  }

  def findNote(noteId: String): Future[(StatusCode, HttpError) Either Note] =
    notes.find(noteId) map (_.fold(notFoundError)(Right.apply))

  def updateNote(noteId: String,
                 data: NoteDataRequest): Future[(StatusCode, HttpError) Either Unit] =
    // we allow to update single field at one time
    if (data.title.isDefined || data.body.isDefined)
      notes.update(noteId, data.title, data.body) map Right.apply
    else
      Future.successful(Left(BadRequest → HttpErrors.BadRequest))

  def deleteNote(noteId: String): Future[Unit] = notes.remove(noteId)

  private val notFoundError: (StatusCode, HttpError) Either Note =
    Left(NotFound → HttpErrors.NoteNotFound)

}
