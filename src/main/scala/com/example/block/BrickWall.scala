package com.example.block

import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.Graphics
import com.example.Images

class BrickWall(pos: Vector2f) extends Block(Block.WALL, pos) {
    
    def draw(g: Graphics) {
        defaultDraw(g, Images.i.WALL)
    }
    
}
