import sbt._

// format: off

object Version {
  final val Akka          = "2.4.12"
  final val AkkaHttp      = "2.4.11"
  final val AkkaHttpJson  = "1.10.1"
  final val Json4s        = "3.4.2"
  final val ReactiveMongo = "0.12.0"
  final val Scala         = "2.11.8"
  final val ScalaTest     = "3.0.0"
  final val Spray         = "1.3.4"
  
}

object Library {
  val akka          = "com.typesafe.akka" %% "akka-actor"             % Version.Akka
  val akkaHttp      = "com.typesafe.akka" %% "akka-http-experimental" % Version.AkkaHttp
  val akkaHttpJson  = "de.heikoseeberger" %% "akka-http-json4s"       % Version.AkkaHttpJson
  val akkaSlf4j     = "com.typesafe.akka" %% "akka-slf4j"             % Version.Akka
//  val json4sJackson = "org.json4s"        %% "json4s-jackson"         % Version.Json4s
  val json4sNative  = "org.json4s"        %% "json4s-native"          % Version.Json4s
  val reactiveMongo = "org.reactivemongo" %% "reactivemongo"          % Version.ReactiveMongo

  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % Version.AkkaHttp
  val scalaTest     = "org.scalatest"     %% "scalatest"              % Version.ScalaTest

}

object Dependencies {
  import Library._

  val iqNotes = Seq(
    akka,
    akkaHttp,
    akkaHttpJson,
    akkaSlf4j,
    json4sNative,
//    json4sJackson,
    reactiveMongo,

    akkaHttpTestKit % "test",
    scalaTest % "test"
  )
}
 
  

