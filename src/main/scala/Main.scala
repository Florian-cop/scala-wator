package wator

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.stage.Screen
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.layout.{VBox, HBox, Priority}
import scalafx.scene.control.{Button, Slider, Label}
import scalafx.scene.paint.Color.{Red, White, Blue}
import scalafx.animation.{Animation, Timeline, KeyFrame}
import scalafx.util.Duration
import scala.util.Random
import wator.Constants
import scalafx.beans.property.{ObjectProperty, BooleanProperty}

object WatorApp extends JFXApp3 {
  override def start(): Unit = {
    val bounds = Screen.primary.visualBounds
    val screenWidth = bounds.width.intValue
    val screenHeight = bounds.height.intValue
    val cellSize = Constants.CellSize
    val cols = screenWidth / cellSize
    val rows = screenHeight / cellSize
    val tunaCount = Constants.InitialTunaCount
    val sharkCount = Constants.InitialSharkCount
    val state = ObjectProperty(GameState(generateLife(tunaCount, sharkCount, cols, rows, cellSize), cols, rows, cellSize))

    val canvas = new Canvas(screenWidth, screenHeight)
    val gc: GraphicsContext = canvas.graphicsContext2D
    def render(): Unit = {
      gc.fill = White
      gc.fillRect(0, 0, screenWidth, screenHeight)
      state.value.entities.foreach { fish =>
        gc.fill = fish.color
        gc.fillRect(fish.x * cellSize, fish.y * cellSize, cellSize, cellSize)
      }
    }
    render()

    val playPause = new Button(Constants.PauseText)
    val running = BooleanProperty(true)
    val speedSlider = new Slider(Constants.SliderMinInterval, Constants.SliderMaxInterval, Constants.SliderDefaultInterval) {
      showTickLabels = true
      showTickMarks = true
      majorTickUnit = Constants.SliderMajorTickUnit
      blockIncrement = Constants.SliderBlockIncrement
    }
    val intervalLabel = new Label(s"${Constants.IntervalLabelPrefix}${speedSlider.value.value.toInt}${Constants.IntervalLabelSuffix}")

    def mkTimeline(intervalMs: Double) = new Timeline {
      cycleCount = Animation.Indefinite
      keyFrames = Seq(KeyFrame(Duration(intervalMs), onFinished = _ => {
        state.value = state.value.next()
        render()
      }))
    }
    val loop = mkTimeline(speedSlider.value.value)
    loop.play()

    speedSlider.value.onChange { (_, _, v) =>
      val ms = v.doubleValue.toInt
      intervalLabel.text = s"${Constants.IntervalLabelPrefix}$ms${Constants.IntervalLabelSuffix}"
      loop.stop()
      loop.keyFrames = Seq(KeyFrame(Duration(v.doubleValue), onFinished = _ => {
        state.value = state.value.next()
        render()
      }))
      if (running.value)
        loop.play()
    }

    playPause.onAction = _ =>
      if (running.value) {
        loop.stop(); playPause.text = Constants.PlayText; running.value = false
      } else {
        loop.play(); playPause.text = Constants.PauseText; running.value = true
      }

    stage = new PrimaryStage {
      title = Constants.WindowTitle
      width = screenWidth; height = screenHeight
      scene = new Scene(new VBox {
        children = Seq(
          canvas,
          new HBox(Constants.HBoxSpacing) { children = Seq(playPause, intervalLabel, speedSlider) }
        )
        VBox.setVgrow(canvas, Priority.Always)
      })
    }
  }

  private def generateLife(tunaCount: Int, sharkCount: Int, cols: Int, rows: Int, cellSize: Int): List[Fish] = {
    val allPositions = Random.shuffle((for {
      x <- 0 until cols
      y <- 0 until rows
    } yield (x, y)).toList)
    val tunaPositions  = allPositions.take(tunaCount)
    val sharkPositions = allPositions.slice(tunaCount, tunaCount + sharkCount)
    val tunas = tunaPositions.map { case (x, y) =>
      Tuna(x, y, tBreed = 3, breedTimer = 0, color = Red, width = cellSize, height = cellSize)
    }
    val sharks = sharkPositions.map { case (x, y) =>
      Shark(x, y, sBreed = 3, breedTimer = 0, sEnergy = 10, color = Blue, width = cellSize, height = cellSize)
    }
    tunas ++ sharks
  }
}
