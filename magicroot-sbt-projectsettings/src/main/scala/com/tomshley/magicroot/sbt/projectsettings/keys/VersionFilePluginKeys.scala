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

protected[projectsettings] trait VersionFilePluginKeys extends BasicSbtSettingsKeys {
  // @see https://gitlab.com/tomshley/tomshley-oss-dependencies/-/tree/main/cicd-pipelines/common/specs/naming-conventions.yml
  val magicRootBaseVersion: SettingKey[String] = settingKey[String]("Base version read from the VERSION file")

  private def findVersionFile(dir: File): Option[File] = {
    val candidate = dir / "VERSION"
    if (candidate.exists()) Some(candidate)
    else Option(dir.getParentFile).flatMap(findVersionFile)
  }

  /** @deprecated Use TomshleyCIBuildVersionPlugin instead. Since 1.3.3. */
  lazy val versionFileSettings: Seq[Def.Setting[String]] = Seq(
    magicRootBaseVersion := {
      val projectRoot = (ThisBuild / baseDirectory).value
      val versionFile = findVersionFile(projectRoot).getOrElse(projectRoot / "VERSION")
      val versionFileContents: Seq[String] =
        if (versionFile.exists()) IO.readLines(versionFile)
        else Seq("0.0.0")
      versionFileContents.filter(s => !s.isBlank).mkString("-").trim.stripPrefix("v")
    },
    version := magicRootBaseVersion.value
  )
}
