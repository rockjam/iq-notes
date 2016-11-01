import sbt._

// format: off

object Version {
  final val Akka          = "2.3.9"
  final val Json4s        = "3.4.2"
  final val ReactiveMongo = "0.12.0"
  final val Scala         = "2.11.8"
  final val ScalaTest     = "3.0.0"
  final val Spray         = "1.3.4"
  
}

object Library {
  val akka          = "com.typesafe.akka" %% "akka-actor"     % Version.Akka
  val akkaSlf4j     = "com.typesafe.akka" %% "akka-slf4j"     % Version.Akka
  val json4sJackson = "org.json4s"        %% "json4s-jackson" % Version.Json4s
  val json4sNative  = "org.json4s"        %% "json4s-native"  % Version.Json4s
  val sprayCan      = "io.spray"          %% "spray-can"      % Version.Spray
  val sprayHttp     = "io.spray"          %% "spray-http"     % Version.Spray
  val sprayHttpx    = "io.spray"          %% "spray-httpx"    % Version.Spray
  val sprayIo       = "io.spray"          %% "spray-io"       % Version.Spray
  val sprayRouting  = "io.spray"          %% "spray-routing"  % Version.Spray
  val sprayUtil     = "io.spray"          %% "spray-util"     % Version.Spray
  val reactiveMongo = "org.reactivemongo" %% "reactivemongo"  % Version.ReactiveMongo
  val scalaTest     = "org.scalatest"     %% "scalatest"      % Version.ScalaTest
}

object Dependencies {
  import Library._

  val iqNotes = Seq(
    akka,
    akkaSlf4j,
    json4sNative,
//    json4sJackson,
    sprayCan,
    sprayHttp,
    sprayHttpx,
    sprayIo,
    sprayRouting,
    sprayUtil,
    reactiveMongo
  )
}
 
  

