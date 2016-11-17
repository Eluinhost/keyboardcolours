package gg.uhc.keyboardcolours

object Main {
  import gg.uhc.keyboardcolours.api.LedColour

  val WHITE = LedColour(255, 255, 255)
  val RED = LedColour(241, 50, 36)
  val GOLD = LedColour(239, 206, 11)
  val ELECTRIC_BLUE = LedColour(125, 249, 255)
  val DIMMER_BLUE = LedColour(125, 160, 170)

  def main(args: Array[String]): Unit = {
    import java.nio.file.{Files, Paths}
    import akka.actor.ActorSystem
    import gg.uhc.keyboardcolours.api.{Device, KeyboardLayout, Location, SdkInterface}
    import scala.io.StdIn
    import scala.concurrent.duration._

    // save the SDK somewhere JNA can read it
    if (!Files.exists(Paths.get("SDK.dll"))) {

      println("SDK file cannot be found, enter [y] to write to current directory or any other line to quit")
      StdIn.readLine.toLowerCase match {
        case "y" ⇒
          Files.copy(getClass.getResourceAsStream("/SDK.dll"), Paths.get("SDK.dll"))
        case _ ⇒
          return
      }
    }

    val lib = new SdkInterface(device = Device.MKeys_L)

    lib.getDeviceLayout match {
      case None ⇒
        println("WARNING: Failed to find device layout")
      case Some(KeyboardLayout.EU) ⇒
      case Some(_) ⇒
        println("WARNING: Not EU keyboard layout")
    }

    if (!lib.setControllingLeds(true)) {
      println("Failed to gain control of LEDs")
      return
    }

    println("Gained control of LEDs")

    val reactor = Location(0, 0) :: Nil // Esc

    val typingKeys =
      (Location(1, 2) until Location(12, 4)) ++ // Q to /
      (Location(18, 2) until Location(20, 5)) :+ // num7 to num.
      Location(1, 5) :+ // lwin
      Location(11, 5) // rwin

    val shield =
      Location(16, 1) :: // home
      Location(12, 5) :: // FN
      Nil

    val borderingKeys =
      (Location(1, 0) until Location(21, 0)) ++ // F1 to P4
      (Location(0, 1) until Location(14, 1)) ++ // ` to bkspc
      (Location(0, 2) until Location(0, 5)) ++ // tab to lctrl
      (Location(2, 5) until Location(10, 5)) ++ // lalt to ralt
      (Location(14, 2) until Location(14, 5)) ++ // enter to rctrl
      (Location(15, 4) until Location(17, 5)) ++ // arrows
      (Location(21, 1) until Location(21, 4)) ++ // num- to numenter
      (Location(18, 1) until Location(20, 1)) ++ // numlk to num*
      (Location(15, 2) until Location(17, 2)) :+ // del to pgdown
      Location(15, 1) :+ // ins
      Location(17, 1) // pgup

    reactor.foreach(l ⇒ lib.setLedColor(l, ELECTRIC_BLUE))
    typingKeys.foreach(l ⇒ lib.setLedColor(l, RED))
    shield.foreach(l ⇒ lib.setLedColor(l, WHITE))
    borderingKeys.foreach(l ⇒ lib.setLedColor(l, GOLD))

    println("Setup static colours")

    val actorSystem = ActorSystem()
    val scheduler = actorSystem.scheduler
    implicit val executor = actorSystem.dispatcher

    val end = 18
    var current = 0
    var increment = 1
    val stepR = (DIMMER_BLUE.red - ELECTRIC_BLUE.red).toDouble / end
    val stepB = (DIMMER_BLUE.blue - ELECTRIC_BLUE.blue).toDouble / end
    val stepG = (DIMMER_BLUE.green - ELECTRIC_BLUE.green).toDouble / end

    scheduler.schedule(0.seconds, 50.milliseconds) {
      val c = LedColour(
        red = (ELECTRIC_BLUE.red + (current * stepR)).toInt,
        blue = (ELECTRIC_BLUE.blue + (current * stepB)).toInt,
        green = (ELECTRIC_BLUE.green + (current * stepG)).toInt
      )
      reactor.foreach(l ⇒ lib.setLedColor(l, c))

      current += increment

      if (current == end) {
        increment = -1
      } else if (current == 0) {
        increment = 1
      }
    }

    println("Animations setup")

    println("Enter any line to quit")
    StdIn.readLine()

    // make sure to shutdown actors so JVM dies
    actorSystem.shutdown()

    if (lib.setControllingLeds(false)) {
      println("Control handed back to keyboard")
    } else {
      println("Failed to hand control back to keyboard")
    }
  }
}
