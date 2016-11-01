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

import spray.http.StatusCodes
import spray.routing.Route
import spray.routing.Directives._

final class NotesHandler extends HttpRoutes {
//  curl -H "Content-Type: application/json" -X PUT -d '{"title": "note 1", "body": "some text"}' http://localhost:3000/api/note?access_token=some-uuid

  // format: off
  def routes: Route = pathPrefix("note") {
    complete(StatusCodes.OK)
  }
  // format: on
}
