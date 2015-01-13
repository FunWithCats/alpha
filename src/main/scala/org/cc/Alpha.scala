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


package org.cc

import swing._
import main.scala.org.cc._
import asm.AST.Expr
import asm.Pos.Positioned
import asm.Pruner.EscapedChar
import main.scala.org.cc.beta._
import gui._
import java.io.{FileWriter, File}
import javax.swing.{ImageIcon, UIManager}
import javax.imageio.ImageIO
import main.scala.org.cc.asm.Tokens.Token
import asm.{UASMParser, Pruner, UASMLexer}

object Alpha extends SimpleSwingApplication { mainapp =>
  private val version = "0.4.3"

  private val cliWelcome = "Alpha v" + version + "\n" +
    "Copyright Christophe Calvès 2013\n" +
    "Licenced under the terms of the GPLv3+\n" +
    "See http://christophe.calves.me/wiki/index.php?title=Alpha for more details"

  private val aboutMessage = "<html>" +
    "<h1>Alpha</h1>" +
    "<ul>" +
    " <li><bf>Version:</bf>  <it>" + version + "</it></li>" +
    " <li><bf>Copyright:</bf>  <it>Christophe Calvès 2013</it></li>" +
    " <li><bf>Licence:<bf>  <it>GPLv3+</it></li>" +
    "</ul>" +
    "<p>See <a href=\"http://christophe.calves.me/wiki/index.php?title=Alpha\">" +
    "http://christophe.calves.me/wiki/index.php?title=Alpha</a> " +
    "for more details.</p>"




  // The virtual beta machine and its control object
  private lazy val vm       = new VM
  private lazy val vmc      = new VMControl(vm)


  // Configuration options, set by command line parameters
  private object config {
    var fullUI      : Boolean      = true
    var program     : Option[File] = None
    var runFast     : Boolean      = false
    var fullScreen  : Boolean      = false
  }


  // A file chooser
  private val fileChooser = new FileChooser(new File(System.getProperty("user.dir"))) {
    multiSelectionEnabled = false
    fileFilter            = new javax.swing.filechooser.FileNameExtensionFilter("beta programs" , "bin")
    title                 = "Choisissez un programe (fichier .bin)"
  }


  def fullUI = new MainFrame { mainFrame =>
    try UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
    catch { case e : Exception => {} }

    try { iconImage = ImageIO.read(mainapp.getClass.getResource("/resources/Alpha.png"))
        }
    catch { case e : Exception => {} }

    private val aboutDialog = new Frame {
      contents = new BoxPanel(Orientation.Vertical) {
        contents += new Label("<html>" + aboutMessage.replace("\n","<br/>"))
      }
      visible=false
    }

    resizable = false
    if (!config.program.isEmpty) vmc.loadProgram(config.program.get)


    // An action to choose a file and load the program
    private val loadFile = Action("Ouvrir") {
      fileChooser.showOpenDialog(mainFrame.contents(0))
      if (fileChooser.selectedFile != null) {
        vmc.loadProgram(fileChooser.selectedFile)
        title = fileChooser.selectedFile.getPath
      }
    }

    private val prune = Action("Elaguer") {
      fileChooser.showOpenDialog(mainFrame.contents(0))
      if (fileChooser.selectedFile != null) {
          val f  : File                              = fileChooser.selectedFile
          System.out.println("File = " + f.getPath)
          val i  : Iterator[Char]                    = scala.io.Source.fromFile(f).toIterator
          val p  : Iterator[Positioned[EscapedChar]] = Pruner.prune(i)
          val t  : Iterator[Positioned[Token]]       = UASMLexer.tokenize(p)
          val e  : Option[Positioned[Expr]]          = UASMParser.parse(t)
          System.out.println("Expr = " + e.toString)
          System.out.println("END\n\n")
      }
    }


    private val exitApp = Action("Quitter")        { vmc.terminate
      mainFrame.close
      System.exit(0)
    }
    private val step    = Action("Avancer")        { vmc.step                               }
    private val run     = Action("Executer")       { vmc.run                                }
    private val stop    = Action("Stoper")         { vmc.stop                               }
    private val reset   = Action("Reinitialiser")  { vmc.reset                              }
    private val reload  = Action("Recharger")      { vmc.reload                             }
    private val clock   = Action("Horloge")        { vm.interrupt(Interruption.IHClockNop)  }
    private val repack  = Action("Redimensionner") { mainFrame.pack                         }
    private val about   = Action("A propos")       { aboutDialog.visible = true             }

    title = "Alpha " + version

    menuBar = new MenuBar {
      contents += new Menu("Fichier") {
        contents += new MenuItem(loadFile)
 //       contents += new MenuItem(prune)

        contents += new Separator
        contents += new MenuItem(exitApp)
      }
      contents += new Menu("Simulation") {
        contents += new MenuItem(step)
        contents += new MenuItem(run)
        contents += new MenuItem(stop)
        contents += new MenuItem(reset)
        contents += new MenuItem(reload)
      }
      contents += new Menu("Interruptions") {
        contents += new MenuItem(clock)
      }
      contents += new Menu("Fenetre") {
        contents += new MenuItem(repack)
      }
      contents += new Menu("Aide") {
        contents += new MenuItem(about)
      }
    }

    contents = new BorderPanel {
      import BorderPanel.Position._


      val toolBar = new ToolBar {
        contents += new Button(loadFile)
    //    contents += new Button(prune)
        contents += new Button(step)
        contents += new Button(run)
        contents += new Button(stop)
        contents += new Button(reset)
        contents += new Button(reload)
        contents += new Button(repack)
        contents += new Button(exitApp)
      }
      layout(toolBar) = North

      val center = new TabbedPane {
        import TabbedPane._
        val screen = new Page("Ecran" , vmc.screen)
        pages += screen
        pages += new Page("Etats" , new InfoPanel(vmc))

        listenTo(selection,mouse.clicks)
        reactions += {
          case _ => if (selection.page == screen) vmc.screen.requestFocus
        }
      }
      layout(center) = Center
    }

  }

  trait FullScreen extends Frame {
    peer.setUndecorated(config.fullScreen)
  }

  def onlyScreen = new MainFrame with FullScreen { mainFrame =>
    contents = vmc.screen
    resizable = false

    // We need a file to load!
    val file : File = { if (config.program.isEmpty) {
                           var notOk = true
                           do { fileChooser.showOpenDialog(mainFrame.contents(0))
                                if (fileChooser.selectedFile != null) notOk = !fileChooser.selectedFile.isFile
                              }
                           while (notOk)
                           fileChooser.selectedFile
                        }
                        else config.program.get
                      }

    title = file.getPath

    // Going Full screen if needed
    if (config.fullScreen) {
      val graphicsEnv    = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment
      val device         = graphicsEnv.getDefaultScreenDevice
      if (device.isFullScreenSupported) {
        val oldDisplayMode = device.getDisplayMode
        val newDisplayMode = new java.awt.DisplayMode(800,600,java.awt.DisplayMode.BIT_DEPTH_MULTI, java.awt.DisplayMode.REFRESH_RATE_UNKNOWN)

        def restore = {
          device.setDisplayMode(oldDisplayMode)
          device.setFullScreenWindow(null)
        }

        try {
          device.setFullScreenWindow(peer)
          device.setDisplayMode(newDisplayMode)
        }
        catch { case e : IllegalArgumentException      => restore
                case e : UnsupportedOperationException => restore
              }
      }
    }

    // Starting the vm
    vmc.loadProgram(file)
    vmc.run
  }

  def top = if (config.fullUI) fullUI else onlyScreen

  override def main(args : Array[String]) {
    var displayUI = true

    for (arg <- args) {
      arg match {
      case "--fullUI"         => config.fullUI       = true
      case "--screenOnly"     => config.fullUI       = false
      case "--fullScreen"     => { config.fullUI     = false
                                   config.fullScreen = true
                                 }
      case "--windowed"       => config.fullScreen   = false
      case "--help" | "-h"    => {
        System.out.println(cliWelcome + "\n\n"
                            + "Usage: Alpha [options] [filepath]\n\n"
                            + "Options:\n========\n"
                            + "\t--fullUI\tDisplay the full UI.\n"
                            + "\t--screenOnly\tDisplay only the screen.\n"
                            + "\t\t--fullScreen\tfull screen mode.\n"
                            + "\t\t--windowed\twindowed mode.\n"
                            + "\t-h --help\tshow this message.\n"
                            + "\n\n\tfilepath\tA file to load.\n"
                          )
        displayUI = false
        }
      case s              => { val f = new File(s)
                               if (f.isFile) config.program = Some(f)
                             }
      }
    }
    if (displayUI) super.main(args)
    else System.exit(0)
  }
}