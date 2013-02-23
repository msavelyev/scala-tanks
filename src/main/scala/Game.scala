import org.newdawn.slick._
import scala.collection.mutable

class Game(gameClient: GameClient) extends BasicGame("Hello") with KeyListener {
    
    object MoveDirection extends Enumeration {
        type MoveDirection = Value
        val Up, Down, Left, Right, None = Value
    }

    gameClient.game = this
    gameClient.connect()

    var tank: Image = _

    val scale = 1.0f
    val step = 26f

    var playerId = -1

    var x = 0.0f
    var y = 0.0f
    val speed = 0.1f

    val players = mutable.Map[Int, Player]()
    var moveDirection = MoveDirection.None

    def update(container: GameContainer, delta: Int) {
        moveDirection match {
            case MoveDirection.Up => y -= speed * delta
            case MoveDirection.Down => y += speed * delta
            case MoveDirection.Left => x -= speed * delta
            case MoveDirection.Right => x += speed * delta
            case _ =>
        }
    }

    def connected(playerId: Int, x: Int, y: Int) {
        players(playerId) = Player(playerId, x, y)
    }

    def disconnected(playerId: Int) {
        players -= playerId
    }

    def move(playerId: Int, dx: Int, dy: Int) {
        players(playerId) = players(playerId).move(dx, dy)
    }

    def init(container: GameContainer) {
        tank = new Image("tank.png")

        container.getInput.addKeyListener(this)
    }

    def render(container: GameContainer, g: Graphics) {
        g.setColor(Color.black)
        g.fillRect(0, 0, container.getWidth, container.getHeight)

        tank.draw(x, y, scale)

        g.setColor(Color.white)
        g.drawString("" + (x / step).toInt + ";" + (y / step).toInt, 10, 25)

        for (player <- players.values) {
            tank.draw(player.x * step, player.y * step)
        }
    }


    override def keyPressed(key: Int, c: Char) {
        key match {
            case Input.KEY_UP => 
                moveDirection = MoveDirection.Up
                tank.rotate(0 - tank.getRotation)
            case Input.KEY_DOWN =>
                moveDirection = MoveDirection.Down
                tank.rotate(180 - tank.getRotation)
            case Input.KEY_LEFT =>
                moveDirection = MoveDirection.Left
                tank.rotate(270 - tank.getRotation)
            case Input.KEY_RIGHT =>
                moveDirection = MoveDirection.Right
                tank.rotate(90 - tank.getRotation)
            case _ =>
        }
    }

    override def keyReleased(key: Int, c: Char) {
        (key, moveDirection) match {
            case (Input.KEY_UP, MoveDirection.Up) => moveDirection = MoveDirection.None 
            case (Input.KEY_DOWN, MoveDirection.Down) => moveDirection = MoveDirection.None
            case (Input.KEY_LEFT, MoveDirection.Left) => moveDirection = MoveDirection.None
            case (Input.KEY_RIGHT, MoveDirection.Right) => moveDirection = MoveDirection.None
            case _ =>
        }
    }
}

object Game {

    def main(args: Array[String]) {
        val gameClient = new GameClient
        val app = new AppGameContainer(new Game(gameClient))

        app.setDisplayMode(800, 600, false)
        app.start()
    }

}
