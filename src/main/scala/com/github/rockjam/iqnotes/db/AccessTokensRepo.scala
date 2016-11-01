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

import java.util.UUID

import akka.actor.ActorSystem
import com.github.rockjam.iqnotes.models.AccessToken
import reactivemongo.bson.{ BSONDocumentWriter, Macros }

import scala.concurrent.Future

final class AccessTokensRepo(implicit system: ActorSystem) {

  private val mongoExt = MongoExtension(system)
  import mongoExt.executor

  private def accessTokens = mongoExt.collection("access_tokens")

  implicit val atw: BSONDocumentWriter[AccessToken] = Macros.writer[AccessToken]

  def generateAccessTokens()(implicit system: ActorSystem): Future[AccessToken] = {
    val token = AccessToken(UUID.randomUUID().toString)
    accessTokens.flatMap(_.insert(token)) map (_ ⇒ token)
  }
}
