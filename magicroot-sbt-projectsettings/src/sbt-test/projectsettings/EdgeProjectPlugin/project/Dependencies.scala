import sbt.*

object Dependencies {

  val BasicAmazonAwsGroup = "com.amazonaws"
  val BasicAmazonAwsGroupVersion = "1.12.239"

  val BasicSksamuelGroup = "com.sksamuel.avro4s"
  val BasicSksamuelGroupVersion = "4.1.0"

  val BasicGithubJwtGroup = "com.github.jwt-scala"
  val BasicGithubJwtGroupVersion = "9.0.5"

  val BasicAwsSdkGroup = "software.amazon.awssdk"
  val BasicAwsSdkGroupVersion = "2.17.209"

  val BasicLightbendAkkaGroup = "com.lightbend.akka"
  val BasicLightbendAkkaR2DBCGroup = "0.6.1"
  val BasicLightbendAkkaProjectionGroup = "1.2.4"

  val BasicAkkaGroupVersion = "2.6.19"
  val BasicAkkaGroup = "com.typesafe.akka"
  val BasicAkkaCompatGroupVersion = "2.6.18"

  val BasicAkkaHTTPGroupVersion = "10.2.9"
  val BasicAkkaHTTPGroup = "com.typesafe.akka"

  val BasicPulsar4sGroupVersion = "2.8.1"
  val BasicPulsar4sGroup = "com.clever-cloud.pulsar4s"

  val BasicChEpflScalaGroupVersion = "1.4.0"
  val BasicChEpflScalaGroup = "ch.epfl.scala"

  val BasicIOCucumberGroupVersion = "7.3.3"
  val BasicIOCucumberGroup = "io.cucumber"
  val BasicIOCucumberScalaGroupVersion = "8.2.6"

  val BasicOrgScalariformGroupVersion = "0.2.10"
  val BasicOrgScalariformGroup = "org.scalariform"

  val BasicScalatestGroupVersion = "3.2.12"
  val BasicScalatestGroup = "org.scalatest"

  val BasicJson4sGroupVersion = "4.1.0-M1"
  val BasicJson4sGroup = "org.json4s"

  val BasicIOJvmUuidGroupVersion = "0.3.1"
  val BasicIOJvmUuidGroup = "io.jvm.uuid"

  val BasicScalaLangGroupVersion = "2.1.0"
  val BasicScalaLangGroup = "org.scala-lang.modules"

  val BasicTototoshiGroupVersion = "2.5.0"
  val BasicTototoshiGroup = "com.github.tototoshi"

  val BasicCassandraGroupVersion = "4.0.0"
  val BasicCassandraGroup = "com.datastax.cassandra"

  val BasicElasticGroupVersion = "8.2.2"
  val BasicElasticGroup = "org.elasticsearch"

  val ScalaPbDefGroupVersion = "0.11.11"
  val ScalaPbDefGroup = "com.thesamet.scalapb"

  val IOCucumberScalaGroupVersion = "7.3.3"

  val ScalatestFlatSpecGroupVersion = "3.2.12"

  val JunitPlatformGroupVersion = "1.9.0-RC1"
  val JunitPlatformGroup = "org.junit.platform"

  val compilerplugin: ModuleID = ScalaPbDefGroup %% "compilerplugin" % ScalaPbDefGroupVersion
  val runtimeGrpc: ModuleID = ScalaPbDefGroup %% "scalapb-runtime-grpc" % ScalaPbDefGroupVersion

  val driverCore: ModuleID = BasicCassandraGroup % "cassandra-driver-core" % BasicCassandraGroupVersion

  val elasticsearch: ModuleID = BasicElasticGroup % "elasticsearch" % BasicElasticGroupVersion

  val xml: ModuleID = BasicScalaLangGroup %% "scala-xml" % BasicScalaLangGroupVersion

  val avro4s: ModuleID = BasicSksamuelGroup %% "avro4s-core" % BasicSksamuelGroupVersion cross CrossVersion.for3Use2_13

  val javaSdk: ModuleID = (BasicAmazonAwsGroup % "aws-java-sdk" % BasicAmazonAwsGroupVersion)
    .exclude("com.fasterxml.jackson.core", "jackson-databind")
  val auth: ModuleID = BasicAwsSdkGroup % "auth" % BasicAwsSdkGroupVersion
  val jwtCore: ModuleID = BasicGithubJwtGroup %% "jwt-core" % BasicGithubJwtGroupVersion cross CrossVersion.for2_13Use3
  val jwtUpickle: ModuleID = BasicGithubJwtGroup %% "jwt-upickle" % BasicGithubJwtGroupVersion cross CrossVersion.for2_13Use3

  val akkaPersistenceR2DBC: ModuleID = BasicLightbendAkkaGroup %% "akka-persistence-r2dbc" % BasicLightbendAkkaR2DBCGroup
  val akkaProjectionR2DBC: ModuleID = BasicLightbendAkkaGroup %% "akka-projection-r2dbc" % BasicLightbendAkkaR2DBCGroup
  val akkaProjectionEventsourced: ModuleID = BasicLightbendAkkaGroup %% "akka-projection-eventsourced" % BasicLightbendAkkaProjectionGroup cross CrossVersion.for3Use2_13
  val akkaProjectionCore: ModuleID = BasicLightbendAkkaGroup %% "akka-projection-core" % BasicLightbendAkkaProjectionGroup cross CrossVersion.for3Use2_13

  val akkaActorTyped: ModuleID = BasicAkkaGroup %% "akka-actor-typed" % BasicAkkaGroupVersion cross CrossVersion.for3Use2_13
  val akkaStreamTyped: ModuleID = BasicAkkaGroup %% "akka-stream-typed" % BasicAkkaGroupVersion cross CrossVersion.for3Use2_13
  val akkaPki: ModuleID = BasicAkkaGroup %% "akka-pki" % BasicAkkaGroupVersion cross CrossVersion.for3Use2_13
  val akkaRemote: ModuleID = BasicAkkaGroup %% "akka-remote" % BasicAkkaGroupVersion cross CrossVersion.for3Use2_13
  val akkaSlf4j: ModuleID = BasicAkkaGroup %% "akka-slf4j" % BasicAkkaGroupVersion cross CrossVersion.for3Use2_13
  val akkaStream: ModuleID = BasicAkkaGroup %% "akka-stream" % BasicAkkaCompatGroupVersion cross CrossVersion.for3Use2_13
  val akkaProtobuf3: ModuleID = BasicAkkaGroup %% "akka-protobuf-v3" % BasicAkkaCompatGroupVersion cross CrossVersion.for3Use2_13
  val akkaPersistenceTyped: ModuleID = BasicAkkaGroup %% "akka-persistence-typed" % BasicAkkaGroupVersion cross CrossVersion.for3Use2_13
  val akkaPersistence: ModuleID = BasicAkkaGroup %% "akka-persistence" % BasicAkkaCompatGroupVersion cross CrossVersion.for3Use2_13
  val akkaPersistenceQuery: ModuleID = BasicAkkaGroup %% "akka-persistence-query" % BasicAkkaCompatGroupVersion cross CrossVersion.for3Use2_13
  val akkaHttp: ModuleID = BasicAkkaHTTPGroup %% "akka-http" % BasicAkkaHTTPGroupVersion cross CrossVersion.for3Use2_13
  val akkaHttpXml: ModuleID = (BasicAkkaHTTPGroup %% "akka-http-xml" % BasicAkkaHTTPGroupVersion cross CrossVersion.for3Use2_13)
    .exclude(xml.organization, xml.name+"_2.12")
    .exclude(xml.organization, xml.name+"_2.13")
  val akkaHttpSpray: ModuleID = BasicAkkaHTTPGroup %% "akka-http-spray-json" % BasicAkkaHTTPGroupVersion cross CrossVersion.for3Use2_13
  val akkaHttpTest: ModuleID = BasicAkkaHTTPGroup %% "akka-http-testkit" % BasicAkkaHTTPGroupVersion % Test cross CrossVersion.for3Use2_13
  val akkaTestkit: ModuleID = BasicAkkaHTTPGroup %% "akka-testkit" % BasicAkkaGroupVersion % Test cross CrossVersion.for3Use2_13

  val pulsar4s: ModuleID = BasicPulsar4sGroup %% "pulsar4s-core" % BasicPulsar4sGroupVersion cross CrossVersion.for3Use2_13
  val pulsar4sAkkaStream: ModuleID = BasicPulsar4sGroup %% "pulsar4s-akka-streams" % BasicPulsar4sGroupVersion cross CrossVersion.for3Use2_13
  val pulsar4sAvro: ModuleID = BasicPulsar4sGroup %% "pulsar4s-avro" % BasicPulsar4sGroupVersion cross CrossVersion.for3Use2_13

  val scalafix: ModuleID = BasicChEpflScalaGroup % "scalafix" % BasicChEpflScalaGroupVersion cross CrossVersion.for3Use2_13

  val cucumberCore: ModuleID = BasicIOCucumberGroup % "cucumber-core" % BasicIOCucumberGroupVersion
  val cucumberJvm: ModuleID = BasicIOCucumberGroup % "cucumber-jvm" % BasicIOCucumberGroupVersion
  val cucumberJUnit: ModuleID = BasicIOCucumberGroup % "cucumber-junit" % BasicIOCucumberGroupVersion
  val cucumberScala: ModuleID = BasicIOCucumberGroup %% "cucumber-scala" % BasicIOCucumberScalaGroupVersion

  val scalariform: ModuleID = (BasicOrgScalariformGroup % "scalariform" % BasicOrgScalariformGroupVersion cross CrossVersion.for3Use2_13)
    .exclude(xml.organization, xml.name+"_2.13")

  val scalaTest: ModuleID = BasicScalatestGroup %% "scalatest" % BasicScalatestGroupVersion % Test

  val json4sJackson: ModuleID = BasicJson4sGroup %% "json4s-jackson" % BasicJson4sGroupVersion cross CrossVersion.for3Use2_13

  val scalaUUID: ModuleID = BasicIOJvmUuidGroup %% "scala-uuid" % BasicIOJvmUuidGroupVersion cross CrossVersion.for3Use2_13

  val slickJodaMapper: ModuleID = BasicTototoshiGroup %% "slick-joda-mapper" % BasicTototoshiGroupVersion cross CrossVersion.for3Use2_13

  val cucumberJUnitPlatformEngine: ModuleID = BasicIOCucumberGroup % "cucumber-junit-platform-engine" % IOCucumberScalaGroupVersion % Test

  val scalatestFlatspec: ModuleID = BasicScalatestGroup %% "scalatest-flatspec" % ScalatestFlatSpecGroupVersion % Test

  val junitPlatformSuite: ModuleID = JunitPlatformGroup % "junit-platform-suite" % JunitPlatformGroupVersion % Test

}