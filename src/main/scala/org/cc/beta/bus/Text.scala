/*
 * This file is part of Alpha.
 *
 * Alpha is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.scala.org.cc.bus

import java.awt.{Dimension, Font, Color}
import main.scala.org.cc._
import beta.Screen
import scala.inline
import java.awt.image.BufferedImage

case class Text(dim : Dimension) extends Bus {
  private val charWidth  = 10
  private val charHeight = 20

  @inline final val image = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB)
  private       val g2D   = image.createGraphics

  val width    = dim.width  / charWidth
  val height   = dim.height / charHeight
  val integers = width * height

  private val textBuffer : Array[Int] = { val arr = new Array[Int](integers)
                                           for (i <- 0 to (integers - 1)) arr(i) = 0
                                           arr
                                         }
  private val font    = { var size = 16   // On Windows, it selects very small size so we start at at least size 16.
                          do { size = size + 1
                               g2D.setFont(new Font(Font.MONOSPACED,Font.PLAIN, size))
                             }
                          while(g2D.getFontMetrics.getMaxAdvance <= charWidth)
                          System.out.println("Using font size " + (size - 1))
                          new Font(Font.MONOSPACED,Font.PLAIN, size - 1)
                        }
  g2D.setFont(font)

  private val metrics     = g2D.getFontMetrics
  private val fontHeight  = metrics.getHeight
  private val transY      = (charHeight.toDouble * (metrics.getAscent.toDouble / fontHeight.toDouble)).toInt - 1
  private val chars       = new Array[Char](1)

  @inline final def get(addr : Int) : Int = textBuffer(addr)

  final def set(addr : Int, value : Int) =  {
    textBuffer(addr) = value

    val char       = (value & 0xFF).toChar

    val background =  value >>> 20
    val foreground = (value >>> 8) & 0xFFF


    val background_rgb  = new Color( ((background & 0xF00) << 12) | // red
                                     ((background & 0x0F0) <<  8) | // green
                                     ((background & 0x00F) <<  4)   // blue

                                   )
    val foreground_rgb  = new Color( ((foreground & 0xF00) << 12) | // red
                                     ((foreground & 0x0F0) <<  8) | // green
                                     ((foreground & 0x00F) <<  4)   // blue
                                   )

    chars(0) = char

    var x : Int = ((addr % width) * charWidth )
    var y : Int = ((addr / width) * charHeight)

    g2D.setColor(background_rgb)
    g2D.fillRect(x,y,charWidth,charHeight)

    g2D.setColor(foreground_rgb)
    g2D.drawChars(chars,0,1,x , y + transY)
  }
}
