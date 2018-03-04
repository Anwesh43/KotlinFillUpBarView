package ui.anwesome.com.fillupbarview

/**
 * Created by anweshmishra on 05/03/18.
 */
import android.content.*
import android.view.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

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
    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if (animated) {
                try {
                    updatecb()
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class FillUpBar(var i : Int) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint, size : Float, w : Float, h : Float) {
            paint.color = Color.parseColor("#FF8F00")
            val origY = h - size
            val newY = i * size
            val y = origY + (newY - origY) * state.scales[1]
            canvas.drawRect(RectF(0f, y, w * state.scales[0], y + size) , paint)
        }
        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
    data class FillUpBarContainer(var n : Int) {
        val fillUpBars : ConcurrentLinkedQueue<FillUpBar> = ConcurrentLinkedQueue()
        val state = ContainerState(n)
        fun draw(canvas : Canvas, paint : Paint) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            fillUpBars.forEach {
                val size = h / n
                it.draw(canvas, paint, size, w, h)
            }
        }
        fun update(stopcb : (Float, Int) -> Unit) {
            fillUpBars.at(state.j)?.update {
                stopcb(it, state.j)
                state.incrementCounter()
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            fillUpBars.at(state.j)?.startUpdating(startcb)
        }
    }
}
fun ConcurrentLinkedQueue<FillUpBarView.FillUpBar>.at(index : Int):FillUpBarView.FillUpBar? {
    var i = 0
    forEach {
        if (i == index) {
            return it
        }
        i++
    }
    return null
}