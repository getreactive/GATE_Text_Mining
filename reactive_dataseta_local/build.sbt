name := "LocalProcess"

version := "1.0"

scalaVersion := "2.10.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.4-SNAPSHOT"

libraryDependencies += "com.typesafe.akka" % "akka-remote_2.10" % "2.4-SNAPSHOT"

libraryDependencies += "org.json" % "json" % "20090211"

libraryDependencies += "com.maxmind.geoip" % "geoip-api" % "1.2.10"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"

libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1.1"

libraryDependencies += "commons-codec" % "commons-codec" % "1.5"

libraryDependencies += "org.mongodb" % "casbah-commons_2.10" % "2.5.0"

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "2.10.1"


