package com.mayreh.thankyou

import org.scalatest.FlatSpec

class GitHubRepoTest extends FlatSpec {

  it should "parse https github web url" in {
    assert(GitHubRepo.fromUrl("https://github.com/opt-tech/chronoscala") == Some(GitHubRepo("opt-tech", "chronoscala")))
  }

  it should "parse https github repo url" in {
    assert(GitHubRepo.fromUrl("https://github.com/opt-tech/chronoscala.git") == Some(GitHubRepo("opt-tech", "chronoscala")))
  }

  it should "parse ssh github repo url" in {
    assert(GitHubRepo.fromUrl("git@github.com:opt-tech/chronoscala.git") == Some(GitHubRepo("opt-tech", "chronoscala")))
  }

  it should "return None if non-github web url" in {
    assert(GitHubRepo.fromUrl("http://www.example.com").isEmpty)
  }

  it should "return None if non-github url" in {
    assert(GitHubRepo.fromUrl("git://git.apache.org/kafka.git").isEmpty)
  }
}
