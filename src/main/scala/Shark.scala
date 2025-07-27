package wator

import scalafx.scene.paint.Color

final case class Shark(
    override val x: Int,
    override val y: Int,
    sBreed: Int = 5,
    breedTimer: Int = 0,
    sEnergy: Int,
    override val color: Color,
    override val width: Int,
    override val height: Int
) extends Fish(x, y, color, width, height)

