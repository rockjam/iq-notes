lazy val iqnotes = project
  .in(file("."))
  .settings(libraryDependencies ++= Dependencies.iqNotes)
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

libraryDependencies ++= Vector(
  Library.scalaTest % "test"
)

initialCommands := """|import com.github.rockjam.iqnotes._
                      |""".stripMargin
