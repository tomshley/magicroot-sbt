# Changelog

All notable changes to this project are documented in this file.

This project follows Semantic Versioning.

---

## [2.0.1] — 2026-04-23

### Fixed
- **PekkoProjectSettings** — `pekko-projection-*` and `pekko-persistence-r2dbc`
  incorrectly shared `PekkoManagementVersion` in 2.0.0. Bumping management to
  `1.2.1` broke resolution because those families only publish up to `1.1.0`
  (Maven Central shows `1.0.0`, `1.1.0-M1`, `1.1.0` for both).
  Introduced a separate `PekkoProjectionVersion = "1.1.0"` constant and
  pointed `pekko-projection-core`, `pekko-projection-r2dbc`,
  `pekko-projection-eventsourced`, `pekko-projection-durable-state`, and
  `pekko-persistence-r2dbc` at it. `pekko-management-*` artifacts continue
  to use `PekkoManagementVersion` (now correctly `1.2.1`). No other version
  changes.

---

## [2.0.0] — 2026-04-23

Major version bump to signal the coordinated Pekko family upgrade
(`1.1.x` → `1.5.x`) and the library/SDK refresh. No plugin-API, setting-key,
or auto-import surface was renamed — the major bump is a downstream heads-up
that runtime semantics of the vendored Pekko stack have moved forward.
Downstream consumers should test in a non-production environment before
promoting.

### Changed

`PekkoProjectSettings` (coordinated Pekko family — `1.1.x` → `1.5.x`,
within the Pekko 1.x BC contract; skips the 1.2.0 `RecoverWith` operator
regression fixed in 1.5.0 per upstream release notes):

- `PekkoVersion`            `1.1.3`    → `1.5.0`
- `PekkoHttpVersion`        `1.1.0`    → `1.3.0`
- `PekkoManagementVersion`  `1.0.0`    → `1.2.1`
- `PekkoKafkaConnector`     `1.1.0-M1` → `1.1.0` (milestone → GA; resolves
  the `Transactional.flow` stall observed against cp-kafka 7.6 where the
  producer-network thread went silent after `Discovered group coordinator`
  and never issued `AddPartitionsToTxn`. Transitive `kafka-clients` becomes
  `3.8.0`, replacing the Confluent-shipped `3.6.0-ccs`.)

Library / SDK refresh:

- `Avro4sVersion`          `5.0.13`  → `5.0.15`  (patch)
- `LogbackVersion`         `1.5.13`  → `1.5.32`  (patch; CVE fixes)
- `ScalaTest`              `3.2.19`  → `3.2.20`  (patch)
- `TwilioVersion`          `10.6.3`  → `12.0.0`  (SMS API surface stable;
  `Twilio.init`, `Message.creator().create*()`, `PhoneNumber` unchanged)
- `AwsSdkVersion`          `2.25.11` → `2.42.39` (within 2.x major; S3-only
  consumer usage remains API-compatible)
- `ScalaPBVersion`         `0.11.15` → `0.11.20` (patch within 0.11.x)
- `ProtobufJavaVersion`    `3.25.1`  → `3.25.5`  (patch within 3.25.x; 4.x
  deliberately held — breaks ScalaPB 0.11.x runtime)

### Deliberately held

- `KafkaStreamsVersion` / `KafkaAvroVersion` — held at `7.6.0`. Confluent
  Platform 8.x is based on Apache Kafka 4.0 (KIP-896) which removes pre-2.1
  client protocol support; Confluent requires a staged 7.9 → 8.x migration
  via the `DeprecatedRequestsPerSec` metric scan. Not reactive-hotfix
  material.
- `TestContainers` — held at `1.20.0`. The 2.x line relocates
  `org.testcontainers.containers.KafkaContainer` →
  `org.testcontainers.kafka.ConfluentKafkaContainer` (for cp-kafka images)
  and renames every module artifact with a `testcontainers-` prefix.
  Requires code changes in downstream Kafka test fixtures; should land as
  its own migration.
- `Json4sVersion` — `4.0.7` is the latest stable (`4.1.0-M8` is a
  milestone).

### Migration notes

- Downstream projects bump `magicroot-sbt-projectsettings` to `2.0.0` in
  `project/plugins.sbt`.
- No source changes required — `PekkoProjectSettings.Versions` field names
  are unchanged.
- `AkkaProjectSettings` (legacy, untouched; still pins Confluent 6.2.0 /
  Akka 2.9 / Alpakka Kafka 5.0) is unaffected by this release. No known
  in-tree consumers.

---

## [1.3.22] — 2026-04-23

### Changed
- **PekkoProjectSettings**: Bumped `KafkaAvroVersion` and `KafkaStreamsVersion`
  from `6.2.0` to `7.6.0` (Confluent Platform). This aligns the transitive
  `kafka-clients` artifact with the `confluentinc/cp-kafka:7.6.0` broker image
  used in AMI platform CI (kafka-clients `2.8.0` → `3.6.0`). The prior 2.8-era
  client's transactional producer protocol handshake hung indefinitely against
  a 3.6 broker — `ProducerId set` succeeded but the subsequent `Discovered
  transaction coordinator` retry loop never committed a record. Non-transactional
  `SendProducer` paths worked under the 2.8 → 3.6 skew, which is why projects
  without transactional producer tests stayed green.

### Migration notes
- Downstream projects should bump `magicroot-sbt-projectsettings` to `1.3.22`
  in `project/plugins.sbt`.
- No source changes required — API surface of `PekkoProjectSettings.Versions`
  is unchanged.
- `AkkaProjectSettings` (legacy, untouched at `6.2.0`) still pins the older
  Confluent artifacts; consumers of `AkkaProjectSettings` will see no change.

---

## [1.3.21] — 2026-04-17

### Fixed
- **GitLabSourceDependencyPlugin**: Restored exclusive env-var credential guard — when
  `CI_JOB_TOKEN` is present, file-based credentials are skipped. The 1.3.20 change to
  prepend env creds alongside file creds caused Coursier to send the wrong credential
  when resolving cross-project packages, resulting in GitLab returning 404 for published
  artifacts (e.g. `ami-platform-schemas_3`).

---

## [1.3.20] — 2026-04-17

### Changed
- **GitLabSourceDependencyPlugin**: Env-var credential is now prepended before file-based
  credentials instead of replacing them. SBT matches the first credential by realm+host,
  so the env credential takes priority while file credentials for other realms/hosts are
  preserved.
- **PublishGitLabPlugin**: `gitLabPublishCredentials` now uses `GitLabEnvCredential` internally
  instead of hardcoded `CI_JOB_TOKEN` / realm / host strings, consistent with
  `GitLabSourceDependencyPlugin`'s env-var credential pattern.

---

## [1.3.19] — 2026-04-17

### Added
- **GitLabSourceDependencyPlugin**: `magicRootGitlabCredentialEnvOverride` setting accepts a
  `GitLabEnvCredential(realm, host, user, passwordEnvVar)` for env-var-based authentication.
  When the env var is present (e.g. `CI_JOB_TOKEN` in CI), it constructs credentials inline
  and skips file-based credentials, avoiding realm+host collisions with publish-only tokens.
  Falls through to `magicRootGitlabCredentials` file mapping when the env var is absent.

---

## [1.3.18] — 2026-03-26

### Fixed
- **DockerPublishPlugin**: `dockerUpdateLatest` now only creates `latest` tag for local builds or Git tag pipelines
  - Branch/release/hotfix pipelines no longer publish plain `latest` Docker tag
  - Local `publishLocal` and tag pipelines still create `latest` for ergonomics and production deployment

---

## [1.3.17] — 2026-03-25

### Fixed
- **IRSA Support**: Added `software.amazon.awssdk:sts` to `pekkoStorageLibraries` for IAM Roles for Service Accounts support

---

## [1.3.16] — 2026-03-25

### Fixed
- **DockerPublishPlugin**: Single-platform builds now push images to registry after `--load` (previously only loaded locally)
- **DockerPublishPlugin**: Added `dockerBuildxSkipPush` setting for local-only builds (opt-in via `true`)

### Changed
- **DockerPublishPlugin**: Refactored to idiomatic sbt conventions
  - All task keys moved to `autoImport` (standard sbt plugin canon)
  - Use Scala 3 wildcard import consistently
  - Replace Java-style size check with pattern match destructuring
  - Use single string interpolation throughout
  - Add exit code validation for `docker buildx use` command
  - Make `dockerBuildxSettings` private with explicit type
  - Extract `dockerAliases.value` to avoid double evaluation
  - Remove disconnected `skipPush` logging; each branch logs contextually

---

## [1.3.6] — 2026-02-15

### Fixed
- **TomshleyCIBuildVersionPlugin**: Now reads `TOMSHLEY_CICD_BUILD_VERSION` environment variable directly instead of composing version from `magicRootBaseVersion` + `TOMSHLEY_CICD_BUILD_REVISION` qualifier
- Respects cicd-pipelines as canonical source for version strategy
- Removed `tomshleyCIBuildVersionQualifier` SettingKey (no longer needed)

## [1.3.5] — 2026-02-15

### Added
- **GitLabSourceDependencyPlugin**: `magicRootGitlabPublicResolvers` setting for public Package Registries (resolver without credentials)

### Changed
- **GitLabSourceDependencyPlugin**: Resolver generation now combines public + private IDs with deterministic ordering (`.sorted`)
- **AcceptanceFeaturesPackagingPluginKeys**: `magicRootVerifyFeaturesOnPackage` default set to `false` (automatic verification removed)
- Extracted `verifyJarFeatures()` as private helper method to reduce duplication

### Fixed
- **Critical**: Removed circular task dependency in `Compile / packageBin` that prevented plugin loading
- **Medium**: Fixed nondeterministic resolver ordering from `Set.toSeq` conversion
- Manual verification via `magicRootVerifyFeaturesJar` task remains available

## [1.3.4] — 2026-02-12

### Added
- **BasicLoggingPlugin** — wraps logback-classic from PekkoProjectSettings.Libraries.basicLoggingLibraries
- **AcceptanceTestPlugin** — now includes testFrameworks, junit-interface, and delegates to ProjectSettingsDefs.acceptanceTestProject
- Acceptance test version constants in ProjectSettingsVersions (cucumber 7.18.1, cucumber-scala 8.23.1, allure 2.25.0, playwright 1.41.0, junit-interface 0.13.3)
- VersionFilePlugin walks up directories to find VERSION file (supports runners/jvm/ monorepo layout)

### Changed
- Renamed setting keys with `magicRoot` prefix: baseVersion, publishGitLabProjectId, gitlabCredentials, acceptanceFeaturesDependency
- Wired BasicLoggingPlugin into EdgeIngestProjectPlugin.requires
- Bumped LogbackVersion from 1.5.6 to 1.5.13

### Removed
- EdgeWebPlugin and WebPluginKeys (unused sbt-web legacy)

## [1.3.12] — 2026-02-25

### Added
- **LibProjectProtobufPlugin** — standalone protobuf plugin with ScalaPB and protobuf-java dependencies
- **PekkoProjectSettings.Libraries.protobufLibraries** — ScalaPB v0.11.15 and protobuf-java v3.25.1 dependencies
- **ProjectSettingsDefs.protobufProject** — protobuf project settings
- **PekkoProjectSettings.Versions.ScalaPBVersion** — 0.11.15 version constant
- **PekkoProjectSettings.Versions.ProtobufJavaVersion** — 3.25.1 version constant

## [1.3.11] — 2026-02-25

### Added
- **LibProjectPekkoMessagingPlugin** — standalone Pekko messaging plugin with Twilio SDK integration
- **LibProjectPekkoStoragePlugin** — standalone Pekko storage plugin with AWS SDK integration
- **PekkoProjectSettings.Libraries.pekkoMessagingLibraries** — Twilio SDK v10.6.3 dependency
- **PekkoProjectSettings.Libraries.pekkoStorageLibraries** — AWS SDK v2.25.11 (S3 + Auth) dependencies
- **ProjectSettingsDefs.pekkoMessagingProject** — messaging project settings
- **ProjectSettingsDefs.pekkoStorageProject** — storage project settings

### Changed
- Added TwilioVersion (10.6.3) and AwsSdkVersion (2.25.11) to PekkoProjectSettings.Versions

## Unreleased

- Initial OSS standardization (LICENSE, NOTICE, CONTRIBUTING, CODE_OF_CONDUCT, SECURITY, CHANGELOG, ROADMAP)
