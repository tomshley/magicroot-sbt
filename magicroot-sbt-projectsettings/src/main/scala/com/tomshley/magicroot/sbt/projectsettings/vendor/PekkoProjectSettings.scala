package com.tomshley.magicroot.sbt.projectsettings.vendor

import sbt.*

object PekkoProjectSettings {
  object Versions {
    // Pekko 1.5.0 is the latest stable (Apr 2026). Binary-compatible with 1.1.x
    // per Pekko's 1.x semver contract. Bumping skips over the 1.2.0
    // RecoverWith operator regression (fixed in 1.5.0 per release notes).
    val PekkoVersion = "1.5.0"
    val PekkoManagementVersion = "1.2.1"
    // pekko-projection-* and pekko-persistence-r2dbc ship on a separate release
    // cadence from pekko-management; their latest stable is 1.1.0 (Maven Central
    // only has 1.0.0 / 1.1.0-M1 / 1.1.0 for both families). BC-compatible with
    // pekko 1.5.x per Pekko's 1.x contract. Previously these references shared
    // `PekkoManagementVersion`, which broke after bumping management to 1.2.1.
    val PekkoProjectionVersion = "1.1.0"
    // pekko-connectors-kafka 1.1.0 GA (Sep 2024) replaces the 1.1.0-M1 milestone.
    // M1's Transactional.flow stalled after `Discovered group coordinator` against
    // cp-kafka:7.6 (no AddPartitionsToTxn, no commit). 1.1.0 GA targets pekko 1.1.x
    // (we run 1.5.x — BC-compatible within 1.x) and bundles kafka-clients 3.8.0,
    // replacing the prior Confluent 7.6-shipped 3.6.0-ccs transitive.
    val PekkoKafkaConnector = "1.1.0"
    val PekkoHttpVersion = "1.3.0"
    // Confluent Platform kept at 7.6.0 — 8.x is based on Apache Kafka 4.0 (KIP-896)
    // which removes pre-2.1 client protocol support and requires a staged 7.9 → 8.x
    // production migration with DeprecatedRequestsPerSec scans. Not reactive-hotfix
    // material.
    val KafkaStreamsVersion = "7.6.0"
    val KafkaAvroVersion = "7.6.0"
    val Avro4sVersion = "5.0.15"
    val LogbackVersion = "1.5.32"
    val Json4sVersion = "4.0.7"
    // Testcontainers kept at 1.20.0 — 2.x moves KafkaContainer to
    // org.testcontainers.kafka.{ConfluentKafkaContainer|KafkaContainer}, requires
    // per-module testcontainers- artifacts, drops JUnit 4 (unused by us). Should be
    // its own migration branch.
    val TestContainers = "1.20.0"
    val ScalaTest = "3.2.20"
    val TwilioVersion = "12.0.0"
    val AwsSdkVersion = "2.42.39"
    val ScalaPBVersion = "0.11.20"
    // protobuf-java kept in 3.25.x — 4.x breaks ScalaPB 0.11.x runtime compatibility
    // (ScalaPB 1.0.0 is alpha). Pekko 1.5 internally ships protobuf-java 4.33.5 but
    // that copy is shaded; user classpath stays on 3.25.x.
    val ProtobufJavaVersion = "3.25.5"
    // pekko-persistence-r2dbc 1.1.0 demoted r2dbc-postgresql to <scope>test</scope>
    // (was compile in 1.0.0), so downstream projects no longer get the driver
    // transitively. Pin explicitly here so anyone consuming `pekkoPersistenceLibraries`
    // or `pekkoProjectionLibraries` still has a working Postgres driver at runtime.
    // 1.0.7.RELEASE is the version that pekko-persistence-r2dbc 1.1.0's POM references.
    val R2dbcPostgresqlVersion = "1.0.7.RELEASE"
  }

  object Resolvers {
    val pekkoResolvers: Seq[MavenRepository] = Seq(
      Resolver.ApacheMavenSnapshotsRepo
    )
    val pekkoKafkaResolvers: Seq[MavenRepository] = Seq(
      "Confluent Maven Repository" at "https://packages.confluent.io/maven/"
    )
  }

  object Libraries {
    val basicLoggingLibraries: Seq[ModuleID] = Seq(
      "ch.qos.logback" % "logback-classic" % PekkoProjectSettings.Versions.LogbackVersion
    )
    val basicSerializationLibraries: Seq[ModuleID] = Seq(
      "org.json4s" %% "json4s-jackson" % PekkoProjectSettings.Versions.Json4sVersion
    )
    val basicTestLibraries: Seq[ModuleID] = Seq(
      "org.scalatest" %% "scalatest" % PekkoProjectSettings.Versions.ScalaTest % Test
    )
    val pekkoActorLibraries: Seq[ModuleID] = Seq(
      "org.apache.pekko" %% "pekko-stream" % PekkoProjectSettings.Versions.PekkoVersion,
      "org.apache.pekko" %% "pekko-actor-typed" % PekkoProjectSettings.Versions.PekkoVersion,
      "org.apache.pekko" %% "pekko-management" % PekkoProjectSettings.Versions.PekkoManagementVersion,
      "org.apache.pekko" %% "pekko-management-cluster-http" % PekkoProjectSettings.Versions.PekkoManagementVersion,
      "org.apache.pekko" %% "pekko-discovery-kubernetes-api" % PekkoProjectSettings.Versions.PekkoManagementVersion,
      "org.apache.pekko" %% "pekko-discovery" % PekkoProjectSettings.Versions.PekkoVersion,
      "org.apache.pekko" %% "pekko-management-cluster-bootstrap" % PekkoProjectSettings.Versions.PekkoManagementVersion,
      "org.apache.pekko" %% "pekko-http" % PekkoProjectSettings.Versions.PekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % PekkoProjectSettings.Versions.PekkoHttpVersion,
      "org.apache.pekko" %% "pekko-serialization-jackson" % PekkoProjectSettings.Versions.PekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j" % PekkoProjectSettings.Versions.PekkoVersion,
      "org.apache.pekko" %% "pekko-cluster-sharding-typed" % PekkoProjectSettings.Versions.PekkoVersion,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % PekkoProjectSettings.Versions.PekkoVersion % Test,
      "org.apache.pekko" %% "pekko-stream-testkit" % PekkoProjectSettings.Versions.PekkoVersion % Test,
      "org.apache.pekko" %% "pekko-http-testkit" % PekkoProjectSettings.Versions.PekkoHttpVersion % Test,
    )

    val pekkoPersistenceLibraries: Seq[ModuleID] = Seq(
      "org.apache.pekko" %% "pekko-persistence-r2dbc" % PekkoProjectSettings.Versions.PekkoProjectionVersion,
      "org.apache.pekko" %% "pekko-persistence-typed" % PekkoProjectSettings.Versions.PekkoVersion,
      "org.apache.pekko" %% "pekko-persistence-testkit" % PekkoProjectSettings.Versions.PekkoVersion % Test,
      // Postgres R2DBC driver — required at runtime. Upstream pekko-persistence-r2dbc 1.1.0
      // marked it as test-scope (see R2dbcPostgresqlVersion note above).
      "org.postgresql" % "r2dbc-postgresql" % PekkoProjectSettings.Versions.R2dbcPostgresqlVersion,
    )

    val pekkoProjectionLibraries: Seq[ModuleID] = Seq(
      "org.apache.pekko" %% "pekko-projection-core" % PekkoProjectSettings.Versions.PekkoProjectionVersion,
      "org.apache.pekko" %% "pekko-projection-r2dbc" % PekkoProjectSettings.Versions.PekkoProjectionVersion,
      "org.apache.pekko" %% "pekko-projection-eventsourced" % PekkoProjectSettings.Versions.PekkoProjectionVersion,
      "org.apache.pekko" %% "pekko-projection-durable-state" % PekkoProjectSettings.Versions.PekkoProjectionVersion,
      "org.apache.pekko" %% "pekko-persistence-query" % PekkoProjectSettings.Versions.PekkoVersion,
    )

    val pekkoKafkaLibraries: Seq[ModuleID] = Seq(
      "com.sksamuel.avro4s" %% "avro4s-core" % PekkoProjectSettings.Versions.Avro4sVersion,
      "io.confluent" % "kafka-streams-avro-serde" % PekkoProjectSettings.Versions.KafkaStreamsVersion,
      "io.confluent" % "kafka-avro-serializer" % PekkoProjectSettings.Versions.KafkaAvroVersion,
      "org.apache.pekko" %% "pekko-connectors-kafka" % PekkoProjectSettings.Versions.PekkoKafkaConnector,
      "org.apache.pekko" %% "pekko-connectors-kafka-cluster-sharding" % PekkoProjectSettings.Versions.PekkoKafkaConnector,
      "org.testcontainers" % "kafka" % PekkoProjectSettings.Versions.TestContainers % Test,
    )

    val pekkoMessagingLibraries: Seq[ModuleID] = Seq(
      "com.twilio.sdk" % "twilio" % PekkoProjectSettings.Versions.TwilioVersion
    )

    val pekkoStorageLibraries: Seq[ModuleID] = Seq(
      "software.amazon.awssdk" % "s3" % PekkoProjectSettings.Versions.AwsSdkVersion,
      "software.amazon.awssdk" % "auth" % PekkoProjectSettings.Versions.AwsSdkVersion,
      "software.amazon.awssdk" % "sts" % PekkoProjectSettings.Versions.AwsSdkVersion
    )

    val protobufLibraries: Seq[ModuleID] = Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime" % PekkoProjectSettings.Versions.ScalaPBVersion,
      "com.google.protobuf" % "protobuf-java" % PekkoProjectSettings.Versions.ProtobufJavaVersion
    )
  }
}
