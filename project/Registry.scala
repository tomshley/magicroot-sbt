// Copyright (c) 2017–present Tomshley LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import sbt.*

object Registry {
  def credentials(credentialFileOption: Option[File]): Credentials =
    sys.env
      .get("CI_JOB_TOKEN")
      .map(
        Credentials(
          "GitLab Packages Registry",
          "gitlab.com",
          "gitlab-ci-token",
          _
        )
      )
      .getOrElse(Credentials(credentialFileOption.getOrElse(file(".secure_files") / ".credentials.gitlab")))

  def additionalResolvers(projectId: Int): Seq[MavenRepository] = Seq(
    "GitLab" at
      s"https://gitlab.com/api/v4/projects/$projectId/packages/maven"
  )

  def publishToGitlab(projectId: Int): Option[MavenRepository] =
    Some(
      "gitlab" at s"https://gitlab.com/api/v4/projects/$projectId/packages/maven"
    )
}
