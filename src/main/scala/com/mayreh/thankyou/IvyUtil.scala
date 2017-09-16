package com.mayreh.thankyou

import sbt._

import scala.util.Try

case class ScmUrl(url: String)

object IvyUtil {

  /**
   * Retrieve SCM url from content of pom.xml (cached to ivy-*.xml.original).
   * Check by following priority:
   *   1. project \ scm \ url
   *   2. project \ url
   *   3. project \ licenses \ license \ url
   */
  def scmUrlFromJarPath(jarPath: File): Try[ScmUrl] = Try {
    val moduleRoot = jarPath.getParentFile.getParentFile
    val ivyXml = moduleRoot.listFiles(FileFilter.globFilter("ivy-*.xml.original")).last
    val url = (scala.xml.XML.loadFile(ivyXml) \ "scm" \ "url").text

    ScmUrl(url)
  }
}
