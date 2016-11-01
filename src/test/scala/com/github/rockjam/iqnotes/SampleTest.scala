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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest._

import scala.concurrent.Future

class SampleTest extends FlatSpecLike with Matchers with ScalaFutures {

  behavior of "stone"

  it should "lay on ground forever" in layForever

  it should "lay on ground in future" in layInFuture

  def layForever(): Unit =
    println("Stone lays on the ground")

  def layInFuture(): Unit = {
    val asyncLayForver = Future.successful(true).futureValue
    asyncLayForver shouldEqual true
  }
}
