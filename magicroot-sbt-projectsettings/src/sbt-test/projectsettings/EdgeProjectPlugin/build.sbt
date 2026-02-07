import Dependencies._



val rootProject =
  publishableProject("edge-project-plugin-test", Some(file(".")))
    .enablePlugins(EdgeProjectPlugin)
//    .settings(
//      assembly / assemblyJarName := "projectsbttest.jar"
//    )


//assembly / assemblyMergeStrategy := {
//  case PathList("META-INF", xs@_*) =>
//    xs map {
//      _.toLowerCase
//    } match {
//      case "manifest.mf" :: Nil | "index.list" :: Nil | "dependencies" :: Nil =>
//        MergeStrategy.discard
//      case ps@x :: xs if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
//        MergeStrategy.discard
//      case "plexus" :: xs =>
//        MergeStrategy.discard
//      case "services" :: xs =>
//        MergeStrategy.filterDistinctLines
//      case "spring.schemas" :: Nil | "spring.handlers" :: Nil =>
//        MergeStrategy.filterDistinctLines
//      case _ => MergeStrategy.first
//    }
//  case "application.conf" => MergeStrategy.concat
//  case "reference.conf" => MergeStrategy.concat
//  case _ => MergeStrategy.first
//}

//TaskKey[Unit]("check") := {
//  val process = sys.process.Process("java", Seq("-jar", (crossTarget.value / "projectsbttest.jar").toString))
//  val out = (process !!)
//  if (out.trim != "hello") sys.error("unexpected output: " + out)
//  ()
//}
//
//TaskKey[Unit]("checkOrg") := {
//  if (organization.value != "systems.toqen") sys.error(organization.value + " is not systems.toqen")
//}
//
//TaskKey[Unit]("checkScalaDir") := {
//  val suffix = if (scalaBinaryVersion.value.toDouble >= 3) "3" else "2"
//  List((Compile / unmanagedSourceDirectories).value, (Test / unmanagedSourceDirectories).value) foreach { dirs =>
//    if (!dirs.exists(_.getAbsolutePath().endsWith(s"scala-$suffix"))) {
//      sys.error(s"$dirs did not contain a scala-$suffix directory")
//    }
//  }
//}
//
//TaskKey[Unit]("checkQuality") := {
//  if (!libraryDependencies.value.contains(cucumberCore)) sys.error("Missing cucumberCore")
//  if (!libraryDependencies.value.contains(cucumberJvm)) sys.error("Missing cucumberJvm")
//  if (!libraryDependencies.value.contains(cucumberJUnit)) sys.error("Missing cucumberJUnit")
//  if (!libraryDependencies.value.contains(cucumberScala)) sys.error("Missing cucumberScala")
//  if (!libraryDependencies.value.contains(scalaTest)) sys.error("Missing scalaTest")
//  if (!libraryDependencies.value.contains(cucumberJUnitPlatformEngine)) sys.error("Missing cucumberJUnitPlatformEngine")
//  if (!libraryDependencies.value.contains(Dependencies.scalafix)) sys.error("Missing scalafix")
//  if (!libraryDependencies.value.contains(scalariform)) sys.error("Missing scalariform")
//  if (!libraryDependencies.value.contains(junitPlatformSuite)) sys.error("Missing junitPlatformSuite")
//  if (!libraryDependencies.value.contains(scalatestFlatspec)) sys.error("Missing scalatestFlatspec")
//}
//
//TaskKey[Unit]("checkBasicSerialization") := {
//  if (!libraryDependencies.value.contains(json4sJackson)) sys.error("Missing json4sJackson")
//  if (!libraryDependencies.value.contains(xml)) sys.error("Missing scala-xml")
//}
//
//TaskKey[Unit]("checkBasicAkka") := {
//  if (!libraryDependencies.value.contains(akkaPki)) sys.error("Missing akkaPki")
//  if (!libraryDependencies.value.contains(akkaTestkit)) sys.error("Missing akkaTestkit")
//  if (!libraryDependencies.value.contains(akkaSlf4j)) sys.error("Missing akkaSlf4j")
//  if (!libraryDependencies.value.contains(akkaRemote)) sys.error("Missing akkaRemote")
//  if (!libraryDependencies.value.contains(akkaActorTyped)) sys.error("Missing akkaActorTyped")
//  if (!libraryDependencies.value.contains(akkaStreamTyped)) sys.error("Missing akkaStreamTyped")
//  if (!libraryDependencies.value.contains(akkaStream)) sys.error("Missing akkaStream")
//}
//
//TaskKey[Unit]("checkAvro") := {
//  if (!libraryDependencies.value.contains(avro4s)) sys.error("Missing avro4s")
//}
//
//TaskKey[Unit]("checkProto") := {
//  if (!libraryDependencies.value.contains(compilerplugin)) sys.error("Missing compilerplugin")
//  if (!libraryDependencies.value.contains(runtimeGrpc)) sys.error("Missing runtimeGrpc")
//  if (!libraryDependencies.value.contains(akkaProtobuf3)) sys.error("Missing akkaProtobuf3")
//}
//
//TaskKey[Unit]("checkTime") := {
//  if (!libraryDependencies.value.contains(slickJodaMapper)) sys.error("Missing slickJodaMapper")
//}

//TaskKey[Unit]("checkIOJvmUuidDependencies") := {
//  if (!libraryDependencies.value.contains(scalaUUID)) sys.error("Missing scalaUUID")
//}








