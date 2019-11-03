package top.Uaa.snakeExamen

import android.graphics.Color
import android.graphics.Paint

/**
 * Created by IT on 8/15/2018.
 */
object SnakeGamePaint {
    /**
     * Zona para pintar las cosas
     * */
    val snakeBodyPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    /**
     * Pintar la cabeza de la serpiente
     * */
    val snakeHeaderPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.BLUE
    }
    /**
     * Color de la comida
     * */
    val foodPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.RED
    }

    /**
     * Paredes
     * */
    val wallPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.BLACK
    }
}