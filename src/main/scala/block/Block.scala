package block

import org.newdawn.slick.geom.{ShapeRenderer, Vector2f, Rectangle, Shape}
import org.newdawn.slick.{Color, Graphics}

class Block(val blockType: Int, val pos: Vector2f) extends Entity {
    
    val blockSize = 32f
    
    val shape: Shape = new Rectangle(pos.x, pos.y, blockSize, blockSize)

    def collidesWith(e: Entity): Boolean = {
        blockType match {
            case Block.WALL => shape.intersects(e.shape)
            case Block.BORDER => shape.intersects(e.shape)
            case _ => false
        }
    }

    def draw(g: Graphics) {
        blockType match {
            case Block.WALL =>
                g.setColor(Color.white)
                ShapeRenderer.textureFit(shape, Images.i.WALL, 2f, 2f)
            case _ =>
        }
    }
    
    def damage(direction: Direction.Direction) {
        
    }
}

object Block {
    val EMPTY = 0
    val WALL = 1
    val BORDER = 2
    
    def apply(blockType: Int, pos: Vector2f) = new Block(blockType, pos)
}
