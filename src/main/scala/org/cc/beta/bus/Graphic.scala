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

import main.scala.org.cc._
import beta.Screen
import scala.inline
import java.awt.{Dimension, Color}
import java.awt.image.BufferedImage

case class Graphic(dim : Dimension, scale : Int) extends Bus {
  val width  = dim.width  / scale
  val height = dim.height / scale

  @inline final val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  private       val g2D   = image.createGraphics

  val integers = (width * height) / 2

  val graphicBuffer : Array[Int] = new Array[Int](integers)
  for (i <- 0 to (integers - 1)) graphicBuffer(i) = 0

  @inline final def get(addr : Int) : Int = graphicBuffer(addr)

  final def set(addr : Int, value : Int) =  {
    graphicBuffer(addr) = value

    val left_color = value & 0xFFFF
    val leftpixel_rgb : Int = ((left_color & 0xF00) << 12) | // red
                              ((left_color & 0x0F0) <<  8) | // green
                              ((left_color & 0x00F) <<  4)   // blue

    val left_base : Int = addr << 1 // 2 pixel by addr
    val left_x    : Int = left_base % width
    val left_y    : Int = left_base / width

    val right_color= value >>> 16
    val rightpixel_rgb : Int = ((right_color & 0xF00) << 12) | // red
                               ((right_color & 0x0F0) <<  8) | // green
                               ((right_color & 0x00F) <<  4)   // blue

    val right_base : Int = left_base | 1
    val right_x    : Int = right_base % width
    val right_y    : Int = right_base / width

    image.setRGB(left_x ,left_y ,leftpixel_rgb)
    image.setRGB(right_x,right_y,rightpixel_rgb)
  }
}
