package com.example

import org.newdawn.slick._
import geom.{Vector2f, ShapeRenderer, Rectangle, Shape}
import collection.mutable

class Game(gameClient: GameClient) extends BasicGame("Hello") with Loggable with KeyListener {
    
    gameClient.game = this
//    gameClient.connect()

    val scale = 1.0f
    
    var playerId = -1
    var tank: Tank = _
    var world: World = _

    val players = mutable.Map[Int, Tank]()
    val bullets = mutable.Map[Int, Bullet]()
    
    def update(container: GameContainer, delta: Int) {
        val newTank = tank.move(delta)
        
        if(!world.collidesWith(newTank)) {
            tank = newTank
        }
        
        var bulletsToRemove = List[Int]()
        bullets.foreach(pair => {
            val newBullet = pair._2.move(delta)
            
            if(world.collidesWith(newBullet)) {
                for(block <- world.getColliders(newBullet)) {
                    world.updateBlock(block.damage(newBullet.step()))
                }
                bulletsToRemove = newBullet.playerId :: bulletsToRemove
            } else {
                bullets(newBullet.playerId) = newBullet
            }
        })
        
        for(playerId <- bulletsToRemove) {
            bullets.remove(playerId)
        }
    }

    def connected(playerId: Int, x: Int, y: Int) {
        players(playerId) = Tank(playerId, new Vector2f(x * World.BLOCK_SIZE, y * World.BLOCK_SIZE))
    }

    def disconnected(playerId: Int) {
        players -= playerId
    }

    def move(playerId: Int, dx: Int, dy: Int) {
//        players(playerId) = players(playerId).move(dx, dy)
    }

    def init(container: GameContainer) {
        container.getInput.addKeyListener(this)

        tank = Tank(playerId, new Vector2f(0, 0))
        world = new World(container.getWidth, container.getHeight)
    }

    def render(container: GameContainer, g: Graphics) {
        drawBackground(g, container)
        
        world.draw(g)
        tank.draw(g)
        drawOtherPlayers(g)
        drawHud(g)
        drawBullets(g)
    }

    def drawBullets(g: Graphics) {
        for(bullet <- bullets.values) {
            bullet.draw(g)
        }
    }

    def drawBackground(g: Graphics, container: GameContainer) {
        g.setColor(Color.black)
        g.fillRect(0, 0, container.getWidth, container.getHeight)
    }

    def drawOtherPlayers(g: Graphics) {
        for (tank <- players.values) {
            tank.draw(g)
        }
    }

    def drawHud(g: Graphics) {
        g.setColor(Color.white)
        g.drawString("" + tank.x.formatted("%.3f") + ";" + tank.y.formatted("%.3f"), 10, 25)
        
        val cY = math.floor(tank.y / World.BLOCK_SIZE).toInt
        val cX = math.floor(tank.x / World.BLOCK_SIZE).toInt
        g.drawString("" + cX + ";" + cY, 10, 35)
    }

    override def keyPressed(key: Int, c: Char) {
        key match {
            case Input.KEY_UP =>
                tank = tank.rotate(Direction.Up).fixHorizontally
            case Input.KEY_DOWN =>
                tank = tank.rotate(Direction.Down).fixHorizontally
            case Input.KEY_LEFT =>
                tank = tank.rotate(Direction.Left).fixVertically
            case Input.KEY_RIGHT =>
                tank = tank.rotate(Direction.Right).fixVertically
            case Input.KEY_SPACE =>
                if(!bullets.contains(playerId)) {
                    bullets(playerId) = Bullet(playerId, tank)
                }
            case _ =>
        }
    }

    override def keyReleased(key: Int, c: Char) {
        if(Helper.sameDirection(key, tank.direction)) {
            tank = tank.stopMoving
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
