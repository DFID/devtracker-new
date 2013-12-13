package uk.gov.dfid.support

import java.io.{File, InputStream}
import java.nio.file.{Paths, Files}

trait FileSystemSupport {
  protected def mkdirp(path: String) = {
    import sys.process._
    s"mkdir -p $path".!!
    path
  }

  protected def save(stream: InputStream, path: String) {
    new File(path).delete()
    Files.copy(stream, Paths.get(path))
  }

  protected def fileLastModified(path: String) = new File(path) match {
    case file if file.exists => Some(file.lastModified)
    case _ => None
  }

  protected def ls(dir: String) = {
    new File(dir).list
  }

  protected def rm(file: String) = {
    new File(file).delete()
  }
}