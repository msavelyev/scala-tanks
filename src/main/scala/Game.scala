import org.newdawn.slick._
import geom.{ShapeRenderer, Rectangle, Shape}
import collection.mutable

class Game(gameClient: GameClient) extends BasicGame("Hello") with Loggable with KeyListener {
    
    gameClient.game = this
//    gameClient.connect()

    var tank: Image = _
    var wall: Image = _
    var bullet: Image = _

    val scale = 1.0f
    val blockSize = 32f

    var playerId = -1

    var x = 0.0f
    var y = 0.0f
    val speed = 0.1f
    val bulletSpeed = 0.2f

    val players = mutable.Map[Int, Player]()
    var moveDirection = MoveDirection.Right
    val bullets = mutable.Map[Int, Bullet]()
    
    var moving = false
    
    val initWorld = Array(
        Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 0, 1, 1, 1, 1, 1, 1, 0, 1),
        Array(1, 0, 1, 0, 0, 0, 0, 1, 0, 1),
        Array(1, 0, 1, 0, 1, 1, 0, 1, 0, 1),
        Array(1, 0, 1, 0, 1, 0, 0, 1, 0, 1),
        Array(1, 0, 1, 0, 0, 0, 0, 1, 0, 1),
        Array(1, 0, 1, 0, 1, 1, 1, 1, 0, 1),
        Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 0, 1, 1, 1, 1, 1, 1, 1, 1)
    )
    
    var realWorld: Array[Array[Option[Shape]]] = _
    
    def isThereAWall(x: Int, y: Int) = {
        def bla(x: Int, y: Int) = {
            var result = true
            /*if(x < -1 || y < -1) {
                true
            } else */if(y < 0 || x < 0) {
                result = false 
            } else if(y >= realWorld.length) {
                result = false
            } else if(x >= realWorld(y).length) {
                result = false
            } else {
                result = realWorld(y)(x).nonEmpty
            }
            INFO << "at " + x + ";" + y + " = " + result
            
            result
        }
        
        bla(x - 1, y - 1)
    }

    def update(container: GameContainer, delta: Int) {
        if(moving) {
            val iY = (y / 32).toInt
            val iX = (x / 32).toInt
            moveDirection match {
                case MoveDirection.Up =>
                    var canMove = true
                    if(x % 32 == 0) {
                        canMove &= !isThereAWall(iX, iY)
                    } else {
                        canMove &= !isThereAWall(iX, iY)
                        canMove &= !isThereAWall(iX + 1, iY)
                    }
                    if(canMove) { 
                        y -= speed * delta
                    }
                case MoveDirection.Down =>
                    var canMove = true
                    if(x % 32 == 0) {
                        canMove &= !isThereAWall(iX, iY + 1)
                    } else {
                        canMove &= !isThereAWall(iX, iY + 1)
                        canMove &= !isThereAWall(iX + 1, iY + 1)
                    }
                    if(canMove) {
                        y += speed * delta
                    }
                case MoveDirection.Left =>
                    var canMove = true
                    if(y % 32 == 0) {
                        canMove &= !isThereAWall(iX, iY)
                    } else {
                        canMove &= !isThereAWall(iX, iY)
                        canMove &= !isThereAWall(iX, iY + 1)
                    }
                    if(canMove) {
                        x -= speed * delta
                    }
                case MoveDirection.Right =>
                    var canMove = true
                    if(y % 32 == 0) {
                        canMove &= !isThereAWall(iX + 1, iY)
                    } else {
                        canMove &= !isThereAWall(iX + 1, iY)
                        canMove &= !isThereAWall(iX + 1, iY + 1)
                    }
                    if(canMove) {
                        x += speed * delta
                    }
                case _ =>
            }
        }
        
        var bulletsToRemove = List[Bullet]()
        bullets.foreach(pair => {
            val b = pair._2
            b.move(bulletSpeed * delta)
            if(b.x < -blockSize || b.x > container.getWidth) {
                bulletsToRemove = b :: bulletsToRemove
            } else if(b.y < -blockSize || b.y > container.getHeight) {
                bulletsToRemove = b :: bulletsToRemove
            }
        })
        
        for(bullet <- bulletsToRemove) {
            bullets.remove(bullet.playerId)
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
        wall = new Image("wall16.png")
        bullet = new Image("bullet.png")

        container.getInput.addKeyListener(this)
        
        realWorld = Array.ofDim(10, 10)
        for(x <- 0 until initWorld.length) {
            for(y <- 0 until initWorld(x).length) {
                val block = initWorld(x)(y)
                
                if(block == 1) {
                    realWorld(x)(y) = Some(new Rectangle((y + 1) * blockSize, (x + 1) * blockSize, blockSize + 1, blockSize + 1))
                } else {
                    realWorld(x)(y) = None
                }
            }
        }
    }

    def render(container: GameContainer, g: Graphics) {
        drawBackground(g, container)
        
        drawDebug(g)
        drawWalls(g)
        drawTank(g)
        drawOtherPlayers()
        drawHud(g)
        drawBullets(g)
    }

    def drawBullets(graphics: Graphics) {
        bullets.foreach(pair => {
            bullet.draw(pair._2.x, pair._2.y)
        }) 
    }

    def drawBackground(g: Graphics, container: GameContainer) {
        g.setColor(Color.black)
        g.fillRect(0, 0, container.getWidth, container.getHeight)
    }

    def drawOtherPlayers() {
        for (player <- players.values) {
            tank.draw(player.x * blockSize, player.y * blockSize)
        }
    }

    def drawDebug(g: Graphics) {
        g.setColor(new Color(.9f, .9f, .9f, .2f))
        for (x <- 0 to 100) {
            g.drawLine(x * 8, 0, x * 8, 600)
            if (x % 2 == 0) {
                g.drawLine(x * 8, 0, x * 8, 600)
            }
        }
        for (y <- 0 to 75) {
            g.drawLine(0, y * 8, 800, y * 8)
            if (y % 2 == 0) {
                g.drawLine(0, y * 8, 800, y * 8)
            }
        }
    }

    def drawHud(g: Graphics) {
        g.setColor(Color.white)
        g.drawString("" + x.formatted("%.3f") + ";" + y.formatted("%.3f"), 10, 25)
    }

    def drawWalls(g: Graphics) {
        g.setColor(Color.white)
        for (x <- 0 until realWorld.length) {
            for (y <- 0 until realWorld(x).length) {
                val shape = realWorld(x)(y)
                shape match {
                    case Some(s) =>
                        ShapeRenderer.textureFit(s, wall, 2f, 2f)
                    case _ =>
                }
            }
        }
    }

    def drawTank(g: Graphics) {
        tank.draw(x, y, scale)

        g.setColor(Color.red)
        g.drawRect(x, y, blockSize, blockSize)
    }

    override def keyPressed(key: Int, c: Char) {
        key match {
            case Input.KEY_UP => 
                moving = true
                moveDirection = MoveDirection.Up
                tank.rotate(0 - tank.getRotation)
                x = 16 * scala.math.round(x / 16) 
            case Input.KEY_DOWN =>
                moving = true
                moveDirection = MoveDirection.Down
                tank.rotate(180 - tank.getRotation)
                x = 16 * scala.math.round(x / 16)
            case Input.KEY_LEFT =>
                moving = true
                moveDirection = MoveDirection.Left
                tank.rotate(270 - tank.getRotation)
                y = 16 * scala.math.round(y / 16)
            case Input.KEY_RIGHT =>
                moving = true
                moveDirection = MoveDirection.Right
                tank.rotate(90 - tank.getRotation)
                y = 16 * scala.math.round(y / 16)
            case Input.KEY_SPACE =>
                if(!bullets.contains(playerId)) {
                    bullets(playerId) = Bullet(playerId, x, y, moveDirection)
                }
            case _ =>
        }
    }

    override def keyReleased(key: Int, c: Char) {
        (key, moveDirection) match {
            case (Input.KEY_UP, MoveDirection.Up) => moving = false 
            case (Input.KEY_DOWN, MoveDirection.Down) => moving = false
            case (Input.KEY_LEFT, MoveDirection.Left) => moving = false
            case (Input.KEY_RIGHT, MoveDirection.Right) => moving = false
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
