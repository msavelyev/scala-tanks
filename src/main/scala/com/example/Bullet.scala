package com.example

import org.newdawn.slick.geom.{Vector2f, Rectangle}
import org.newdawn.slick.Graphics

class Bullet(val playerId: Int, val pos: Vector2f, val direction: Direction.Direction) extends Entity {
    
    val x = pos.x
    val y = pos.y
    
    val picture = Images.i.BULLET.copy()
    picture.rotate(Helper.angleByDirection(direction) - picture.getRotation)
    
    val shape = new Rectangle(pos.x + 13, pos.y + 12, 6, 8)
    
    def move(delta: Int) = Bullet.move(this, delta)
    
    override def toString = {
        pos.x.formatted("%.3f") + ";" + pos.y.formatted("%.3f") 
    }

    def collidesWith(e: Entity): Boolean = {
        shape.intersects(e.shape)
    }

    def draw(g: Graphics) {
        picture.draw(pos.x, pos.y)
    }
}

object Bullet {
    val SPEED = 0.2f
    
    def apply(playerId: Int, tank: Tank) =
        new Bullet(playerId, tank.pos, tank.direction)
    
    def move(bullet: Bullet, delta: Int) =
        new Bullet(
            bullet.playerId,
            Helper.move(bullet.pos, SPEED, delta, bullet.direction),
            bullet.direction
        )
}