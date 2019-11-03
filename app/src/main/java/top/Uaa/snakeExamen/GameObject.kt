package top.Uaa.snakeExamen

import android.graphics.Canvas
import android.graphics.Paint

/**
 * clase principal de todos los juegos en el juego Snake, todos los objetos del juego heredarán de esta clase
 * */
open class GameObject(var row: Int, var column: Int) {

    /**
     * Dibujar objetos del juego

     * @param canvas Objeto de lona
     * @param paint Objeto de pincel

     * */
    open fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        canvas.drawRect(x, y, x + SnakeGameConfiguration.GRID_WIDTH, y + SnakeGameConfiguration.GRID_HEIGHT, paint)
    }
}