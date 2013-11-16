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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
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
public class frmSalasSujas extends javax.swing.JFrame {
	private JButton btnAbrirLog;
	private JFreeChart chart;
	private JPanel jPanel1;
	private JLabel jLabel1;
	ArrayList<Individuo> individuosSelecionados = null;
	String diretorio;
	String identificacao;

	public frmSalasSujas(ArrayList<Individuo> individuosSelecionados, String diretorio, String identificacao) {
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
	
	private DefaultCategoryDataset obtemDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		//Percorre os melhores individuos para plotagem no grafico [Primeira serie do grafico]
		for (Individuo ind : individuosSelecionados) {
			if (ind.getSelecionado()) {
				//TODO: Ajustar aqui para ficar parametrizado
				dataset.addValue(ind.percentualSalaSuja, "Indivíduo selecionado", ind.getGeracao());
			}
		}	
		
		for (int i = 1; i <= Estrategia.max_gera; i++) {
			int cont = 1;
			for (Individuo ind : individuosSelecionados) {
				if (ind.getGeracao() == i && !ind.getSelecionado()) {					
					//TODO: Ajustar aqui para ficar parametrizado
					dataset.addValue(ind.percentualSalaSuja, "Indivíduo " + cont, ind.getGeracao());
					cont++;
				}
			}
		}	
		
		return dataset;
	}
	
	private JFreeChart createChart() {

		DefaultCategoryDataset dataset = obtemDataset();
		
		//TODO: Ajustar para ficar parametrizado
		JFreeChart chart = ChartFactory.createLineChart(
				"Fitness obtido nas gerações", //title
				"Gerações", //x-axis category
				"Percentual de salas sujas (%)", //x-axis label
				dataset, //data
				PlotOrientation.VERTICAL, //orientation
				false, //create legend?
				true, //create tooltips?
				false //generate URLS?
		);      
		
		chart.setTitle(new TextTitle("Percentual de salas sujas", new Font("Arial", Font.BOLD, 16)));
		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.gray);       
		plot.setDomainGridlinePaint(Color.white);		

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	    rangeAxis.setAutoRange(true);
	    rangeAxis.setAutoRangeIncludesZero(false);

		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setSeriesLinesVisible(0, true);   
		
		renderer.setFillPaint(Color.black);     
		renderer.setSeriesPaint(0, Color.black);
		renderer.setSeriesShapesVisible(0, true);
		
		renderer.setBaseItemLabelFont(new Font("SansSerif", Font.PLAIN, 7)); 
		renderer.setSeriesItemLabelsVisible(0, true);
		renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());		
		
		for (int i = 1; i <= Estrategia.max_gera; i++) {
			renderer.setSeriesShapesVisible(i, true);
			renderer.setSeriesPaint(i, Color.blue);
			renderer.setSeriesLinesVisible(i, false);
			renderer.setSeriesItemLabelsVisible(i, false);
			renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(1.5F, 1.5F));
		}
		
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
