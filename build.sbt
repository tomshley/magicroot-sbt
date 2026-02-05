import sbt.{addDependencyTreePlugin, addSbtPlugin, file}

lazy val gitlabCIProjectId = 70100400

lazy val assemblySettings = Seq(
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", _*) => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
)

lazy val publishSettings = Seq(
  ThisBuild / resolvers ++= Registry.additionalResolvers(gitlabCIProjectId),
  ThisBuild / credentials += Registry.credentials(
    Some((ThisBuild / baseDirectory).value / ".secure_files" / ".credentials.gitlab")
  ),
  ThisBuild / publishTo := Registry.publishToGitlab(gitlabCIProjectId)
)

lazy val commonProject = magicrootProject("magicroot-common", Dependencies.commonProject, Scala3.settings)
lazy val commonSbtProject =
  magicrootSbtProject("magicroot-sbt-common", Dependencies.commonProject, Scala3.settings)
lazy val settingsPluginProject =
  magicrootScriptedPluginProject(
    "projectsettings",
    Dependencies.settingsPluginProject,
    Scala3.settings,
    Seq(
      addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "1.1.0-M1"),
      addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2"),
      addSbtPlugin("org.playframework.twirl" % "sbt-twirl" % "2.0.1"),
      addSbtPlugin("com.github.sbt" % "sbt-web-build-base" % "2.0.2"),
      addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.4"),
      addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1"),
      addDependencyTreePlugin
    ),
    addSbtWeb("1.5.8")
  ).enablePlugins(SbtWebBase)
lazy val templatePluginProject =
  magicrootScriptedPluginProject("projecttemplate", Dependencies.templatePluginProject, Scala3.settings)

lazy val magicrootPlugins = (project in file("."))
  .aggregate(
    commonProject,
    commonSbtProject,
    settingsPluginProject,
    templatePluginProject
  )
  .settings(
    publish / skip := true,
    publishArtifact := false
  )
  .settings(publishSettings *)

def magicrootProject(projectName: String, additionalSettings: sbt.Def.SettingsDefinition*): Project = {
  Project(id = projectName, base = file(projectName))
    .enablePlugins(AssemblyPlugin)
    .settings(
      assembly / assemblyJarName := s"${projectName}.jar",
      organization := "com.tomshley.magicroot",
      name := projectName,
      version := {
        val versionFile = (ThisBuild / baseDirectory).value / "VERSION"
        val versionFileContents: Seq[String] = if (versionFile.exists()) IO.readLines(versionFile) else if (version.value.nonEmpty) Seq(version.value) else Seq.empty[String]
        versionFileContents.filter(s => !s.isBlank).mkString("-").trim.stripPrefix("v")
      },
      licenses := {
        val tagOrBranch =
          if (version.value.endsWith("SNAPSHOT")) "main"
          else "v" + version.value
        Seq(
          ("APACHE-2.0", url("https://raw.githubusercontent.com/tomshley/sbt-magicroot/" + tagOrBranch + "/LICENSE"))
        )
      },
      scalacOptions += "-Wconf:cat=deprecation&msg=.*JavaConverters.*:s"
    )
    .settings(publishSettings *)
    .settings(additionalSettings *)
}
def magicrootSbtProject(projectName: String, additionalSettings: sbt.Def.SettingsDefinition*): Project = {
  magicrootProject(projectName, additionalSettings *)
    .settings(
      sbtPlugin := true
    )
    .dependsOn(commonProject)
}
def magicrootScriptedPluginProject(pluginProjectName: String,
                                   additionalSettings: sbt.Def.SettingsDefinition*): Project = {
  val magicrootProjectName = "magicroot-sbt-" + pluginProjectName
  magicrootSbtProject(magicrootProjectName, additionalSettings *)
    .settings(
      scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
      scriptedBufferLog := false
    )
    .dependsOn(commonProject, commonSbtProject)
}
