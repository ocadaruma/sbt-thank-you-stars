package com.mayreh.thankyou

import java.net.HttpURLConnection

import sbt._

import scala.util.Try
import scala.util.parsing.json.JSON

/**
 * Represents a GitHub repository.
 */
case class GitHubRepo(owner: String, repo: String)
object GitHubRepo {

  /**
   * Parse scm url to GitHubRepo if url represents GitHub repository.
   */
  def fromUrl(url: String): Option[GitHubRepo] = {
    Try {
      // handle ssh url
      if (url.startsWith("git@github.com") || url.startsWith("git://git@github.com")) {
        val uri = new java.net.URI(url.prepend("git://"))
        val owner = uri.getAuthority.split(':').last
        val repo = uri.getPath.split('/').last.trimSuffix(".git")
        Some(GitHubRepo(owner, repo))
      } else {
        // handle https url
        val uri = new java.net.URI(url)
        (uri.getHost, uri.getPath.split('/')) match {
          case ("github.com", Array(_, owner, repo, _*)) =>
            Some(GitHubRepo(owner, repo.trimSuffix(".git")))
          case _ =>
            None
        }
      }
    }.toOption.flatten
  }
}

/**
 * Provides features to access GitHub.
 */
class GitHubClient(personalAccessToken: String) {

  def star(repo: GitHubRepo): Unit = {
    val u = url(s"https://api.github.com/user/starred/${repo.owner}/${repo.repo}")

    var conn: HttpURLConnection = null
    try {
      conn = u.openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("PUT")
      conn.setRequestProperty("Authorization", s"token ${personalAccessToken}")
      conn.getResponseCode
    } finally {
      Option(conn).foreach(_.disconnect())
    }
  }
}

case class ConfigurationException(msg: String) extends RuntimeException(msg)
object GitHubClient {

  def apply(): GitHubClient = {

    val specifiedToken = sys.env.get("THANK_YOU_STARS_GITHUB_TOKEN")
    val jsonFile = sys.env.get("THANK_YOU_STARS_JSON_FILE").map(file)
      .orElse(sys.props.get("user.home").map(file(_) / ".thank-you-stars.json"))

    specifiedToken.map(new GitHubClient(_))
      .orElse(jsonFile.flatMap { file =>
        JSON.parseFull(IO.read(file)).map { m =>
          val token = m.asInstanceOf[Map[String, String]]("token")
          new GitHubClient(token)
        }
      })
      .getOrElse(throw ConfigurationException(
        """Personal Access Token is required.
          |1. Specify THANK_YOU_STARS_GITHUB_TOKEN. or
          |2. Specify THANK_YOU_STARS_JSON_FILE (location of json configuration file). or
          |3. Locate json configuration file to $HOME/.thank-you-stars.json""".stripMargin))
  }
}
