package wator

object Constants {
  val CellSize: Int = 10

  val InitialTunaCount: Int = 500
  val InitialSharkCount: Int = 150
  val SliderMinInterval: Double = 50.0
  val SliderMaxInterval: Double = 1000.0
  val SliderDefaultInterval: Double = 200.0
  val SliderMajorTickUnit: Double = 250.0
  val SliderBlockIncrement: Double = 50.0

  val HBoxSpacing: Double = 10.0

  val WindowTitle: String = "Wator"
  val PlayText: String = "Play"
  val PauseText: String = "Pause"
  val IntervalLabelPrefix: String = "Interval: "
  val IntervalLabelSuffix: String = " ms"

  val EnergyGain: Int = 1
  val EnergyLoss: Int = 1
  val InitialTunaBreedThreshold: Int = 3
  val InitialSharkBreedThreshold: Int = 3
  val InitialSharkEnergy: Int = 1
  val SharkMaxEnergy: Int = 5
}
