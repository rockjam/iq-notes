version := "0.0.1-SNAPSHOT"

lazy val iqnotes = project
  .in(file("."))
  .settings(libraryDependencies ++= Dependencies.iqNotes)
  .settings(
    defaultLinuxInstallLocation in Docker := "/var/lib/iqnotes"
  )
  .settings(testSettings)
  .enablePlugins(
    JavaServerAppPackaging,
    DockerPlugin,

    AutomateHeaderPlugin,
    GitVersioning
  )

lazy val testSettings = Seq(
  fork in Test := false,
  parallelExecution in Test := false
)

initialCommands := """|import com.github.rockjam.iqnotes._
                      |""".stripMargin
