package com.example

import org.newdawn.slick.geom.{Vector2f, Shape}
import org.newdawn.slick.Graphics

trait Entity {
    
    val shape: Shape
    
    def collidesWith(e: Entity): Boolean
    def draw(g: Graphics)

    def getTopRight: Vector2f = new Vector2f(shape.getMaxX, shape.getMinY)
    def getTopLeft: Vector2f = new Vector2f(shape.getMinX, shape.getMinY)
    def getBottomRight: Vector2f = new Vector2f(shape.getMaxX, shape.getMaxY)
    def getBottomLeft: Vector2f = new Vector2f(shape.getMinX, shape.getMaxY)
    
    def getCorners = List(getBottomLeft, getBottomRight, getTopLeft, getTopRight)
    
}
