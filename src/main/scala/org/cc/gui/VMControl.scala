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

package main.scala.org.cc.gui

import events._
import scala.swing._
import main.scala.org.cc.beta.{Interruption, Proc, Halt, VM}
import main.scala.org.cc.{SynchronizedVar}
import java.io.File

class VMControl(_vm : VM) extends SwingWorker with Publisher { vmc =>
  @inline final def vm = _vm

  private val _screen = new ScreenUI(this)
  def screen = _screen

  @inline final def update = {
    _screen.updateScreen
    publish(VMUpdate)
  }

  // repeat vm.step for 1/hertz seconds
  private val hertz       : Int    = 30
  private val miliPerTick : Long   = 1000L / hertz

  // Synchronised variables
  import VMControl._

  private var _shouldExit      = new SynchronizedVar[Boolean](false)

  private val _continueRunning = new SynchronizedVar[Boolean](false) {
     private var timer : java.util.Timer     = null
     private var task  : java.util.TimerTask = null

     override def set(b : Boolean) : Unit  = this.synchronized[Unit]{
       // Undefine the task
       if (task  != null) {
         task.cancel
         task = null
       }

       // Undefine the timer
       if (timer != null) {
         timer.cancel
         timer = null
       }

       if (b) {
         timer = new java.util.Timer()
         task  = new java.util.TimerTask {
           def run = vm.interrupt(new Interruption.IHClock {
             @inline final def exec = _screen.updateScreen
           })}
         timer.scheduleAtFixedRate(task,miliPerTick,miliPerTick)
       }
       value = b
     }
  }

  @inline final def isRunning  = _continueRunning.get
  @inline final def unsafeIsRunning = _continueRunning.get


  object Stats {
    private var oldTime   = System.nanoTime
    private var oldCycles = vm.cycles

    var ips = 0L

    def begin {
      oldTime   = System.nanoTime
      oldCycles = vm.cycles
    }

    def end {
      val newTime   = System.nanoTime
      val newCycles = vm.cycles
      ips = (1000000000L * (newCycles - oldCycles))  / (newTime - oldTime)
    }

  }

  def act {
     loopWhile(!_shouldExit.get) {
       update
       react {
         case Stop           => ()
         case Step           => try { vm.step }
                                catch { case Halt => ()
                                        case e    => System.out.println("Error: " + e.toString)
                                      }
         case LoadProgram(f) => vm.loadProgram(f)
         case Reset          => vm.reset
         case Reload         => vm.reload
         case Run            => { Stats.begin
                                  try vm.run(_continueRunning)
                                  catch { case Halt => ()
                                          case e    => System.out.println("Error: " + e.toString)
                                        }
                                  Stats.end
                                }
         }
       }
  }



  def sendCommand(cmd : Command) {
    _shouldExit.set(false)
    _continueRunning.set(false)
    import scala.actors.Actor._
    this.getState match {
      case State.Terminated => this.restart
      case State.New        => this.start
      case _                => ()
    }
    this ! cmd
  }


  def loadProgram(file : File)   = sendCommand(LoadProgram(file))
  def reset                      = sendCommand(Reset)
  def reload                     = sendCommand(Reload)
  def run                        = sendCommand(Run)
  def step                       = sendCommand(Step)
  def stop                       = sendCommand(Stop)

  def terminate                  = _shouldExit.set(true)
}


object VMControl {
  abstract class Command
  case object Step                       extends Command
  case class  LoadProgram(file : File)   extends Command
  case object Run                        extends Command
  case class  SpeedFactor(n : Int)       extends Command
  case object Reset                      extends Command
  case object Reload                     extends Command
  case object Stop                       extends Command
}