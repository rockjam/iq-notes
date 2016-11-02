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
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes._
import com.github.rockjam.iqnotes.db.{ AccessTokensCollection, NotesCollection }
import com.github.rockjam.iqnotes.models._

import scala.concurrent.Future

trait NotesLogic {

  implicit val system: ActorSystem
  import system.dispatcher

  private lazy val tokens = new AccessTokensCollection
  private lazy val notes  = new NotesCollection

  protected def tokenExists(token: String): Future[Boolean] = tokens.exists(token)

  // TODO: add pagination. requires some sort key in note(timestamp for example)
  protected def listNotes(): Future[List[Note]] = notes.findAll()

  protected def createNote(data: NoteDataRequest): Future[(StatusCode, HttpError) Either NoteId] = {
    val optData = for {
      title <- data.title
      body  <- data.body
    } yield title → body

    optData map {
      case (title, body) ⇒
        notes.create(title, body) map Right.apply
    } getOrElse Future.successful(Left(BadRequest → HttpErrors.BadRequest))
  }

  protected def findNote(noteId: String): Future[(StatusCode, HttpError) Either Note] =
    notes.find(noteId) map (_.fold(notFoundError)(Right.apply))

  protected def updateNote(noteId: String,
                           data: NoteDataRequest): Future[(StatusCode, HttpError) Either Unit] =
    // we allow to update single field in update request
    if (data.title.isDefined || data.body.isDefined)
      notes.update(noteId, data.title, data.body) map Right.apply
    else
      Future.successful(Left(BadRequest → HttpErrors.BadRequest))

  protected def deleteNote(noteId: String): Future[Unit] = notes.remove(noteId)

  private val notFoundError: (StatusCode, HttpError) Either Note =
    Left(NotFound → HttpErrors.NoteNotFound)

}
