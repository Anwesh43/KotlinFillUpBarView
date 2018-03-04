package ui.anwesome.com.fillupbarview

/**
 * Created by anweshmishra on 05/03/18.
 */
import android.content.*
import android.view.*
import android.graphics.*
class FillUpBarView(ctx : Context, var n : Int = 5) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas : Canvas) {

    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class State(var j : Int = 0, var prevScale : Float = 0f, var dir : Int = 0) {
        val scales : Array<Float> = arrayOf(0f, 0f)
        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if(Math.abs(scales[j] - prevScale) > 1) {
                prevScale = scales[j] + dir
                j += dir
                if(j == scales.size || j == -1) {
                    j -= dir
                    dir = 0
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0) {
                dir = (1 - 2 * prevScale.toInt())
                startcb()
            }
        }
    }
    data class ContainerState(var n : Int, var j : Int = 0, var dir : Int = 1) {
        fun incrementCounter() {
            j += dir
            if(j == n || j == -1) {
                dir *= -1
                j += dir
            }
        }
    }
}