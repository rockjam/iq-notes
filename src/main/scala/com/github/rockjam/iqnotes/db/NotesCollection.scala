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

package com.github.rockjam.iqnotes.db

import akka.actor.ActorSystem
import com.github.rockjam.iqnotes.models.{ Note, NoteId }
import reactivemongo.bson.{
  BSONDocument,
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  BSONString,
  Macros
}

import scala.concurrent.Future

final class NotesCollection(implicit system: ActorSystem) extends Collection("notes")(system) {

  private implicit val nr: BSONDocumentReader[Note]  = Macros.reader[Note]
  private implicit val rnw: BSONDocumentWriter[Note] = Macros.writer[Note]

  def findAll(): Future[List[Note]] =
    collection.flatMap(
      _.find(BSONDocument.empty).cursor[Note]().collect[List](1000, stopOnError = true)
    )

  def find(id: String): Future[Option[Note]] =
    collection.flatMap(_.find(BSONDocument("_id" → id)).one[Note])

  def create(title: String, body: String): Future[NoteId] = {
    val id = BSONObjectID.generate().stringify
    collection.flatMap(_.insert(Note(id, title, body))) map (_ ⇒ NoteId(id))
  }

  def update(noteId: String, title: Option[String], body: Option[String]): Future[Unit] = {
    val updateFields = title.map("title" → BSONString(_)) ++ body.map("body" → BSONString(_))
    collection.flatMap(
      _.findAndUpdate(
        selector = BSONDocument("_id" → noteId),
        update = BSONDocument("$set"  → BSONDocument(updateFields))
      ) map (_ ⇒ ()))
  }

  def remove(noteId: String): Future[Unit] =
    collection.flatMap(_.remove(BSONDocument("_id" → noteId))) map (_ ⇒ ())

}
