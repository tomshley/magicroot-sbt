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
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.*

protected[projectsettings] trait WebPluginKeys extends BasicSbtSettingsKeys {
  val gzip = TaskKey[Pipeline.Stage]("gzip-compress", "Add gzipped files to asset pipeline.")

  lazy val webContentCopy: TaskKey[Unit] =
    taskKey[Unit]("Generates project template content")

  val simpleUrlUpdate = TaskKey[Pipeline.Stage](
    "simple-url-update",
    "Update assets url in static css or js files with in asset pipeline."
  )

  val algorithms = SettingKey[Seq[String]](
    "digest-algorithms",
    "Types of checksum used in the digest pipeline to generate."
  )
}
