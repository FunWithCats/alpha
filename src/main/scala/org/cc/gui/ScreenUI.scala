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

import events.ScreenRepaint
import main.scala.org.cc.beta.{VM, Screen}
import scala.swing._
import event._
import event.MouseClicked
import event.MouseMoved
import java.awt.Graphics2D
import java.awt.image.{BufferStrategy, BufferedImage}

class ScreenUI(vmc : VMControl) extends Canvas {
  preferredSize = vmc.vm.screen.size
  ignoreRepaint = true
  focusable     = true

  @inline final def updateScreen = repaint
  override final def paint(g : Graphics2D) = g.drawImage(vmc.vm.screen.image,0,0,size.width, size.height, null)

  private def point2Int(p : Point) : Int = {
    import Math._
    // Do  not use a screen bigger than 4096x4096 !!!!!
    (min(max(p.getX.toInt,0), vmc.vm.screen.width )  << 12) | min(max(p.getY.toInt,0), vmc.vm.screen.height)
  }

  listenTo(vmc,keys,mouse.clicks,mouse.moves)
  reactions += {
    //case ScreenRepaint     => updateScreen
    //    case e : KeyPressed    => System.out.println("Key " + e.key.id.toString + " pressed")
    //    case e : KeyReleased   => System.out.println("Key " + e.key.id.toString + " released")
    case e : KeyTyped      =>  if (vmc.isRunning) vmc.vm.keyboardInterrupt(e.char.toInt)
    case e : MouseMoved    => {
      if (vmc.unsafeIsRunning) {
        this.requestFocus
        vmc.vm.mouseInterrupt(point2Int(e.point))
      }
    }
    case e : MouseClicked  => {
      if (vmc.unsafeIsRunning) {
        this.requestFocus
        vmc.vm.mouseInterrupt(point2Int(e.point) | 0x1000000)
      }
    }
  }
}