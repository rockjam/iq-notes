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

package com.github.rockjam.iqnotes.utils

import akka.actor.ActorSystem
import org.apache.commons.codec.digest.DigestUtils

object SecurityUtils {

  private def secretKey()(implicit system: ActorSystem): String =
    system.settings.config.getString("secret")

  def passwordHash(pass: String)(implicit system: ActorSystem): String =
    passwordHash(pass, secretKey())

  def isValidPassword(userInput: String, passHash: String)(implicit system: ActorSystem): Boolean =
    passwordHash(userInput) == passHash

  private def passwordHash(pass: String, secret: String): String =
    DigestUtils.sha256Hex(s"${pass}:${secret}".getBytes)

}
