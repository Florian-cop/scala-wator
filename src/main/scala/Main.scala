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
    val (screenWidth, screenHeight, cols, rows) = computeDimensions()
    val state = createState(cols, rows)
    val (canvas, gc) = setupCanvas(screenWidth, screenHeight)
    def render(): Unit = drawState(gc, state.value, cols)
    render()

    val (playPause, running, speedSlider, intervalLabel) = createControls()

    val loop = createTimeline(speedSlider, render)
    loop.play()
    
    setupInteractions(loop, speedSlider, playPause, running, render)

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

  private def computeDimensions(): (Int, Int, Int, Int) = {
    val bounds       = Screen.primary.visualBounds
    val screenWidth  = bounds.width.intValue()
    val screenHeight = bounds.height.intValue()
    val cellSize     = Constants.CellSize
    val cols         = screenWidth / cellSize
    val rows         = screenHeight / cellSize
    (screenWidth, screenHeight, cols, rows)
  }

  private def createState(cols: Int, rows: Int): ObjectProperty[GameState] = {
    val initialFish = generateLife(Constants.InitialTunaCount, Constants.InitialSharkCount, cols, rows, Constants.CellSize)
    ObjectProperty(GameState.fromList(initialFish, cols, rows, Constants.CellSize))
  }

  private def setupCanvas(width: Int, height: Int): (Canvas, GraphicsContext) = {
    val canvas = new Canvas(width, height)
    val gc     = canvas.graphicsContext2D
    (canvas, gc)
  }

  private def drawState(gc: GraphicsContext, state: GameState, cols: Int): Unit = {
    gc.fill = White
    gc.fillRect(0, 0, gc.canvas.width(), gc.canvas.height())
    val cellSize = state.cellSize
    state.grid.zipWithIndex.foreach {
      case (Some(fish), idx) =>
        val col = idx % cols
        val row = idx / cols
        gc.fill = fish.color
        gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize)
      case _ =>
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

  private def createControls(): (Button, BooleanProperty, Slider, Label) = {
    val playPause = new Button(Constants.PauseText)
    val running = BooleanProperty(true)
    val speedSlider = new Slider(Constants.SliderMinInterval, Constants.SliderMaxInterval, Constants.SliderDefaultInterval) {
      showTickLabels = true
      showTickMarks = true
      majorTickUnit = Constants.SliderMajorTickUnit
      blockIncrement = Constants.SliderBlockIncrement
    }
    val intervalLabel = new Label(s"${Constants.IntervalLabelPrefix}${speedSlider.value.value.toInt}${Constants.IntervalLabelSuffix}")
    (playPause, running, speedSlider, intervalLabel)
  }

  private def createTimeline(speedSlider: Slider, render: () => Unit): Timeline = {
    new Timeline {
      cycleCount = Animation.Indefinite
      keyFrames = Seq(KeyFrame(Duration(speedSlider.value.value), onFinished = _ => {
        render()
      }))
    }
  }

  private def setupInteractions(loop: Timeline, speedSlider: Slider, playPause: Button, running: BooleanProperty, render: () => Unit): Unit = {
    speedSlider.value.onChange { (_, _, v) =>
      val ms = v.doubleValue.toInt
      loop.stop()
      loop.keyFrames = Seq(KeyFrame(Duration(v.doubleValue), onFinished = _ => {
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
  }
}
