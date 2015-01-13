package main.scala.org.cc.asm

import main.scala.org.cc._
import Pos._

object Pruner {
  case class EscapedChar(char : Char, escaped : Boolean = false)

  def prune(i : Iterator[Char]) = new IteratorCache[Positioned[EscapedChar]] { self =>
    protected def update : Option[Positioned[EscapedChar]] = mode.current().reify

    var pos = start

    def read : Option[Positioned[Char]] = {
      if (i.hasNext) {
        val oldPos = pos
        val ch  = i.next
        val offset = pos.offset + 1
        var line   = pos.line
        var column = pos.column
        ch match { case '\n' => { line   += 1
                                  column  = 0
                                }
                   case _    => { column += 1 }
                 }
        pos = Position(offset,line,column)
        Some(Positioned(ch, oldPos, pos))
      }
      else None
    }

    def readM : OptPos[Char]   = OptPos.reflect[Char](read)

    type Mode = () => OptPos[EscapedChar]

    object mode {
      var current  : Mode = () => standard

      val stack          = new scala.collection.mutable.Stack[Mode]
      def push(m : Mode) = { stack.push(current)
                             current = m
                             m
                           }
      def pop            = { try { current = stack.pop() }
                             catch { case e : Throwable => {} }
                             current
                           }
    }

    def string(delim:Char) : OptPos[EscapedChar] = readM.>>= {
      case '\\' => { readM.>>= { case 'n'  => OptPos.unit(EscapedChar('\n', false))
                                 case 't'  => OptPos.unit(EscapedChar('\t', false))
                                 case 'r'  => OptPos.unit(EscapedChar('\r', false))
                                 case '\\' => OptPos.unit(EscapedChar('\\', false))
                                 case '\'' => OptPos.unit(EscapedChar('\'', true ))
                                 case '\"' => OptPos.unit(EscapedChar('\'', true ))
                                 case '\n' => string(delim)
                                 case '\r' => string(delim)
                                 case '\t' => string(delim)
                                 case c    => throw new Exception("Invalid escape char \\" + c + ".")
                               }
                   }
      case c    => { if (c == delim) mode.pop
                     OptPos.unit(EscapedChar(c, false))
                   }
    }

    def lineComment : OptPos[EscapedChar] = readM.>>= {
      case c @ ('\n' | '\r') => { mode.pop
                                  OptPos.unit(EscapedChar(c,false))
                                }
      case _                 => lineComment
    }

    val lineCommentMode : Mode = () => lineComment

    def select(c : Char) = c match {
      case '\"' | '\'' => { mode.push(() => string(c))
                            OptPos.unit(EscapedChar(c , false))
                          }
      case '|'         => mode.push(lineCommentMode)()
      case _           => OptPos.unit(EscapedChar(c , false))
    }

    def standard : OptPos[EscapedChar] = readM.>>=(select(_))
  }
}
