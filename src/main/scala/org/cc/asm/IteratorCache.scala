package main.scala.org.cc.asm

trait IteratorCache[A] extends Iterator[A] {
  private var cache : Option[A] = null

  protected def update : Option[A]

  def hasNext : Boolean = {
    if (cache == null) cache = update
    !cache.isEmpty
  }

  def next : A = {
    if (cache == null) cache = update
    val r = cache.get
    cache = null
    r
  }
}