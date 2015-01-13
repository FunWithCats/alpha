package main.scala.org.cc.asm

import main.scala.org.cc.asm.Pos.Positioned

object AST {
  abstract class Op
  case object Plus  extends Op
  case object Minus extends Op
  case object Mult  extends Op
  case object Div   extends Op
  case object Mod   extends Op

  case object And   extends Op
  case object Or    extends Op
  case object Xor   extends Op
  case object Neg   extends Op
  case object Not   extends Op

  case object Eq    extends Op
  case object Lt    extends Op
  case object Gt    extends Op

  type AST = List[Positioned[Statement]]

  abstract class Statement
  case class Include(file: String)                                      extends Statement
  case class Text(value : String, istext : Boolean)                     extends Statement
  case class Align(value : Int)                                         extends Statement
  case class Def(name: String, args : Option[List[String]], body : AST) extends Statement

  type PExpr   = Positioned[Expr]
  type POp     = Positioned[Op]
  type PString = Positioned[String]

  abstract class Expr
  case class  Bin(left : PExpr, op : POp, right: PExpr)          extends Expr
  case class  Uni(op: POp, expr : PExpr)                         extends Expr
  case class  EInt(value : Int)                                  extends Expr
  case class  Fun(name  : PString, args : Option[List[PExpr]])   extends Expr
  case object Dot                                                extends Expr

  def bin(left : PExpr , op : POp, right : PExpr) : PExpr = {
    left >> op >> right >> Positioned.unit(Bin(left,op,right))
  }

  def uni(op : POp, expr : PExpr ) : PExpr = {
    op >> expr >> Positioned.unit(Uni(op ,expr))
  }

  def fun(name : PString, args : Option[List[PExpr]]) : PExpr = {
    name >> ( args match {
               case None    => Positioned.unit(Fun(name,None))
               case Some(l) => Positioned.sequence(l) >> Positioned.unit(Fun(name,Some(l)))
             })
  }
}
