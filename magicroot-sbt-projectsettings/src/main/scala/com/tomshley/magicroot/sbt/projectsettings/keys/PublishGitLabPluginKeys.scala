/*
 * copyright 2023 tomshley llc
 *
 * licensed under the apache license, version 2.0 (the "license");
 * you may not use this file except in compliance with the license.
 * you may obtain a copy of the license at
 *
 * http://www.apache.org/licenses/license-2.0
 *
 * unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "as is" basis,
 * without warranties or conditions of any kind, either express or implied.
 * see the license for the specific language governing permissions and
 * limitations under the license.
 *
 * @author thomas schena @sgoggles <https://github.com/sgoggles> | <https://gitlab.com/sgoggles>
 *
 */

package com.tomshley.magicroot.sbt.projectsettings.keys

import com.tomshley.magicroot.sbt.common.BasicSbtSettingsKeys
import sbt.Keys.*
import sbt.{ Def, * }
sealed trait GitLabCredentials {
  private val defaultEnvCredential = GitLabEnvCredential(
    "GitLab Packages Registry", "gitlab.com", "gitlab-ci-token", "CI_JOB_TOKEN"
  )

  def gitLabPublishCredentials(credentialFileOption: Option[File]): Credentials =
    sys.env
      .get(defaultEnvCredential.passwordEnvVar)
      .map(pw =>
        Credentials(
          defaultEnvCredential.realm,
          defaultEnvCredential.host,
          defaultEnvCredential.user,
          pw,
        )
      )
      .getOrElse(Credentials(credentialFileOption.getOrElse(file(".credentials.gitlab"))))

  def gitLabPublishAdditionalResolvers(projectId: Int): Seq[MavenRepository] = Seq(
    "GitLab" at
      s"https://gitlab.com/api/v4/projects/$projectId/packages/maven"
  )

  def gitLabPublishTo(projectId: Int): Option[MavenRepository] =
    Some(
      "gitlab" at s"https://gitlab.com/api/v4/projects/$projectId/packages/maven"
    )
}
protected[projectsettings] trait PublishGitLabPluginKeys
    extends BasicSbtSettingsKeys
    with GitLabCredentials {
  val magicRootPublishGitLabProjectId: SettingKey[Int] = settingKey[Int]("Project Id of the gitlab project")

  lazy val publishSettings: Seq[
    Def.Setting[? >: Seq[Resolver] & Task[Seq[Credentials]] & Task[Option[Resolver]] <: Equals]
  ] = Seq(
    ThisBuild / resolvers ++= gitLabPublishAdditionalResolvers(magicRootPublishGitLabProjectId.value),
    ThisBuild / credentials += gitLabPublishCredentials(
      Some((ThisBuild / baseDirectory).value / ".credentials.gitlab")
    ),
    ThisBuild / publishTo := gitLabPublishTo(magicRootPublishGitLabProjectId.value),
  )
}
