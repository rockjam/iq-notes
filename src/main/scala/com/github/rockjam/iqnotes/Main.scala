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
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.github.rockjam.iqnotes.http.{ AuthHandler, HttpConfig, NotesHandler }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {

  implicit val system = ActorSystem("iq-notes")
  implicit val mat    = ActorMaterializer()

  import system.dispatcher

//  implicit def myRejectionHandler =
//    RejectionHandler.newBuilder()
//      .handle { case MissingCookieRejection(cookieName) =>
//        complete(HttpResponse(BadRequest, entity = "No cookies, no service!!!"))
//      }
//      .handle { case AuthorizationFailedRejection =>
//        complete((Forbidden, "You're out of your depth!"))
//      }
//      .handle { case ValidationRejection(msg, _) =>
//        complete((InternalServerError, "That wasn't valid! " + msg))
//      }
//      .handleAll[MethodRejection] { methodRejections =>
//      val names = methodRejections.map(_.supported.name)
//      complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
//    }
//      .handleNotFound { complete((NotFound, "Not here!")) }
//      .result()
//

  private val routes = pathPrefix("api") {
    (new AuthHandler).routes ~ (new NotesHandler).routes
  }

  private val config =
    HttpConfig
      .load(system.settings.config)
      .getOrElse(throw new RuntimeException(
        "Failed to get http config. Make sure you provided interface and port"))

  Http().bindAndHandle(routes, config.interface, config.port)
}
