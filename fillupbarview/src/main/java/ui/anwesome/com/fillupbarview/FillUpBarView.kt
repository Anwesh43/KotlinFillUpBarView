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
}