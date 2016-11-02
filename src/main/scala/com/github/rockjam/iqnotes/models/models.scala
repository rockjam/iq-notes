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

package com.github.rockjam.iqnotes.models

case class NoteDataRequest(title: Option[String], body: Option[String])

case class UserRegisterRequest(username: String, password: String)

case class AuthorizeRequest(username: String, password: String)

case class Note(_id: String, title: String, body: String)

case class NoteId(_id: String)

case class User(username: String, passwordHash: String)

case class AccessToken(accessToken: String)

case class HttpError(error: String, message: String)

object HttpErrors {
  val AuthError    = HttpError("AUTH_ERROR", "User or password wrong")
  val BadRequest   = HttpError("BAD_REQUEST", "Parameter not found")
  val SameUsername = HttpError("SAME_USERNAME", "User with this username already exists")
  val NoteNotFound = HttpError("NOTE_NOT_FOUND", "Note not found")
}
