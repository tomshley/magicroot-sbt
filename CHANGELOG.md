# Changelog

All notable changes to this project are documented in this file.

This project follows Semantic Versioning.

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
