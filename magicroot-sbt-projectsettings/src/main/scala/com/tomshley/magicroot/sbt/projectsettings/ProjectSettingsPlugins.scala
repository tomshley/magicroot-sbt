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
object LibProjectPekkoMessagingPlugin extends AutoPlugin {
  override val requires: Plugins = LibProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.pekkoProject ++
      ProjectSettingsDefs.pekkoMessagingProject
}
object LibProjectPekkoStoragePlugin extends AutoPlugin {
  override val requires: Plugins = LibProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.pekkoProject ++
      ProjectSettingsDefs.pekkoStorageProject
}
object LibProjectProtobufPlugin extends AutoPlugin {
  override val requires: Plugins = LibProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.protobufProject
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

  object autoImport {
    val dockerBuildxPlatforms = settingKey[Seq[String]]("Target platforms for docker buildx builds")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++
      ProjectSettingsDefs.dockerPublishProject ++ dockerBuildxSettings

  lazy val ensureDockerBuildx = taskKey[Unit]("Ensure that docker buildx configuration exists")
  lazy val dockerBuildWithBuildx = taskKey[Unit]("Build docker images using buildx")
  lazy val dockerBuildxSettings = Seq(
    dockerBuildxPlatforms := Seq("linux/amd64"),
    ensureDockerBuildx := {
      Process("docker buildx use default").!
      val rc = Process("docker buildx inspect default --bootstrap").!
      require(rc == 0, s"Failed to bootstrap default buildx builder (exit code $rc)")
    },
    dockerBuildWithBuildx := {
      val platforms = dockerBuildxPlatforms.value
      val stageDir = baseDirectory.value / "target" / "docker" / "stage"
      val log = streams.value.log
      log.info(s"Building image with Buildx for platforms: ${platforms.mkString(",")}")
      if (platforms.size == 1) {
        dockerAliases.value.foreach { alias =>
          val rc = Process(
            s"docker buildx build --platform=${platforms.head} --load -t " +
              alias + " .",
            stageDir,
          ).!
          require(rc == 0, s"Buildx build failed for $alias (exit code $rc)")
        }
      } else {
        dockerAliases.value.foreach { alias =>
          val platformTags = platforms.map { platform =>
            val suffix = platform.replace("linux/", "").replace("/", "-")
            val platTag = s"$alias-$suffix"
            log.info(s"Building platform $platform as $platTag")
            val rc = Process(
              s"docker buildx build --platform=$platform --push -t $platTag .",
              stageDir,
            ).!
            require(rc == 0, s"Buildx build failed for $platTag (exit code $rc)")
            platTag
          }
          log.info(s"Creating manifest list for $alias")
          val createRc = Process(
            s"docker manifest create --amend $alias ${platformTags.mkString(" ")}"
          ).!
          require(createRc == 0, s"Manifest create failed for $alias (exit code $createRc)")
          val pushRc = Process(s"docker manifest push $alias").!
          require(pushRc == 0, s"Manifest push failed for $alias (exit code $pushRc)")
        }
      }
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

/** GitLabSourceDependencyPlugin - manages GitLab Maven resolvers and credentials for source dependencies */
object GitLabSourceDependencyPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  object autoImport {
    val magicRootGitlabPublicResolvers = settingKey[Set[Int]](
      "GitLab project IDs for public Package Registries (resolver only, no credentials)"
    )
    val magicRootGitlabCredentials = settingKey[Map[Int, File]](
      "GitLab project ID to credential file mapping for private Package Registries"
    )
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    magicRootGitlabPublicResolvers := Set.empty,
    magicRootGitlabCredentials := Map.empty,
    Keys.resolvers ++= (
      magicRootGitlabPublicResolvers.value.toSeq ++
      magicRootGitlabCredentials.value.keys.toSeq
    ).distinct.sorted.map(id =>
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

/** AcceptanceFeaturesPackagingPlugin - packages .feature files into the project JAR */
object AcceptanceFeaturesPackagingPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends AcceptanceFeaturesPackagingPluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ magicRootFeaturesPackagingSettings
}
