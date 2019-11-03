package top.Uaa.snakeExamen

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import top.Uaa.snakeExamen.R
import top.Uaa.snakeExamen.Interfaces.OnCrashListener
import top.Uaa.snakeExamen.Interfaces.OnEatenFoodListener

class MainActivity : AppCompatActivity() {
    //Puntaje del juego
    var point = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Configuracion cuando coma la serpiente
        snake.eatenListener = object : OnEatenFoodListener {
            override fun onEaten() {
                point += 1
                tvPoint.text = point.toString()
            }
        }

        snake.crashListener = object : OnCrashListener {
            override fun onCrash() {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("The snake has dead!")
                        .setMessage("Which are you want to do?")
                        .setPositiveButton("Restart", DialogInterface.OnClickListener { dialog, which ->
                            snake.restart()
                            dialog.dismiss()
                        })
                        .setNegativeButton("Exit game", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }).show()
            }

        }
    }

    fun start(view: View) {
        if (!snake.isStarted) {
            snake.start()
        }
    }

    fun move(view: View) {
        if (!snake.isStarted) {
            Toast.makeText(this, "Please click start button!", Toast.LENGTH_SHORT).show()
            return
        }

        when (view.id) {
            R.id.btnUp -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_UP
            R.id.btnDown -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_DOWN
            R.id.btnLeft -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_LEFT
            R.id.btnRight -> snake.direction = SnakeGameView.DIRECTION.DIRECTION_RIGHT
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        snake.isRunning = false
    }
}
