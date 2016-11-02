version := "0.0.1-SNAPSHOT"

lazy val iqnotes = project
  .in(file("."))
  .settings(libraryDependencies ++= Dependencies.iqNotes)
  .settings(
    defaultLinuxInstallLocation in Docker := "/var/lib/iqnotes"
  )
  .enablePlugins(
    JavaServerAppPackaging,
    DockerPlugin,

    AutomateHeaderPlugin,
    GitVersioning
  )

initialCommands := """|import com.github.rockjam.iqnotes._
                      |""".stripMargin
