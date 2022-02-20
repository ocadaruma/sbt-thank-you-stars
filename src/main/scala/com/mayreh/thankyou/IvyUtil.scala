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
    // find ivy-*.xml.original of latest version
    val ivyXmlFile =
      jarPath.getParentFile.getParentFile.listFiles(FileFilter.globFilter("ivy-*.xml.original")).lastOption

    val ivyXml = ivyXmlFile.map(scala.xml.XML.loadFile)

    // find *.pom of latest version
    val mavenXmlFile =
      jarPath.getParentFile.listFiles(FileFilter.globFilter("*.pom")).lastOption

    val mavenXml = mavenXmlFile.map(scala.xml.XML.loadFile)

    def candidates(elem: scala.xml.Elem) =
      Seq(
        elem \ "scm" \ "url",
        elem \ "url",
        elem \ "licenses" \ "license" \ "url"
      ).collect {
        case nodes if nodes.nonEmpty => nodes.text
      }

    ScmUrl(Seq(mavenXml, ivyXml).flatten.map(candidates).reduceLeft(_ ++ _))
  }
}
