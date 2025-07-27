package wator

import munit.FunSuite

class GameStateSimpleTest extends FunSuite {
  val cellSize = 10
  val cols = 5
  val rows = 4

  test("GameState.fromList with empty list creates correct grid structure") {
    val gameState = GameState.fromList(List.empty, cols, rows, cellSize)
    
    assertEquals(gameState.cols, cols)
    assertEquals(gameState.rows, rows) 
    assertEquals(gameState.cellSize, cellSize)
    assertEquals(gameState.grid.size, cols * rows)
    
    // All cells should be empty
    val emptyCount = gameState.grid.count(_.isEmpty)
    assertEquals(emptyCount, cols * rows)
  }

  test("GameState companion object construction") {
    val gridSize = cols * rows
    val gameState = GameState.fromList(List.empty, cols, rows, cellSize)
    
    assertEquals(gameState.grid.length, gridSize)
    assert(gameState.grid.forall(_.isEmpty))
  }

  test("GameState.next with empty grid") {
    val gameState = GameState.fromList(List.empty, cols, rows, cellSize)
    val nextState = gameState.next()
    
    assertEquals(nextState.grid.count(_.nonEmpty), 0)
    assertEquals(nextState.cols, cols)
    assertEquals(nextState.rows, rows)
    assertEquals(nextState.cellSize, cellSize)
  }

  test("grid dimensions and indexing") {
    val gameState = GameState.fromList(List.empty, 3, 4, cellSize)
    
    assertEquals(gameState.cols, 3)
    assertEquals(gameState.rows, 4) 
    assertEquals(gameState.grid.size, 12) // 3 * 4
  }
}