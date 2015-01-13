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

import events.VMUpdate
import scala.swing._
import event.Event
import main.scala.org.cc.beta._

class InfoPanel(vmc : VMControl) extends BoxPanel(Orientation.Vertical) {  ip =>
  private def pc     = vmc.vm.proc.PC
  private def regs   = vmc.vm.proc.regs
  private def memory = vmc.vm.memory

  // A panel for registers
  val regsPanel = new GridBagPanel{
    import swing.GridBagPanel.Fill

    private val cstr = new Constraints

    for (i <- 0 to 31)  {
      val ri = new Label("R" + i.toString + " = ")
      cstr.fill  = Fill.Horizontal
      cstr.weightx = 0.5
      cstr.weighty = 0.5
      cstr.gridx = 0
      cstr.gridy = i
      layout(ri) = cstr
    }

    for (i <- 0 to 31)  {
      val ri = new Label {
        listenTo(vmc)
        reactions += {  case VMUpdate => text = regs(i).formatted("%08X") }
      }
      cstr.fill  = Fill.Horizontal
      cstr.weightx = 0.5
      cstr.weighty = 0.5
      cstr.gridx = 1
      cstr.gridy = i
      layout(ri) = cstr
    }
  }

  // A panel for memory
  object displayMemory extends Publisher { dm =>
    object UpdateDisplayMemory extends Event

    def update = publish(UpdateDisplayMemory)

    private var from = 0

    import main.scala.org.cc.Ord



    val textField = new TextField
    val action    = Action("OK") {
      from = try { Ord.hex2Int(textField.text.toIterator) & 0xFFFFFFFC }
             catch { case _ => 0 }
      update
    }
    val button  = new Button(action)

    val control = new BoxPanel(Orientation.Vertical) {
      contents += new Label("Afficher la mémoire depuis")
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += textField
        contents += button
      }
    }

    val memContent = new GridBagPanel {  gbp =>
      import swing.GridBagPanel.Fill

      val cstr = new Constraints

      val number    = 32
      val locations    = new Array[Label](number)
      val values       = new Array[Label](number)
      val instructions = new Array[Label](number)

      listenTo(dm,vmc)

      def updateLabels = {
        for(i <- 0 to number - 1)  {
          locations(i).text = (from + (4 * i)).formatted("%08X => ")
          val addr = from + (4 * i)
          try { val value = memory(addr)
                values(i).text = value.formatted(" [%08X] ")
                instructions(i).text = Instruction.print(value, addr & 0x7FFFFFFC)
              }
          catch { case e : IndexOutOfBoundsException => { values(i).text = " Bad Address "
                                                          instructions(i).text = " Bad Address"
                                                        }
                }
        }
        gbp.revalidate
      }

      reactions += { case UpdateDisplayMemory => updateLabels
                     case VMUpdate            => updateLabels
                   }


      for (i <- 0 to number - 1)  {
        val mi = new Label
        locations(i) = mi
        cstr.fill  = Fill.Horizontal
        cstr.gridx = 0
        cstr.gridy = i
        layout(mi) = cstr
      }

      for (i <- 0 to number - 1)  {
        val mi = new Label
        values(i) = mi
        cstr.fill  = Fill.Horizontal
        cstr.gridx = 1
        cstr.gridy = i
        layout(mi) = cstr
      }


      for (i <- 0 to number - 1)  {
        val mi = new Label
        instructions(i) = mi
        cstr.fill  = Fill.Horizontal
        cstr.gridx = 2
        cstr.gridy = i
        layout(mi) = cstr
      }

    }

    val panel = new BoxPanel(Orientation.Vertical) {
      contents += new Label("MEMORY")
      contents += control
      contents += memContent
    }
  }


  val pcpanel = new BoxPanel(Orientation.Vertical) {
    private val instruction = new Instruction
    instruction.instruction = memory(pc)

    contents += new Label {
        listenTo(vmc)
        reactions += {  case VMUpdate => text = "PC = " + pc.formatted("%08X") }
    }
    contents += new Label(instruction.print(pc)) {
      listenTo(vmc)
      reactions += {  case VMUpdate => text = try { val addr = pc & 0x7FFFFFFC
                                                    Instruction.print(memory(addr),addr)
                                                  }
                                              catch { case e : IndexOutOfBoundsException => "Bad Address" }
                   }
    }

    contents += new Label {
      listenTo(vmc)
      reactions += {  case VMUpdate => text = "IPS = " + vmc.Stats.ips.toString }
    }

    contents += new Label {
      listenTo(vmc)
      reactions += {  case VMUpdate => text = "Interrupt Pending = " + vmc.vm.interruptionsPending.toString }
    }


    contents += new Label {
      listenTo(vmc)
      reactions += {  case VMUpdate => text = "Etat : " + vmc.getState.toString }
    }
  }


  contents += new FlowPanel { fp =>
    contents += pcpanel
    contents += regsPanel
    contents += displayMemory.panel
  }
}
