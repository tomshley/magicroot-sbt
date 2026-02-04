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

package com.tomshley.magicroot.sbt.projectsettings.vendor

import sbt.*

/**
 * Boilerplate JVM library settings for magicroot projects.
 * References published artifacts from com.tomshley.boilerplate
 */
object BoilerplateProjectSettings {
  
  object Versions {
    val boilerplateVersion = "1.1.0"
  }
  
  object Resolvers {
    val boilerplateResolvers: Seq[MavenRepository] = Seq(
      "gitlab-maven-boilerplate" at "https://gitlab.com/api/v4/projects/70100980/packages/maven"
    )
  }
  
  object Libraries {
    import Versions._
    
    /** Core utilities: logging, JSON, time, config */
    val boilerplateCoreLibraries: Seq[ModuleID] = Seq(
      "com.tomshley.boilerplate" %% "boilerplate-core" % boilerplateVersion
    )
    
    /** Pekko actor utilities: cluster, sharding, HTTP routing */
    val boilerplatePekkoLibraries: Seq[ModuleID] = Seq(
      "com.tomshley.boilerplate" %% "boilerplate-pekko" % boilerplateVersion
    )
    
    /** CloudEvents transport abstractions */
    val boilerplateTransportLibraries: Seq[ModuleID] = Seq(
      "com.tomshley.boilerplate" %% "boilerplate-transport" % boilerplateVersion
    )
    
    /** Blob storage with S3 implementation (claim-check pattern) */
    val boilerplateStorageLibraries: Seq[ModuleID] = Seq(
      "com.tomshley.boilerplate" %% "boilerplate-storage" % boilerplateVersion
    )
    
    /** Kafka producer utilities with Avro/Proto serialization */
    val boilerplateKafkaLibraries: Seq[ModuleID] = Seq(
      "com.tomshley.boilerplate" %% "boilerplate-kafka" % boilerplateVersion
    )
    
    /** Event-sourced entity and projection abstractions */
    val boilerplatePersistenceLibraries: Seq[ModuleID] = Seq(
      "com.tomshley.boilerplate" %% "boilerplate-persistence" % boilerplateVersion
    )
    
    /** Transactional outbox pattern with Kafka publisher */
    val boilerplateOutboxLibraries: Seq[ModuleID] = Seq(
      "com.tomshley.boilerplate" %% "boilerplate-outbox" % boilerplateVersion
    )
    
    /** All boilerplate libraries combined */
    val boilerplateAllLibraries: Seq[ModuleID] = 
      boilerplateCoreLibraries ++
      boilerplatePekkoLibraries ++
      boilerplateTransportLibraries ++
      boilerplateStorageLibraries ++
      boilerplateKafkaLibraries ++
      boilerplatePersistenceLibraries ++
      boilerplateOutboxLibraries
  }
}
