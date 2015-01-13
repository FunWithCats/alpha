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

package main.scala.org.cc.beta.bus

import main.scala.org.cc.bus.Bus

class Ram4G extends Bus {
  val pages = new Array[Array[Int]](1024)

  @inline final def integers = 1 << 30

  @inline final def get(addr : Int) : Int = {
    val page : Int = addr >>> 20
    if (pages(page) == null) 0
    else pages(page)(addr & 0xFFFFF)
  }

  @inline final def set(addr : Int, value : Int) {
    val page : Int = addr >>> 20
    if (pages(page) == null) pages(page) = new Array[Int](0x100000)
    pages(page)(addr & 0xFFFFF) = value
  }
}