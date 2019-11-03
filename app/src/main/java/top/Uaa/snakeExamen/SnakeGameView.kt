package top.Uaa.snakeExamen

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import top.littledavid.logerlibrary.e
import top.Uaa.snakeExamen.Interfaces.OnCrashListener
import top.Uaa.snakeExamen.Interfaces.OnEatenFoodListener
import java.util.*
import kotlin.concurrent.thread

class SnakeGameView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    /**
     * Aqui esta el arreglo de la serpiente
     * */
    private val snake = mutableListOf<SnakeBlock>()

    /**
     * comida
     * */
    private lateinit var food: Food

    /**
     * Mapa
     * */
    private lateinit var gridList: MutableList<MutableList<PointF>>


    var crashListener: OnCrashListener? = null


    var eatenListener: OnEatenFoodListener? = null


    var direction = DIRECTION.DIRECTION_RIGHT
        set(value) {//Direcciones para la serpiente
            when (value) {
                DIRECTION.DIRECTION_UP -> {
                    if (field != DIRECTION.DIRECTION_DOWN) {
                        field = value
                    }
                }
                DIRECTION.DIRECTION_DOWN -> {
                    if (field != DIRECTION.DIRECTION_UP) {
                        field = value
                    }
                }
                DIRECTION.DIRECTION_LEFT -> {
                    if (field != DIRECTION.DIRECTION_RIGHT) {
                        field = value
                    }
                }
                DIRECTION.DIRECTION_RIGHT -> {
                    if (field != DIRECTION.DIRECTION_LEFT) {
                        field = value
                    }
                }
            }
        }

    private var frequency: Long = 800

    /**
     * Boolean de inicio
     * */
    var isStarted = false
        private set

    private val random = Random()

    var isRunning = true


    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!isStarted)
            return

        drawSnake(canvas!!)
        drawFood(canvas)
    }

    private fun drawSnake(canvas: Canvas) {
        snake.forEach {
            val pointF = this.gridList[it.row][it.column]
            if (it.isHead) {
                it.draw(canvas, pointF.x, pointF.y, SnakeGamePaint.snakeHeaderPaint)
            } else {
                it.draw(canvas, pointF.x, pointF.y, SnakeGamePaint.snakeBodyPaint)
            }
        }
    }


    private fun drawFood(canvas: Canvas) {
        val pointF = this.gridList[food.row][food.column]

        food.draw(canvas, pointF.x, pointF.y,SnakeGamePaint.foodPaint)
    }


    private fun moveTo() {
        var newHeadRow = snake[0].row
        var newHeadColumn = snake[0].column
        when (this.direction) {
            DIRECTION.DIRECTION_UP -> {
                newHeadRow -= 1
            }
            DIRECTION.DIRECTION_DOWN -> {
                newHeadRow += 1
            }
            DIRECTION.DIRECTION_LEFT -> {
                newHeadColumn -= 1
            }
            DIRECTION.DIRECTION_RIGHT -> {
                newHeadColumn += 1
            }
        }

        if (food.row == newHeadRow && food.column == newHeadColumn) {
            snake[0].isHead = false

            val newHead = SnakeBlock(newHeadRow, newHeadColumn, true)
            snake.add(0, newHead)

            if (this.eatenListener != null) {
                this.eatenListener!!.onEaten()
            }
            if (frequency > 500) {
                frequency -= 50
            }
            generateFoodInRandom()
        } else {
            for (i in this.snake.size - 1 downTo 1) {
                val previous = this.snake[i - 1]
                val current = this.snake[i]
                current.row = previous.row
                current.column = previous.column

            }
            val head = snake[0]
            head.row = newHeadRow
            head.column = newHeadColumn

            if (head.row < 0
                    || head.row > SnakeGameConfiguration.GAME_ROW_COUNT - 1
                    || head.column < 0
                    || head.column > SnakeGameConfiguration.GAME_COLUMN_COUNT - 1
                    ) {
                isStarted = false
                if (this.crashListener != null) {
                    "Out of the border".e()
                    "head row ${head.row}".e()
                    "head column ${head.column}".e()
                    crashListener!!.onCrash()
                }
            }
            else if (snake.firstOrNull { it.isHead == false && it.row == head.row && it.column == head.column } != null) {
                isStarted = false
                if (this.crashListener != null) {
                    "Catch itself".e()
                    "head row ${head.row}".e()
                    "head column ${head.column}".e()
                    crashListener!!.onCrash()
                }
            }
        }

        this.invalidate()
    }
    private fun measureGameMap() {
        val w = this.width
        val h = this.height
        SnakeGameConfiguration.GRID_HEIGHT = (h / SnakeGameConfiguration.GAME_ROW_COUNT).toFloat()
        SnakeGameConfiguration.GRID_WIDTH = (w / SnakeGameConfiguration.GAME_COLUMN_COUNT).toFloat()
    }

    /*dibujado del mapa*/
    private fun generateGird() {
        this.gridList = mutableListOf()

        for (i in 0 until SnakeGameConfiguration.GAME_ROW_COUNT) {
            val tempList = mutableListOf<PointF>()
            for (j in 0 until SnakeGameConfiguration.GAME_COLUMN_COUNT) {
                val point = PointF(j * SnakeGameConfiguration.GRID_WIDTH, i * SnakeGameConfiguration.GRID_HEIGHT)
                tempList.add(point)
            }
            this.gridList.add(tempList)
        }
    }

    private fun generateFoodInRandom() {
        var row = this.random.nextInt(SnakeGameConfiguration.GAME_ROW_COUNT)
        var column = this.random.nextInt(SnakeGameConfiguration.GAME_COLUMN_COUNT)
        while (true) {
            //避免生成的食物和贪吃蛇的位置重叠
            if (this.snake.firstOrNull { it.row == row && it.column == column } == null)
                break
            row = this.random.nextInt(SnakeGameConfiguration.GAME_ROW_COUNT)
            column = this.random.nextInt(SnakeGameConfiguration.GAME_COLUMN_COUNT)
        }
        this.food = Food(row, column)
    }

    private fun generateSnake() {
        this.snake.clear()
        this.snake.add(SnakeBlock(0, 2, true))
        this.snake.add(SnakeBlock(0, 1, false))
        this.snake.add(SnakeBlock(0, 0, false))
    }


    fun start() {
        //初始化地图
        //1. 计算地图的布局
        this.measureGameMap()

        this.generateGird()
        this.generateFoodInRandom()
        this.generateSnake()

        isStarted = true
        this.invalidate()
        thread {
            while (isRunning) {
                if (isStarted) {
                    this.post {
                        moveTo()
                    }
                    SystemClock.sleep(this.frequency)
                }
            }
        }
    }


    fun restart() {
        this.generateGird()
        this.generateFoodInRandom()
        this.generateSnake()
        this.direction = DIRECTION.DIRECTION_RIGHT
        isStarted = true
        invalidate()
    }


    object DIRECTION {
        val DIRECTION_UP = 0
        val DIRECTION_DOWN = 1
        val DIRECTION_LEFT = 2
        val DIRECTION_RIGHT = 3
    }
}