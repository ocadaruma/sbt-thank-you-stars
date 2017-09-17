package com.mayreh.thankyou

import sbt._

case class ScmUrl(candidates: Seq[String])

object IvyUtil {

  /**
   * Retrieve SCM url from content of pom.xml (cached to ivy-*.xml.original).
   * Check by following priority:
   *   1. project \ scm \ url
   *   2. project \ url
   *   3. project \ licenses \ license \ url
   */
  def scmUrlFromJarPath(jarPath: File): ScmUrl = {
    val moduleRoot = jarPath.getParentFile.getParentFile

    // find ivy-*.xml.original of latest version
    val ivyXmlFile = moduleRoot.listFiles(FileFilter.globFilter("ivy-*.xml.original")).last
    val ivyXml = scala.xml.XML.loadFile(ivyXmlFile)

    val candidates = Seq(
      ivyXml \ "scm" \ "url",
      ivyXml \ "url",
      ivyXml \ "licenses" \ "license" \ "url").collect {
      case nodes if nodes.nonEmpty => nodes.text
    }

    ScmUrl(candidates)
  }
}
