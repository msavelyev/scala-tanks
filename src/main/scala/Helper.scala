import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.Input

object Helper {
    def sameDirection(key: Int, direction: Direction.Direction) = {
        (key, direction) match {
            case (Input.KEY_UP, Direction.Up) => true
            case (Input.KEY_DOWN, Direction.Down) => true
            case (Input.KEY_LEFT, Direction.Left) => true
            case (Input.KEY_RIGHT, Direction.Right) => true
            case _ => false
        }
    }
    
    def move(pos: Vector2f, speed: Float, delta: Int, direction: Direction.Direction) = {
        val diff: Float = speed * delta
        direction match {
            case Direction.Up => new Vector2f(pos.x, pos.y - diff)
            case Direction.Down => new Vector2f(pos.x, pos.y + diff)
            case Direction.Left => new Vector2f(pos.x - diff, pos.y)
            case Direction.Right => new Vector2f(pos.x + diff, pos.y)
        }
    }

    def angleByDirection(direction: Direction.Direction) = {
        direction match {
            case Direction.Up => 0
            case Direction.Down => 180
            case Direction.Left => 270
            case Direction.Right => 90
        }
    }
}
