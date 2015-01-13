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

import main.scala.org.cc._
import beta.Interruption.InterruptHandler
import main.scala.org.cc.bus._
import concurrent.{Lock}
import scala.collection.mutable._

case class Proc(interruption : () => Option[(InterruptHandler, Boolean)] , memory : RamVideo, io:IO) { proc =>
  // The program counter
  var PC : Int = 0
  @inline final def supervisorMode = (PC & 0x80000000) != 0
  @inline final def userMode       = (PC & 0x80000000) == 0

  // We have 32 registers of 4 bytes each, signed
  val regs = new Registers

  // Number of cycles executed
  private var _cycles : Int = 0
  def cycles = _cycles

  // Handling of interupts
  var interrupted   : Boolean                 = false

  // Instruction cache
  private val instruction         : Instruction = new Instruction
  instruction.proc_=(this)
  instruction.mem  = memory
  instruction.io   = io

  @inline final def currentInstruction : Instruction = {
    val i = new Instruction
    i.instruction = memory(PC & 0x7FFFFFFC)
    i
  }

  @inline final def step {
    // If an interruption occurs
    // acquire the lock is costy
    // So we start by checking interruptions.isEmpty
    // it's not accurate but avoid to much synchronizations
    if (interrupted && userMode) {
      interruption() match {
        case None           =>   interrupted = false
        case Some ((ih, b)) => { interrupted = b
          regs(Proc.XP) = PC + 4
          PC = ih.address
          ih.exec
        }
      }
    }

    instruction.instruction = memory(PC & 0x7FFFFFFC)

    // Update the PC
    _cycles += 1
    PC      += 4
    // Execution of the instruction
    instruction.exec
  }

  @inline final def run(continueRunning : SynchronizedVar[Boolean]) {
    continueRunning.set(true)
    while (continueRunning.value) {
      // If an interruption occurs
      // acquire the lock is costy
      // So we start by checking interruptions.isEmpty
      // it's not accurate but avoid to much synchronizations
      if (interrupted && userMode) {
        interruption() match {
          case None           =>   interrupted = false
          case Some ((ih, b)) => { interrupted = b
                                   regs(Proc.XP) = PC + 4
                                   PC = ih.address
                                   ih.exec
                                 }
        }
      }

      instruction.instruction = memory(PC & 0x7FFFFFFC)
      // Update the PC
      _cycles += 1
      PC      += 4
      // Execution of the instruction
      instruction.exec
    }
  }

}


object Proc {
  @inline final val XP = 30 // eXception Pointer
  @inline final val SP = 29 // Stack Pointer, next available stack adress
  @inline final val LP = 28 // Linkage Pointer (return address)
  @inline final val BP = 27 // Base Pointer, base of stack frame
}