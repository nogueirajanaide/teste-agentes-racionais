package testador.thestes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import ambiente.Estado;

import testador.thestes.Populacao;
import utils.ArquivoUtils;

public abstract class Estrategia{
		
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	public static int geracao = 0, max_gera, max_fit, repeticao, qLinhaColuna, max_interacoes;
	public static Double melhorFitness = 0.0;
	public int N, L, Pm, CodAgente, QE;
	protected int[][] elementos = null;
	public static String path;
	public static ArquivoUtils arquivo;
	public static Populacao melhoresIndividuos =  new Populacao();
	public static boolean selecionaIndividuo = false;
	public static ArrayList<FuncaoAvaliacao> funcaoAvaliacao = new ArrayList<FuncaoAvaliacao>();
	//Representa os objetivos do agente (Chave: Objetivo; Valor: Peso)
	public static Hashtable<String, Double> objetivos = new Hashtable<String, Double>();
	public static double penalidade = 0;
		
	public Estrategia() {
		configuraParametros();
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que configura os parametros
	 * */
	protected void configuraParametros() {
		try {
			DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();		
			Document doc = xml.newDocumentBuilder().parse(new File("src\\utils\\config.xml"));
			
			N = Integer.parseInt(doc.getElementsByTagName("N").item(0).getTextContent());
			CodAgente = Integer.parseInt(doc.getElementsByTagName("CodAgente").item(0).getTextContent());
			Pm = Integer.parseInt(doc.getElementsByTagName("Pm").item(0).getTextContent());
			max_gera = Integer.parseInt(doc.getElementsByTagName("Max_gera").item(0).getTextContent());
			max_fit = Integer.parseInt(doc.getElementsByTagName("Max_fit").item(0).getTextContent());
			max_interacoes = Integer.parseInt(doc.getElementsByTagName("Max_interacoes").item(0).getTextContent());
			path = doc.getElementsByTagName("Path").item(0).getTextContent();
			repeticao = Integer.parseInt(doc.getElementsByTagName("Repeticao").item(0).getTextContent());
			penalidade = Integer.parseInt(doc.getElementsByTagName("Penalidade").item(0).getTextContent());
			
			QE = doc.getElementsByTagName("Qtde").getLength();			
			elementos = new int[QE][2]; //coluna 1: qtde; coluna 2: codigo do elemento
			for (int i = 0; i < doc.getElementsByTagName("Qtde").getLength(); i++) {
				elementos[i][0] = Integer.parseInt(doc.getElementsByTagName("Qtde").item(i).getTextContent());
				L += elementos[i][0];
				elementos[i][1] = Integer.parseInt(doc.getElementsByTagName("Codigo").item(i).getTextContent());
			}
			qLinhaColuna = (int)Math.sqrt(L);
			
			//Obtem os objetivos com sua respectiva pontuacao
			for (int i = 0; i <  doc.getElementsByTagName("Objetivo").getLength(); i++)
				objetivos.put(doc.getElementsByTagName("Objetivo").item(i).getAttributes().getNamedItem("nome").getNodeValue(),
						Double.parseDouble(doc.getElementsByTagName("Objetivo").item(i).getAttributes().getNamedItem("peso").getNodeValue()));
			
			//Obtem as medidas de avaliacao
			for (int i = 0; i <  doc.getElementsByTagName("Medida").getLength(); i++) {
				FuncaoAvaliacao avaliacao = new FuncaoAvaliacao();
				avaliacao.setElemento(Integer.parseInt(doc.getElementsByTagName("Elemento").item(i).getTextContent()));
				avaliacao.setAcao(doc.getElementsByTagName("Acao").item(i).getTextContent());
				
				Enumeration<String> e = objetivos.keys();
				while(e.hasMoreElements())
				{
					String objetivo = (String)e.nextElement();
					avaliacao.pontuacao.put(objetivo, 
							avaliacao. new Avaliacao(Double.parseDouble(doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("pont").getNodeValue()),
										  (Integer.parseInt(doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("problema").getNodeValue()) == 1 ? true : false)));
					
					/*System.out.println(objetivo + " pont: " + Double.parseDouble(doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("pont").getNodeValue()) + " prob " +
							doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("problema").getNodeValue());
					
					System.out.println(avaliacao.pontuacao.get(objetivo).problema);
					System.out.println(Integer.parseInt(doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("problema").getNodeValue()) == 1);*/
				}
				funcaoAvaliacao.add(avaliacao);
			}
		
			arquivo = new ArquivoUtils(path);
			arquivo.iniciaImpressao();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem uma populacao para ser testada
	 * @param populacao testada
	 * @return nova populacao
	 * */
	public abstract Populacao obtemPopulacao(Populacao populacaoAnalisada);
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que gera a populacao inicial de forma randomica (de acordo com a qtde de salas limpas, sujeira, obstaculos, lixeira e tomada)
	 * @return populacao inicial
	 * */
	public Populacao geraPopulacaoInicial() {
		
		Populacao populacaoInicial = new Populacao();			
		for (int j = 0; j < N; j++) {
			populacaoInicial.getIndividuo().add(new Individuo(L));
			//Adiciona o agente na primeira posicao do cenario
			//populacaoInicial.getIndividuo().get(j).getCenario()[0][0] = CodAgente;
			
			for(int i = 0; i < QE; i++)
				preencheCenario(populacaoInicial.getIndividuo().get(j), elementos[i][0], elementos[i][1]);
			
		}
		
		System.out.println(populacaoInicial.getIndividuo());
		
		return populacaoInicial;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que aloca randomica os elementos no cenario
	 * @param individuo
	 * @param quantidade de elementos
	 * @param codigo do elemento a ser inserido no cenario
	 * @param indice do individuo
	 * @return individuo com alocacao dos elementos no cenario
	 * */
	private Individuo preencheCenario(Individuo individuo, int qtde, int codigo){
		
		Random r = new Random();
		int randomLinha, randomColuna;		
		
		for(int j = 0; j < qtde; j++) {
			do {
				randomLinha = r.nextInt(qLinhaColuna);
				randomColuna = r.nextInt(qLinhaColuna);				
			} while (individuo.getCenario()[randomLinha][randomColuna] != null);
			individuo.setValorCenario(randomLinha, randomColuna, codigo);
		}
		return individuo;
	}
		
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que seleciona entre os individuos da populacao atual e da populacao anterior os que possuem o maior fitness
	 * @param populacao
	 * @param melhores da populacao anterior
	 * @param indice da geracao
	 * @param populacao com os individuos de maior fitness entre a populacao e a populacao anterior
	 * */
	public Populacao selecionaMelhores(Populacao populacao, Populacao melhoresAnt, int geracao) {
		
		selecionaIndividuo = true;
		Populacao melhoresIndividuos = new Populacao();
		Double maiorFitness = populacao != null ? obtemMelhorFitness(populacao) : null; //Maior fitness da populacao atual
		Double maiorFitnessAnt = melhoresAnt != null ? obtemMelhorFitness(melhoresAnt): null; //Maior fitness da populacao anterior

		//O melhor individuo eh aquele que possui o maior fitness
		//Verifica se os individuos da populacao anterior sao melhores que os da populacao atual
		//Se for, adiciona os individuos da populacao anterior na lista dos melhores individuos
		if (maiorFitnessAnt != null && maiorFitness != null && maiorFitnessAnt >= maiorFitness) {
			for(Individuo ind : melhoresAnt.getIndividuo()){
				if (ind.getAvaliacao() != null && ind.getAvaliacao() == maiorFitnessAnt &&
						!verificaMelhorIndividuoRepetido(melhoresIndividuos, ind) )
					melhoresIndividuos.getIndividuo().add(ind);
			}
		}
		
		//Verifica se os individuos da populacao atual sao melhores que a populacao anterior
		//Se for, adiciona os individuos da populacao atual na lista dos melhores individuos
		if (maiorFitnessAnt == null || (maiorFitness != null && maiorFitnessAnt != null) && 
				maiorFitness > maiorFitnessAnt)
		{
			for(Individuo ind : populacao.getIndividuo()){
				if (ind.getAvaliacao() != null && ind.getAvaliacao() == maiorFitness &&
						!verificaMelhorIndividuoRepetido(melhoresIndividuos, ind) )
					melhoresIndividuos.getIndividuo().add(ind);
			}
		}

		//arquivo.imprime1(geracao, populacao, obtemFitnessMedio(populacao), melhoresIndividuos);
		return melhoresIndividuos;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que verifica se o individuo ja foi inserido na lista dos melhores individuos
	 * @param melhoresIndividuos
	 * @param individuo
	 * @return true -> ja foi inserido na lista
	 */
	private boolean verificaMelhorIndividuoRepetido(Populacao melhoresIndividuos, Individuo individuo) {
		for(Individuo ind : melhoresIndividuos.getIndividuo()) {
			if (ind.getCenario().equals(individuo.getCenario())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem o melhor fitness entre os individuos
	 * como a funcao de avaliacao eh de maximizacao, o melhor fitness eh o maior
	 * TODO: Analisar para deixar uma funcao mais generica
	 * @param populacao
	 * @return melhor fitness entre os individuos
	 * */
	private Double obtemMelhorFitness(Populacao populacao) {
		Double melhorFitness = null;
		for(Individuo i : populacao.getIndividuo()) {
			
			//Define o melhorFitness para o primeiro individuo
			if (melhorFitness == null)
				melhorFitness = i.getAvaliacao();
						
			if (i.getAvaliacao() != null && i.getAvaliacao() > melhorFitness)
				melhorFitness = i.getAvaliacao();
		}
		return melhorFitness;
	}
}