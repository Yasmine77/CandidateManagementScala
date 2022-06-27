name := """auth"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
///SwaggerPlugin

scalaVersion := "2.13.8"
//swaggerDomainNameSpaces := Seq("models")
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"
libraryDependencies +=jdbc
libraryDependencies +=  "mysql" % "mysql-connector-java" % "8.0.11"
libraryDependencies += "com.dedipresta" %% "scala-crypto" % "1.0.0"
// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0" % Test,
  "com.github.t3hnar" %% "scala-bcrypt" % "4.1",
)
resolvers += Resolver.bintrayRepo("iheartradio", "maven")
routesImport ++= Seq("models.enums.OfferType._")
routesImport ++= Seq("models.enums.Roles._")
routesImport ++= Seq("models.enums.AppStatus._")

libraryDependencies += "com.typesafe.play" %% "play-mailer" % "8.0.0"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "8.0.0"
