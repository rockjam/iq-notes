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
import com.github.rockjam.iqnotes.models.User
import reactivemongo.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros }

import scala.concurrent.Future

final class UsersRepo(implicit system: ActorSystem) {

  private val mongoExt = MongoExtension(system)
  import mongoExt.executor

  private def users = mongoExt.collection("users")

  private implicit val uw: BSONDocumentWriter[User] = Macros.writer[User]
  private implicit val ur: BSONDocumentReader[User] = Macros.reader[User]

  def findUser(username: String): Future[Option[User]] =
    users.flatMap(_.find(BSONDocument("username" → username)).one[User])

  // TODO: make sure username is unique
  def create(user: User): Future[Unit] =
    users.flatMap(_.insert(user)) map (_ ⇒ ())

}
