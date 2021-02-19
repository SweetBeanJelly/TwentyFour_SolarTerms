package sweetbeanjelly.project.twentyfour_solarterms

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val PageWeatherMain = WeatherMainActivity()
    private val PageTwentyFour = TwentyFourMainActivity()

    var navigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.frame, PageWeatherMain).commit()

        navigation = findViewById(R.id.bottom_navigation)
        navigation!!.setOnNavigationItemSelectedListener(ItemSelectedListener())
    }

    inner class ItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

            when (menuItem.itemId) {
                R.id.navigation_1 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, PageWeatherMain).commit()
                }
                R.id.navigation_2 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, PageTwentyFour).commit()
                }
                R.id.navigation_3 -> {
                }
            }
            return true
        }
    }
}

