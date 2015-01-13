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

package main.scala.org.cc.beta

class Registers {
  @inline final val regs : Array[Int] = new Array[Int](31)

  @inline final def apply(reg:Int) : Int =
    if (reg == 31) 0
    else regs(reg)

  @inline final def update(reg : Int , value : Int) = if (reg != 31) regs(reg) = value
}
