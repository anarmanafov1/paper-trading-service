val Http4sVersion = "1.0.0-M38"
val CirceVersion = "0.14.3"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.11"
val MunitCatsEffectVersion = "1.0.7"

lazy val root = (project in file("."))
  .settings(
    organization := "com.cp",
    name := "com",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.10",
    libraryDependencies ++= Seq(
      "org.http4s"            %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"            %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"            %% "http4s-circe"        % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"          % Http4sVersion,
      "io.circe"              %% "circe-generic"       % CirceVersion,
      "org.scalameta"         %% "munit"               % MunitVersion           % Test,
      "org.typelevel"         %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"        %  "logback-classic"     % LogbackVersion         % Runtime,
      "org.typelevel"         %% "log4cats-slf4j"      % "2.6.0",
      "com.github.pureconfig" %% "pureconfig"          % "0.17.1",
      "org.scalameta"         %% "svm-subs"            % "20.2.0",
      "org.scalatestplus"     %% "mockito-4-11"        % "3.2.16.0" % "test"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
