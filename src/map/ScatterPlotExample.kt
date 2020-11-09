import map.Coordinate
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * @author imssbora
 */
class ScatterPlotExample(title: String?) : JFrame(title) {
    private fun getSpiralCoordinate(): List<Coordinate> {
        val spiralCoordinates = ArrayList<Coordinate>()
        val r = 0.5 / Math.PI
        val d = 1.0
        for (n in 0..500) {
            val t = sqrt(2.0 * d * n / r)
            val rt = r * t

            val x =  rt * cos(t)
            val y =  rt * sin(t)
            spiralCoordinates.add(Coordinate(x, y))
        }
        return spiralCoordinates
    }
    private fun createDataset(): XYDataset {
        val dataset = XYSeriesCollection()
        val series = XYSeries("Spiral")
        for (coordinate in getSpiralCoordinate()) {
            series.add(coordinate.x, coordinate.y)
        }
        dataset.addSeries(series)
        return dataset
    }

    companion object {
        private const val serialVersionUID = 6294689542092367723L

        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                val example = ScatterPlotExample("Archimedian Spiral")
                example.setSize(800, 400)
                example.setLocationRelativeTo(null)
                example.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
                example.isVisible = true
            }
        }
    }

    init {

        // Create dataset
        val dataset: XYDataset = createDataset()

        // Create chart
        val chart: JFreeChart = ChartFactory.createScatterPlot(
                "Archimedian Spiral",
                "X-Axis", "Y-Axis", dataset,
                PlotOrientation.HORIZONTAL, false, false, false)


        //Changes background color
        val plot: XYPlot = chart.getPlot() as XYPlot
        plot.setBackgroundPaint(Color(255, 228, 196))


        // Create Panel
        val panel = ChartPanel(chart)
        contentPane = panel
    }
}