package com.mayreh.thankyou

import sbt._
import Keys._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Try

object SbtPlugin extends AutoPlugin {

  object autoImport {
    val thankYouStars: TaskKey[Unit] = taskKey[Unit]("Give your dependencies stars on GitHub!")
  }

  import autoImport._

  override def trigger = allRequirements

  override def projectSettings: Seq[Setting[_]] = Seq(
    thankYouStars := {
      val updateReport = update.value
      def moduleKey = (m: ModuleID) => {
        val scalaSuffix = s"_${scalaBinaryVersion.value}"
        val normalizedArtifact = m.name.trimSuffix(scalaSuffix)
        (m.organization, normalizedArtifact, m.revision)
      }
      def rejectScalaLibs = (m: ModuleReport) => {
        m.module.name match {
          case "scala-library" => false
          case "scala-compiler" => false
          case _ => true
        }
      }

      val thisModule = moduleKey(organization.value %% name.value % version.value)
      val logger = sLog.value

      val client = GitHubClient()

      val modules = updateReport.configurations.flatMap { config =>
        config.modules.collect {
          // reject scala-libs and filter only directly dependent modules
          case m if m.callers.exists(c => moduleKey(c.caller) == thisModule) && rejectScalaLibs(m) => m
        }
      }.distinctBy(m => moduleKey(m.module)) // deduplicate

      @tailrec
      def iterateUntilGitHubRepoFound(jars: Seq[File]): Option[GitHubRepo] = jars match {
        case Nil =>
          None
        case head +: tail =>
          IvyUtil.scmUrlFromJarPath(head).candidates.flatMap(GitHubRepo.fromUrl) match {
            case repo +: _ => Some(repo)
            case _ => iterateUntilGitHubRepoFound(tail)
          }
      }

      val cache = mutable.Set.empty[GitHubRepo]
      modules.foreach { module =>
        Try {
          module.homepage.flatMap(GitHubRepo.fromUrl)
            .orElse(iterateUntilGitHubRepoFound(module.artifacts.map { case (_, file) => file }))
            .fold {
              logger.info(s"---- Skipped. No GitHub repo found for ${module.module}")
            } { repo =>
              if (!cache(repo)) {
                cache += repo
                client.star(repo)
                logger.info(s"Starred! https://github.com/${repo.owner}/${repo.repo}")
              }
            }
        }.recover {
          case e =>
            logger.warn(s"An error occurred while starring ${module.module}")
            logger.trace(e)
        }
      }
    }
  )
}
