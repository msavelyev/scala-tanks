package com.example.block

import scala.util.control.Breaks._
import org.newdawn.slick.geom._
import org.newdawn.slick.{Color, Graphics}
import com.example._
import block.BrickWall.WallMask

class BrickWall(pos: Vector2f, val shapes: List[Shape], val mask: WallMask) extends Block(Block.WALL, pos) with Loggable {
    
    override val shape: Shape = null

    def this(pos: Vector2f, mask: WallMask) =
        this(
            pos,
            mask.createShapes(pos),
            mask
        )
    
    def this(pos: Vector2f) =
        this(
            pos,
            WallMask.FULL
        )
    
    def draw(g: Graphics) {
        g.setColor(Color.white)
        
        for(shape <- shapes) {
            ShapeRenderer.textureFit(shape, Images.i.WALL, .5f, .5f)
        }
    }


    override def collidesWith(e: Entity): Boolean = {
        val intersected: List[Shape] = shapes.filter(s => s.intersects(e.shape))
        
        INFO << "----------------------------------------"
        for(shape <- intersected) {
            INFO << "intersected with " + shape.getX + ";" + shape.getY
        }
        
        intersected.nonEmpty
    }

    override def damage(bullet: Bullet): Block = {
        val newWallMask = mask.boom(bullet.direction)
        new BrickWall(pos, newWallMask.createShapes(pos), newWallMask)
    }
    
}

object BrickWall {
    class WallMask(val mask: Array[Array[Int]]) extends Loggable {
        def boom(direction: Direction.Direction): WallMask = {
            direction match {
                case Direction.Down =>
                    withoutTopRow()
                case Direction.Up =>
                    flip().withoutTopRow().flip()
                case Direction.Left =>
                    transpose().flip().withoutTopRow().flip().transpose()
                case Direction.Right =>
                    transpose().withoutTopRow().transpose()
            }
        }
        
        override def toString = {
            var result = ""
            for(i <- 0 until mask.length) {
                result += "["
                for(j <- 0 until mask.length) {
                    result += mask(i)(j) + ","
                }
                result += "],"
            }
            
            result
        }
        
        private def transpose() = {
            val newMask: Array[Array[Int]] = Array.ofDim(4, 4)
            for(i <- 0 until mask.length) {
                for(j <- 0 until mask.length) {
                    newMask(i)(j) = mask(j)(i)
                    newMask(j)(i) = mask(i)(j)
                }
            }
            
            val result = new WallMask(newMask)
            INFO << "transposed " + this + " into " + result
            result
        }
        
        private def flip() = {
            val newMask: Array[Array[Int]] = Array.ofDim(4, 4)
            for(i <- 0 until mask.length) {
                newMask(mask.length - 1 - i) = mask(i)
            }
            
            val result = new WallMask(newMask)
            INFO << "flipped " + this + " into " + result
            result
        }
        
        private def withoutTopRow() = {
            val newMask: Array[Array[Int]] = Array.ofDim(4, 4)
            for(i <- 0 until mask.length) {
                for(j <- 0 until mask.length) {
                    newMask(i)(j) = mask(i)(j)
                }
            }
            
            breakable {
                for(i <- 0 until newMask.length) {
                    if(newMask(i).contains(1)) {
                        newMask(i) = WallMask.ZERO_ROW
                        break()
                    }
                }
            }
            
            val result = new WallMask(newMask)
            INFO << "withoutTopRow " + this + " into " + result
            result        }
        
        def createShapes(pos: Vector2f) = {
            var shapes = List[Shape]()
            for(i <- 0 until mask.length) {
                for(j <- 0 until mask.length) {
                    if(mask(i)(j) == 1) {
                        val x = math.round(pos.x + j * 8).toFloat
                        val y = math.round(pos.y + i * 8).toFloat
                        INFO << "shape at " + x + ";" + y
                        val shape = new Rectangle(x, y, 8f, 8f)
                        shapes = shape :: shapes
                    }
                }
            }
            
            shapes
        }
    }
    
    object WallMask {
        val FULL = new WallMask(
            Array(
                Array(1, 1, 1, 1),
                Array(1, 1, 1, 1),
                Array(1, 1, 1, 1),
                Array(1, 1, 1, 1)
            )
        )
        
        val ZERO_ROW = Array(0, 0, 0, 0)
    }
}
