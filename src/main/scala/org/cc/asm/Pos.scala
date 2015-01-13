package main.scala.org.cc.asm

import main.scala.org.cc._
import Monadic._

object Pos {
  case class Position(offset: Long, line : Long, column : Long) {
    @inline final def max(p2 : Position) = if (offset >= p2.offset) this else p2
    @inline final def min(p2 : Position) = if (offset <= p2.offset) this else p2
  }

  val start = Position(0,0,0)

  trait MPositioned extends Monad[Positioned] {
    def unit[B](a : B) : Positioned[B] = Positioned(a,start,start)
    def join[B](m : Positioned[Positioned[B]])  : Positioned[B] = m match {
      case Positioned(Positioned(x, begin2, end2) , begin1, end1) => Positioned(x, begin1, end1.max(end2) )
    }
    def fmap[A,B](m : Positioned[A], f: A => B) : Positioned[B] = Positioned(f(m.value),m.begin,m.end)
  }

  case class Positioned[A]( value : A, begin : Position = start, end : Position = start)
       extends Monadic[Positioned,A] with MPositioned {
    def self = this
  }

  object Positioned extends MPositioned



  trait MOptPos extends Monad[OptPos] {
    def unit[A](a : A) = OptPos.reflect[A](Some(Positioned(a)))

    def fmap[A,B](m : OptPos[A], f: A => B) = OptPos.reflect[B](m.reify match {
      case None                      => None
      case Some(Positioned(a,p1,p2)) => Some(Positioned(f(a),p1,p2))
    })

    def join[B](m : OptPos[OptPos[B]]) : OptPos[B] = {
      OptPos.reflect[B](
        m.reify match {
          case None             => None
          case Some (Positioned(a,p1,p2)) => a.reify match {
            case None         => None
            case Some (Positioned(c,_,p3)) => Some (Positioned(c , p1 , p2.max(p3)))
          }
        }
      )
    }
  }

  abstract class OptPos[A] extends Monadic[OptPos,A] with MOptPos {
    def reify : Option[Positioned[A]]
    def self  = this
  }

  object OptPos extends MOptPos {
    def reify = None
    def reflect[A](o : Option[Positioned[A]]) = new OptPos[A] { def reify = o }
  }
}