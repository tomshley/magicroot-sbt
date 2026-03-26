# Changelog

All notable changes to this project are documented in this file.

This project follows Semantic Versioning.

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
