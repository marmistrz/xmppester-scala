// The simplest possible sbt build file is just one line:

scalaVersion := "2.12.4"

name := "xmppester"
organization := "marmistrz"
version := "0.1"

libraryDependencies += "rocks.xmpp" % "xmpp-core-client" % "0.7.5"
libraryDependencies += "rocks.xmpp" % "xmpp-extensions-client" % "0.7.5"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.7.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xlint",                            // Enable linting
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused",                     // Warn about anything unused.
  "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
)


val _insideCI = sys.env.get("CI") == Some("true")
scalastyleFailOnWarning := _insideCI
scalacOptions ++= {
  if (_insideCI) {
    Seq("-Xfatal-warnings")
  } else {
    Seq()
  }
}
cancelable in Global := true
