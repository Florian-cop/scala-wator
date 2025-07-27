package wator
import wator.Constants._
import scala.util.Random

private type Pos = Int
private val neighborOffsets: Seq[(Int, Int)] = (
  for { dx <- -1 to 1; dy <- -1 to 1 if dx != 0 || dy != 0 }
  yield (dx, dy)
).toSeq
private val rand = new Random()
final case class GameState(
    grid: Vector[Option[Fish]],
    gridWidth: Int,
    gridHeight: Int,
    cellSize: Int 
  ) {
  private def toPos(x: Int, y: Int): Pos = y * gridWidth + x
  private def posX(p: Pos): Int = p % gridWidth
  private def posY(p: Pos): Int = p / gridWidth

  def next: GameState = {
    val fishes   = grid.flatten
    val original = fishes.map(e => toPos(e.x, e.y) -> e).toMap

    val order = rand.shuffle(fishes)
    val (resultSeq, _, _) = order.foldLeft((Vector.empty[Fish], Set.empty[Pos], Set.empty[Pos])) {
      case ((acc, occ, seen), fish) =>
        val pos = toPos(fish.x, fish.y)
        if (seen(pos)) (acc, occ, seen)
        else {
          val (newFishes, newOcc, newSeen) = fish match {
            case t: Tuna  => processTuna(t, occ, seen, original)
            case s: Shark => processShark(s, occ, seen, original)
            case other    => (Seq(other), occ + pos, seen + pos)
          }
          (acc ++ newFishes.toVector, newOcc, newSeen)
        }
    }
    GameState.fromList(resultSeq.toList, gridWidth, gridHeight, cellSize)
  }



  private def neighbors(pos: Pos): Seq[Pos] = {
    val x = posX(pos); val y = posY(pos)
    neighborOffsets.map { case (dx, dy) =>
      toPos((x + dx + gridWidth) % gridWidth, (y + dy + gridHeight) % gridHeight)
    }
  }

  private def processTuna(
    t: Tuna,
    newOccupied: Set[Pos],
    processed: Set[Pos],
    original: Map[Pos, Fish]
   ): (Seq[Fish], Set[Pos], Set[Pos]) = {
    val adjFree = neighbors(toPos(t.x, t.y))
      .filter(p => !original.contains(p) && !newOccupied(p))
    if (adjFree.nonEmpty) {
      val p = adjFree(rand.nextInt(adjFree.size))
      val nx = posX(p); val ny = posY(p)
      val newTimer   = t.breedTimer + 1
      if (newTimer >= t.tBreed)
        (Seq(
           Tuna(nx, ny, t.tBreed, 0, t.color, t.width, t.height),
           Tuna(t.x, t.y, t.tBreed, 0, t.color, t.width, t.height)
         ),
         newOccupied + p + toPos(t.x, t.y), processed + p + toPos(t.x, t.y))
      else
        (Seq(Tuna(nx, ny, t.tBreed, newTimer, t.color, t.width, t.height)),
         newOccupied + p, processed + p)
    } else
      (Seq(t),
       newOccupied + toPos(t.x, t.y), processed + toPos(t.x, t.y))
  }

  private def processShark(
    s: Shark,
    newOccupied: Set[Pos],
    processed: Set[Pos],
    original: Map[Pos, Fish]
   ): (Seq[Fish], Set[Pos], Set[Pos]) = {
    val adj = neighbors(toPos(s.x, s.y))
    val preyPos  = adj.find(p => original.get(p).exists(_.isInstanceOf[Tuna]))
    val (nx, ny, eaten) = preyPos.map(p => (posX(p), posY(p), true)).getOrElse {
      val free = adj.filter(p => !original.contains(p) && !newOccupied(p))
      if (free.nonEmpty) {
        val p = free(rand.nextInt(free.size))
        (posX(p), posY(p), false)
      } else
        (s.x, s.y, false)
    }
    val rawE    = s.sEnergy + (if (eaten) EnergyGain else -EnergyLoss)
    val energy1 = math.min(rawE, SharkMaxEnergy)
    val moved   = (nx != s.x) || (ny != s.y)
    val breed1  = if (moved) s.breedTimer + 1 else s.breedTimer
    if (energy1 > 0) {
      if (moved && breed1 >= s.sBreed)
        (Seq(
           Shark(nx, ny, s.sBreed, 0, energy1, s.color, s.width, s.height),
           Shark(s.x, s.y, s.sBreed, 0, energy1, s.color, s.width, s.height)
         ),
         newOccupied + toPos(nx, ny) + toPos(s.x, s.y), processed + toPos(nx, ny) + toPos(s.x, s.y))
      else
        (Seq(Shark(nx, ny, s.sBreed, breed1, energy1, s.color, s.width, s.height)),
         newOccupied + toPos(nx, ny), processed + toPos(nx, ny))
    } else
      (Seq.empty,
       newOccupied,
       processed + toPos(s.x, s.y))
  }
}

object GameState {
  def fromList(entities: List[Fish], cols: Int, rows: Int, cellSize: Int): GameState = {
    val size  = cols * rows
    val empty = Vector.fill(size)(Option.empty[Fish])
    val grid  = entities.foldLeft(empty) { (g, e) =>
      g.updated(e.y * cols + e.x, Some(e))
    }
    GameState(grid, cols, rows, cellSize)
  }
}
