package sweetbeanjelly.project.twentyfour_solarterms

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.weather_main.*
import org.json.JSONException
import org.json.JSONObject
import sweetbeanjelly.project.testweather.network.ApiEndpoint
import sweetbeanjelly.project.twentyfour_solarterms.adapter.MainAdapter
import sweetbeanjelly.project.twentyfour_solarterms.model.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherMainActivity : Fragment(), LocationListener {

    private var lat: Double? = null
    private var lng: Double? = null
    private var strDate: String? = null
    private var mainAdapter: MainAdapter? = null
    private val modelMain: MutableList<ModelMain> = ArrayList()
    var permissionArrays = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    lateinit var txt_date: TextView
    lateinit var txt_weather: TextView
    lateinit var txt_time: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.weather_main, container, false)

        setWindowFlag(activity!!, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setWindowFlag(activity!!, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        activity!!.window.statusBarColor = Color.TRANSPARENT
        if (checkIfAlreadyhavePermission1() && checkIfAlreadyhavePermission2()) { } else { requestPermissions(permissionArrays, 101) }

        txt_date = view.findViewById(R.id.txt_date)
        txt_weather = view.findViewById(R.id.txt_weather)
        txt_time = view.findViewById(R.id.txt_time)

        val dateNow = Calendar.getInstance().time
        strDate = DateFormat.format("EEE", dateNow) as String

        val systemTime = System.currentTimeMillis()
        val format = SimpleDateFormat("a HH시 mm분", Locale.KOREA).format(systemTime)
        txt_time.text = format

        mainAdapter = MainAdapter(modelMain)

        val rvListWeather = view.findViewById<RecyclerView>(R.id.rvListWeather)
        rvListWeather.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        rvListWeather.setHasFixedSize(true)
        rvListWeather.adapter = mainAdapter

        getToday()
        getLocation()

        return view
    }

    private fun getToday() {
        val date = Calendar.getInstance().time
        val today = DateFormat.format("yyyy년 MM월 dd일", date) as String
        val formatDate = "$today ${strDate}요일"
        txt_date.text = formatDate
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 115)
            return
        }
        val locationManager = activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = locationManager.getBestProvider(criteria, true)!!
        val location = locationManager.getLastKnownLocation(provider)
        if (location != null) onLocationChanged(location) else locationManager.requestLocationUpdates(provider, 20000, 0f, this)
    }

    override fun onLocationChanged(location: Location) {
        lng = location.longitude
        lat = location.latitude

        getListWeather()
    }

    private fun getListWeather() {
        AndroidNetworking.get(ApiEndpoint.BASEURL + ApiEndpoint.Daily + "lat=" + lat + "&lon=" + lng + ApiEndpoint.UnitsAppidDaily)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val city = response.getString("timezone")
                        val jsonArray = response.getJSONArray("daily")

                        for (i in 0 until jsonArray.length()) {
                            val dataApi = ModelMain()
                            val objectList = jsonArray.getJSONObject(i)
                            val jsonObjectOne = objectList.getJSONObject("temp")
                            val jsonArrayOne = objectList.getJSONArray("weather")
                            val jsonObjectTwo = jsonArrayOne.getJSONObject(0)
                            val strWeather = jsonObjectTwo.getString("main")
                            val strDescWeather = jsonObjectTwo.getString("description")
                            val longDate = objectList.optLong("dt")
                            val formatDate = SimpleDateFormat("dd일")
                            val readableDate = formatDate.format(Date(longDate * 1000))
                            val longDay = objectList.optLong("dt")
                            val format = SimpleDateFormat("EEEE")
                            val readableDay = format.format(Date(longDay * 1000))

                            if (city == "Asia/Seoul") txt_city.text = "부산시 기장"

                            if(i == 0) {

                                when (strDescWeather) {
                                    "broken clouds", "overcast clouds", "scattered clouds", "few clouds" -> {
                                        txt_weather.text = "흐림"
                                    }
                                    "light rain" -> {
                                        txt_weather.text = "약한 비"
                                    }
                                    "haze" -> {
                                        txt_weather.text = "안개"
                                    }
                                    "moderate rain" -> {
                                        txt_weather.text = "흐리고 비"
                                    }
                                    "heavy intensity rain" -> {
                                        txt_weather.text = "폭우"
                                    }
                                    "clear sky" -> {
                                        txt_weather.text = "맑음"
                                    }
                                    else -> {
                                        txt_weather.text = strWeather
                                    }
                                }
                                txt_temp.text = String.format(
                                    Locale.getDefault(),
                                    "%.0f°C",
                                    jsonObjectOne.getDouble("day")
                                )

                                continue
                            }

                            dataApi.nameDate = "$readableDate $readableDay"
                            dataApi.descWeather = jsonObjectTwo.getString("description")
                            dataApi.tempMin = jsonObjectOne.getDouble("min")
                            dataApi.tempMax = jsonObjectOne.getDouble("max")
                            modelMain.add(dataApi)
                        }
                        mainAdapter?.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(e: ANError) {
                    Toast.makeText(context!!, "인터넷에 연결할 수 없습니다!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkIfAlreadyhavePermission1(): Boolean {
        val result = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfAlreadyhavePermission2(): Boolean {
        val result = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                val intent = activity!!.intent
                activity!!.finish()
                startActivity(intent)
            } else {
                getLocation()
            }
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }
}