package main.scala.org.cc.asm

import main.scala.org.cc.asm.Pos._
import main.scala.org.cc.asm.Pruner._
import scala.util.parsing.combinator.PackratParsers
import main.scala.org.cc.asm.Tokens._
import main.scala.org.cc.asm.AST._
import collection.immutable.PagedSeq


object UASMParser extends PackratParsers {

  type Elem  = Positioned[Token]
  type PParser[T] = PackratParser[Positioned[T]]

  def tokenIf(p : Token => Boolean) = acceptIf((e : Elem) => p(e.value))(_ => "End of input")
  def token(t : Token) = tokenIf(_ == t)

  val anyToken = acceptIf(_ => true)(_ => "End of input")

  def lparen = token(TLParen)
  def rparen = token(TRParen)
  def comma  = token(TComma)

  def op  : PParser[Op] = anyToken >> { (e : Elem) => e.value match {
    case TOp(op) => success(e.map(_ => op))
    case _       => failure("operator expexted at " + e.toString)
  }}

  def ident : PParser[String] = anyToken >> { (e : Elem) => e.value match {
    case TIdent(n) => success(e.map(_ => n))
    case _         => failure("integer expexted at " + e.toString)
  }}

  def text : PParser[Boolean] = anyToken >> { (e : Elem) => e.value match {
    case TIdent(".text") => success(e.map(_ => true ))
    case TIdent(".asci") => success(e.map(_ => false))
    case _               => failure("integer expexted at " + e.toString)
  }}

  def kwmacro : PParser[Null] = anyToken >> { (e : Elem) => e.value match {
    case TIdent(".macro") => success(e.map(_ => null))
    case _               => failure("integer expexted at " + e.toString)
  }}

  def align : PParser[Null] = anyToken >> { (e : Elem) => e.value match {
    case TIdent(".align") => success(e.map(_ => null))
    case _                => failure("integer expexted at " + e.toString)
  }}

  lazy val expr : PParser[Expr] = {
    val pdot : PParser[Expr] = token(TDot) ^^ ((e : Elem) => e.map(_ => Dot : Expr))

    val pfun : PParser[Expr] = (ident ~ (lparen ~> (expr ~ (comma ~> expr).*).? <~ rparen).?) ^^ {
      case e ~ None              => fun(e , None)
      case e ~ Some(None)        => fun(e , Some(Nil))
      case e ~ Some(Some(x ~ l)) => fun(e , Some(x :: l))
    }

    val paren : PParser[Expr] = lparen ~> expr <~ rparen

    def int : PParser[Expr] = anyToken >> { (e : Elem) => e.value match {
      case TInt(i) => success(e.map(_ => EInt(i) : Expr))
      case _       => failure("integer expexted at " + e.toString)
    }}

    val term : PParser[Expr] = pdot | pfun | int | paren

    val puni : PParser[Expr] = (op ~ term)        ^^ { case o ~ e => uni(o, e) }
    val pbin : PParser[Expr] = (term ~ op ~ term) ^^ { case l ~ o ~ r => bin(l,o,r) }

    pbin | puni | term
  }

  /*lazy val statement : PParser[Statement] = {


    case class Text(value : String, istext : Boolean)                     extends Statement
    case class Align(value : Int)                                         extends Statement
    case class Def(name: String, args : Option[List[String]], body : AST) extends Statement
  }       */

  def parse(i : Iterator[Positioned[Token]]) : Option[PExpr] = {
    type T = Positioned[Token]

    var reader : Input = { val ps      = PagedSeq.fromIterator(i)
                           val read    = new PagedSeqReaderAny[Elem](ps, null)
                           new PackratReader[Elem](read)
                         }

    expr(reader) match {
      case Success(e , _) => Some(e)
      case _              => None
    }
  }
}
