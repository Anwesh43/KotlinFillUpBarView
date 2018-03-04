package ui.anwesome.com.fillupbarview

/**
 * Created by anweshmishra on 05/03/18.
 */
import android.app.Activity
import android.content.*
import android.view.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

class FillUpBarView(ctx : Context, var n : Int = 5) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }
    data class State(var j : Int = 0, var prevScale : Float = 0f, var dir : Int = 0) {
        val scales : Array<Float> = arrayOf(0f, 0f)
        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if(Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
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
        fun animate(i : Long, updatecb : () -> Unit) {
            if (animated) {
                try {
                    updatecb()
                    Thread.sleep((50 + (i - 1) * 10) / i)
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
            val origY = h - size
            val newY = i * size
            val y = origY + (newY - origY) * state.scales[1]
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#FF8F00")
            canvas.drawRect(RectF(0f, y, w * state.scales[0], y + size) , paint)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = Math.min(w, h) / 50
            paint.color = Color.parseColor("#0D47A1")
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawRect(RectF(0f, y, w , y + size) , paint)
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
        init {
            for(i in 0..n-1) {
                fillUpBars.add(FillUpBar(i))
            }
        }
        fun draw(canvas : Canvas, paint : Paint) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            var i = 0
            fillUpBars.forEach {
                val size = (0.8f * h) / n
                it.draw(canvas, paint, size, w, h)
                i++
                if(i > state.j) {
                    return
                }
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
    data class Renderer(var view : FillUpBarView) {
        val container : FillUpBarContainer = FillUpBarContainer(view.n)
        val animator : Animator = Animator(view)
        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            container.draw(canvas, paint)
            animator.animate((container.state.j+1).toLong()) {
                container.update {scale, j ->
                    animator.stop()
                }
            }
        }
        fun handleTap() {
            container.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity : Activity) : FillUpBarView {
            var view = FillUpBarView(activity)
            activity.setContentView(view)
            return view
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