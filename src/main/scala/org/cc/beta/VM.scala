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


import java.io.{FileInputStream, File}
import main.scala.org.cc.bus._
import main.scala.org.cc._
import util.Random
import java.awt.Dimension
import collection.mutable.Queue
import concurrent.Lock
import collection.mutable
import Interruption._

class VM { vm =>
  // Program to run
  private var path    : String      = null
  private var program : Array[Byte] = null

  private val keyboard = new SynchronizedVar[Int](0)
  private val mouse    = new SynchronizedVar[Int](0)

  object io extends IO {
    @inline final def rdchar         : Int  = vm.keyboard.get
    @inline final def click          : Int  = vm.mouse.get
    @inline final def time           : Int  = (System.currentTimeMillis & 0xFFFFFFFF).toInt
    @inline final def random         : Int  = Random.nextInt
    @inline final def wrchar(r0:Int) : Unit = System.out.println("WRCHAR invalid, use framebuffer instead")
    @inline final def seed(r0:Int)   : Unit = Random.setSeed(r0.toLong)
    @inline final def server(r0:Int) : Unit = System.out.println("Server => " + r0).formatted("%08X")
    @inline final def halt           : Unit = vm.halt
    @inline final def ram            : Int  = vm.memory.size
    @inline final def videoAddress   : Int  = videoBaseAddr
    @inline final def videoMemory    : Int  = memory.video.size
  }

  // Video card
  val videoBaseAddr = 0x80000000

  // Core components
  val dim           = new Dimension(800, 600)
  val interruptLock = new Lock

  var interruptQueue : Queue[InterruptHandler]  = null
  var memory         : RamVideo                 = null
  var proc           : Proc                     = null
  var screen         : Screen                   = null

  def nextInterrupt : Option[(InterruptHandler, Boolean)] = {
    interruptLock.acquire
    if (interruptQueue.nonEmpty) {
      val nextIH = interruptQueue.dequeue
      val b      = interruptQueue.nonEmpty
      interruptLock.release
      Some((nextIH,b))
    }
    else { interruptLock.release
           None
         }
  }

  def init = {
    interruptQueue = new mutable.Queue[InterruptHandler]
    interruptLock.available = true

    memory         = new RamVideo(videoBaseAddr, dim)
    proc           = new Proc((() => nextInterrupt),memory,io)
    screen         = new Screen {
      @inline final def image  = memory.video.image
      @inline final val size   = dim
    }
  }

  init

  def halt = throw Halt

  @inline final def interrupt(handler:InterruptHandler) = {
    interruptLock.acquire
    interruptQueue += handler
    interruptLock.release
    proc.interrupted = true
  }

  @inline final def keyboardInterrupt(value : Int) {
    interrupt(new IHKeyboard {
      def exec { vm.keyboard.set(value) }
    })
  }

  @inline final def mouseInterrupt(value : Int) {
    interrupt(new IHMouse {
      def exec { vm.mouse.set(value) }
    })
  }

  @inline final def cycles                            = proc.cycles
  @inline final def step                              = proc.step
  @inline final def interruptionsPending              = interruptQueue.length
  @inline final def run(v : SynchronizedVar[Boolean]) = proc.run(v)

  def loadProgram(file : File) = {
    if (file != null) {
      //System.out.println("CHargement du programme : " + _path.toString)
      path    = file.getPath
      val is  = new FileInputStream(file)
      program = new Array[Byte](file.length.toInt)
      is.read(program)
      is.close
      reset
    }
  }

  def reset {
    init
    if (program != null) {
      //System.out.println("Reset")
      memory.loadProgram(program)
    }

  }

  def reload = loadProgram(new File(path))
}