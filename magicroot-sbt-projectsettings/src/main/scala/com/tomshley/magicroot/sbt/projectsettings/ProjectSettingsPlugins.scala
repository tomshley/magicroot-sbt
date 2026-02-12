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

package com.tomshley.magicroot.sbt
package projectsettings

import com.tomshley.magicroot.sbt.projectsettings.keys.*
import com.tomshley.magicroot.sbt.projectsettings.settings.ProjectSettingsDefs
import com.typesafe.sbt.packager.Keys.dockerAliases
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker
import com.tomshley.magicroot.sbt.projectsettings.vendor.PekkoProjectSettings
import org.apache.pekko.grpc.sbt.PekkoGrpcPlugin
import play.twirl.sbt.SbtTwirl
import sbt.*
import sbt.Keys.{baseDirectory, publish, publishLocal, streams}

import scala.sys.process.Process

protected[this] object BaseProjectSettingsPlugin extends AutoPlugin {

  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends CommonProjectKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ baseSettings3
}

object ProjectsHelperPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = BaseProjectSettingsPlugin

  object autoImport extends ProjectTypeKeys with CommonProjectKeys
}

object ForkJVMRunConfigPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends ForkJVMRunConfigKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ forkJVMRunConfigSettings
}

object VersionFilePlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends VersionFilePluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ versionFileSettings
}

object TomshleyCIBuildVersionPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = VersionFilePlugin

  object autoImport extends TomshleyCIBuildVersionPluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ tomshleyCIBuildVersionSettings
}

object SecureFilesPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends SecureFilesPluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ secureFilesSettings
}

object PublishGitLabPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends PublishGitLabPluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ publishSettings
}

object LibProjectPlugin extends AutoPlugin {
  override val requires: Plugins = BaseProjectSettingsPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.javaProject ++
      ProjectSettingsDefs.jsonProject ++
      ProjectSettingsDefs.libProject
}
object ProtoOnlyPekkoProjectPlugin extends AutoPlugin {
  override val requires: Plugins = PekkoGrpcPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.scala3Settings
}
object LibProjectPekkoPlugin extends AutoPlugin {
  override val requires: Plugins = LibProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.pekkoProject
}
object LibProjectPekkoPersistencePlugin extends AutoPlugin {
  override val requires: Plugins = LibProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.pekkoProject ++
      ProjectSettingsDefs.pekkoPersistenceProject
}
object LibProjectPekkoProjectionPlugin extends AutoPlugin {
  override val requires: Plugins = LibProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.pekkoProject ++
      ProjectSettingsDefs.pekkoProjectionProject
}
object LibProjectPekkoKafkaPlugin extends AutoPlugin {
  override val requires: Plugins = LibProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.pekkoKafkaProject
}

/** LibProjectPekkoFullPlugin — standalone Pekko library plugin that does not
  * require BaseProjectSettingsPlugin, so it works in sub-project layouts.
  * Bundles: baseSettings3, javaProject, jsonProject, libProject,
  * pekkoProject, pekkoPersistenceProject, pekkoProjectionProject,
  * pekkoKafkaProject.
  */
object LibProjectPekkoFullPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends CommonProjectKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      baseSettings3 ++
      ProjectSettingsDefs.javaProject ++
      ProjectSettingsDefs.jsonProject ++
      ProjectSettingsDefs.libProject ++
      ProjectSettingsDefs.pekkoProject ++
      ProjectSettingsDefs.pekkoPersistenceProject ++
      ProjectSettingsDefs.pekkoProjectionProject ++
      ProjectSettingsDefs.pekkoKafkaProject
}

object LibManagedProjectPlugin extends AutoPlugin {
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings
}

object LibUnmanagedProjectPlugin extends AutoPlugin {
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.unmanagedProject
}

object DockerPublishPlugin extends AutoPlugin {
  override def requires = JavaAppPackaging &&
    DockerPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.dockerPublishProject ++ dockerBuildxSettings

  lazy val ensureDockerBuildx = taskKey[Unit]("Ensure that docker buildx configuration exists")
  lazy val dockerBuildWithBuildx = taskKey[Unit]("Build docker images using buildx")
  lazy val dockerBuildxSettings = Seq(
    ensureDockerBuildx := {
      if (Process("docker buildx inspect multi-arch-builder").! == 1) {
        Process("docker buildx create --use --name multi-arch-builder", baseDirectory.value).!
      }
    },
    dockerBuildWithBuildx := {
      streams.value.log("Building and pushing image with Buildx")
      dockerAliases.value.foreach(alias =>
        Process(
          "docker buildx build --platform=linux/arm64,linux/amd64 --push -t " +
            alias + " .",
          baseDirectory.value / "target" / "docker" / "stage",
        ).!
      )
    },
    Docker / publish := Def
      .sequential(
        Docker / publishLocal,
        ensureDockerBuildx,
        dockerBuildWithBuildx,
      )
      .value,
  )
}

object CoreProjectPlugin extends AutoPlugin {

  override val requires: Plugins = DockerPublishPlugin &&
    PekkoGrpcPlugin &&
    LibProjectPekkoPlugin &&
    LibManagedProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.globalRunSettings
}

object ValueAddProjectPlugin extends AutoPlugin {

  override val requires: Plugins = DockerPublishPlugin &&
    PekkoGrpcPlugin &&
    LibProjectPekkoPlugin &&
    LibProjectPekkoPersistencePlugin &&
    LibProjectPekkoProjectionPlugin &&
    LibProjectPekkoKafkaPlugin &&
    LibManagedProjectPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.globalRunSettings
}

object EdgeProjectPlugin extends AutoPlugin {
  override val requires: Plugins = DockerPublishPlugin &&
    SbtTwirl &&
    PekkoGrpcPlugin &&
    LibProjectPekkoPlugin &&
    LibProjectPekkoPersistencePlugin &&
    LibProjectPekkoProjectionPlugin &&
    LibProjectPekkoKafkaPlugin &&
    LibManagedProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.globalRunSettings
}

// =============================================================================
// Basic Logging Plugin
// =============================================================================

/** BasicLoggingPlugin - provides logback-classic for runtime logging */
object BasicLoggingPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    Keys.libraryDependencies ++= PekkoProjectSettings.Libraries.basicLoggingLibraries
  )
}

// =============================================================================
// Edge Ingest Plugins - for TCP/gRPC ingestion services (AMI-like)
// =============================================================================

/** EdgeIngestProjectPlugin - TCP + gRPC + EventSourced + Kafka (no Twirl) */
object EdgeIngestProjectPlugin extends AutoPlugin {
  override val requires: Plugins = DockerPublishPlugin &&
    BasicLoggingPlugin &&
    PekkoGrpcPlugin &&
    LibProjectPekkoPlugin &&
    LibProjectPekkoPersistencePlugin &&
    LibProjectPekkoProjectionPlugin &&
    LibProjectPekkoKafkaPlugin &&
    LibManagedProjectPlugin
  
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.globalRunSettings
}

// =============================================================================
// Utility Plugins
// =============================================================================

/** GitLabSourceDependencyPlugin - manages GitLab Maven credentials for source dependencies */
object GitLabSourceDependencyPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  object autoImport {
    val magicRootGitlabCredentials = settingKey[Map[Int, File]](
      "GitLab project ID to credential file mapping"
    )
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    magicRootGitlabCredentials := Map.empty,
    Keys.resolvers ++= magicRootGitlabCredentials.value.keys.toSeq.map(id =>
      s"GitLab-$id" at s"https://gitlab.com/api/v4/projects/$id/packages/maven"
    ),
    Keys.credentials ++= magicRootGitlabCredentials.value.values.toSeq
      .distinct
      .filter(_.exists())
      .map(Credentials(_))
  )
}

/** AcceptanceTestPlugin - Cucumber/Gherkin integration for acceptance testing */
object AcceptanceTestPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  object autoImport {
    val magicRootAcceptanceFeaturesDependency = settingKey[Option[ModuleID]](
      "Optional dependency containing .feature files"
    )
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    magicRootAcceptanceFeaturesDependency := None,
    Keys.libraryDependencies ++= magicRootAcceptanceFeaturesDependency.value.toSeq.map(_ % Test),
  ) ++ ProjectSettingsDefs.acceptanceTestProject
}
