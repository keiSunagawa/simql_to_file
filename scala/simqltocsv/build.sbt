import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val simqlJVM = ProjectRef(file("../simql/simql"), "simql")

lazy val root = (project in file("."))
  .settings(
    name := "simqlToCsv",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc"        % "3.3.+",
  //    "com.h2database"  %  "h2"                 % "1.4.+"
//      "ch.qos.logback"  %  "logback-classic"    % "1.2.+"
    ),
    libraryDependencies += ("org.apache.spark" %% "spark-sql" % "3.0.0-SNAPSHOT-k"),
    libraryDependencies += "log4j" % "log4j" % "1.2.14",
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
//      case PathList("javax", xs @ _*)         => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.last // FIXME unsafe
      case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.last // FIXME unsafe
      case "application.conf"                            => MergeStrategy.concat
      case "unwanted.txt"                                => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
    // javaOptions in run ++= Seq(
    //   "-Dlog4j.debug=false",
    //   "-Dlog4j.configuration=log4j.properties")
  ).dependsOn(simqlJVM)

// Uncomment the following for publishing to Sonatype.
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for more detail.

// ThisBuild / description := "Some descripiton about your project."
// ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
// ThisBuild / homepage    := Some(url("https://github.com/example/project"))
// ThisBuild / scmInfo := Some(
//   ScmInfo(
//     url("https://github.com/your-account/your-project"),
//     "scm:git@github.com:your-account/your-project.git"
//   )
// )
// ThisBuild / developers := List(
//   Developer(
//     id    = "Your identifier",
//     name  = "Your Name",
//     email = "your@email",
//     url   = url("http://your.url")
//   )
// )
// ThisBuild / pomIncludeRepository := { _ => false }
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// ThisBuild / publishMavenStyle := true
