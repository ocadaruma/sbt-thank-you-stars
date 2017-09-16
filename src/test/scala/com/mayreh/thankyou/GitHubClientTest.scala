package com.mayreh.thankyou

import java.net.HttpURLConnection

import org.scalatest.FlatSpec
import sbt._

class GitHubClientTest extends FlatSpec {

  def starred(repo: GitHubRepo): Boolean = {
    val token = sys.env("THANK_YOU_STARS_GITHUB_TOKEN")
    val u = url(s"https://api.github.com/user/starred/${repo.owner}/${repo.repo}")

    var conn: HttpURLConnection = null
    try {
      conn = u.openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestProperty("Authorization", s"token ${token}")
      conn.getResponseCode == 204
    } finally {
      Option(conn).foreach(_.disconnect())
    }
  }

  it should "can star a repository" in {

    val client = GitHubClient()
    client.star(GitHubRepo("opt-tech", "chronoscala"))

    assert(starred(GitHubRepo("opt-tech", "chronoscala")))
  }
}
