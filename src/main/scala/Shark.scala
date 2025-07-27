package wator

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import ujson.Bool

final case class Shark(
    sBreed: Int,
    sEnergy: Int,
    val x: Int,
    val y: Int,
    val color: Color,
    val width: Int,
    val height: Int
) extends Fish(x, y, color, width, height) {

  def canReproduce(numberCycle: Int): Boolean =
    sBreed == numberCycle
}

