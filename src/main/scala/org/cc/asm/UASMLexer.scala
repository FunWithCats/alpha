package main.scala.org.cc.asm

import main.scala.org.cc._
import asm.Pruner.EscapedChar
import asm.AST._
import util.parsing.combinator.PackratParsers
import collection.immutable.PagedSeq

import scala.language.implicitConversions


object Tokens {
  abstract class Token
  case class  TKeyword(value : String)  extends Token
  case class  TInt(value : Int)         extends Token
  case class  TString(value : String)   extends Token
  case class  TOp(op : Op)              extends Token
  case class  TIdent(name: String)      extends Token

  case object TLParen                   extends Token
  case object TRParen                   extends Token
  case object TLBrace                   extends Token
  case object TRBrace                   extends Token
  case object TComma                    extends Token
  case object TDot                      extends Token

  case object TMacro                    extends Token
  case object TAlign                    extends Token
  case class  TText(istext: Boolean)    extends Token
  case class  TInclude(file : String)   extends Token
  case class  TIgnored(text : String)   extends Token

  case object TEof                      extends Token
  case class  TError(msg: String)       extends Token
}

object UASMLexer extends PackratParsers {
  import Pos._
  import Tokens._

  type Elem  = Positioned[EscapedChar]
  type PLexer[T] = PackratParser[Positioned[T]]

  def predChar(p : Char => Boolean)(e : Elem) = p(e.value.char)
  def acceptIfChar(p : Char => Boolean) = acceptIf(predChar(p))(_ => "end of input.")

  def predOrd(p : Int => Boolean)(e : Elem) = p(e.value.char.toInt)
  def acceptIfOrd(p : Int => Boolean) = acceptIf(predOrd(p))(_ => "end of input.")

  def isDoubleQuote(c : Elem) = if (c.value == EscapedChar('\"' , false)) true else false
  def isSingleQuote(c : Elem) = if (c.value == EscapedChar('\'' , false)) true else false

  val doubleQuote    = acceptIf(isDoubleQuote(_))(_ => "end of input.")
  val notDoubleQuote = acceptIf(!isDoubleQuote(_))(_ => "end of input.")

  val singleQuote    = acceptIf(isSingleQuote(_))(_ => "end of input.")
  val notSingleQuote = acceptIf(!isSingleQuote(_))(_ => "end of input.")

  val notNewLine = acceptIfChar(_ != '\n')

  /*
  * IMPLICITS
  */

  implicit def echar2char(e : EscapedChar) : Char = e.char
  implicit def posEChar2Char(p : Positioned[EscapedChar]) : Char = p.value.char

  implicit def listChar2string[T](l : List[T])(implicit conv : T => Char) : String =
    l.foldLeft(new StringBuffer)((s,e) => s.append(conv(e))).toString

  def char(c : Char) = acceptIfChar(_ == c)

  def chars(l : List[Char]): PLexer[String] = {
    def aux(r : List[Char]) : PackratParser[List[Elem]] = r match {
      case Nil      => success(Nil)
      case c :: Nil => char(c) ^^(List(_))
      case c :: q   => (char(c) ~ aux(q)) ^^ { case a ~ b => a :: b }
    }

    aux(l).map(Positioned.sequence(_).map(listChar2string(_)))
  }

  /*
   * Tokens
   */

  def digit    = acceptIfOrd((x : Int) => (x >= Ord.Zero) && (x <= Ord.Nine))
  def hexDigit = acceptIfOrd((x : Int) => ((x >= Ord.Zero) && (x <= Ord.Nine)) ||
    ((x >= Ord.A   ) && (x <= Ord.F   )) ||
    ((x >= Ord.a   ) && (x <= Ord.f   ))
  )
  def letter   = acceptIfOrd((x : Int) =>     ((x >= Ord.A) && (x <= Ord.Z))
                                           || ((x >= Ord.a) && (x <= Ord.z))
                                           ||  (x == Ord.underscore)
                            )

  def anyChar = acceptIf(_ => true)(_ => "end of input.")

  val singleChar: PLexer[Token] =  anyChar >> { (c : Elem) => {
    def aux(t : Token) : PLexer[Token] = success(c.map(_ => t))

    c.value.char match {
      case '(' => aux(TLParen   )
      case ')' => aux(TRParen   )
      case '{' => aux(TLBrace   )
      case '}' => aux(TRBrace   )
      case ',' => aux(TComma    )
      case '.' => aux(TDot      )
      case '+' => aux(TOp(Plus ))
      case '-' => aux(TOp(Minus))
      case '*' => aux(TOp(Mult ))
      case '/' => aux(TOp(Div  ))
      case '%' => aux(TOp(Mod  ))
      case '&' => aux(TOp(And  ))
      case '$' => aux(TOp(Or   ))
      case '^' => aux(TOp(Xor  ))
      case '~' => aux(TOp(Neg  ))
      case '!' => aux(TOp(Not  ))
      case '=' => aux(TOp(Eq   ))
      case '<' => aux(TOp(Lt   ))
      case '>' => aux(TOp(Gt   ))
      case _   => failure("Lexing error at " + c.toString)
    }
  }}


  val space = acceptIfChar(c => c match {
      case ' ' | '\t' | '\r' | '\n' => true
      case _                        => false
    })

  val blank = acceptIfChar(c => c match {
    case ' ' | '\t' | '\r' => true
    case _                 => false
  })

  def kwret(t : Token) = ((x : Positioned[String]) => x.map(_ => t))

  val kwmacro : PLexer[Token] = chars(".macro".toList) ^^ kwret(TMacro)
  val kwalign : PLexer[Token] = chars(".align".toList) ^^ kwret(TAlign)
  val kwtext  : PLexer[Token] = chars(".text".toList)  ^^ kwret(TText(true))
  val kwasci  : PLexer[Token] = chars(".asci".toList)  ^^ kwret(TText(true))

  val kwinclude : PLexer[Token]  = (chars(".include".toList) ~> (blank.+ ~> notNewLine.*)) ^^ { case f =>
    Positioned.sequence(f).map((x : List[EscapedChar]) => TInclude(listChar2string(x)) : Token)
  }


  val kwignored : PLexer[Token]  = (char('.') ~ letter.+ ~ notNewLine.*) ^^ { case e ~ l ~ r =>
    Positioned.sequence(e :: (l ++ r)).map((x : List[EscapedChar]) => TIgnored(listChar2string(x)) : Token)
  }

  val keywords : PLexer[Token] = kwmacro | kwalign | kwtext | kwasci | kwinclude | kwignored

  val ident   : PLexer[Token] = (letter ~ (letter | digit | char('_')).*) ^^ { case e ~ l =>
    Positioned.sequence(e :: l).map((x : List[EscapedChar]) => TIdent(listChar2string(x)) : Token)
  }

  val hex     : PLexer[Token] = char('0') ~ ((char('x') | char('X')) ~> hexDigit.+) ^^ { case e ~ l =>
    Positioned.sequence(e :: l).map((x : List[EscapedChar]) => TInt(Ord.hex2Int(x.map(_.char).toIterator)) : Token)
  }

  def dec     : PLexer[Token] = (char('+') | char('-')).* ~ digit.+ ^^ { case s ~ i =>
    Positioned.sequence(s ++ i).map((x : List[EscapedChar]) => TInt(Ord.dec2Int(x.map(_.char).toIterator)) : Token)
  }

  val string  : PLexer[Token] = (doubleQuote ~ notDoubleQuote.* ~ doubleQuote) ^^ { case first ~ str ~ last =>
    Positioned(TString(listChar2string(str)) : Token ,first.begin,last.end)
  }

  val eof     : PLexer[Token] = new PackratParser[Positioned[Token]] {
    def apply(in : Input) : ParseResult[Positioned[Token]] = {
      if (in.atEnd) Success(Positioned(TEof), in)
      else Failure("No the end of the input." , in)
    }
  }

  val error   : PLexer[Token] = anyChar >> { (x : Elem) =>
    success(x.map(_ => TError("Lexing error at " + x.toString) : Token))
  }

  val token   : PLexer[Token] =  space.* ~> ((keywords | singleChar) | (hex | dec) | (string | ident) | (eof | error))

  val tokens  : PackratParser[List[Positioned[Token]]] = phrase(token.*)


  def tokenize(i : Iterator[Positioned[EscapedChar]]) : Iterator[Positioned[Token]] = new IteratorCache[Positioned[Token]] {
    type T = Positioned[Token]

    private var reader : Input = { val ps      = PagedSeq.fromIterator(i)
                                   val read    = new PagedSeqReaderAny[Elem](ps, null)
                                   new PackratReader[Elem](read)
                                 }

    protected def update: Option[T] = {
      if (reader.atEnd) None
      else {
        token(reader) match { case Success(t : T , r) => { reader = r
                                                           Some(t : Positioned[Token])
                                                         }
                              case _                  => None
                            }
      }
    }
  }

  def tokenList(i : Iterator[Positioned[EscapedChar]]) : List[Positioned[Token]] = {
    tokens(new PagedSeqReaderAny[Elem](PagedSeq.fromIterator(i), null)) match {
      case Success(l , _) => l
      case _              => Nil
    }
  }

}
