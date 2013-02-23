package com.example

import org.newdawn.slick.geom.{Vector2f, Rectangle, Shape}
import org.newdawn.slick.{Color, Graphics}

class Tank(
    val playerId: Int,
    val pos: Vector2f,
    val direction: Direction.Direction,
    val moving: Boolean
) extends Entity {
    
    val picture = Images.i.TANK.copy()
    
    def this(playerId: Int, pos: Vector2f) = this(playerId, pos, Direction.Up, false)
    
    val shape: Shape = new Rectangle(pos.x, pos.y, World.BLOCK_SIZE, World.BLOCK_SIZE)
    
    def collidesWith(e: Entity): Boolean = shape.intersects(e.shape)
    
    def rotate(direction: Direction.Direction) = Tank.rotate(this, direction)
    def startMoving = Tank.startMoving(this)
    def stopMoving = Tank.stopMoving(this)
    def fixHorizontally = Tank.setPosition(
        this,
        new Vector2f(World.HALF_BLOCK_SIZE * scala.math.round(pos.x / World.HALF_BLOCK_SIZE), pos.y)
    )
    def fixVertically = Tank.setPosition(
        this,
        new Vector2f(pos.x, World.HALF_BLOCK_SIZE * scala.math.round(pos.y / World.HALF_BLOCK_SIZE))
    )
    

    def draw(g: Graphics) {
        g.setColor(Color.white)
        picture.rotate(Helper.angleByDirection(direction) - picture.getRotation)
        picture.draw(pos.x, pos.y)

        g.setColor(Color.red)
        g.drawRect(pos.x, pos.y, World.BLOCK_SIZE, World.BLOCK_SIZE)
    }
    
    def move(delta: Int) = Tank.move(this, delta)
    
}

object Tank {
    val SPEED = 0.1f
    
    def apply(playerId: Int, pos: Vector2f) = new Tank(playerId, pos)
    
    def setPosition(tank: Tank, pos: Vector2f) = new Tank(tank.playerId, pos, tank.direction, tank.moving)
    
    def rotate(tank: Tank, direction: Direction.Direction) =
        new Tank(tank.playerId, tank.pos, direction, true)
    def move(tank: Tank, delta: Int) = {
        if(tank.moving) {
            new Tank(tank.playerId, Helper.move(tank.pos, SPEED, delta, tank.direction), tank.direction, true)
        } else {
            tank
        }
    }
    def startMoving(tank: Tank) = new Tank(tank.playerId, tank.pos, tank.direction, true)
    def stopMoving(tank: Tank) = new Tank(tank.playerId, tank.pos, tank.direction,false)
}