package wator

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Rectangle2D
import scalafx.scene.Scene
import scalafx.scene.paint.Color.Red
import scalafx.scene.paint.Color.White
import scalafx.scene.paint.Color.Blue
import scalafx.scene.shape.Rectangle
import scalafx.stage.Screen
import scala.util.Random
import scalafx.beans.property.{BufferProperty, IntegerProperty, ObjectProperty}
import scalafx.animation.Timeline.*
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.util.Duration

object WatorApp extends JFXApp3 {

  override def start(): Unit = {
    val screenBounds: Rectangle2D             = Screen.primary.visualBounds
    val (boardWidth, boardHeight): (Int, Int) = (screenBounds.width.intValue, screenBounds.height.intValue)
    val entitySize = 10
    val cols = boardWidth  / entitySize
    val rows = boardHeight / entitySize
    val nTunas = 500
    val nSharks = 50
    var fish: List[Fish] = generateLife(nTunas, nSharks,cols,rows, entitySize)
    val state: ObjectProperty[List[Fish]] = ObjectProperty(fish)

    val gameState: ObjectProperty[GameState] = ObjectProperty(
      GameState(fish, boardWidth, boardHeight)
    )

    val emptyCells: Seq[Rectangle] = for {
      newX <- 0 until cols
      newY <- 0 until rows
    } yield new Rectangle {
      translateX = newX * entitySize
      translateY = newY * entitySize
      width      = entitySize
      height     = entitySize
      fill = White
    }

    stage = new PrimaryStage {
      title  = "Wator"
      width  = boardWidth
      height = boardHeight
      scene = new Scene {
        content = gameState.value.draw()
        // state.onChange {
        //   content = gameState.value.draw()
        // }
      }
    }
  }
  def generateLife(nT: Int, nS: Int, cols:Int, rows:Int, entitySize:Int): List[Fish] = {
    val tunas: List[Tuna] = List.fill(nT) {
      val x = Random.nextInt(cols)
      val y = Random.nextInt(rows)
      Tuna(0, x, y, Red, entitySize, entitySize)
    }
    val sharks: List[Shark] = List.fill(nS) {
      val x = Random.nextInt(cols)
      val y = Random.nextInt(rows)
      Shark(0, 1, x, y, Blue, entitySize, entitySize)
    }
    tunas ++ sharks
  }

  
  // def infiniteTimeline(fish: ObjectProperty[List[Fish]], gameState: ObjectProperty[GameState]): Timeline =
  //   new Timeline {
  //     keyFrames = List(KeyFrame(time = Duration(25), onFinished = _ => updateState(fish, gameState)))
  //     cycleCount = Indefinite
  //   }

  // def updateState(state: ObjectProperty[List[Fish]], gameState: ObjectProperty[GameState]): Unit = {
  //   // val board = state.value.map(p => (p.x, p.y) -> p.direction).toMap()
  //   // state.update(gameState.value.move(board, gameState))
  // }
}
