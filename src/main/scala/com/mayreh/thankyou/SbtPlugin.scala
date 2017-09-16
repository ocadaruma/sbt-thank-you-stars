package com.mayreh.thankyou

import sbt._
import Keys._

import scala.annotation.tailrec

object SbtPlugin extends AutoPlugin {

  object autoImport {
    val thankYouStars: TaskKey[Unit] = taskKey[Unit]("")
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
      val thisModule = moduleKey(organization.value %% name.value % version.value)
      val logger = sLog.value

      val client = GitHubClient()

      val modules = updateReport.configurations.flatMap { config =>
        config.modules.collect {
          case m if m.callers.exists(c => moduleKey(c.caller) == thisModule) => m
        }
      }.distinctBy(m => moduleKey(m.module))

      @tailrec
      def iterateUntilGitHubRepoFound(jars: Seq[File]): Option[GitHubRepo] = jars match {
        case Nil =>
          None
        case head +: tail =>
          IvyUtil.scmUrlFromJarPath(head).flatMap(u => GitHubRepo.fromUrl(u.url)) match {
            case result @ Some(_) => result
            case _ => iterateUntilGitHubRepoFound(tail)
          }
      }

      modules.foreach { module =>
        module.homepage.flatMap(GitHubRepo.fromUrl)
          .orElse(iterateUntilGitHubRepoFound(module.artifacts.map { case (_, file) => file }))
          .foreach { repo =>
//            client.star(repo)
            logger.info(s"Starred! https://github.com/${repo.owner}/${repo.repo}")
          }
      }
    }
  )
}
