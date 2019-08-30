import sbtcrossproject.{CrossType, crossProject}
import com.typesafe.sbt.packager.archetypes._

lazy val scalaV = "2.12.6"
val airframeVersion = "0.45"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint") // AWS only supports Java 8

val serverDeps = Seq(
    "software.amazon.awssdk" % "dynamodb" % "2.7.26",
    "com.typesafe.play" %% "play-json" % "2.6.9",
    "com.lihaoyi" %% "autowire" % "0.2.6",
    "com.typesafe" % "config" % "1.3.4",
    "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
    "com.softwaremill.macwire" % "macros_2.11" % "2.3.3"
)

lazy val root = Project(id = "lambda-scala-function-root",
    base = file(".")).aggregate(lambda,lambdaOffline,client).settings(
    run := {
        (run in lambdaOffline in Compile).evaluated
    }
).disablePlugins(RevolverPlugin)

lazy val lambda = (project in file("lambda")).
  settings(
    name := "lambda",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= serverDeps,
    retrieveManaged := true,
    mainClass in assembly := Some("lambda.LambdaHandler"),
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false, includeDependency = false),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case n if n.startsWith("reference.conf") => MergeStrategy.concat // make akka and sbt-assembly play nice
      //case PathList("scala","test","resources","test.conf") => MergeStrategy.discard
      case x => MergeStrategy.first
    },
  assemblyOutputPath in assemblyPackageDependency := {
      file("lambda/target/scala-2.12/aws_layer/java/lib/awsFatJarDependency.jar") // We're relying on terraform to zip this up in the directory structure AWS prefers :)
    }
  ).dependsOn(sharedJvm).aggregate(sharedJvm).disablePlugins(RevolverPlugin)

lazy val lambdaOffline = (project in file("lambda-offline")).settings(
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    WebKeys.packagePrefix in Assets := "public/",
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    managedClasspath in Runtime += (packageBin in Assets).value,
    name := "lambda-offline",
    version := "1.0",
    scalaVersion := scalaV,
    libraryDependencies ++= serverDeps ++ Seq(
        "com.typesafe.akka" %% "akka-http"   % "10.1.9",
        "com.typesafe.akka" %% "akka-stream" % "2.5.23",
        "com.vmunier" %% "scalajs-scripts" % "1.1.3",
        "org.scalactic" %% "scalactic" % "3.0.8",
        "org.scalatest" %% "scalatest" % "3.0.8" % "test"
    ),
    mainClass in Compile := Some("lambda.WebServer")
).enablePlugins(SbtWeb).dependsOn(lambda, sharedJvm).aggregate(sharedJvm)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSWeb)
  .settings(libraryDependencies ++= Seq(
      "org.scala-js" %% "scalajs-stubs" % "0.6.22" % "provided",
      "org.scalatest" %% "scalatest" % "3.0.3" % "test",
      "com.typesafe.play" %% "play-json" % "2.6.9",
      "com.beachape" %%% "enumeratum" % "1.5.12",
      "com.lihaoyi" %% "upickle" % "0.7.5",
      "org.scalatest" %% "scalatest" % "3.0.3" % "test",
      "software.amazon.awssdk" % "dynamodb" % "2.7.26",
  )).disablePlugins(RevolverPlugin)


lazy val client = (project in file("client")).settings(
    scalaVersion := scalaV,
    scalaJSUseMainModuleInitializer := true,
    scalacOptions ++= Seq("-Xmax-classfile-name","78","-Xxml:coalescing"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "0.9.1",
        "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
        "com.thoughtworks.binding" %%% "dom" % "latest.release",
        "com.thoughtworks.binding" %%% "futurebinding" % "latest.release",
        "fr.hmil" %%% "roshttp" % "2.0.2",
        "com.typesafe.play" %% "play-json" % "2.6.9",
        "com.lihaoyi" %%% "autowire" % "0.2.6",
        "org.scala-js" %% "scalajs-stubs" % "0.6.22" % "provided",
        "org.webjars" %% "webjars-play" % "2.6.1",
        "org.wvlet.airframe" %%% "airframe" % airframeVersion,
        "com.lihaoyi" %%% "upickle" % "0.7.5"
    )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).dependsOn(sharedJs).aggregate(sharedJs).disablePlugins(RevolverPlugin)


lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val buildTask = taskKey[Unit]("Compiles and packages dependency jar, main jar and scalajs scripts for AWS")
buildTask := {
    val a = (assembly in (lambda, Compile)).value
    val b = (fullOptJS in (client, Compile)).value
    val c = (assemblyPackageDependency in (lambda, Compile)).value
}