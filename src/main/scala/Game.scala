import org.newdawn.slick._
import scala.collection.mutable.Map

class Game( gameClient: GameClient ) extends BasicGame( "Hello" ) with KeyListener  {

    gameClient.game = this
    gameClient.connect()

    var tank: Image = null

    val scale = 1.0f
    val step = 26f

    var playerId = -1

    var x = 0.0f
    var y = 0.0f

    val players = Map[Int, Player]()

    def update(container: GameContainer, delta: Int) {

    }

    def connected( playerId: Int, x: Int, y: Int ) {
        players( playerId ) = Player( playerId, x, y )
    }

    def disconnected( playerId: Int ) {
        players -= playerId
    }

    def move( playerId: Int, dx: Int, dy: Int ) {
        players( playerId ) = players( playerId ).move( dx, dy )
    }

    def init(container: GameContainer) {
        tank = new Image( "tank.png" )

        container.getInput.addKeyListener( this )
    }

    def render(container: GameContainer, g: Graphics) {
        g.setColor( Color.black )
        g.fillRect( 0, 0, container.getWidth, container.getHeight )

        tank.draw( x, y, scale )

        g.setColor( Color.white )
        g.drawString( "" + ( x / step ).toInt + ";" + ( y / step ).toInt, 10, 25 )

        for( player <- players.values ) {
            tank.draw( player.x * step, player.y * step )
        }
    }

    override def keyReleased(key: Int, c: Char) {
        key match {
            case Input.KEY_UP => {
                y -= step
                tank.rotate( 0 - tank.getRotation )
                gameClient.move( 0, -1 )
            }
            case Input.KEY_DOWN => {
                y += step
                tank.rotate( 180 - tank.getRotation )
                gameClient.move( 0, 1 )
            }
            case Input.KEY_LEFT => {
                x -= step
                tank.rotate( 270 - tank.getRotation )
                gameClient.move( -1, 0 )
            }
            case Input.KEY_RIGHT => {
                x += step
                tank.rotate( 90 - tank.getRotation )
                gameClient.move( 1, 0 )
            }
            case _ =>
        }
    }
}

object Game {

    def main( args: Array[ String ] ) {
        val gameClient = new GameClient
        val app = new AppGameContainer( new Game( gameClient ) )

        app.setDisplayMode( 800, 600, false )
        app.start()
    }

}
