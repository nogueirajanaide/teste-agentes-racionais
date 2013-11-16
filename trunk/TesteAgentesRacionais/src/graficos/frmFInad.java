package graficos;
import info.clearthought.layout.TableLayout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import testador.thestes.Estrategia;
import testador.thestes.Individuo;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class frmFInad extends javax.swing.JFrame {
	private JButton btnAbrirLog;
	private JFreeChart chart;
	private JPanel jPanel1;
	private JLabel jLabel1;
	ArrayList<Individuo> individuosSelecionados = null;
	String diretorio;
	String identificacao;

	public frmFInad(ArrayList<Individuo> individuosSelecionados, String diretorio, String identificacao) {
		super();
		this.individuosSelecionados = individuosSelecionados;
		this.diretorio = diretorio;
		this.identificacao = identificacao;
		initGUI();
		this.setLocationRelativeTo(null);
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			TableLayout thisLayout = new TableLayout(new double[][] {{23.0, 747.0, TableLayout.FILL}, {20.0, 399.0, 29.0, 183.0, TableLayout.FILL}});
			thisLayout.setHGap(5);
			thisLayout.setVGap(5);
			getContentPane().setLayout(thisLayout);
			getContentPane().setBackground(new java.awt.Color(206,206,255));
			this.setTitle("Resultados");
			{
				btnAbrirLog = new JButton();
				getContentPane().add(btnAbrirLog, "1,2,r,f");
				btnAbrirLog.setText("Abrir Log");
				btnAbrirLog.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						btnAbrirLogActionPerformed(evt);
					}
				});
			}
			{	
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, "2, 1");
				
				chart = createChart();
				jPanel1.setBackground(new java.awt.Color(206,206,255));
			
				final ChartPanel chartPanel = new ChartPanel(chart);
				getContentPane().add(chartPanel, "1, 1");
				chartPanel.setPreferredSize(new java.awt.Dimension(513, 248));
				chartPanel.setFont(new java.awt.Font("Arial",0,10));
				chartPanel.setAutoscrolls(true);
				chartPanel.setOpaque(false);
			}
			
			pack();
			this.setSize(843, 519);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private XYDataset obtemDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		XYSeries xySeries1 = new XYSeries("Indivíduos selecionados");
		//Percorre os melhores individuos para plotagem no grafico [Primeira serie do grafico]
		for (Individuo ind : individuosSelecionados) {
			if (ind.getSelecionado()) {
				//TODO: Ajustar aqui para ficar parametrizado
				xySeries1.add(ind.fInad.get("Energia"), ind.fInad.get("Limpeza"));
			}
		}	
		
		XYSeries xySeries2 = new XYSeries("Indivíduos não selecionados");
		//Percorre os demais individuos para plotagem no grafico
		for (Individuo ind : individuosSelecionados) {
			if (!ind.getSelecionado()) {
				//TODO: Ajustar aqui para ficar parametrizado
				xySeries2.add(ind.fInad.get("Energia"), ind.fInad.get("Limpeza"));
				//cont++;
			}
		}
		dataset.addSeries(xySeries1);
		dataset.addSeries(xySeries2);
		return dataset;
	}
	
	private JFreeChart createChart() {
		
		XYDataset dataset = obtemDataset();
		
		//TODO: Ajustar para ficar parametrizado
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Finad dos casos de testes", //title
				"Energia", //axis x
				"Limpeza", //axis y
				dataset, //data
				PlotOrientation.VERTICAL, //orientation
				false,  //create legend?
				true, //create tooltips?
				false //generate URLS?
		);
				
		chart.setTitle(new TextTitle("Finad dos casos de testes", new Font("Arial", Font.BOLD, 16)));
		chart.setBackgroundPaint(Color.white);
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.gray);
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();  
		//Serie 0 -> Individuos selecionados
	    renderer.setSeriesLinesVisible(0, false);
	    renderer.setSeriesShapesVisible(0, true);
	    renderer.setItemLabelsVisible(true);
	    renderer.setSeriesPaint(0, Color.black);
	    renderer.setSeriesShape(0, ShapeUtilities.createRegularCross(2.5F, 2.5F));
	    
	    //Serie 1 -> Individuos nao selecionados
	    renderer.setSeriesLinesVisible(1, false);
	    renderer.setSeriesShapesVisible(1, true);
	    renderer.setSeriesPaint(1, Color.blue);
	    renderer.setSeriesShape(1, ShapeUtilities.createRegularCross(1.5F, 1.5F));
	    
	    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	    rangeAxis.setAutoRange(true);
	    rangeAxis.setAutoRangeIncludesZero(false);
	    
	    frmMenuPrincipal.exportaGrafico(chart, diretorio, identificacao);
		return chart;
	}

	private void btnAbrirLogActionPerformed(ActionEvent evt) {
		try {
			
			Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + Estrategia.path);
			p.waitFor();
			
		} catch (Exception e) {
			JOptionPane.showConfirmDialog(this, "Erro ao abrir o arquivo de log.");
		}
	}
}
