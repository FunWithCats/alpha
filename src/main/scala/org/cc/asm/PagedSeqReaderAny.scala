package main.scala.org.cc.asm

/**
 * Created with IntelliJ IDEA.
 * User: tof
 * Date: 17/03/13
 * Time: 19:27
 * To change this template use File | Settings | File Templates.
 */
import scala.collection.immutable.PagedSeq
import util.parsing.input.{Reader, Position}

class PagedSeqReaderAny[+T](ps: PagedSeq[T], val eof : T, override val offset: Int = 0) extends Reader[T] {
    def first = if (ps.isDefinedAt(offset)) ps(offset) else eof

    def rest: PagedSeqReaderAny[T] =
      if (ps.isDefinedAt(offset)) new PagedSeqReaderAny[T](ps, eof, offset + 1)
      else this

    def pos: Position = new Position {
      def column = offset
      def line   = 0
      def lineContents = throw new NoSuchMethodError("not a char sequence reader")
    }

    def atEnd = !ps.isDefinedAt(offset)

    override def drop(n: Int): PagedSeqReaderAny[T] = new PagedSeqReaderAny[T](ps, eof, offset + n)
}
