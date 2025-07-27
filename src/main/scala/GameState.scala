package wator
import wator.Constants._

import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color

/** Représente l’état du monde Wator sous forme de grille indexée */
final case class GameState(
  grid: Vector[Option[Fish]],
  cols: Int,
  rows: Int,
  cellSize: Int
  ) {
  /** Dessine toutes les cellules du jeu */
  def draw(): List[Rectangle] =
    grid.zipWithIndex.map { case (opt, idx) =>
      val col = idx % cols
      val row = idx / cols
      opt.map(_.draw()).getOrElse(createCell(col, row, Color.White))
    }.toList

  /** Calcule la génération suivante */
  def next(): GameState = {
    import scala.util.Random

    val rand     = new Random()
    val fishes   = grid.flatten
    val original = fishes.map(e => (e.x, e.y) -> e).toMap

    val (nextFish, _, _) = rand.shuffle(fishes).foldLeft((List.empty[Fish], Set.empty[(Int, Int)], Set.empty[(Int, Int)])) {
      case ((acc, occupied, seen), fish) if seen(fish.x, fish.y) =>
        (acc, occupied, seen)
      case ((acc, occupied, seen), fish) =>
        fish match {
          case t: Tuna  => processTuna(t, acc, occupied, seen, rand, original)
          case s: Shark => processShark(s, acc, occupied, seen, rand, original)
          case other    => (acc :+ other, occupied + ((other.x, other.y)), seen + ((other.x, other.y)))
        }
    }
    GameState.fromList(nextFish, cols, rows, cellSize)
  }

  /** Génère les coordonnées des voisins en mode torus */
  private def neighbors(x: Int, y: Int): Seq[(Int, Int)] =
    for {
      dx <- -1 to 1
      dy <- -1 to 1
      if dx != 0 || dy != 0
    } yield ((x + dx + cols) % cols, (y + dy + rows) % rows)

  /** Crée une cellule graphique vide */
  private def createCell(col: Int, row: Int, fillColor: Color): Rectangle =
    new Rectangle {
      translateX = col * cellSize
      translateY = row * cellSize
      width      = cellSize
      height     = cellSize
      fill       = fillColor
    }

  // Traitement des Tunas et Sharks extrait pour clarté
  private def processTuna(
    t: Tuna,
    acc: List[Fish],
    occupied: Set[(Int, Int)],
    seen: Set[(Int, Int)],
    rand: scala.util.Random,
    original: Map[(Int, Int), Fish]
  ): (List[Fish], Set[(Int, Int)], Set[(Int, Int)]) = {
    val adjFree = neighbors(t.x, t.y).filter(p => !original.contains(p) && !occupied(p))
    if (adjFree.nonEmpty) {
      val p@(nx, ny) = adjFree(rand.nextInt(adjFree.size))
      val newTimer   = t.breedTimer + 1
      if (newTimer >= t.tBreed)
        (acc :+ Tuna(nx, ny, t.tBreed, 0, t.color, t.width, t.height)
           :+ Tuna(t.x, t.y, t.tBreed, 0, t.color, t.width, t.height),
         occupied + p + ((t.x, t.y)),
         seen + p + ((t.x, t.y)))
      else
        (acc :+ Tuna(nx, ny, t.tBreed, newTimer, t.color, t.width, t.height), occupied + p, seen + p)
    } else
      (acc :+ t, occupied + ((t.x, t.y)), seen + ((t.x, t.y)))
  }

  private def processShark(
    s: Shark,
    acc: List[Fish],
    occupied: Set[(Int, Int)],
    seen: Set[(Int, Int)],
    rand: scala.util.Random,
    original: Map[(Int, Int), Fish]
  ): (List[Fish], Set[(Int, Int)], Set[(Int, Int)]) = {
    val adj      = neighbors(s.x, s.y)
    val preyPos  = adj.find(p => original.get(p).exists(_.isInstanceOf[Tuna]))
    val (nx, ny, eaten) = preyPos.map(p => (p._1, p._2, true)).getOrElse {
      val free = adj.filter(p => !original.contains(p) && !occupied(p))
      if (free.nonEmpty) {
        val p = free(rand.nextInt(free.size))
        (p._1, p._2, false)
      } else
        (s.x, s.y, false)
    }
    val rawE    = s.sEnergy + (if (eaten) EnergyGain else -EnergyLoss)
    val energy1 = math.min(rawE, SharkMaxEnergy)
    val moved   = (nx != s.x) || (ny != s.y)
    val breed1  = if (moved) s.breedTimer + 1 else s.breedTimer
    if (energy1 > 0) {
      if (moved && breed1 >= s.sBreed)
        (acc :+ Shark(nx, ny, s.sBreed, 0, energy1, s.color, s.width, s.height)
             :+ Shark(s.x, s.y, s.sBreed, 0, energy1, s.color, s.width, s.height),
         occupied + ((nx, ny)) + ((s.x, s.y)), seen + ((nx, ny)) + ((s.x, s.y)))
      else
        (acc :+ Shark(nx, ny, s.sBreed, breed1, energy1, s.color, s.width, s.height), occupied + ((nx, ny)), seen + ((nx, ny)))
    } else
      (acc, occupied, seen + ((s.x, s.y)))
  }
}

object GameState {
/** Compagnon pour construire GameState depuis liste */
  def fromList(entities: List[Fish], cols: Int, rows: Int, cellSize: Int): GameState = {
    val size  = cols * rows
    val empty = Vector.fill(size)(Option.empty[Fish])
    val grid  = entities.foldLeft(empty) { (g, e) =>
      g.updated(e.y * cols + e.x, Some(e))
    }
    GameState(grid, cols, rows, cellSize)
  }
}
