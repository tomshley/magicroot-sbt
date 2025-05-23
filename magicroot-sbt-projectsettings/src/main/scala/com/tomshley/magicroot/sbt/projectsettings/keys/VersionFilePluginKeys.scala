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
import sbt.{Def, *}

protected[projectsettings] trait VersionFilePluginKeys extends BasicSbtSettingsKeys {
  lazy val versionFileSettings: Seq[Def.Setting[String]] = Seq(
    version := {
      val versionFile = (ThisBuild / baseDirectory).value / "VERSION"
      val versionFileContents: Seq[String] = if (versionFile.exists()) IO.readLines(versionFile) else if (version.value.nonEmpty) Seq(version.value) else Seq.empty[String]
      versionFileContents.filter(s => !s.isBlank).mkString("-").trim.stripPrefix("v")
    }
  )
}
