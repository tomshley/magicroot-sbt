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

protected[projectsettings] trait AcceptanceFeaturesPackagingPluginKeys extends BasicSbtSettingsKeys {
  val magicRootFeaturesRoot: SettingKey[File] =
    settingKey[File]("Shared feature files directory")

  val magicRootVerifyFeaturesJar: TaskKey[Unit] =
    taskKey[Unit]("Verify JAR contains expected feature files at correct paths")

  private def findFeaturesDir(dir: File): Option[File] = {
    val candidate = dir / "features"
    if (candidate.exists() && candidate.isDirectory) Some(candidate)
    else Option(dir.getParentFile).flatMap(findFeaturesDir)
  }

  lazy val magicRootFeaturesPackagingSettings: Seq[Def.Setting[?]] = Seq(
    magicRootFeaturesRoot := {
      val projectBase = baseDirectory.value
      findFeaturesDir(projectBase).getOrElse {
        sys.error(
          s"AcceptanceFeaturesPackagingPlugin: unable to locate a features directory from ${projectBase.getAbsolutePath}. " +
            "Set magicRootFeaturesRoot explicitly for this project layout."
        )
      }
    },
    watchSources += WatchSource(magicRootFeaturesRoot.value, "*.feature", NothingFilter),
    Compile / resourceGenerators += Def.task {
      val featuresDir = magicRootFeaturesRoot.value
      val managedDir = (Compile / resourceManaged).value / "features"
      val cacheDir = streams.value.cacheDirectory / "features-copy"
      val outputsFile = cacheDir / "outputs.list"
      val featureFiles = (featuresDir ** "*.feature").get.toSet
      val expectedTargets = featureFiles.map { f =>
        val relative = featuresDir.toPath.relativize(f.toPath).toString.replace('\\', '/')
        managedDir / relative
      }

      if (!featuresDir.exists() || !featuresDir.isDirectory) {
        sys.error(
          s"AcceptanceFeaturesPackagingPlugin: configured features directory does not exist: ${featuresDir.getAbsolutePath}"
        )
      }

      IO.createDirectory(cacheDir)

      val previousOutputs =
        if (outputsFile.exists()) IO.readLines(outputsFile).map(file).toSet
        else Set.empty[File]

      val staleOwnedOutputs = previousOutputs -- expectedTargets
      IO.delete(staleOwnedOutputs.toSeq)

      val cached = FileFunction.cached(cacheDir, FilesInfo.lastModified, FilesInfo.exists) {
        inputs =>
          inputs.map { f =>
            val relative = featuresDir.toPath.relativize(f.toPath).toString.replace('\\', '/')
            val target = managedDir / relative
            IO.copyFile(f, target)
            target
          }
      }
      cached(featureFiles)
      val stableExpectedTargets = expectedTargets.toSeq.sortBy(_.getAbsolutePath)
      IO.writeLines(outputsFile, stableExpectedTargets.map(_.getAbsolutePath), append = false)
      stableExpectedTargets
    }.taskValue,
    magicRootVerifyFeaturesJar := {
      val jar = (Compile / packageBin).value
      val featuresDir = magicRootFeaturesRoot.value
      val expected = (featuresDir ** "*.feature").get.map { f =>
        "features/" + featuresDir.toPath.relativize(f.toPath).toString.replace('\\', '/')
      }.toSet
      val entries = {
        val zf = new java.util.zip.ZipFile(jar)
        try {
          import scala.jdk.CollectionConverters.*
          zf.entries.asScala.map(_.getName).filter(_.endsWith(".feature")).toSet
        } finally zf.close()
      }
      val missing = expected -- entries
      val unexpected = entries -- expected
      if (missing.nonEmpty || unexpected.nonEmpty) {
        sys.error(
          s"JAR feature verification failed!\n" +
            s"  Missing: ${missing.mkString(", ")}\n" +
            s"  Unexpected: ${unexpected.mkString(", ")}"
        )
      }
      streams.value.log.info(s"JAR verified: ${entries.size} feature files at correct paths")
    },
  )
}
