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
import akka.http.scaladsl.model.StatusCodes.Created
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.rockjam.iqnotes.logic.AuthLogic
import com.github.rockjam.iqnotes.models._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{ native, DefaultFormats }

object AuthRoutes {
  def apply()(implicit system: ActorSystem): Route = (new AuthRoutes).routes
}

private[http] final class AuthRoutes(implicit val system: ActorSystem)
    extends HttpRoutes
    with AuthLogic
    with Json4sSupport {

  implicit val serialization = native.Serialization
  implicit val formats       = DefaultFormats

  // format: off
  def routes: Route =
    path("login") {
      post {
        entity(as[AuthorizeRequest]) { auth ⇒
          onSuccess(validateAuth(auth)) {
            case Right(res) ⇒ complete(res)
            case Left(err) ⇒ complete(err)
          }
        }
      }
    } ~
    path("register") {
      post {
        entity(as[UserRegisterRequest]) { reg ⇒
          onSuccess(register(reg)) {
            case Right(res) ⇒ complete(Created → None)
            case Left(err) ⇒ complete(err)
          }
        }
      }
    }
  // format: on
}
