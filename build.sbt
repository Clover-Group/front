
val Http4sVersion = "0.20.0"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val PureConfigVersion = "0.10.2"
val CatsEffectVersion="1.2.0"
val CatsCoreVersion="2.0.0-M1"
//val KindProjVersion = "0.10.0"
val KindProjVersion = "0.9.3"
val BetterMonadicForVersion = "0.3.0"

resolvers += Resolver.sonatypeRepo("releases")

lazy val root = (project in file("."))
  .settings(
    organization := "CloverGroup",
    name := "front",
    version := "0.1.0",
    scalaVersion := "2.12.8",
    scalacOptions ++= Seq("-Ypartial-unification"),
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client"  % Http4sVersion,
      "org.http4s"      %% "http4s-circe"         % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"           % Http4sVersion,
      "io.circe"        %% "circe-generic"        % CirceVersion,
      "org.specs2"      %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"   % "logback-classic"      % LogbackVersion, 

      "org.typelevel"   %% "cats-core"            % CatsCoreVersion, 
      "org.typelevel"   %% "cats-effect"          % CatsEffectVersion,
      "com.github.pureconfig" %% "pureconfig"     % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion
      //"com.github.pureconfig" %% "pureconfig-fs2" % PureConfigVersion
    )
    //addCompilerPlugin("org.spire-math" %% "kind-projector"     % KindProjVersion),
    //addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % BetterMonadicForVersion)
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)
