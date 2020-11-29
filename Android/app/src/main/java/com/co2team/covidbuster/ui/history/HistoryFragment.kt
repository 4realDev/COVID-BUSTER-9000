package com.co2team.covidbuster.ui.history

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.co2team.covidbuster.R
import com.co2team.covidbuster.model.RoomCo2Data
import com.co2team.covidbuster.service.BackendService
import com.co2team.covidbuster.service.OnDataReceivedCallback
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class HistoryFragment : Fragment(), OnChartValueSelectedListener {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel
    private lateinit var co2LineChart: LineChart

    private lateinit var lastTimeUpdatedTime: TextView
    private lateinit var lastTimeUpdatedDate: TextView
    private lateinit var lastTimeUpdatedCO2Value: TextView
    private lateinit var lastTimeUpdatedSafetyStatus: TextView

    // Constants for limit lines
    private val limitLineDangerThreshold = 395.0f
    private val limitLineWarningThreshold = 375.0f
    private val limitLineSafeThreshold = 325.0f

    // "T" is used to split date from time inside a String -> 2007-12-03T10:15:30
    private val localDateTimeDelimiter = "T"

    private val backendService = BackendService()
    private val chartData = ArrayList<RoomCo2Data>()
    private val roomLabelList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.history_fragment, container, false)

        co2LineChart = view.findViewById(R.id.co2LineChart)
        lastTimeUpdatedTime = view.findViewById(R.id.lastTimeUpdatedTimeTv)
        lastTimeUpdatedDate = view.findViewById(R.id.lastTimeUpdatedDateTv)
        lastTimeUpdatedCO2Value = view.findViewById(R.id.lastTimeUpdatedCo2ValueTv)
        lastTimeUpdatedSafetyStatus = view.findViewById(R.id.lastTimeUpdatedSafetyStatusTv)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel

        // TODO: insert the correct room ID
        backendService.readCo2MeasurementsForRoom(1, object : OnDataReceivedCallback {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(roomCo2Data: List<RoomCo2Data>) {
                chartData.addAll(roomCo2Data)

                val data = createOrLoadLineData()
                val set = createOrLoadLineDataSet(data)

                for (roomData in roomCo2Data) {
                    println("I have new history data from the backend. Co2: " + chartData.first().co2ppm + " on date: " + roomData.created)
                    val yAxisRepresentingCo2Ppm = roomData.co2ppm.toFloat()

                    val roomDataCreatedDate: String = roomData.created.toString().substringAfter(localDateTimeDelimiter)
                    roomLabelList.add(roomDataCreatedDate)
                    co2LineChart.xAxis.valueFormatter = IndexAxisValueFormatter(roomLabelList)

                    val entry = Entry(set.entryCount.toFloat(), yAxisRepresentingCo2Ppm)
                    co2LineChart.data.addEntry(entry, 0)
                }

                lastTimeUpdatedDate.text = getString(R.string.history_fragment_last_time_updated_time) + " " + roomCo2Data.last().created.toString().substringBefore(localDateTimeDelimiter)
                lastTimeUpdatedTime.text = getString(R.string.history_fragment_last_time_updated_date) + " " + roomCo2Data.last().created.toString().substringAfter(localDateTimeDelimiter)
                lastTimeUpdatedCO2Value.text = getString(R.string.history_fragment_last_time_updated_co2_value) + " " + roomCo2Data.last().co2ppm.toString() + " ppm"

                setColorAndSafetyStatusAccordingLatestCo2Measure(roomCo2Data.last().co2ppm.toFloat(), co2LineChart.data.getDataSetByIndex(0) as LineDataSet?)

                co2LineChart.data.notifyDataChanged()
                co2LineChart.notifyDataSetChanged()
                co2LineChart.moveViewToX(co2LineChart.data.entryCount - 7.toFloat())
            }
        })

        setupLineChart()
        setupAxis()
        setupLimitLines()
        setupLegend()
    }

    private fun createOrLoadLineData(): LineData {
        var data = co2LineChart.data

        // if LineData object has no data, create a new empty one and set it to the line graph
        if (data == null) {
            data = LineData()
            co2LineChart.data = data
        }
        return data
    }

    private fun createOrLoadLineDataSet(data: LineData): LineDataSet {
        var set = data.getDataSetByIndex(0) as LineDataSet? // take the data set from the LineData object

        // if LineData has no DataSet yet, create a new empty one and add it to the LineData object
        if (set == null) {
            set = LineDataSet(null, null)
            setupLineDataSet(set)
            data.addDataSet(set)
        }
        return set
    }

    private fun setupLineChart() {
        co2LineChart.description.isEnabled = false
        co2LineChart.setNoDataText(getString(R.string.history_fragment_no_data_text))
        co2LineChart.setBackgroundColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.colorPrimary))

        co2LineChart.setTouchEnabled(true)  // enable touch gestures

        // disabling scaling (default is enable)
        // if using scaling is recommended to use also "co2LineChart.setPinchZoom(true);" for avoiding scaling x and y separately
        co2LineChart.setScaleEnabled(false)
        co2LineChart.isDragEnabled = true

        // enable value highlighting
        co2LineChart.isHighlightPerTapEnabled = true
        co2LineChart.isHighlightPerDragEnabled = true

        co2LineChart.setExtraOffsets(0f, 0f, 0f, 16f)   // spacing between x axis and legend

        co2LineChart.animateX(2000)
        co2LineChart.invalidate()   // refresh the drawing
    }

    private fun setupLineDataSet(lineDataSet: LineDataSet) {
        lineDataSet.lineWidth = 2.5f
        lineDataSet.color = ContextCompat.getColor(requireActivity().applicationContext, R.color.colorAccent)

        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2f
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT  // show only the left y axis

        lineDataSet.setDrawCircles(true)
        lineDataSet.circleRadius = 4.5f
        lineDataSet.setCircleColor(ContextCompat.getColor(requireActivity().applicationContext, R.color.colorAccent))
        lineDataSet.setDrawCircleHole(false)

        lineDataSet.highlightLineWidth = 1f
        lineDataSet.highLightColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.colorAccent)

        lineDataSet.valueTextSize = 6f  // make value text invisible

        // fill line underneath
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillFormatter = IFillFormatter { _, _ -> co2LineChart.axisLeft.axisMinimum }
        val drawable = ContextCompat.getDrawable(requireActivity().applicationContext, R.drawable.fade_accent_safe)
        lineDataSet.fillDrawable = drawable
    }

    private fun setupAxis() {
        val rightAxis = co2LineChart.axisRight
        rightAxis.isEnabled = false // disable right axis
        val leftAxis = co2LineChart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.textSize = 10f
        leftAxis.setDrawAxisLine(false) // remove y axis line

        leftAxis.xOffset = 12f  // space between y axis and y axis value labels

        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawLimitLinesBehindData(true)

        val bottomAxis = co2LineChart.xAxis
        bottomAxis.textColor = Color.WHITE
        bottomAxis.textSize = 10f
        bottomAxis.setDrawAxisLine(false)

        // starts immediately with six empty x values
        bottomAxis.spaceMin = 0f
        bottomAxis.spaceMax = 6f
        bottomAxis.setDrawGridLines(false)
        bottomAxis.enableGridDashedLine(10f, 10f, 0f)
        bottomAxis.setDrawLimitLinesBehindData(true)
        bottomAxis.position = XAxis.XAxisPosition.BOTTOM
    }

    private fun setupLimitLines() {
        co2LineChart.axisLeft.removeAllLimitLines()
        val dangerLimit = LimitLine(limitLineDangerThreshold)
        dangerLimit.lineWidth = 1f
        dangerLimit.lineColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_danger_zone_red)

        val warningLimit = LimitLine(limitLineWarningThreshold)
        warningLimit.lineWidth = 1f
        warningLimit.lineColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_warning_zone_yellow)

        val safeLimit = LimitLine(limitLineSafeThreshold)
        safeLimit.lineWidth = 1f
        safeLimit.lineColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_safe_zone_green)

        co2LineChart.axisLeft.addLimitLine(dangerLimit)
        co2LineChart.axisLeft.addLimitLine(warningLimit)
        co2LineChart.axisLeft.addLimitLine(safeLimit)
    }

    private fun setupLegend() {
        // get the legend (only possible after setting data)
        val l = co2LineChart.legend
        l.textColor = Color.WHITE
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.isWordWrapEnabled = true
        l.xEntrySpace = 16f // spacing between entries
        l.yOffset = 12f     // spacing under legend

        // setup custom legend entries
        val dangerZoneLegend = LegendEntry(getString(R.string.history_fragment_danger_label), Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_danger_zone_red))
        val warningZoneLegendEntry = LegendEntry(getString(R.string.history_fragment_warning_label), Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_warning_zone_yellow))
        val safeZoneLegendEntry = LegendEntry(getString(R.string.history_fragment_safe_label), Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_safe_zone_green))
        l.setCustom(arrayOf(dangerZoneLegend, warningZoneLegendEntry, safeZoneLegendEntry))
    }

    @SuppressLint("SetTextI18n")
    private fun setColorAndSafetyStatusAccordingLatestCo2Measure(co2Measure: Float, set: LineDataSet?) {
        val drawable: Drawable
        val colorId: Int
        val safetyStatus: String
        when {
            co2Measure > limitLineDangerThreshold -> {
                drawable = ContextCompat.getDrawable(requireActivity().applicationContext, R.drawable.fade_accent_danger)!!
                colorId = ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_danger_zone_red)
                safetyStatus = getString(R.string.history_fragment_danger_label)
            }
            co2Measure > limitLineWarningThreshold -> {
                drawable = ContextCompat.getDrawable(requireActivity().applicationContext, R.drawable.fade_accent_warning)!!
                colorId = ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_warning_zone_yellow)
                safetyStatus = getString(R.string.history_fragment_warning_label)
            }
            else -> {
                drawable = ContextCompat.getDrawable(requireActivity().applicationContext, R.drawable.fade_accent_safe)!!
                colorId = ContextCompat.getColor(requireActivity().applicationContext, R.color.covidbuster_safe_zone_green)
                safetyStatus = getString(R.string.history_fragment_safe_label)
            }
        }
        set?.fillDrawable = drawable
        set?.color = colorId
        set?.setCircleColor(colorId)
        lastTimeUpdatedSafetyStatus.text = getString(R.string.history_fragment_last_time_updated_safety_status) + " " + safetyStatus
        lastTimeUpdatedSafetyStatus.setTextColor(colorId)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Toast.makeText(requireActivity().applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected() {}
}