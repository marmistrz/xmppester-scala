// The simplest possible sbt build file is just one line:

scalaVersion := "2.12.4"

name := "xmppester"
organization := "marmistrz"
version := "1.0"

libraryDependencies += "rocks.xmpp" % "xmpp-core-client" % "0.7.5"

