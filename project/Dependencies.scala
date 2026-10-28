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

import sbt.Keys.*
import sbt.{Def, *}

object Dependencies {
  val Scala213 = "2.13.12"
  val Scala212 = "2.12.18"
  val Scala3 = "3.3.1"
  val Scala2Versions: Seq[String] = Seq(Scala213, Scala212)
  val ScalaVersions: Seq[String] = Dependencies.Scala2Versions :+ Dependencies.Scala3

  val commonProject: Seq[Def.Setting[?]] = Seq(
    libraryDependencies ++= Seq(
        )
  )
  val settingsPluginProject: Seq[Def.Setting[?]] = Seq(
    libraryDependencies ++= Seq(
        )
  )
  val templatePluginProject: Seq[Def.Setting[?]] = Seq(
    Test / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources",
    Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources",

    libraryDependencies ++= Seq(
        )
  )
}
