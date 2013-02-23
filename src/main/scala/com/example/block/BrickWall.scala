package com.example.block

import org.newdawn.slick.geom.{Rectangle, Shape, Transform, Vector2f}
import org.newdawn.slick.Graphics
import com.example._

class BrickWall(pos: Vector2f, override val shape: Shape) extends Block(Block.WALL, pos) with Loggable {
    
    def this(pos: Vector2f) = this(pos, new Rectangle(pos.x, pos.y, World.BLOCK_SIZE + 1, World.BLOCK_SIZE + 1))
    
    def draw(g: Graphics) {
        defaultDraw(g, Images.i.WALL)
    }

    override def damage(bullet: Bullet): Block = {
        val subtract: Array[Shape] = shape.subtract(bullet.shape)
        INFO << "substracted size " + subtract.length
        new BrickWall(pos, subtract(0))
    }
}
