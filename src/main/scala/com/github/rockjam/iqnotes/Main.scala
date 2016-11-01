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

import akka.actor.ActorSystem
import com.github.rockjam.iqnotes.http.{ AuthHandler, HttpConfig, NotesHandler }
import spray.routing.SimpleRoutingApp

object Main extends SimpleRoutingApp with App {

  implicit val system = ActorSystem("iq-notes")

  private val routes = pathPrefix("api") {
    (new AuthHandler).routes ~ (new NotesHandler).routes
  }

  private val config =
    HttpConfig
      .load(system.settings.config)
      .getOrElse(throw new RuntimeException(
        "Failed to get http config. Make sure you provided interface and port"))

  startServer(config.interface, config.port)(routes)
}
