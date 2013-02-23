package com.example.block

import org.newdawn.slick.geom.{ShapeRenderer, Vector2f, Rectangle, Shape}
import org.newdawn.slick.{Image, Color, Graphics}
import com.example.{World, Direction, Entity}

abstract class Block(val blockType: Int, val pos: Vector2f) extends Entity {
    
    val shape: Shape = new Rectangle(pos.x, pos.y, World.BLOCK_SIZE + 1, World.BLOCK_SIZE + 1)

    def collidesWith(e: Entity): Boolean = shape.intersects(e.shape)

    def draw(g: Graphics)
    
    protected def defaultDraw(g: Graphics, texture: Image) {
        g.setColor(Color.white)
        ShapeRenderer.textureFit(shape, texture, 2f, 2f)
    }
    
    def damage(direction: Direction.Direction) { }
}

object Block {
    val EMPTY = 0
    val WALL = 1
    val BORDER = 2
}
