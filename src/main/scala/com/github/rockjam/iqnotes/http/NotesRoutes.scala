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
import akka.http.scaladsl.model.StatusCodes.{ Forbidden, NoContent }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive0, Route }
import com.github.rockjam.iqnotes.logic.NotesLogic
import com.github.rockjam.iqnotes.models._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{ native, DefaultFormats }

import scala.util.Success

object NotesRoutes {
  def apply()(implicit system: ActorSystem): Route = (new NotesRoutes).routes
}

private[http] final class NotesRoutes(implicit val system: ActorSystem)
    extends HttpRoutes
    with NotesLogic
    with Json4sSupport {

  implicit val serialization = native.Serialization
  implicit val formats       = DefaultFormats

  // format: off
  def routes: Route =
    authorizeUser {
      pathPrefix("note" / Segment) { noteId ⇒
        // get note by id
        get {
          onSuccess(findNote(noteId)) {
            case Right(note) ⇒ complete(note)
            case Left(err) ⇒ complete(err)
          }
        } ~
        // update note by id
        post {
          entity(as[NoteDataRequest]) { req ⇒
            onSuccess(updateNote(noteId, req)) {
              case Right(_) ⇒ complete(NoContent)
              case Left(err) ⇒ complete(err)
            }
          }
        } ~
        // delete note by id
        delete {
          onSuccess(deleteNote(noteId)) {
            complete(NoContent)
          }
        }
      } ~
      pathPrefix("note") {
        // create new note
        put {
          entity(as[NoteDataRequest]) { req ⇒
            onSuccess(createNote(req)) {
              case Right(noteId) ⇒ complete(noteId)
              case Left(err) ⇒ complete(err)
            }
          }
        } ~
        // get list of all notes
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

  private def authorizeUser: Directive0 =
    parameters("access_token".?) flatMap {
      case Some(accessToken) ⇒
        extractExecutionContext flatMap { implicit ec ⇒
          onComplete(tokenExists(accessToken)).flatMap {
            case Success(true) ⇒ pass
            case _             ⇒ complete(unauthorizedError)
          }
        }
      case None ⇒ complete(unauthorizedError)
    }

  private val unauthorizedError: (StatusCode, HttpError) = Forbidden → HttpErrors.AuthError

}
