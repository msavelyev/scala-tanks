package com.example.block

import org.newdawn.slick.geom.Vector2f
import com.example.{Direction, Entity}
import org.newdawn.slick.Graphics

class Empty(pos: Vector2f) extends Block(Block.EMPTY, pos) {
    
    override def collidesWith(e: Entity): Boolean = false

    def draw(g: Graphics) {}
}
