package wator

import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*
import scalafx.beans.property.{BufferProperty, IntegerProperty, ObjectProperty}
import wator.Tuna
import wator.Shark

final case class GameState(fish: List[Fish], boardWidth: Int, boardHeight: Int) {
    def draw(): List[Rectangle] =
        fish.map(_.draw())
}
