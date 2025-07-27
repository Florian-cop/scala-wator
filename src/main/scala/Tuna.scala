package wator

import scalafx.scene.paint.Color

final case class Tuna(
  override val x: Int,
  override val y: Int,
  tBreed: Int = 3,
  breedTimer: Int = 0,
  override val color: Color,
  override val width: Int,
  override val height: Int
) extends Fish(x, y, color, width, height)
