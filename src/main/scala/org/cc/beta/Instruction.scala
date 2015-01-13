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

import main.scala.org.cc.bus.Bus
import util.Random
import scala.inline
import main.scala.org.cc.bus.RamVideo

class Instruction {
  @inline final var _proc       : Proc      = null
  @inline final var regs        : Registers = null
  @inline final var mem         : RamVideo  = null
  @inline final var io          : IO        = null
  @inline final var instruction : Int       = 0

  @inline final def proc = _proc
  @inline final def proc_=(p : Proc) {
    _proc = p
    if (p != null) regs = p.regs
  }

  @inline final def opcode  : Int =  instruction >>> 26
  @inline final def Rc      : Int = (instruction >>> 21) & 0x1F
  @inline final def Ra      : Int = (instruction >>> 16) & 0x1F
  @inline final def Rb      : Int = (instruction >>> 11) & 0x1F
  @inline final def literal : Int = (instruction.shortValue : Int)

  @inline private def invalid {
    if (_proc.userMode) { _proc.regs(Proc.XP) = _proc.PC
      _proc.PC = Interruption.IHIllegalInstruction.address
    }
    else { System.out.println("Illegal instruction while in supervisor mode at " + _proc.PC.formatted("%08X"))
      throw Halt
    }
  }

  @inline final def exec  : Unit = opcode match {
    // ADD, SUB, MULT, DIV
    case 0x20 => regs(Rc) = regs(Ra) + regs(Rb)
    case 0x21 => regs(Rc) = regs(Ra) - regs(Rb)
    case 0x22 => regs(Rc) = regs(Ra) * regs(Rb)
    case 0x23 => regs(Rc) = regs(Ra) / regs(Rb)

    //CMPEQ, CMPLT, CMPLE
    case 0x24 => regs(Rc) = if (regs(Ra) ==  regs(Rb)) 1 else 0
    case 0x25 => regs(Rc) = if (regs(Ra) <   regs(Rb)) 1 else 0
    case 0x26 => regs(Rc) = if (regs(Ra) <=  regs(Rb)) 1 else 0

    // AND, OR, XOR
    case 0x28 => regs(Rc) = regs(Ra) & regs(Rb)
    case 0x29 => regs(Rc) = regs(Ra) | regs(Rb)
    case 0x2A => regs(Rc) = regs(Ra) ^ regs(Rb)

    //SHL, SHR, SRA
    case 0x2C => regs(Rc) = regs(Ra) <<  (regs(Rb) & 0x1F)
    case 0x2D => regs(Rc) = regs(Ra) >>> (regs(Rb) & 0x1F)
    case 0x2E => regs(Rc) = regs(Ra) >>  (regs(Rb) & 0x1F)


    // ADD, SUBC, MULTC, DIVC
    case 0x30 => regs(Rc) = regs(Ra) + literal
    case 0x31 => regs(Rc) = regs(Ra) - literal
    case 0x32 => regs(Rc) = regs(Ra) * literal
    case 0x33 => regs(Rc) = regs(Ra) / literal

    //CMPEQ, CMPLT, CMPLE
    case 0x34 => regs(Rc) = if (regs(Ra) ==  literal) 1 else 0
    case 0x35 => regs(Rc) = if (regs(Ra) <   literal) 1 else 0
    case 0x36 => regs(Rc) = if (regs(Ra) <=  literal) 1 else 0

    // AND, OR, XOR
    case 0x38 => regs(Rc) = regs(Ra) & literal
    case 0x39 => regs(Rc) = regs(Ra) | literal
    case 0x3A => regs(Rc) = regs(Ra) ^ literal

    //SHL, SHR, SRA
    case 0x3C => regs(Rc) = regs(Ra) <<  (literal & 0x1F)
    case 0x3D => regs(Rc) = regs(Ra) >>> (literal & 0x1F)
    case 0x3E => regs(Rc) = regs(Ra) >>  (literal & 0x1F)

    // LD, ST, LDR
    case 0x18 => regs(Rc) = mem(regs(Ra) + literal)
    case 0x19 => mem(regs(Ra) + literal) = regs(Rc)
    case 0x1F => regs(Rc) = mem((_proc.PC & 0x7FFFFFFC)  + 4 * literal)


    // JMP, BEQ, BNE
    case 0x1B => { // If (Ra >>> 31 == 1) the jump wants to go in supervisor mode
                   // If we already are in supervisor mode, then Ok, we stay in this mode
                   // If we are not, we stay in user mode!
                   // In supervisor mode, PC | 0x7FFFFFFC = 0xFFFFFFFC
                   // In user       mode, PC | 0x7FFFFFFC = 0x7FFFFFFC
                   val EA   = regs(Ra) & (proc.PC | 0x7FFFFFFC)
                   regs(Rc) = _proc.PC
                   _proc.PC  = EA
                 }
    case 0x1C => { val TEMP = regs(Ra)
                   regs(Rc) = _proc.PC
                   if (TEMP == 0) _proc.PC += 4 * literal
                 }
    case 0x1D => { val TEMP = regs(Ra)
                   regs(Rc) = _proc.PC
                   if (TEMP != 0) _proc.PC += 4 * literal
                 }

    // Invalid Instructions and SVC
    case 0x00 => { if(Ra == 0 && Rb == 0 && _proc.supervisorMode)
                      literal match { case 0 => { _proc.PC -= 4
                                                  io.halt
                                                }                                                  // HALT()
                                      case 1  => _proc.regs(0) = io.rdchar                             // RDCHAR()
                                      case 2  => io.wrchar(_proc.regs(0))                              // WRCHAR()
                                      case 3  => _proc.regs(0) = _proc.cycles                           // CYCLES()
                                      case 4  => _proc.regs(0) = io.time                               // TIME()
                                      case 5  => _proc.regs(0) = io.click                              // CLICK
                                      case 6  => _proc.regs(0) = Random.nextInt                        // RANDON()
                                      case 7  => io.seed(_proc.regs(0))                                // SEED()
                                      case 8  => io.server(_proc.regs(0))                              // SERVER()
                                      case 9  => _proc.regs(0) = io.ram                                // RAM()
                                      case 10 => _proc.regs(0) = io.videoAddress                       // VIDEOADDRESS()
                                      case 11 => _proc.regs(0) = io.videoMemory                        // VIDEOMEM()
                                      case _  => invalid
                                    }
                   else invalid
                 }
    case _    => invalid
  }

  @inline final def print(pc : Int)  : String = opcode match {
    // ADD, SUB, MULT, DIV
    case 0x20 => "ADD(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x21 => "SUB(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x22 => "MUL(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x23 => "DIV(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"

    //CMPEQ, CMPLT, CMPLE
    case 0x24 => "CMPEQ(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x25 => "CMPLT(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x26 => "CMPLE(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"

    // AND, OR, XOR
    case 0x28 => "AND(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x29 => "OR(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x2A => "XOR(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"

    //SHL, SHR, SRA
    case 0x2C => "SHL(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x2D => "SHR(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"
    case 0x2E => "SRA(R" + Ra.toString + ", R" + Rb.toString + ", R" + Rc.toString + ")"


    // ADD, SUB, MULT, DIV
    case 0x30 => "ADDC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"
    case 0x31 => "SUBC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"
    case 0x32 => "MULC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"
    case 0x33 => "DIVC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"

    //CMPEQ, CMPLT, CMPLE
    case 0x34 => "CMPEQC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"
    case 0x35 => "CMPLTC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"
    case 0x36 => "CMPLEC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"

    // AND, OR, XOR
    case 0x38 => "ANDC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"
    case 0x39 => "ORC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"
    case 0x3A => "XORC(R" + Ra.toString + ", " + literal.formatted("%08X") + ", R" + Rc.toString + ")"

    //SHL, SHR, SRA
    case 0x3C => "SHL(R" + Ra.toString + ", " + (literal & 0x1F).toString + ", R" + Rc.toString + ")"
    case 0x3D => "SHR(R" + Ra.toString + ", " + (literal & 0x1F).toString + ", R" + Rc.toString + ")"
    case 0x3E => "SRA(R" + Ra.toString + ", " + (literal & 0x1F).toString + ", R" + Rc.toString + ")"

    // LD, ST, LDR
    case 0x18 => "LD(R" + Ra.toString + ", " + literal.formatted("%X") + ", R" + Rc.toString + ")"
    case 0x19 => "ST(R" + Rc.toString + ", " + literal.formatted("%X") + ", R" + Ra.toString + ")"
    case 0x1F => { val label = 4 * (literal + 1) + pc
                   "LDR(" + label.formatted("08X") + ", R" + Rc.toString + ")"
                 }


    // JMP, BEQ, BNE
    case 0x1B => "JMP(R" + Ra.toString + ", R" + Rc.toString + ")"
    case 0x1C => { val label = 4 * (literal + 1) + pc
                   "BEQ(R" + Ra.toString + ", " + label.formatted("%08X") + ", R" + Rc.toString + ")"
                 }
    case 0x1D => { val label = 4 * (literal + 1) + pc
                   "BNE(R" + Ra.toString + ", " + label.formatted("%08X") + ", R" + Rc.toString + ")"
                 }

    // Invalid Instructions and SVC
    case 0x00 => { if(Ra == 0 && Rb == 0)  literal match {
                    case 0  => "HALT()"
                    case 1  => "RDCHAR()"
                    case 2  => "WRCHAR()"
                    case 3  => "CYCLE()"
                    case 4  => "TIME()"
                    case 5  => "CLICK()"
                    case 6  => "RANDOM()"
                    case 7  => "SEED()"
                    case 8  => "SERVER()"
                    case 9  => "RAM()"
                    case 10 => "VIDEOADDRESS()"
                    case 11 => "VIDEOMEMORY()"
                    case _ => instruction.formatted("%08X")
                   }
                   else instruction.formatted("%08X")
                 }
    case _    => instruction.formatted("%08X")
  }
}

object Instruction {
  private val inst = new Instruction

  final def print(x : Int, PC:Int) : String = inst.synchronized({ inst.instruction = x
                                                                  inst.print(PC)
                                                                })
}