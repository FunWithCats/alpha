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

abstract class Bus {
  @inline def get(addr : Int) : Int
  @inline def set(addr : Int, value : Int)
  @inline def integers : Int

  @inline final def apply(addr : Int) : Int = get (addr >>> 2)
  @inline final def update(addr :Int , value : Int) = set (addr >>> 2, value)

  @inline final def size = integers << 2

  @inline final def loadProgram(arr : Array[Byte]) = {
    val insts = arr.length / 4
    for (i <- 0 to insts - 1) {
      val base = 4 * i
      this(base) =  ((arr(base    ) : Int) & 0xFF)        |
                   (((arr(base + 1) : Int) & 0xFF) <<  8) |
                   (((arr(base + 2) : Int) & 0xFF) << 16) |
                   (((arr(base + 3) : Int) & 0xFF) << 24)
    }
  }
}


object Bus {
  @inline final def littleEndianGet(tab : Array[Byte] , addr : Int) : Int = {
       ((tab(addr  ) : Int) & 0xFF)         |
      (((tab(addr+1) : Int) & 0xFF) <<  8)  |
      (((tab(addr+2) : Int) & 0xFF) << 16)  |
      (((tab(addr+3) : Int) & 0xFF) << 24)
  }

  @inline final def littleEndianSet(tab : Array[Byte] ,addr : Int, value : Int) = {
    tab(addr  ) = ( value         & 0xFF).byteValue
    tab(addr+1) = ((value >>>  8) & 0xFF).byteValue
    tab(addr+2) = ((value >>> 16) & 0xFF).byteValue
    tab(addr+3) = ((value >>> 24) & 0xFF).byteValue
  }

  @inline final def ofArray(arr : Array[Int]) : Bus = new Bus {
    @inline final def integers = arr.length
    @inline final def get(addr : Int) : Int = arr(addr)
    @inline final def set(addr: Int, value : Int) = arr(addr) = value
  }
}
