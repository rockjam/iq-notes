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

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.rockjam.iqnotes.db.MongoExtension
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }
import org.scalatest.{ BeforeAndAfterEach, FlatSpecLike, Matchers }

trait SpecBase
    extends FlatSpecLike
    with Matchers
    with ScalaFutures
    with BeforeAndAfterEach
    with ScalatestRouteTest {

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(15, Seconds))

  override def beforeEach: Unit =
    whenReady(MongoExtension(system).db.flatMap { db ⇒
      db.drop()
    })(_ ⇒ ())

}
