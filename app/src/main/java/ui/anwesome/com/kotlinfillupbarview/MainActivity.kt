package ui.anwesome.com.kotlinfillupbarview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.fillupbarview.FillUpBarView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FillUpBarView.create(this)
    }
}
