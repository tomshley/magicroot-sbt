/*
 * Copyright 2023 Tomshley LLC
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

import com.tomshley.magicroot.sbt.projectsettings.keys.{CommonProjectKeys, ForkJVMRunConfigKeys, ProjectTypeKeys, PublishGitLabPluginKeys, SecureFilesPluginKeys, VersionFilePluginKeys, WebPluginKeys}
import com.tomshley.magicroot.sbt.projectsettings.settings.ProjectSettingsDefs
import com.typesafe.sbt.packager.Keys.dockerAliases
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker
import com.typesafe.sbt.web.SbtWeb
import org.apache.pekko.grpc.sbt.PekkoGrpcPlugin
import play.twirl.sbt.SbtTwirl
import sbt.Keys.{baseDirectory, publish, publishLocal, streams}
import sbt.*

import scala.sys.process.Process

protected[this] object BaseProjectSettingsPlugin extends AutoPlugin {

  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends CommonProjectKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] = {
    super.projectSettings ++ baseSettings3
  }
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

  override def projectSettings: Seq[Def.Setting[?]] = {
    super.projectSettings ++ forkJVMRunConfigSettings
  }
}

object VersionFilePlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends VersionFilePluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] = {
    super.projectSettings ++ versionFileSettings
  }
}

object SecureFilesPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends SecureFilesPluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] = {
    super.projectSettings ++ secureFilesSettings
  }
}

object PublishGitLabPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends PublishGitLabPluginKeys

  import autoImport.*

  override def projectSettings: Seq[Def.Setting[?]] = {
    super.projectSettings ++ publishSettings
  }
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
    ProjectSettingsDefs.dockerPublishProject ++dockerBuildxSettings

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
      dockerAliases.value.foreach(
        alias => Process("docker buildx build --platform=linux/arm64,linux/amd64 --push -t " +
          alias + " .", baseDirectory.value / "target" / "docker"/ "stage").!
      )
    },
    Docker / publish := Def.sequential(
      Docker / publishLocal,
      ensureDockerBuildx,
      dockerBuildWithBuildx
    ).value
  )
}

object CoreProjectPlugin extends AutoPlugin {

  override val requires: Plugins = DockerPublishPlugin &&
    PekkoGrpcPlugin &&
    LibProjectPekkoPlugin &&
    LibManagedProjectPlugin
  override def projectSettings: Seq[Def.Setting[?]] =
    super.projectSettings ++ ProjectSettingsDefs.globalRunSettings
//    ProjectSettingsDefs.magicrootAkkaGrpcProject
}
object ValueAddProjectPlugin extends AutoPlugin {

  override val requires
  : Plugins = DockerPublishPlugin &&
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
    DockerPublishPlugin &&
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

object EdgeWebPlugin extends AutoPlugin {
  override def requires: Plugins = SbtWeb

  override def trigger: PluginTrigger = AllRequirements

  object autoImport extends WebPluginKeys

  import autoImport.*

//  override def projectSettings: Seq[Setting[_]] = Seq(
//    gzip / includeFilter := "*.html" || "*.css" || "*.js",
//    gzip / excludeFilter := HiddenFileFilter,
//    gzip / target := webTarget.value / gzip.key.label,
//    deduplicators += SbtWeb.selectFileFrom((gzip / target).value),
//    gzip := gzipFiles.value,
//    webContentCopy := {
//      val assetsDir = (Assets / sourceDirectory).value
//      val publicDir = (Assets / resourceDirectory).value
//      val managedDir = (Assets / resourceManaged).value
//      val targetWebDir = webTarget.value
//      val resources = publicDir.**(_.isFile).get()
//      val mappings = resources pair relativeTo(publicDir)
//      //      val renamed = mappings map { case (file, path) => file -> path.replaceAll("js", "paste.js") }
//      //      val copies = renamed map { case (file, path) => file -> managedDir / path }
//      println("&*****************")
//      println("(Assets/sourceDirectory)" + assetsDir)
//      println("(Assets/resourceDirectory)" + publicDir)
//      println("(Assets / resourceManaged)" + managedDir)
//      println("(webTarget)" + targetWebDir)
//      println("(resources)" + resources)
//      println("(mappings)" + mappings)
//      //      println("(renamed)" + renamed)
//      //      println("(copies)" + copies)
//      println("&*****************")
//
//      val (js, other) = mappings partition (_._2.endsWith(".js"))
//      val minFile = targetWebDir / "js" / "all.min.js"
//      IO.touch(minFile)
//      val minMappings = Seq(minFile) pair relativeTo(targetWebDir)
//      minMappings ++ other
//    }
//  )
//
//  def gzipFiles: Def.Initialize[Task[Pipeline.Stage]] = Def.task {
//    val targetDir = (gzip / target).value
//    val include = (gzip / includeFilter).value
//    val exclude = (gzip / excludeFilter).value
//    mappings =>
//      val gzipMappings = for {
//        (file, path) <- mappings if !file.isDirectory && include.accept(file) && !exclude.accept(file)
//      } yield {
//        val gzipPath = path + ".gz"
//        val gzipFile = targetDir / gzipPath
//        IO.gzip(file, gzipFile)
//        (gzipFile, gzipPath)
//      }
//      mappings ++ gzipMappings
//  }
}
