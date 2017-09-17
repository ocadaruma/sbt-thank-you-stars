package com.mayreh.thankyou

import java.io.File

import org.scalatest.FlatSpec

class IvyUtilTest extends FlatSpec {

  it should "find scmInfo from jar file" in {
    val jarPath = new File(getClass.getClassLoader.getResource("cache/jp.ne.opt/chronoscala_2.12/jars/chronoscala_2.12-0.1.1.jar").toURI)

    val url = IvyUtil.scmUrlFromJarPath(jarPath)
    assert(url.candidates == Seq(
      "git@github.com:opt-tech/chronoscala.git",
      "https://github.com/opt-tech/chronoscala",
      "https://raw.githubusercontent.com/opt-tech/chronoscala/master/LICENSE"))
  }
}
