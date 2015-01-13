package main.scala.org.cc

object Ord {
  val a    : Int = 'a'.toInt
  val f    : Int = 'f'.toInt
  val z    : Int = 'z'.toInt
  val A    : Int = 'A'.toInt
  val F    : Int = 'F'.toInt
  val Z    : Int = 'Z'.toInt
  val Zero : Int = '0'.toInt
  val Nine : Int = '9'.toInt

  val plus  : Int = '+'.toInt
  val minus : Int = '-'.toInt

  val underscore : Int = '_'.toInt

  final def hex2Int(s : Iterator[Char]) : Int = {
    var r : Int = 0
    while (s.hasNext) {
      val c = s.next.toInt
      if (c >= Ord.A    && c<= Ord.F   ) r = (r << 4) | (c - Ord.A + 10)
      else if (c >= Ord.a    && c<= Ord.f   ) r = (r << 4) | (c - Ord.a + 10)
      else if (c >= Ord.Zero && c<= Ord.Nine) r = (r << 4) | (c - Ord.Zero  )
    }
    r
  }


  // s must be of the form : [+-]*[0-9]* */
  final def dec2Int(s : Iterator[Char]) : Int = {
    var r    : Int = 0
    var sign : Int = 1

    /* Sign */

    while (s.hasNext) {
      val c = s.next.toInt
      if (c >= Ord.Zero && c<= Ord.Nine) r = (10 * r) + (c - Ord.Zero)
      else if (c == minus) sign *= -1
    }

    sign * r
  }
}
