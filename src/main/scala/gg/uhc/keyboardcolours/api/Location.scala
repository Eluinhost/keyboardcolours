package gg.uhc.keyboardcolours.api

case class Location(x: Int, y: Int) {

  def until(other: Location) : Seq[Location] = for {
    x ← this.x to other.x
    y ← this.y to other.y
  } yield Location(x, y)
}
