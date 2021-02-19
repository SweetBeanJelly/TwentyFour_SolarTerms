package sweetbeanjelly.project.twentyfour_solarterms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.weather_list.view.*
import sweetbeanjelly.project.twentyfour_solarterms.R
import sweetbeanjelly.project.twentyfour_solarterms.model.ModelMain
import java.util.*

class MainAdapter(private val items: List<ModelMain>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]

        // random color card view
        val generator = ColorGenerator.MATERIAL
        val color = generator.randomColor
        holder.cardView.setCardBackgroundColor(color)

        holder.txt_name.text = data.nameDate
        holder.temp_min.text = String.format(Locale.getDefault(), "%.0f°C", data.tempMin)
        holder.temp_max.text = String.format(Locale.getDefault(), "%.0f°C", data.tempMax)

        when (data.descWeather) {
            "broken clouds" -> {
                holder.iconTemp.setAnimation(R.raw.broken_clouds)
            }
            "light rain" -> {
                holder.iconTemp.setAnimation(R.raw.light_rain)
            }
            "overcast clouds" -> {
                holder.iconTemp.setAnimation(R.raw.overcast_clouds)
            }
            "moderate rain" -> {
                holder.iconTemp.setAnimation(R.raw.moderate_rain)
            }
            "few clouds" -> {
                holder.iconTemp.setAnimation(R.raw.few_clouds)
            }
            "heavy intensity rain" -> {
                holder.iconTemp.setAnimation(R.raw.heavy_intentsity)
            }
            "clear sky" -> {
                holder.iconTemp.setAnimation(R.raw.clear_sky)
            }
            "scattered clouds" -> {
                holder.iconTemp.setAnimation(R.raw.scattered_clouds)
            }
            else -> {
                holder.iconTemp.setAnimation(R.raw.unknown)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView = itemView.weather_cardview
        var txt_name: TextView = itemView.txt_name
        var temp_min: TextView = itemView.txt_temp_min
        var temp_max: TextView = itemView.txt_temp_max
        var iconTemp: LottieAnimationView = itemView.iconTemp
    }
}