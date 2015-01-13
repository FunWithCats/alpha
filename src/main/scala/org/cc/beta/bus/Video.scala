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

import java.awt.image.BufferedImage
import main.scala.org.cc._
import beta.Screen
import scala.inline
import java.awt.Dimension

case class Video(dim : Dimension) extends Bus {
  protected var text    : Text    = new Text(dim)
  protected var graphic : Graphic = null

  protected var mode : Int = 0

  @inline final def image    : BufferedImage = if (mode == 0) text.image
                                               else           graphic.image

  @inline final def integers = if (mode == 0) text.integers
                               else           graphic.integers

  @inline final def getMode = mode
  @inline final def setMode(m : Int) = {
    if (m != mode) {
      mode = m
      text = new Text(dim)
      graphic = new Graphic(dim,m)
    }
  }

  @inline final def get(addr : Int) = {
    if (mode == 0) text.get(addr)
    else           graphic.get(addr)
  }

  @inline final def set(addr : Int, value : Int) {
    if (mode == 0) text.set(addr, value)
    else           graphic.set(addr , value)
  }
}
