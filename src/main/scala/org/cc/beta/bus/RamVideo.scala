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
import beta.bus.Ram4G
import beta.{Screen}
import java.awt.Dimension

case class RamVideo(videoBaseAddress : Int, dim : Dimension) extends Bus {
  val ram   = new Ram4G
  val video = new Video(dim)

  val videoBaseInt = videoBaseAddress >>> 2
  val modeInt      = videoBaseInt - 1

  @inline final def integers     = 1 << 30
  @inline final def videoNextInt = videoBaseInt + video.integers

  @inline final def get(addr : Int) : Int = {
         if (addr < modeInt     ) ram.get(addr)
    else if (addr < videoBaseInt) video.getMode
    else if (addr < videoNextInt) video.get(addr - videoBaseInt)
    else ram.get(addr)
  }

  @inline final def set(addr : Int, value : Int) = {
         if (addr < modeInt     ) ram.set(addr , value)
    else if (addr < videoBaseInt) video.setMode(value)
    else if (addr < videoNextInt) video.set(addr - videoBaseInt , value)
    else ram.set(addr,value)
  }
}
