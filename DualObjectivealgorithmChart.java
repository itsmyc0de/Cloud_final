package EnergyEfficient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.ui.TextAnchor;


public class DualObjectivealgorithmChart {
public static void main(String[] args) {
	try{
		 Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	     Connection cn = DriverManager.getConnection("jdbc:odbc:driver={SQL Server}; server=(local);database=gridsim1");
	     JDBCCategoryDataset dataset = new JDBCCategoryDataset(cn);
	   String qry = "select distinct a.gridetid,datediff(ss,a.starttime,a.endtime) as dat ,datediff(ss,b.starttime,b.endtime) as dat1 from tbldualobjexecution as a,tbldualobjexecution1 as b where a.gridetid=b.gridetid";
	   //  String qry = "select distinct gridetid,datediff(ss,starttime,endtime) as dat from tbldualobjexecution order by gridetid ";
	     dataset.executeQuery(qry);
	     JFreeChart chart = ChartFactory.createBarChart("ADOS VS ReservedCluster(Execution Time) ", "Cloudlets", "Number Of Time used", dataset, PlotOrientation.VERTICAL, false, true, false);
	     //JFreeChart chart = ChartFactory.createPieChart("Strong Rules",dataset,true,true,true);
	     CategoryPlot p = chart.getCategoryPlot();
	     p.setRangeGridlinePaint(Color.blue);
	     ChartPanel chartPanel = new ChartPanel(chart);
	     chartPanel.setPreferredSize(new java.awt.Dimension(650, 400));






	        final CategoryItemRenderer renderer = new CustomRenderer(
	            new Paint[] {Color.blue, Color.green,
	                Color.yellow, Color.orange, Color.cyan,
	                Color.magenta, Color.blue}
	        );
//	        renderer.setLabelGenerator(new StandardCategoryLabelGenerator());
	        renderer.setItemLabelsVisible(true);
	        final ItemLabelPosition p1 = new ItemLabelPosition(
	            ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 45.0
	        );
	        renderer.setPositiveItemLabelPosition(p1);
	        p.setRenderer(renderer);






	     JFrame f = new JFrame("Chart");
	     f.setContentPane(chartPanel);
	     f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	     f.pack();
	     f.setVisible(true);
	     try {
	         //ChartUtilities.saveChartAsJPEG(new File("F:/chart.jpg"), chart,400,300);
	     } catch (Exception e) {
	         System.out.println("Problem in creating chart.");
	     }
	     cn.close();
		  //System.out.println("return true");

		}
	catch(Exception ee)
	{
		ee.printStackTrace();
	}


}
}
class CustomRenderer extends BarRenderer {

    /** The colors. */
    private Paint[] colors;

    /**
     * Creates a new renderer.
     *
     * @param colors  the colors.
     */
    public CustomRenderer(final Paint[] colors) {
        this.colors = colors;
    }

    /**
     * Returns the paint for an item.  Overrides the default behaviour inherited from
     * AbstractSeriesRenderer.
     *
     * @param row  the series.
     * @param column  the category.
     *
     * @return The item color.
     */
    public Paint getItemPaint(final int row, final int column) {
        return this.colors[column % this.colors.length];
    }
}

