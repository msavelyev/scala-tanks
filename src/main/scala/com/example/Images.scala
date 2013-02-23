package com.example

import org.newdawn.slick.Image

class Images {
    val TANK = new Image("tank.png")
    val WALL = new Image("wall16.png")
    val BULLET = new Image("bullet.png")
}

object Images {
    
    private var images: Images = null
    
    def i = {
        if(images == null) {
            images = new Images()
        }
        
        images
    }
    
}
