import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.ChartFactory;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.DatasetRenderingOrder;

import org.jfree.chart.ChartUtils;
import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.distribution.NormalDistribution;
import java.util.Arrays;
import java.text.DecimalFormat;
public class Normality {
    public static void qqplot(String dname, double[] xi) {
        ChartPlot plot = new QQPlot();
        JFreeChart chart = plot.createChart(dname, xi);

        plot.writeJPEG("qqplot.jpeg", chart, 800, 500);        
    }
    public static void kstest(String dname, double[] xi) {
        ChartPlot plot = new KSTest();
        JFreeChart chart = plot.createChart(dname, xi);

        plot.writeJPEG("kstest.jpeg", chart, 800, 500);        
       
    }
    public static boolean skewnesstest(double[] xi) {
        DAgostinosTest daigo = new SkewnessTest();

        double b1 = daigo.calcTestStatistic(xi);
        return daigo.test(b1, 0.05);
    }
    public static boolean kurtosistest(double[] xi) {
        DAgostinosTest daigo = new KurtosisTest();

        double b2 = daigo.calcTestStatistic(xi);
        return daigo.test(b2, 0.05);
    }
    

    private interface ChartPlot {
        JFreeChart createChart(String dname, double[] xi);
        default void writeJPEG(String fname, JFreeChart chart, int width, int height) {
            File file = new File(fname);
            try {
                ChartUtils.saveChartAsJPEG(file, chart, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private interface DAgostinosTest {
        double calcTestStatistic(double[] xi);
        boolean test(double statistic, double a);
    }

    // Q-Qplot
    private static class QQPlot implements ChartPlot {
        private DescriptiveStatistics stat = null;
        private double m = 0.0;
        private double sd = 0.0;
        public QQPlot() {
            stat = new DescriptiveStatistics();
        }
        public JFreeChart createChart(String dname, double[] xi) {
            Arrays.stream(xi).forEach(stat::addValue);
            m = stat.getMean();     // 平均
            sd = stat.getStandardDeviation();// 標準偏差

            XYPlot plot = createPlot(dname, xi);
            /*--- 横軸 ---*/
            NumberAxis domainAxis = new NumberAxis("標準正規分布");

            plot.setDomainAxis(domainAxis);
            domainAxis.setLowerMargin(0.03);
            domainAxis.setUpperMargin(0.03);
            domainAxis.setLowerBound(-4);
            domainAxis.setUpperBound(4);

            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            return new JFreeChart("正規性の検定", plot);
        }
        private XYPlot createPlot(String dname, double[] xi) {
            XYItemRenderer renderer0 = new XYLineAndShapeRenderer(false, true);
            XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
            XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();

            renderer0.setDefaultToolTipGenerator(toolTipGenerator);
            renderer0.setURLGenerator(null);
            renderer1.setDefaultToolTipGenerator(toolTipGenerator);
            renderer1.setURLGenerator(null);

            XYPlot plot = new XYPlot();
            plot.setOrientation(PlotOrientation.VERTICAL);
            plot.mapDatasetToRangeAxis(0,0);
            plot.mapDatasetToRangeAxis(1,0);
	    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

            /*--- 縦軸 ---*/
            NumberAxis valueAxis0 = new NumberAxis("観測値");
            plot.setRangeAxis(valueAxis0);

            plot.setRenderer(0, renderer0);
            plot.setDataset(0, createDataset0(dname, xi));

            plot.setRenderer(1, renderer1);
            plot.setDataset(1, createDataset1());

            return plot;
        }
        private XYSeriesCollection createDataset0(String dname, double[] xi) {
            double den = sd / Math.sqrt(xi.length);
            XYSeries series = new XYSeries(dname);
 
            for (int i = 0; i < xi.length; i++) {
                double x = (xi[i] - m) / den;

                series.add(x, xi[i]);
            }
            XYSeriesCollection data = new XYSeriesCollection();

            data.addSeries(series);
            return data;
        }
        private XYSeriesCollection createDataset1() {
            XYSeries series = new XYSeries("累積");
            NormalDistribution ndist = new NormalDistribution(0, 1);

            for (double p = 0.001; p < 1.0; p += 0.01) {
                double x = ndist.inverseCumulativeProbability(p);

                series.add(x, x * sd + m);
            }

            XYSeriesCollection data = new XYSeriesCollection();
            data.addSeries(series);
            return data;
        }
    }
    // コルモゴルフ・スミルノフ検定
    private static class KSTest implements ChartPlot {
        private NormalDistribution ndist = null;
        public KSTest() {
            ndist = new NormalDistribution(0, 1);
        }
        public JFreeChart createChart(String dname, double[] xi) {
            NumberAxis domainAxis = new NumberAxis("標準正規分布");
            XYPlot plot = createPlot(dname, xi);

            /*--- 横軸 ---*/
            plot.setDomainAxis(domainAxis);
            domainAxis.setLowerMargin(0.03);
            domainAxis.setUpperMargin(0.03);
            domainAxis.setLowerBound(-4);
            domainAxis.setUpperBound(4);
            
            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            return new JFreeChart("コルモゴルフ・スミルノフ検定", plot);
        }
        private XYPlot createPlot(String dname, double[] xi) {
            XYItemRenderer renderer0 = new XYLineAndShapeRenderer(false, true);
            XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
            XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator();

            renderer0.setDefaultToolTipGenerator(toolTipGenerator);
            renderer0.setURLGenerator(null);
            renderer1.setDefaultToolTipGenerator(toolTipGenerator);
            renderer1.setURLGenerator(null);

            XYPlot plot = new XYPlot();
            plot.setOrientation(PlotOrientation.VERTICAL);
            plot.mapDatasetToRangeAxis(0,0);
            plot.mapDatasetToRangeAxis(1,0);
	    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

            /*--- 縦軸 ---*/
            NumberAxis valueAxis0 = new NumberAxis("確率");
            plot.setRangeAxis(valueAxis0);
            valueAxis0.setLowerBound(0);
            valueAxis0.setUpperBound(1);
            valueAxis0.setTickUnit(new NumberTickUnit(0.1));
            valueAxis0.setNumberFormatOverride(new DecimalFormat("0.0#"));

            plot.setRenderer(0, renderer0);
            plot.setDataset(0, createDataset0(dname, xi));

            plot.setRenderer(1, renderer1);
            plot.setDataset(1, createDataset1());

            return plot;
        }
        private XYSeriesCollection createDataset0(String dname, double[] xi) {
            Arrays.sort(xi);
            DescriptiveStatistics stat = new DescriptiveStatistics();
            Arrays.stream(xi).forEach(stat::addValue);
            double m = stat.getMean();     // 平均
            double sd = stat.getStandardDeviation();// 標準偏差
            double den = sd / Math.sqrt(xi.length);
            double sum = stat.getSum();
            double p = 0.0;

            XYSeries series = new XYSeries(dname);
            for (int n = 0; n < xi.length; n++) {
                double x = (xi[n] - m) / den;
                
                p += xi[n] / sum;
                series.add(x, p);
            }
            XYSeriesCollection data = new XYSeriesCollection();

            data.addSeries(series);
            return data;
        }
        private XYSeriesCollection createDataset1() {
            XYSeries series = new XYSeries("累積");

            for (double x = -4; x < 4; x += 0.01) {
                double y = ndist.cumulativeProbability(x);

                series.add(x, y);
            }
            XYSeriesCollection data = new XYSeriesCollection();

            data.addSeries(series);
            return data;
        }
    }
    // タコスディーノ検定(歪度)
    private static class SkewnessTest implements DAgostinosTest {
        private long n = 0;
        private NormalDistribution ndist = null;
        public SkewnessTest() {
            ndist = new NormalDistribution(0, 1);
        }
        public double calcTestStatistic(double[] xi) {
            DescriptiveStatistics stat = new DescriptiveStatistics();

            Arrays.stream(xi).forEach(stat::addValue);
            n = stat.getN();
            return Math.sqrt(stat.getSkewness());
        }

        public boolean test(double statistic, double a) {
            double ua_2 = ndist.inverseCumulativeProbability(1.0 - a);

            return (Math.abs(statistic) > cnvb2tob2p(ua_2))
                ? true : false;
        }
        private double cnvb2tob2p(double ua_2) {
           double  el = (n + 1.0) * (n + 1.0) * (n + 3.0) * (n + 5.0); // 分子
           double  den = 24 * n * (n - 2.0) * (n - 3.0);               // 分母
           double b2p = Math.sqrt(el / den) * 
                  (long)(ua_2 + 3.0 / (2.0 * n) * (ua_2 * ua_2 * ua_2 - 3 * ua_2));
           
            return b2p;
        }
    }
    // タコスディーノ検定(尖度)
    private static class KurtosisTest implements DAgostinosTest {
        private long n = 0;
        private NormalDistribution ndist = null;
        public KurtosisTest() {
            ndist = new NormalDistribution(0, 1);
        }
        public double calcTestStatistic(double[] xi) {
            DescriptiveStatistics stat = new DescriptiveStatistics();

            Arrays.stream(xi).forEach(stat::addValue);
            n = stat.getN();
      
            return stat.getKurtosis();
        }
        public boolean test(double statistic, double a) {
            boolean ret = false;
            double ua_2 = ndist.inverseCumulativeProbability(1.0 - a);
            double b2p = cnvb2tob2p(statistic);

            double r_val = ua_2 + Math.sqrt(6.0 / n) * (ua_2 * ua_2 - 1.0);
            double l_val = -1.0 * ua_2 + Math.sqrt(6.0 / n) * (ua_2 * ua_2 - 1.0);

            if (b2p > r_val) {
                ret = true;
            }
            if (b2p < l_val) {
                ret = true;
            }
            return ret;
        }
        private double cnvb2tob2p(double b2) {
           double  el = (n + 1.0) * (n + 1.0) * (n + 3.0) * (n + 5.0); // 分子
           double  den = 24 * n * (n - 2.0) * (n - 3.0);               // 分母

           double b2p = Math.sqrt(el / den) * 
                  (long)(b2 - 3 * (n - 1.0) / (n + 3.0));
            
           return b2p;
        }
    }
}

