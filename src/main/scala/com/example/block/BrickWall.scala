package com.example.block

import scala.util.control.Breaks._
import org.newdawn.slick.geom._
import org.newdawn.slick.Graphics
import com.example._
import block.BrickWall.WallMask

class BrickWall(pos: Vector2f, override val shape: Shape, val mask: WallMask) extends Block(Block.WALL, pos) with Loggable {
    
    def this(pos: Vector2f) =
        this(
            pos,
            new Rectangle(pos.x, pos.y, World.BLOCK_SIZE + 1, World.BLOCK_SIZE + 1),
            WallMask.FULL
        )
    
    def this(pos: Vector2f, mask: WallMask) = {
        this(pos, mask.createShape(pos), mask)
    }
    
    def draw(g: Graphics) {
        defaultDraw(g, Images.i.WALL)
    }

    override def damage(bullet: Bullet): Block = {
        val newWallMask = mask.boom(bullet.direction)
        new BrickWall(pos, newWallMask.createShape(pos), newWallMask)
    }
    
}

object BrickWall {
    def apply(pos: Vector2f, mask: WallMask, direction: Direction.Direction) = {
        
        
        new BrickWall(pos)
    }

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
            result
        }
        
        def createShape(pos: Vector2f) = {
            var shapes = List[Shape]()
            for(i <- 0 until mask.length) {
                for(j <- 0 until mask.length) {
                    if(mask(i)(j) == 1) {
                        val x = pos.x + j * 8
                        val y = pos.y + i * 8
                        INFO << "shape at " + x.formatted("%.3f") + ";" + y.formatted("%.3f")
                        val shape = new Rectangle(x, y, 9f, 9f)
                        shapes = shape :: shapes
                    }
                }
            }
            
            if(shapes.nonEmpty) {
                var result = shapes.head
                val t = shapes.tail
                for(shape <- t) {
                    val union: Array[Shape] = result.union(shape)
                    if(union.length > 0) {
                        result = union(0)
                    }
                    if(union.length > 1) {
                        INFO << "union.length = " + union.length
                    }
                }
                
                result
            } else {
                new Rectangle(-World.BLOCK_SIZE, -World.BLOCK_SIZE, 0, 0) 
            }
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
