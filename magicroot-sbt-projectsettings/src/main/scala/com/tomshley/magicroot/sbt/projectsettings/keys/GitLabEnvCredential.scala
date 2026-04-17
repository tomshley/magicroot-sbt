/*
 * Copyright 2026 Tomshley LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Thomas Schena @sgoggles <https://github.com/sgoggles> | <https://gitlab.com/sgoggles>
 */

package com.tomshley.magicroot.sbt.projectsettings.keys

/** Environment-variable-based credential source for CI systems.
 *
 * When `passwordEnvVar` is set in the environment, an SBT [[sbt.Credentials]]
 * instance is constructed from the given realm/host/user and the env var value.
 * This allows CI runners to authenticate without shipping credential files,
 * while local development falls through to file-based credentials.
 *
 * Provider-agnostic: works with any CI that exposes a token as an env var
 * (GitLab CI_JOB_TOKEN, GitHub GITHUB_TOKEN, etc.).
 *
 * @param realm          SBT credential realm (e.g. "GitLab Packages Registry")
 * @param host           SBT credential host  (e.g. "gitlab.com")
 * @param user           Username sent with the credential (e.g. "gitlab-ci-token")
 * @param passwordEnvVar Name of the environment variable containing the token
 */
case class GitLabEnvCredential(
  realm: String,
  host: String,
  user: String,
  passwordEnvVar: String
)
