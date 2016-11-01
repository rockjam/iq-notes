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

import akka.actor.{ ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import akka.dispatch.MessageDispatcher
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{ DefaultDB, MongoDriver }

import scala.concurrent.Future

final class MongoExtensionImpl(system: ActorSystem) extends Extension {

  implicit val executor: MessageDispatcher = system.dispatchers.lookup("db-dispatcher")

  private val config =
    MongoConfig
      .load(system.settings.config)
      .getOrElse(throw new RuntimeException("Failed to load mongo config."))

  private val driver = MongoDriver()
  private val conn   = Future.fromTry(driver.connection(config.mongoUri))

  private val db: Future[DefaultDB] = conn flatMap (_.database(config.dbName))

  def collection(name: String): Future[BSONCollection] = db.map(_.collection(name))
}

object MongoExtension extends ExtensionId[MongoExtensionImpl] with ExtensionIdProvider {
  def createExtension(system: ExtendedActorSystem) = new MongoExtensionImpl(system)
  def lookup()                                     = MongoExtension
}
