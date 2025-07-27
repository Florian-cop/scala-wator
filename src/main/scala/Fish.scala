package wator

import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

sealed trait Fish {
  def x: Int
  def y: Int
  def color: Color
  def width: Int
  def height: Int

  def draw(): Rectangle = new Rectangle {
    translateX = Fish.this.x * Fish.this.width
    translateY = Fish.this.y * Fish.this.height
    this.width = Fish.this.width
    this.height = Fish.this.height
    fill = color
  }
}
