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
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.{ ExecutionContext, Future }

abstract class Collection(name: String)(implicit system: ActorSystem) {
  private val mongoExt = MongoExtension(system)

  protected implicit val ec: ExecutionContext = mongoExt.executor

  protected val collection: Future[BSONCollection] = mongoExt.collection(name)
}
