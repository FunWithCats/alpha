package main.scala.org.cc.beta

object Interruption {

  implicit object IHOrdering extends Ordering[InterruptHandler] {
    def compare(ih1 : InterruptHandler, ih2 : InterruptHandler) : Int =
      (ih2.address & 0x7FFFFFFC) - (ih1.address & 0x7FFFFFFC)
  }

  abstract class InterruptHandler {
    val address : Int
    def exec    : Unit
  }

  case object IHReset              extends InterruptHandler {
    final val address = 0x00000000
    @inline final def exec    = ()
  }

  case object IHIllegalInstruction extends InterruptHandler {
    final val address = 0x80000004
    @inline final def exec    = ()
  }

  abstract class IHClock           extends InterruptHandler {
    final val address = 0x80000008
  }

  abstract class IHKeyboard        extends InterruptHandler {
    final val address = 0x8000000C
  }

  abstract class IHMouse           extends InterruptHandler {
    final val address = 0x80000010
  }


  // Interruptions with exec = nop

  object IHClockNop extends IHClock {
    @inline final def exec = ()
  }
}