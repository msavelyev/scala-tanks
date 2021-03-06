package com.example

import block.{Border, BrickWall, Empty, Block}
import org.newdawn.slick.geom.{Polygon, Shape, ShapeRenderer, Vector2f}
import org.newdawn.slick.{Color, Graphics}

class World(val width: Float, val height: Float) extends Loggable {
    
    private val initWorld = Array(
        Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Array(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1),
        Array(0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1),
        Array(0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1),
        Array(0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1),
        Array(0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1),
        Array(0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1),
        Array(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1)
    )
    
    val alwaysCollide = new Border(new Vector2f()) {
        override def collidesWith(e: Entity): Boolean = true
    }
    
    private val worldWidthInBlocks = math.ceil(width / World.BLOCK_SIZE).toInt
    private val worldHeightInBlocks = math.ceil(height / World.BLOCK_SIZE).toInt

    val realWorld: Array[Array[Block]] = Array.ofDim(worldWidthInBlocks, worldHeightInBlocks)
    
    for(x <- 0 until worldWidthInBlocks) {
        for(y <- 0 until worldHeightInBlocks) {
            val rX = x * World.BLOCK_SIZE
            val rY = y * World.BLOCK_SIZE

            realWorld(x)(y) = 
                if(x < initWorld.length && y < initWorld(x).length && initWorld(y)(x) == 1) {
                    new BrickWall(new Vector2f(rX, rY))
                } else {
                    new Empty(new Vector2f(rX, rY))
                }
        }
    }
    
    def updateBlock(block: Block) {
        val iX = math.floor(block.x / World.BLOCK_SIZE).toInt
        val iY = math.floor(block.y / World.BLOCK_SIZE).toInt
        realWorld(iX)(iY) = block
    }
    
    def collidesWith(e: Entity): Boolean = {
        getColliders(e).nonEmpty
    }
    
    def getColliders(e: Entity): List[Block] = {
        e.getCorners.map(v => {
            val iX = math.floor(v.x / World.BLOCK_SIZE).toInt
            val iY = math.floor(v.y / World.BLOCK_SIZE).toInt

            if(iX < 0 || iY < 0 || iX >= worldWidthInBlocks || iY >= worldHeightInBlocks) {
                alwaysCollide
            } else {
                realWorld(iX)(iY)
            }
        }).filter(b => b.collidesWith(e))
    }
    
    def draw(g: Graphics) {
        g.setColor(new Color(.9f, .9f, .9f, .2f))
        for (x <- 0 to math.ceil(width / World.Q_BLOCK_SIZE).toInt) {
            g.drawLine(x * World.Q_BLOCK_SIZE, 0, x * World.Q_BLOCK_SIZE, height)
            if (x % 2 == 0) {
                g.drawLine(x * World.Q_BLOCK_SIZE, 0, x * World.Q_BLOCK_SIZE, height)
            }
        }
        for (y <- 0 to math.ceil(height / World.Q_BLOCK_SIZE).toInt) {
            g.drawLine(0, y * World.Q_BLOCK_SIZE, width, y * World.Q_BLOCK_SIZE)
            if (y % 2 == 0) {
                g.drawLine(0, y * World.Q_BLOCK_SIZE, width, y * World.Q_BLOCK_SIZE)
            }
        }
        
        g.setColor(Color.white)
        for (x <- 0 until realWorld.length) {
            for (y <- 0 until realWorld(x).length) {
                realWorld(x)(y).draw(g)
            }
        }
    }

}

object World {
    val BLOCK_SIZE = 32f
    val HALF_BLOCK_SIZE = BLOCK_SIZE / 2
    val Q_BLOCK_SIZE = HALF_BLOCK_SIZE / 2
}
