package wator

object ManualTest {
  def main(args: Array[String]): Unit = {
    println("Testing GameState grid structure manually...")
    
    val cols = 3
    val rows = 3
    val cellSize = 10
    
    // Test empty grid creation
    val emptyState = GameState.fromList(List.empty, cols, rows, cellSize)
    println(s"Empty grid size: ${emptyState.grid.size}, expected: ${cols * rows}")
    println(s"Empty cells count: ${emptyState.grid.count(_.isEmpty)}, expected: ${cols * rows}")
    
    // Test grid indexing - can't create Fish due to ScalaFX, but test structure
    println(s"Grid structure: ${emptyState.grid}")
    println(s"Cols: ${emptyState.cols}, Rows: ${emptyState.rows}, CellSize: ${emptyState.cellSize}")
    
    // Test next generation on empty grid
    val nextEmpty = emptyState.next()
    println(s"Next generation empty cells: ${nextEmpty.grid.count(_.isEmpty)}, expected: ${cols * rows}")
    
    println("Manual tests completed successfully!")
  }
}