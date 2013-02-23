package com.example


class Player(val playerId: Int, val x: Int, val y: Int) {

    def move(dx: Int, dy: Int) = {
        Player(playerId, x + dx, y + dy)
    }

}

object Player {
    def apply(playerId: Int, x: Int, y: Int) = new Player(playerId, x, y)
}
