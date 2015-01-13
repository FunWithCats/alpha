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

abstract class IO {
  def rdchar         : Int
  def click          : Int
  def time           : Int
  def random         : Int
  def ram            : Int
  def videoAddress   : Int
  def videoMemory    : Int
  def wrchar(r0:Int) : Unit
  def seed(r0:Int)   : Unit
  def server(r0:Int) : Unit
  def halt           : Unit
}
