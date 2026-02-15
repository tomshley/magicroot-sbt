# Changelog

All notable changes to this project are documented in this file.

This project follows Semantic Versioning.

---

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

## Unreleased

- Initial OSS standardization (LICENSE, NOTICE, CONTRIBUTING, CODE_OF_CONDUCT, SECURITY, CHANGELOG, ROADMAP)
