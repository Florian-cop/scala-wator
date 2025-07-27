package wator

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

abstract class Fish(
    val posX: Int,
    val posY: Int,
    val fishColor: Color,
    val fishWidth: Int,
    val fishHeight: Int
) {
  def draw(): Rectangle = new Rectangle {
    x      = posX
    y      = posY
    width  = fishWidth
    height = fishHeight
    fill   = fishColor
  }
}
