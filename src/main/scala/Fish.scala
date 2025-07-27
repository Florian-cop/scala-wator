package wator

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

abstract class Fish(val x: Int, val y: Int, val color: Color, val width: Int, val height: Int) {
  def draw(): Rectangle = new Rectangle {
    translateX = Fish.this.x * Fish.this.width
    translateY = Fish.this.y * Fish.this.height
    this.width = Fish.this.width
    this.height = Fish.this.height
    fill = color
  }
}
