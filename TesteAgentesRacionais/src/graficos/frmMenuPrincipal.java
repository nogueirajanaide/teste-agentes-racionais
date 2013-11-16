package graficos;
import info.clearthought.layout.TableLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

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
public class frmMenuPrincipal extends javax.swing.JFrame {
	private JMenuBar jMenu;
	private JMenuItem jMenuItem6;
	private JMenuItem jMenuItem7;
	private JMenuItem jMenuItem8;
	private JMenuItem jMenuItem9;
	private JMenuItem jMenuItem15;
	private JMenuItem jMenuItem14;
	private JTextField txtIdentificacao;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JTextField txtDiretorio;
	private JMenu jMenu3;
	private JMenuItem jMenuItem13;
	private JMenuItem jMenuItem12;
	private JMenuItem jMenuItem11;
	private JMenuItem jMenuItem10;
	private JMenuItem jMenuItem5;
	private JMenu jMenu2;
	private JMenuItem jMenuItem4;
	private JMenuItem jMenuItem3;
	private JMenuItem jMenuItem2;
	private JMenuItem jMenuItem1;
	private JMenu jMenu1;
	

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frmMenuPrincipal inst = new frmMenuPrincipal();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public frmMenuPrincipal() {
		super();
		initGUI();
	}
	
	ArrayList<Individuo> individuosSelecionados =  null;
	
	public frmMenuPrincipal(ArrayList<Individuo> individuosSelecionados) {
		super();
		initGUI();
		this.individuosSelecionados = individuosSelecionados;
	}
	
	private void initGUI() {
		try {
			//TableLayout thisLayout = new TableLayout(new double[][] {{108.0, 78.0, TableLayout.FILL}, {26.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}});
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Resultado");
			TableLayout thisLayout = new TableLayout(new double[][] {{108.0, 78.0, TableLayout.FILL, 15.0}, {6.0, TableLayout.MINIMUM, 6.0, TableLayout.MINIMUM}});
			getContentPane().setLayout(thisLayout);
			this.setExtendedState(Frame.MAXIMIZED_BOTH);
			{
				txtDiretorio = new JTextField();
				getContentPane().add(txtDiretorio, "2, 1");
			}
			{
				jLabel1 = new JLabel();
				getContentPane().add(jLabel1, "1, 1");
				jLabel1.setText("Diretório");
			}
			{
				jLabel2 = new JLabel();
				getContentPane().add(jLabel2, "1, 3");
				jLabel2.setText("Identificação");
			}
			{
				txtIdentificacao = new JTextField();
				getContentPane().add(txtIdentificacao, "2, 3");
			}
			{
				jMenu = new JMenuBar();
				setJMenuBar(jMenu);
				{
					jMenu1 = new JMenu();
					jMenu.add(jMenu1);
					jMenu1.setText("Gráficos");
					{
						jMenuItem1 = new JMenuItem();
						jMenu1.add(jMenuItem1);
						jMenuItem1.setText("Utilidade nas gerações");
						jMenuItem1.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoUtilidade(evt);
							}
						});
					}
					{
						jMenuItem2 = new JMenuItem();
						jMenu1.add(jMenuItem2);
						jMenuItem2.setText("Valores dos objetivos");
						jMenuItem2.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoObjetivos(evt);
							}
						});
					}
					{
						jMenuItem3 = new JMenuItem();
						jMenu1.add(jMenuItem3);
						jMenuItem3.setText("Objetivos dos melhores");
						jMenuItem3.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoObjetivosMelhores(evt);
							}
						});
					}
					{
						jMenuItem4 = new JMenuItem();
						jMenu1.add(jMenuItem4);
						jMenuItem4.setText("Salas sujas ao final");
						jMenuItem4.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoSalasSujas(evt);
							}
						});
					}
					{
						jMenuItem6 = new JMenuItem();
						jMenu1.add(jMenuItem6);
						jMenuItem6.setText("Valor dos objetivos das histórias");
						jMenuItem6.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoObjetivosHistorias(evt);
							}
						});
					}
					{
						jMenuItem7 = new JMenuItem();
						jMenu1.add(jMenuItem7);
						jMenuItem7.setText("Objetivos por gerações");
						jMenuItem7.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoObjetivosGeracoes(evt);
							}
						});
					}
					{
						jMenuItem8 = new JMenuItem();
						jMenu1.add(jMenuItem8);
						jMenuItem8.setText("Objetivos por geração individual");
						jMenuItem8.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoObjetivosGeracaoIndividual(evt);
							}
						});
					}
					{
						jMenuItem10 = new JMenuItem();
						jMenu1.add(jMenuItem10);
						jMenuItem10.setText("Episódios problemático por Caso de Teste");
						jMenuItem10.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoEpisodiosProblematicosCT(evt);
							}
						});
					}
					{
						jMenuItem9 = new JMenuItem();
						jMenu1.add(jMenuItem9);
						jMenuItem9.setText("Episódios problemáticos");
						jMenuItem9.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoEpisodiosProblematicos(evt);
							}
						});
					}
					{
						jMenuItem11 = new JMenuItem();
						jMenu1.add(jMenuItem11);
						jMenuItem11.setText("Box-plot utilidade nas gerações");
						jMenuItem11.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoBoxPlotGeracoes(evt);
							}
						});
					}
					{
						jMenuItem12 = new JMenuItem();
						jMenu1.add(jMenuItem12);
						jMenuItem12.setText("Box-plot de episódios problemáticos");
						jMenuItem12.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoBloxPlotEpisodiosProblema(evt);
							}
						});
					}
					{
						jMenuItem13 = new JMenuItem();
						jMenu1.add(jMenuItem13);
						jMenuItem13.setText("Box-plot de objetivo por geração");
						jMenuItem13.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoBloxPlotObjetivoGeracoes(evt);
							}
						});
					}
					{
						jMenuItem15 = new JMenuItem();
						jMenu1.add(jMenuItem15);
						jMenuItem15.setText("Penalidades");
						jMenuItem15.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirGraficoPenalidade(evt);
							}
						});
					}
				}
				{
					jMenu2 = new JMenu();
					jMenu.add(jMenu2);
					jMenu2.setText("Arquivo de log");
					{
						jMenuItem5 = new JMenuItem();
						jMenu2.add(jMenuItem5);
						jMenuItem5.setText("Abrir");
						jMenuItem5.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								abrirLog(evt);
							}
						});
					}
				}
				{
					jMenu3 = new JMenu();
					jMenu.add(jMenu3);
					jMenu3.setText("Exportar dados");
					{
						jMenuItem14 = new JMenuItem();
						jMenu3.add(jMenuItem14);
						jMenuItem14.setText("Exportar");
						jMenuItem14.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								exportarDados(evt);
							}
						});
					}
					
				}
			}
			pack();
			this.setSize(601, 300);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private void abrirLog(ActionEvent evt) {
		try {	
			System.out.println("Entrou aqui!");
			Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + Estrategia.path);
			p.waitFor();
			System.out.println("Entrou aqui!2");

		} catch (Exception e) {
			JOptionPane.showConfirmDialog(this, "Erro ao abrir o arquivo de log.");
		}
	}
	
	private void abrirGraficoUtilidade(ActionEvent evt) {
		new frmUtilidadeGeracoes(individuosSelecionados, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void abrirGraficoObjetivos(ActionEvent evt) {
		new frmFInad(individuosSelecionados, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void abrirGraficoObjetivosMelhores(ActionEvent evt) {
		new frmGraficoObjetivoMelhores(individuosSelecionados, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void abrirGraficoObjetivosHistorias(ActionEvent evt) {
		new frmValorObjetivoHistorias(individuosSelecionados, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void abrirGraficoSalasSujas(ActionEvent evt) {
		new frmSalasSujas(individuosSelecionados, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}

	private void abrirGraficoObjetivosGeracoes(ActionEvent evt) {

		Enumeration<String> e = Estrategia.objetivos.keys();
		while(e.hasMoreElements()) 
		{
			String chave = e.nextElement();
			new frmObjetivosGeracoes(individuosSelecionados, chave, false, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
			new frmObjetivosGeracoes(individuosSelecionados, chave, true, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
		}
	}
	
	private void abrirGraficoEpisodiosProblematicos(ActionEvent evt) {

		Enumeration<String> e = Estrategia.objetivos.keys();
		while(e.hasMoreElements()) 
		{
			String chave = e.nextElement();
			new frmEpisodiosProblemaGeracoes(individuosSelecionados, chave, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
		}
	}
	
	private void abrirGraficoObjetivosGeracaoIndividual(ActionEvent evt) {

		new frmObjetivoGeracaoIndividual(individuosSelecionados, 1, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
		new frmObjetivoGeracaoIndividual(individuosSelecionados, Estrategia.max_gera, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void abrirGraficoBoxPlotGeracoes(ActionEvent evt) {

		new frmBoxPlotUtilidadeGeracoes(individuosSelecionados, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void abrirGraficoEpisodiosProblematicosCT(ActionEvent evt) {

		Enumeration<String> e = Estrategia.objetivos.keys();
		while(e.hasMoreElements()) 
		{
			String chave = e.nextElement();
			new frmEpisodiosProblemaGeracoesCT(individuosSelecionados, chave, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
		}
		
		new frmEpisodiosProblemaGeracoesCT(individuosSelecionados, "Ambos", txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}

	private void abrirGraficoBloxPlotEpisodiosProblema(ActionEvent evt) {

		Enumeration<String> e = Estrategia.objetivos.keys();
		while(e.hasMoreElements()) 
		{
			String chave = e.nextElement();
			new frmBoxPlotEpisodiosProbGeracoes(individuosSelecionados, chave, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
		}
		new frmBoxPlotEpisodiosProbGeracoes(individuosSelecionados, "Ambos", txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void abrirGraficoBloxPlotObjetivoGeracoes(ActionEvent evt) {
		Enumeration<String> e = Estrategia.objetivos.keys();
		while(e.hasMoreElements()) 
		{
			String chave = e.nextElement();
			new frmBoxPlotObjetivoGeracoes(individuosSelecionados, chave, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
		}
	}
	
	private void abrirGraficoPenalidade(ActionEvent evt) {
		
		new frmPenalidade(individuosSelecionados, txtDiretorio.getText(), txtIdentificacao.getText()).setVisible(true);
	}
	
	private void exportarDados(ActionEvent evt) {
		System.out.println("Iniciou o export..." + Estrategia.path);
		//Exporta log
		try {
			File fromFile = new File(Estrategia.path);
			File toFile = new File(txtDiretorio.getText() + "\\" + txtIdentificacao.getText() + " log.txt");
			FileInputStream inFile = new FileInputStream(fromFile);
			FileOutputStream outFile = new FileOutputStream(toFile);
			FileChannel inChannel = inFile.getChannel();
			FileChannel outChannel = outFile.getChannel();
			int bytesWritten = 0;
			long byteCount = inChannel.size();
			while (bytesWritten < byteCount) {
				bytesWritten += inChannel.transferTo(bytesWritten, byteCount - bytesWritten, outChannel);
			}
			inFile.close();
			outFile.close();
			System.out.println("Terminou o export...");
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void exportaGrafico(JFreeChart chart, String diretorio, String identificacao)
	{
		try {
			FileOutputStream arquivo = new FileOutputStream(diretorio + "\\" + identificacao + " " + chart.getTitle().getText() + ".png");
			ChartUtilities.writeChartAsPNG(arquivo, chart, 843, 519);
		} catch (Exception e) { }
	}
}