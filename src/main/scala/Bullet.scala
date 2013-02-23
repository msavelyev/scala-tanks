
class Bullet (val playerId: Int, var x: Float, var y: Float, val direction: MoveDirection.MoveDirection) {
    
    def move(delta: Float) {
        direction match {
            case MoveDirection.Up => y -= delta
            case MoveDirection.Down => y += delta
            case MoveDirection.Left => x -= delta
            case MoveDirection.Right => x += delta
            case _ =>
        }
    }
    
    override def toString = {
        x.formatted("%.3f") + ";" + y.formatted("%.3f") 
    }
    
}

object Bullet {
    def apply(playerId: Int, x: Float, y: Float, direction: MoveDirection.MoveDirection) =
        new Bullet(playerId, x, y, direction)
}