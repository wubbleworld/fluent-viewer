package edu.isi.data;

import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XIntervalSeries;
import org.jfree.data.xy.XIntervalSeriesCollection;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a vertical bar chart.
 *
 * @author David Gilbert
 */
public class XYDemo extends ApplicationFrame {

    /**
     * Constructs the demo application.
     *
     * @param title  the frame title.
     */
    public XYDemo(String title) {

        super(title);

        double[][] starts = new double[3][1];
        double[][] ends = new double[3][1];
        
        starts[0][0] = 0; ends[0][0] = 5;
        starts[1][0] = 6; ends[1][0] = 8;
        starts[2][0] = 10; ends[2][0] = 15;

        // 6,8 12,20, 22,30
        DefaultIntervalCategoryDataset dataset = new DefaultIntervalCategoryDataset(starts, ends);
        
        final JFreeChart chart = ChartFactory.createAreaChart(
                "Fluent Timeline",  // chart title
                "Fluent",              // domain axis label
                "Tick",              // range axis label
                dataset,             // data
                PlotOrientation.VERTICAL,
                true,                // include legend
                true,                // tooltips
                false                // urls
            );
        
        //JFreeChart chart = createChart(data);
        
        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        setContentPane(chartPanel);

    }
	
	
	
//    public XYDemo(String title) {
//
//        super(title);
//
//        TaskSeries s1 = new TaskSeries("Fluent A");
//        Task g = new Task("FluentA", new Date(0), new Date(15));
//        
//        Task t1 = new Task("FluentA", new Date(0), new Date(5));
//        Task t2 = new Task("FluentA", new Date(6), new Date(8));
//        Task t3 = new Task("FluentA", new Date(10), new Date(15));
//        
//        g.addSubtask(t1);
//        g.addSubtask(t2);
//        g.addSubtask(t3);
//        
//        s1.add(g);
//        
//        TaskSeries s2 = new TaskSeries("Fluent B");
//        g = new Task("FluentB", new Date(0), new Date(30));
//        
//        t1 = new Task("FluentB", new Date(6), new Date(8));
//        t2 = new Task("FluentB", new Date(12), new Date(20));
//        t3 = new Task("FluentB", new Date(22), new Date(30));
//        
//        g.addSubtask(t1);
//        g.addSubtask(t2);
//        g.addSubtask(t3);
//        
//        s2.add(g);
//        
//        TaskSeriesCollection collection = new TaskSeriesCollection();
//        collection.add(s1);
//        collection.add(s2);
//
//        
//        final JFreeChart chart = ChartFactory.createGanttChart(
//                "Fluent Timeline",  // chart title
//                "Fluent",              // domain axis label
//                "Tick",              // range axis label
//                collection,             // data
//                true,                // include legend
//                true,                // tooltips
//                false                // urls
//            );
//        
//        XYPlot plot = chart.getXYPlot();
//        //JFreeChart chart = createChart(data);
//        
//        // add the chart to a panel...
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
//        setContentPane(chartPanel);
//
//    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /** 
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(IntervalXYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYBarChart(
            "XY Bar Chart Demo 2",      // chart title
            "Date",                     // domain axis label
            true,
            "Y",                        // range axis label
            dataset,                    // data
            PlotOrientation.VERTICAL,
            true,                       // include legend
            true,
            false
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        //XYPlot plot = chart.getXYPlot();
        //plot.setRenderer(new ClusteredXYBarRenderer());
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;        
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        XYDemo demo = new XYDemo("XY Bar Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}