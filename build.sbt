organization := "com.mayreh"

name := "sbt-thank-you-stars"

version := "0.1-SNAPSHOT"

sbtPlugin := true

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

crossSbtVersions := Seq("0.13.16", "1.0.2")

licenses += (("Apache2.0", url("https://raw.githubusercontent.com/ocadaruma/sbt-thank-you-stars/master/LICENSE")))

startYear := Some(2017)

pomExtra in Global := {
  <url>https://github.com/ocadaruma/sbt-thank-you-stars</url>
    <scm>
      <connection>"scm:git:git@github.com:ocadaruma/sbt-thank-you-stars.git"</connection>
      <url>git@github.com:ocadaruma/sbt-thank-you-stars.git</url>
    </scm>
    <developers>
      <developer>
        <id>ocadaruma</id>
        <name>Haruki Okada</name>
      </developer>
    </developers>
}
