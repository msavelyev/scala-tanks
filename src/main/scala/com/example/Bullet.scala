package com.example

import org.newdawn.slick.geom.{Vector2f, Rectangle}
import org.newdawn.slick.Graphics

class Bullet(val playerId: Int, val pos: Vector2f, val direction: Direction.Direction) extends Entity {
    
    val picture = Images.i.BULLET.copy()
    picture.rotate(Helper.angleByDirection(direction) - picture.getRotation)
    
    val shape = new Rectangle(pos.x + 13, pos.y + 12, 6, 8)
    
    def move(delta: Int) = Bullet.move(this, delta)
    
    def collidesWith(e: Entity): Boolean = {
        shape.intersects(e.shape)
    }

    def draw(g: Graphics) {
        picture.draw(pos.x, pos.y)
    }
    
    def step() = Bullet.step(this)
}

object Bullet {
    val SPEED = 0.2f
    
    def apply(playerId: Int, tank: Tank) =
        new Bullet(playerId, tank.pos, tank.direction)
    
    def step(bullet: Bullet) = {
        val v = Helper.move(bullet.pos, SPEED, 40, bullet.direction)
        new Bullet(
            bullet.playerId,
            new Vector2f(math.round(v.x), math.round(v.y)),
            bullet.direction
        )
    }
    
    def move(bullet: Bullet, delta: Int) =
        new Bullet(
            bullet.playerId,
            Helper.move(bullet.pos, SPEED, delta, bullet.direction),
            bullet.direction
        )
}