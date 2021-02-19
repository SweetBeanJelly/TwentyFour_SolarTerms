package sweetbeanjelly.project.twentyfour_solarterms

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory


class TwentyFourMainActivity : Fragment() {

    private val calendar: Calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)
    private var month = (calendar.get(Calendar.MONTH) + 1).toString()
    private var today = calendar.get(Calendar.DATE).toString() // 요일
    private var week = calendar.get(Calendar.DAY_OF_WEEK)

    lateinit var txt_tfName1: TextView
    lateinit var txt_tfName2: TextView
    lateinit var txt_tfTitle: TextView
    lateinit var txt_timeout1: TextView
    lateinit var txt_timeout2: TextView

    private val job = SupervisorJob()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.twentyfour_main, container, false)

        txt_tfName1 = view.findViewById(R.id.txt_tfName1)
        txt_tfName2 = view.findViewById(R.id.txt_tfName2)
        txt_tfTitle = view.findViewById(R.id.txt_tfTitle)
        txt_timeout1 = view.findViewById(R.id.txt_timeout1)
        txt_timeout2 = view.findViewById(R.id.txt_timeout2)

        // 변환
        if (month.toInt() < 10) month = "0$month"
        if (today.toInt() < 10) today = "0$today"

        // DAY_OF_WEEK
        val str = returnWeek(week)

        val text_today = view.findViewById<TextView>(R.id.txt_today)
        text_today.text = today
        val text_year_month = view.findViewById<TextView>(R.id.txt_year_month)
        text_year_month.text = "${year}년 ${month}월 $str"

        get24Divisions()
        job.cancel()

        return view
    }

    private fun returnWeek(week: Int): String {
        var str = ""
        when(week) {
            1 -> str = "일요일"
            2 -> str = "월요일"
            3 -> str = "화요일"
            4 -> str = "수요일"
            5 -> str = "목요일"
            6 -> str = "금요일"
            7 -> str = "토요일"
        }
        return str
    }

    private fun get24Divisions() = GlobalScope.launch(job) {

        var name24Index1 = "" // 1~2주 중 절기 이름
        var name24Index2 = "" // 3~4주 중 절기 이름

        var date24Index1 = "" // 1~2주 중 절기 요일
        var date24Index2 = "" // 3~4주 중 절기 요일
        var date24 = ""

        var seasonContent = ""
        var check = 0
        var count_check1 = 0
        var count_check2 = 0

        try {

            val key = "85pxiQNHO6gsxSjBFQfzN5lxPOIub30SlkWNkEvKSFjX%2BBl0sCbOltv6etE002jZB5OQkf9LFYqVZgpr2%2FivQg%3D%3D"
            val url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/get24DivisionsInfo?solYear=$year&solMonth=$month&ServiceKey=$key"

            var count = true // 1일과 가깝거나 31일과 가깝거나 확인

            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url) // xml 형태를 파싱하기 위한 코드

            val itemList = xml.getElementsByTagName("item") // 찾고자 하는 데이터가 어느 위치에 있는지

            for (i in 0 until itemList.length) {
                val n: Node = itemList.item(i)

                if (n.nodeType == Node.ELEMENT_NODE) {

                    val element = n as Element
                    val map = mutableMapOf<String, String>()

                    for (j in 0 until element.attributes.length) {
                        map.putIfAbsent(
                            element.attributes.item(j).nodeName,
                            element.attributes.item(j).nodeValue
                        )
                    }

                    if (count) {
                        name24Index1 = element.getElementsByTagName("dateName").item(0).textContent
                        count = false
                        date24 = element.getElementsByTagName("locdate").item(0).textContent
                        date24Index1 = date24.substring(6, 8)
                    } else {
                        name24Index2 = element.getElementsByTagName("dateName").item(0).textContent
                        date24 = element.getElementsByTagName("locdate").item(0).textContent
                        date24Index2 = date24.substring(6, 8)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

            when {
                today < date24Index1 -> {
                    seasonContent = "다가오는 24절기는 $name24Index1 입니다."
                    count_check1 = date24Index1.toInt() - today.toInt()
                    count_check2 = date24Index2.toInt() - today.toInt()
                    check = 1
                }
                today == date24Index1 -> {
                    seasonContent = "오늘은 $name24Index1 입니다."
                    count_check2 = date24Index2.toInt() - today.toInt()
                    check = 2
                }
                today > date24Index1 && today < date24Index2 -> {
                    seasonContent = "다가오는 24절기는 $name24Index2 입니다."
                    count_check1 = date24Index1.toInt() - today.toInt()
                    count_check2 = date24Index2.toInt() - today.toInt()
                    check = 3
                }
                today == date24Index2 -> {
                    seasonContent = "오늘은 $name24Index2 입니다."
                    count_check1 = date24Index1.toInt() - today.toInt()
                    check = 4
                }
                else -> {
                    seasonContent = "이번 달 24절기는 모두 지났습니다."
                    count_check1 = date24Index1.toInt() - today.toInt()
                    count_check2 = date24Index2.toInt() - today.toInt()
                    check = 5
                }
            }
        }

        activity!!.runOnUiThread {
            txt_tfTitle.text = "$seasonContent "
            txt_tfName1.text = "${date24Index1}일 ${name24Index1}"
            txt_tfName2.text = "${date24Index2}일 ${name24Index2}"

            when(check) {
                1 -> {
                    txt_timeout1.text = "${count_check1}일 후"
                    txt_timeout2.text = "${count_check2}일 후"
                }
                2 -> {
                    txt_timeout1.text = "TODAY"
                    txt_timeout2.text = "${count_check2}일 후"
                }
                3 -> {
                    txt_timeout1.text = "${count_check1 * -1}일 전"
                    txt_timeout2.text = "${count_check2}일 후"
                }
                4 -> {
                    txt_timeout1.text = "${count_check1 * -1}일 전"
                    txt_timeout2.text = "TODAY"
                }
                5 -> {
                    txt_timeout1.text = "${count_check1 * -1}일 전"
                    txt_timeout2.text = "${count_check2 * -1}일 전"
                }
            }
        }
    }.start()

}