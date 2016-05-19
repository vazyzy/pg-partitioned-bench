name := "pg-inherit-table-bench"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq (
  "com.typesafe" % "config" % "1.3.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"
)

TwirlKeys.templateImports += "vazyzy.pgbench._"

lazy val root = Project("pg-inherit-table-bench", file(".")).enablePlugins(SbtTwirl)
