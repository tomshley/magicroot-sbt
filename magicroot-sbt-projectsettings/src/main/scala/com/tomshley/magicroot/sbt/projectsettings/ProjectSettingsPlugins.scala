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
import com.typesafe.sbt.web.SbtWeb
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

/** CoreProjectPlugin with boilerplate-core and boilerplate-pekko */
object CoreBoilerplateProjectPlugin extends AutoPlugin {

  override val requires: Plugins = CoreProjectPlugin &&
    BoilerplatePekkoPlugin
  
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings
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

/** ValueAddProjectPlugin with boilerplate persistence, kafka, and outbox */
object ValueAddBoilerplateProjectPlugin extends AutoPlugin {

  override val requires: Plugins = ValueAddProjectPlugin &&
    BoilerplatePersistencePlugin &&
    BoilerplateKafkaPlugin &&
    BoilerplateOutboxPlugin &&
    BoilerplateStoragePlugin
  
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings
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

/** EdgeProjectPlugin with all boilerplate modules */
object EdgeBoilerplateProjectPlugin extends AutoPlugin {
  override val requires: Plugins = EdgeProjectPlugin &&
    BoilerplateAllPlugin
  
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings
}

object EdgeWebPlugin extends AutoPlugin {
  override def requires: Plugins = SbtWeb

  override def trigger: PluginTrigger = AllRequirements

  object autoImport extends WebPluginKeys

}

// =============================================================================
// Boilerplate JVM Module Plugins - orthogonal plugins for each module
// =============================================================================

/** Adds boilerplate-core: logging, JSON, time, config utilities */
object BoilerplateCorePlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BaseProjectSettingsPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplateCoreProject
}

/** Adds boilerplate-pekko: Pekko actor utilities, cluster, sharding */
object BoilerplatePekkoPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BoilerplateCorePlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplatePekkoProject
}

/** Adds boilerplate-transport: CloudEvents transport abstractions */
object BoilerplateTransportPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BoilerplateCorePlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplateTransportProject
}

/** Adds boilerplate-storage: Blob storage with S3 (claim-check pattern) */
object BoilerplateStoragePlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BoilerplatePekkoPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplateStorageProject
}

/** Adds boilerplate-kafka: Kafka producer with Avro/Proto serialization */
object BoilerplateKafkaPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BoilerplatePekkoPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplateKafkaProject
}

/** Adds boilerplate-persistence: Event-sourced entities and projections */
object BoilerplatePersistencePlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BoilerplatePekkoPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplatePersistenceProject
}

/** Adds boilerplate-outbox: Transactional outbox with Kafka publisher */
object BoilerplateOutboxPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BoilerplateKafkaPlugin && BoilerplatePersistencePlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplateOutboxProject
}

/** Adds all boilerplate modules - convenience plugin for full stack */
object BoilerplateAllPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = BaseProjectSettingsPlugin

  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.boilerplateAllProject
}
