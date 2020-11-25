package com.co2team.covidbuster.ui.history

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.co2team.covidbuster.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class HistoryFragment : Fragment(), OnChartValueSelectedListener {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel
    private lateinit var co2LineChart: LineChart

    private lateinit var handler: Handler;
    private lateinit var addEntryRunnable: Runnable;

    // Constants for limit lines
    private val limit_line_danger_threshold = 56.0f;
    private val limit_line_warning_threshold = 55.6f;
    private val limit_line_safe_threshold = 55.2f;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.history_fragment, container, false)
        co2LineChart = view.findViewById(R.id.co2LineChart);
        return view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel

        setupLineChart()
        setupAxis()
        setupLimitLines()
        setupLegend()

        val data = LineData()
        co2LineChart.data = data
        co2LineChart.setOnChartValueSelectedListener(this)
        startAddingValues()
    }

    private fun setupLineChart() {
        co2LineChart.description.isEnabled = false
        co2LineChart.setNoDataText("Wupsi, no data")
        co2LineChart.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))

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
        lineDataSet.color = ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent)

        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2f
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT  // show only the left y axis

        lineDataSet.setDrawCircles(true)
        lineDataSet.circleRadius = 4.5f
        lineDataSet.setCircleColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent))
        lineDataSet.circleHoleColor = ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent)

        lineDataSet.highlightLineWidth = 1f
        lineDataSet.highLightColor = ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent)

        lineDataSet.valueTextSize = 0f  // make value text invisible

        // fill line underneath
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillFormatter = IFillFormatter { _, _ -> co2LineChart.axisLeft.axisMinimum }
        val drawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.fade_accent)
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
        // setup range of the y axis
        leftAxis.axisMinimum = 54.5f
        leftAxis.axisMaximum = 56.5f

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
        val danger_limit = LimitLine(limit_line_danger_threshold /*, "Danger Zone"*/)
        danger_limit.lineWidth = 1f
        danger_limit.lineColor = ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_danger_zone_red)

        val warning_limit = LimitLine(limit_line_warning_threshold)
        warning_limit.lineWidth = 1f
        warning_limit.lineColor = ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_warning_zone_yellow)

        val safe_limit = LimitLine(limit_line_safe_threshold)
        safe_limit.lineWidth = 1f
        safe_limit.lineColor = ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_safe_zone_green)

        co2LineChart.axisLeft.addLimitLine(danger_limit)
        co2LineChart.axisLeft.addLimitLine(warning_limit)
        co2LineChart.axisLeft.addLimitLine(safe_limit)
    }

    private fun setupLegend() {
        // get the legend (only possible after setting data)
        val l = co2LineChart.legend
        l.textColor = Color.WHITE
        l.isWordWrapEnabled = true
        l.xEntrySpace = 16f // spacing between entries
        l.yOffset = 12f     // spacing under legend

        // setup custom legend entries
        val data_line = LegendEntry("CO2 Data", Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent))
        val danger_zone_legend = LegendEntry("Danger", Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_danger_zone_red))
        val warning_zone_legend = LegendEntry("Warning", Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_warning_zone_yellow))
        val safe_zone_legend = LegendEntry("Safe", Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_safe_zone_green))
        l.setCustom(arrayOf(data_line, danger_zone_legend, warning_zone_legend, safe_zone_legend))
    }

    // Add entries to line chart
    private fun addEntry() {
        var data = co2LineChart.data

        // if LineData object does not exist, create one from the first Index point of the DataSet
        if (data == null) {
            data = LineData()
            co2LineChart.data = data
        }
        var set = data.getDataSetByIndex(0) as LineDataSet?
        // set.addEntry(...); // can be called as well

        // if DataSet does not exist, create one and add it to the LineData object
        if (set == null) {
            set = LineDataSet(null, "CO2 Data Set")
            setupLineDataSet(set)
            data.addDataSet(set)
        }

        // add a new random value
        val entry = Entry(set.entryCount.toFloat(), (Math.random() * 1.125f).toFloat() + 55f)

        /*
        if(entry.getY() > limit_line_danger_threshold) {
            set.setFillColor(ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_danger_zone_red));
        } else if(entry.getY() > limit_line_warning_threshold){
            set.setFillColor(ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_warning_zone_yellow));
        } else {
            set.setFillColor(ContextCompat.getColor(activity!!.applicationContext, R.color.covidbuster_safe_zone_green));
        }
         */

        data.addEntry(
                entry, 0
        )

        data.notifyDataChanged()

        // let the chart know it's data has changed
        co2LineChart.notifyDataSetChanged()
        co2LineChart.setVisibleXRangeMaximum(6f)
        //co2LineChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);

        // this automatically refreshes the chart (calls invalidate())
        co2LineChart.moveViewTo(data.entryCount - 7.toFloat(), 50f, YAxis.AxisDependency.LEFT)
    }

    private fun startAddingValues() {
        handler = Handler()
        addEntryRunnable = object : Runnable {
            override fun run() {
                addEntry()
                handler.postDelayed(this, 2500)
            }
        }
        handler.postDelayed(addEntryRunnable, 2500)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Toast.makeText(activity!!.applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected() {}

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(addEntryRunnable);
    }
}