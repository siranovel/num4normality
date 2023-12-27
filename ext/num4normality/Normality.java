import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.ChartFactory;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
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
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.util.Arrays;
import java.text.DecimalFormat;

import org.apache.commons.math3.stat.inference.TestUtils;
public class Normality {
    public static void qqplot(String dname, double[] xi) {
        ChartPlot plot = new QQChartPlot();
        JFreeChart chart = plot.createChart("正規Q-Qプロット", dname, xi);

        plot.writeJPEG("qqplot.jpeg", chart, 800, 500);        
    }
    public static void ksplot(String dname, double[] xi) {
        ChartPlot plot = new KSChartPlot();
        JFreeChart chart = plot.createChart("コルモゴルフ・スミルノフ検定", dname, xi);

        plot.writeJPEG("ksplot.jpeg", chart, 800, 500);        
       
    }
    public static void qqksplot(String dname, double[] xi) {
         ChartPlot plot = new QQKSChartPlot();
        JFreeChart chart = plot.createChart("Q-Q and コルモゴルフ・スミルノフ", dname, xi);

        plot.writeJPEG("qqksplot.jpeg", chart, 1000, 800);        
    }
    public static void ppplot(String dname, double[] xi) {
        ChartPlot plot = new PPChartPlot();
        JFreeChart chart = plot.createChart("正規P-Pプロット", dname, xi);

        plot.writeJPEG("ppplot.jpeg", chart, 800, 500);        
    }
    public static void ppksplot(String dname, double[] xi) {
        ChartPlot plot = new PPKSChartPlot();
        JFreeChart chart = plot.createChart("P-P and コルモゴルフ・スミルノフ", dname, xi);

        plot.writeJPEG("ppksplot.jpeg", chart, 800, 500);        
       
    }
    public static boolean kstest(double[] xi) {
        KSTest ks = new KSTest();

        return ks.test(xi);
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
    public static boolean omnibustest(double[] xi) {
        DAgostinosTest daigo = new OmnibusTest();

        double x = daigo.calcTestStatistic(xi);
        return daigo.test(x, 0.05);
    }
    
    /*********************************/
    /* interface define              */
    /*********************************/
    private interface ChartPlot {
        /* フィールド */
        static final double CLASS_MIN = -4.0;
        static final double CLASS_MAX = 4.0;
        /* メゾット */
        JFreeChart createChart(String title, String dname, double[] xi);
        default void writeJPEG(String fname, JFreeChart chart, int width, int height) {
            File file = new File(fname);
            try {
                ChartUtils.saveChartAsJPEG(file, chart, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private interface CreatePlot {
        XYPlot createPlot(String dname, double[] xi);
    }
    private interface DAgostinosTest {
        double calcTestStatistic(double[] xi);
        boolean test(double statistic, double a);
    }
    /*********************************/
    /* Class define                  */
    /*********************************/
    // Q-Qplot
    private static class QQChartPlot implements ChartPlot {
        public JFreeChart createChart(String title, String dname, double[] xi) {
            XYPlot plot = createPlot(dname, xi);

            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            return new JFreeChart(title, plot);
        }
        private XYPlot createPlot(String dname, double[] xi) {
            CreatePlot plotImpl = new QQPlot();

            return plotImpl.createPlot(dname, xi);
        }
        public static class QQPlot implements CreatePlot {
            private double[][] createData(double[] xi) {
                DescriptiveStatistics stat = new DescriptiveStatistics();
                NormalDistribution ndist = new NormalDistribution(0, 1);

                int n = xi.length;
                Arrays.sort(xi);
                Arrays.stream(xi).forEach(stat::addValue);
                double sum = stat.getSum();
                double[][] data = new double[n][2];
                double p = 0.0;

                for (int i = 0; i < n; i++) {
                    p += xi[i] / sum;
                    double x = 
                        ndist.inverseCumulativeProbability(p * (i + 1.0) / (n + 1.0));

                    data[i][0] = x;
                    data[i][1] = xi[i];
                }
                return data;
            }
            public XYPlot createPlot(String dname, double[] xi) {
                double[][] data = createData(xi);
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

                /*--- 横軸 ---*/
                NumberAxis domainAxis = new NumberAxis("期待値");

                plot.setDomainAxis(domainAxis);
                domainAxis.setLowerMargin(0.03);
                domainAxis.setUpperMargin(0.03);
                domainAxis.setLowerBound(ChartPlot.CLASS_MIN);
                domainAxis.setUpperBound(ChartPlot.CLASS_MAX);

                /*--- 縦軸 ---*/
                NumberAxis valueAxis0 = new NumberAxis("観測値");
                plot.setRangeAxis(valueAxis0);

                plot.setRenderer(0, renderer0);
                plot.setDataset(0, createDataset0(dname, data));

                plot.setRenderer(1, renderer1);
                plot.setDataset(1, createDataset1(data));

                return plot;
            }
            private XYSeriesCollection createDataset0(String dname, double[][] data) {
                XYSeries cu = new XYSeries(dname);
 
                for (int i = 0; i < data.length; i++) {
                    cu.add(data[i][0], data[i][1]);
                }
                XYSeriesCollection series = new XYSeriesCollection();

                series.addSeries(cu);
                return series;
            }
            private XYSeriesCollection createDataset1(double[][] data) {
                SimpleRegression simpleReg = new SimpleRegression(true);
                XYSeries cu     = new XYSeries("累積");

                simpleReg.addData(data);
                double a = simpleReg.getSlope();
                double b = simpleReg.getIntercept();
     
                for (double x = ChartPlot.CLASS_MIN; x < ChartPlot.CLASS_MAX; x += 0.01) {
                    double y = a * x + b;

                    cu.add(x, y);
                }
                XYSeriesCollection series = new XYSeriesCollection();
                series.addSeries(cu);
                return series;
            }
        }
    }
    // コルモゴルフ・スミルノフ検定
    private static class KSChartPlot implements ChartPlot {
        public JFreeChart createChart(String title, String dname, double[] xi) {
            XYPlot plot = createPlot(dname, xi);
            
            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            return new JFreeChart(title, plot);
        }
        private XYPlot createPlot(String dname, double[] xi) {
            CreatePlot plotImpl = new KSPlot();

            return plotImpl.createPlot(dname, xi);
        }
        public static class KSPlot implements CreatePlot {
            private double[][] createData(double[] xi) {
                DescriptiveStatistics stat = new DescriptiveStatistics();

                int n = xi.length;
                Arrays.sort(xi);
                Arrays.stream(xi).forEach(stat::addValue);
                double m = stat.getMean();     // 平均
                double sd = stat.getStandardDeviation();// 標準偏差
                double sum = stat.getSum();
                double[][] data = new double[n][2];
                double p = 0.0;

                for (int i = 0; i < n; i++) {
                    p += xi[i] / sum;
                    data[i][0] = (xi[i] - m) / sd;
                    data[i][1] = p;
                }
                return data;
            }
            public XYPlot createPlot(String dname, double[] xi) {
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

                /*--- 横軸 ---*/
                NumberAxis domainAxis = new NumberAxis("期待値");
                plot.setDomainAxis(domainAxis);

                domainAxis.setLowerMargin(0.03);
                domainAxis.setUpperMargin(0.03);
                domainAxis.setLowerBound(ChartPlot.CLASS_MIN);
                domainAxis.setUpperBound(ChartPlot.CLASS_MAX);

                /*--- 縦軸 ---*/
                NumberAxis valueAxis0 = new NumberAxis("確率");
                plot.setRangeAxis(valueAxis0);
                valueAxis0.setLowerBound(0);
                valueAxis0.setUpperBound(1);
                valueAxis0.setTickUnit(new NumberTickUnit(0.1));
                valueAxis0.setNumberFormatOverride(new DecimalFormat("0.0#"));

                plot.setRenderer(0, renderer0);
                plot.setDataset(0, createDataset0(dname, createData(xi)));

                plot.setRenderer(1, renderer1);
                plot.setDataset(1, createDataset1());

                return plot;
            }
            private XYSeriesCollection createDataset0(String dname, double[][] data) {
                XYSeries cu = new XYSeries(dname);

                for (int i = 0; i < data.length; i++) {
                    cu.add(data[i][0], data[i][1]);
                }
                XYSeriesCollection series = new XYSeriesCollection();

                series.addSeries(cu);
                return series;
            }
            private XYSeriesCollection createDataset1() {
                NormalDistribution ndist = new NormalDistribution(0, 1);
                XYSeries cu = new XYSeries("累積p");

                for (double x = ChartPlot.CLASS_MIN; x < ChartPlot.CLASS_MAX; x += 0.01) {
                    double y = ndist.cumulativeProbability(x);

                    cu.add(x, y);
                }
                XYSeriesCollection series = new XYSeriesCollection();

                series.addSeries(cu);
                return series;
            }
        }
    }
    // Q-Q and KSplot
    private static class QQKSChartPlot implements ChartPlot {
        private CreatePlot plot0 = new QQChartPlot.QQPlot();
        private CreatePlot plot1 = new KSChartPlot.KSPlot();
        public JFreeChart createChart(String title, String dname, double[] xi) {
            XYPlot plot = createPlot(dname, xi);
            
            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            return new JFreeChart(title, plot);
        }
        private XYPlot createPlot(String dname, double[] xi) {
            /*--- 横軸 ---*/
            NumberAxis domainAxis = new NumberAxis("期待値");

            CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainAxis);
            domainAxis.setLabel("期待値");
            domainAxis.setLowerMargin(0.03);
            domainAxis.setUpperMargin(0.03);
            domainAxis.setLowerBound(ChartPlot.CLASS_MIN);
            domainAxis.setUpperBound(ChartPlot.CLASS_MAX);

            plot.add(plot0.createPlot(dname, xi), 1);
            plot.add(plot1.createPlot(dname, xi), 1);
            return plot;
        }

    }
    // P-Pplot
    private static class PPChartPlot implements ChartPlot {
        public JFreeChart createChart(String title, String dname, double[] xi) {
            XYPlot plot = createPlot(dname, xi);

            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            return new JFreeChart(title, plot);
        }
        private XYPlot createPlot(String dname, double[] xi) {
            CreatePlot plotImpl = new PPPlot();

            return plotImpl.createPlot(dname, xi);
        }
        public static class PPPlot implements CreatePlot {
            private double[][] createData(double[] xi) {
                DescriptiveStatistics stat = new DescriptiveStatistics();
                NormalDistribution ndist = new NormalDistribution(0, 1);

                int n = xi.length;
                Arrays.sort(xi);
                Arrays.stream(xi).forEach(stat::addValue);
                double m = stat.getMean();     // 平均
                double sd = stat.getStandardDeviation();// 標準偏差
                double sum = stat.getSum();
                double[][] data = new double[n][2];
                double p = 0.0;

                for (int i = 0; i < n; i++) {
                    p += xi[i] / sum;
                    double x = (xi[i] - m) / sd;

                    ndist.cumulativeProbability(x);
                    data[i][0] = ndist.cumulativeProbability(x);
                    data[i][1] = p;
                }
                return data;
            }
            public XYPlot createPlot(String dname, double[] xi) {
                double[][] data = createData(xi);
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

                /*--- 横軸 ---*/
                NumberAxis domainAxis = new NumberAxis("観測累積確率");

                plot.setDomainAxis(0, domainAxis);
                domainAxis.setLowerMargin(0.03);
                domainAxis.setUpperMargin(0.03);
                domainAxis.setLowerBound(0.0);
                domainAxis.setUpperBound(1.0);
                domainAxis.setTickUnit(new NumberTickUnit(0.1));
                domainAxis.setNumberFormatOverride(new DecimalFormat("0.0#"));
                /*--- 縦軸 ---*/
                NumberAxis valueAxis0 = new NumberAxis("予測累積確率");
                plot.setRangeAxis(valueAxis0);
                valueAxis0.setLowerBound(0);
                valueAxis0.setUpperBound(1);
                valueAxis0.setTickUnit(new NumberTickUnit(0.1));
                valueAxis0.setNumberFormatOverride(new DecimalFormat("0.0#"));

                plot.setRenderer(0, renderer0);
                plot.setDataset(0, createDataset0(dname, data));

                plot.setRenderer(1, renderer1);
                plot.setDataset(1, createDataset1(data));
                return plot;
            }
            private XYSeriesCollection createDataset0(String dname, double[][] data) {
                XYSeries cu = new XYSeries(dname);

                for (int i = 0; i < data.length; i++) {
                    cu.add(data[i][0], data[i][1]);
                }
                XYSeriesCollection series = new XYSeriesCollection();

                series.addSeries(cu);
                return series;
            }
            private XYSeriesCollection createDataset1(double[][] data) {
                SimpleRegression simpleReg = new SimpleRegression(true);
                XYSeries cu     = new XYSeries("累積");

                simpleReg.addData(data);
                double a = simpleReg.getSlope();
                double b = simpleReg.getIntercept();
     
                for (double x = ChartPlot.CLASS_MIN; x < ChartPlot.CLASS_MAX; x += 0.01) {
                    double y = a * x + b;

                    cu.add(x, y);
                }
                XYSeriesCollection series = new XYSeriesCollection();
                series.addSeries(cu);
                return series;
            }
            
        }
    }
    // P-P and KSplot
    private static class PPKSChartPlot implements ChartPlot {
        private CreatePlot plot0 = new PPChartPlot.PPPlot();
        private CreatePlot plot1 = new KSChartPlot.KSPlot();
        public JFreeChart createChart(String title, String dname, double[] xi) {
            XYPlot plot = createPlot(dname, xi);

            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            return new JFreeChart(title, plot);
        }
        private XYPlot createPlot(String dname, double[] xi) {
            /*--- 縦軸 ---*/
            NumberAxis rangeAxis = new NumberAxis("予測累積確率");
            CombinedRangeXYPlot plot = new CombinedRangeXYPlot(rangeAxis);
            rangeAxis.setLowerMargin(0.03);
            rangeAxis.setUpperMargin(0.03);
            rangeAxis.setLowerBound(0.0);
            rangeAxis.setUpperBound(1.0);
            rangeAxis.setTickUnit(new NumberTickUnit(0.1));
            rangeAxis.setNumberFormatOverride(new DecimalFormat("0.0#"));

            plot.add(plot0.createPlot(dname, xi), 2);
            plot.add(plot1.createPlot(dname, xi), 3);

            return plot;
        }
    }
    // KS検定
    private static class KSTest {
        public boolean test(double[] xi) {
            double[] data = new double[xi.length];
            Arrays.sort(xi);
            DescriptiveStatistics stat = new DescriptiveStatistics();
            Arrays.stream(xi).forEach(stat::addValue);
            double m = stat.getMean();     // 平均
            double sd = stat.getStandardDeviation();// 標準偏差
            NormalDistribution ndist = new NormalDistribution(0, 1);

            for (int i = 0; i < xi.length; i++) {
                data[i] = (xi[i] - m) / sd; 
            }
            boolean ret = TestUtils.kolmogorovSmirnovTest(ndist, data, 0.05);
            return ret;
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
            double ua_2 = ndist.inverseCumulativeProbability(1.0 - a / 2.0);

            return (Math.abs(statistic) > calcNormStatic(ua_2))
                ? true : false;
        }
        private double calcNormStatic(double ua_2) {
           double  el = (n + 1.0) * (n + 1.0) * (n + 3.0) * (n + 5.0); // 分子
           double  den = 24 * n * (n - 2.0) * (n - 3.0);               // 分母
           double b2p = Math.sqrt(el / den) * 
                  (ua_2 + 3.0 / (2.0 * n) * (ua_2 * ua_2 * ua_2 - 3 * ua_2));
           
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
            double ua_2 = ndist.inverseCumulativeProbability(1.0 - a / 2.0);

            double r_val = ua_2 + Math.sqrt(6.0 / n) * (ua_2 * ua_2 - 1.0);
            double l_val = -1.0 * ua_2 + Math.sqrt(6.0 / n) * (ua_2 * ua_2 - 1.0);

            if (statistic > r_val) {
                ret = true;
            }
            if (statistic < l_val) {
                ret = true;
            }
            return ret;
        }
    }
    // オムニバス検定
    private static class OmnibusTest implements DAgostinosTest {
        private DAgostinosTest skewness = null;
        private DAgostinosTest kurtosis = null;
        public OmnibusTest() {
            skewness = new SkewnessTest();
            kurtosis = new KurtosisTest();
        }
        public double calcTestStatistic(double[] xi) {
            double x1 = skewness.calcTestStatistic(xi);
            double x2 = kurtosis.calcTestStatistic(xi);

            return x1 * x1 + x2 * x2;
        }
        public boolean test(double statistic, double a) {
            ChiSquaredDistribution chi2Dist = new ChiSquaredDistribution(2);
            double p = chi2Dist.cumulativeProbability(statistic);

            return (p < a) ? true  : false;
        }
    }
}

