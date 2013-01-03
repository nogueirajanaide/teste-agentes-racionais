package testador;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import utils.ArquivoUtils;

public class Estrategia{
		
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	static int geracao = 0, max_gera, melhorFitness, max_fit;
	int N, L, Pm, CodAgente, QE;
	int[][] elementos = null;
	String path;
	static ArquivoUtils arquivo;
	static Populacao melhoresIndividuos =  new Populacao();
	static ArrayList<FuncaoAvaliacao> funcaoAvaliacao = new ArrayList<FuncaoAvaliacao>();
		
	public Estrategia() {
		configuraParametros();
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que configura os parametros
	 * */
	private void configuraParametros() {
		try {
			DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();		
			Document doc = xml.newDocumentBuilder().parse(new File("src\\utils\\config.xml"));
			
			N = Integer.parseInt(doc.getElementsByTagName("N").item(0).getTextContent());
			L = Integer.parseInt(doc.getElementsByTagName("L").item(0).getTextContent());
			CodAgente = Integer.parseInt(doc.getElementsByTagName("CodAgente").item(0).getTextContent());
			Pm = Integer.parseInt(doc.getElementsByTagName("Pm").item(0).getTextContent());
			max_gera = Integer.parseInt(doc.getElementsByTagName("Max_gera").item(0).getTextContent());
			max_fit = Integer.parseInt(doc.getElementsByTagName("Max_fit").item(0).getTextContent());
			path = doc.getElementsByTagName("Path").item(0).getTextContent();
			QE = Integer.parseInt(doc.getElementsByTagName("QE").item(0).getTextContent());
			
			elementos = new int[QE][2]; //coluna 1: qtde; coluna 2: codigo do elemento
			for (int i = 0; i < doc.getElementsByTagName("Qtde").getLength(); i++) {
				elementos[i][0] = Integer.parseInt(doc.getElementsByTagName("Qtde").item(i).getTextContent());
				elementos[i][1] = Integer.parseInt(doc.getElementsByTagName("Codigo").item(i).getTextContent());
			}
			
			//Obtem as medidas de avaliacao
			for (int i = 0; i <  doc.getElementsByTagName("Medida").getLength(); i++) {
				FuncaoAvaliacao avaliacao = new FuncaoAvaliacao();
				avaliacao.setElemento(Integer.parseInt(doc.getElementsByTagName("Elemento").item(i).getTextContent()));
				avaliacao.setAcao(doc.getElementsByTagName("Acao").item(i).getTextContent());
				avaliacao.setPontuacao(Integer.parseInt(doc.getElementsByTagName("Pontuacao").item(i).getTextContent()));
				
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
	public Populacao obtemPopulacao(Populacao populacaoAnalisada) {
		
		System.out.println("obtem Populacao");
		Populacao populacao = null;
		  
		if (geracao == 0) {
			//System.out.println("Geracao = 0");
			//Gera a populacao inicial
			populacao = geraPopulacaoInicial();
			melhoresIndividuos = selecionaMelhores(populacao, melhoresIndividuos, geracao);
			//geracao++;
		} else {
			
			//System.out.println("Geracao > 0");
			//if (geracao < Max_gera && melhorFitness <= Max_fit) {
				
				System.out.println("Entrou no if");
				//geracao++;
				populacao = sucessor(populacaoAnalisada);
				melhoresIndividuos = selecionaMelhores(populacao, melhoresIndividuos, geracao);
				melhorFitness = melhoresIndividuos.getIndividuo().get(0).getAvaliacao();
			//}		
		}
		
		//Acabou o teste
		if (populacao == null) {
			arquivo.imprime3(populacaoAnalisada, melhoresIndividuos);
			System.out.println("Melhores indivíduos = " + melhoresIndividuos);	
		}
		return populacao;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que gera a populacao inicial de forma randomica (de acordo com a qtde de salas limpas, sujeira, obstaculos, lixeira e tomada)
	 * @return populacao inicial
	 * */
	public Populacao geraPopulacaoInicial() {
		Populacao populacaoInicial = new Populacao();			
		for (int j = 0; j < N; j++) {
			populacaoInicial.getIndividuo().add(new Individuo(L+1)); //+1 para incluir o agente
			//Adiciona o agente na primeira posicao do cenario
			populacaoInicial.getIndividuo().get(j).getCenario()[0] = CodAgente;
			
			for(int i = 0; i < QE; i++)
				preencheCenario(populacaoInicial.getIndividuo().get(j), elementos[i][0], elementos[i][1], j);		
		}
		
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
	private Individuo preencheCenario(Individuo individuo, int qtde, int codigo, int linha){
		Random r = new Random();
		int numRandom;
		for(int j = 0; j < qtde; j++) {
			do {
				numRandom = r.nextInt(L);
			} while (individuo.getCenario()[numRandom+1] != null); //+1 pq o agente estah na posicao 0
			individuo.setValorCenario(numRandom+1, codigo);
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
		int maiorFitness = obtemMaiorFitness(populacao);
		Populacao melhoresIndividuos = new Populacao();
		for(Individuo ind : populacao.getIndividuo()){
			if (ind.getAvaliacao() == maiorFitness)
				melhoresIndividuos.getIndividuo().add(ind);
		}
		
		arquivo.imprime1(geracao, populacao, obtemFitnessMedio(populacao), melhoresIndividuos);
		return melhoresIndividuos;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem o maior fitness entre os individuos
	 * @param populacao
	 * @return maior fitness entre os individuos
	 * */
	private int obtemMaiorFitness(Populacao populacao) {
		int maiorFitness = 0;
		for(Individuo i : populacao.getIndividuo()) {
			if (i.getAvaliacao() > maiorFitness)
				maiorFitness = i.getAvaliacao();
		}
		return maiorFitness;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem a populacao "sucessora"
	 * @param populacao
	 * @return populacao "sucessora"
	 * */
	private Populacao sucessor(Populacao populacao) {
		try {		
			//Formacao de pares
			Populacao populacaoPares = formaPares(populacao);
			
			//Cruzamento
			Populacao populacaoCruzada = cruzamento(populacaoPares.clone());
			
			//Mutacao
			int[] paresMutacao = obtemParesMutacao();		
			Populacao populacaoMutacao = mutacao(populacaoCruzada.clone(), paresMutacao);
								
			arquivo.imprime2(populacaoPares, populacaoCruzada, paresMutacao, populacaoMutacao);
			return populacaoMutacao;
		}
		catch(CloneNotSupportedException e) {}
		return null;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem a media do fitness dos individuos de uma populacao
	 * @param populacao
	 * @return fitness medio dos individuos
	 * */
	private double obtemFitnessMedio(Populacao populacao) {
		int fitness = 0;
		for(int i = 0; i < populacao.getIndividuo().size(); i++) {
			fitness += populacao.getIndividuo().get(i).getAvaliacao();
		}
		return fitness/populacao.getIndividuo().size();
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que forma pares de individuos de acordo com o metodo da roleta
	 * @param populacao
	 * @return pares entre os individuos da populacao
	 * */
	private Populacao formaPares(Populacao populacao) {
		//Obtem o fitness acumulado de cada individuo
		int fitness = 0;
		for (Individuo ind : populacao.getIndividuo()) {
			fitness += ind.getAvaliacao(); 
			ind.setFitnessAcumulado(fitness);
		}		
		
		//Seleciona os individuos conforme metodo da roleta
		Populacao pares = new Populacao();
		try {
			Random random = new Random();
			for (int i = 0; i < N; i++) {
				int selecao = random.nextInt(fitness);
				pares.getIndividuo().add(obtemIndividuoFitnessAcumulado(populacao, selecao).clone());	
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return pares;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem o individuo que contem o fitness acumulado de acordo com o numero selecionado 
	 * @param populacao
	 * @param numero selecionado
	 * @return individuo
	 * */
	private Individuo obtemIndividuoFitnessAcumulado(Populacao populacao, int selecao) {
		Individuo individuoSelecionado = null;
		for(int i = 0; i < populacao.getIndividuo().size(); i++) {
			if (i == 0) {
				if (selecao <= populacao.getIndividuo().get(i).getFitnessAcumulado()) {
					individuoSelecionado = populacao.getIndividuo().get(i); break; }
			} else {
				if (selecao > populacao.getIndividuo().get(i-1).getFitnessAcumulado() &&
					selecao <= populacao.getIndividuo().get(i).getFitnessAcumulado()) {
					individuoSelecionado = populacao.getIndividuo().get(i); break; }
			}
		}
		return individuoSelecionado;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que cruza os pares de individuos, trocando os Pc primeiros genes entre os pares 
	 * o Pc eh obtido aleatoriamente
	 * @param populacao (em pares)
	 * @return populacao cruzada
	 * */
	private Populacao cruzamento(Populacao pares) {
		Random random = new Random();
		for (int i = 0; i < N; i+=2) {
			Integer[] aux = pares.getIndividuo().get(i).getCenario().clone();
			int Pc = random.nextInt(N);
			for (int j = 1; j <= Pc; j++) {
				pares.getIndividuo().get(i).getCenario()[j] = pares.getIndividuo().get(i+1).getCenario()[j];
				pares.getIndividuo().get(i+1).getCenario()[j] = aux[j];
			}
		}
		return pares;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem os pares para realizar a mutacao - os pares sao constituidos de (IndiceIndividuo, IndiceGene)
	 * @return pares da mutacao
	 * */
	private int[] obtemParesMutacao() {
		int qtdePares = Pm; //TODO: Math.round((N * L * Pm) / 100f) + 1;
		int[] paresMutacao = new int[qtdePares * 2];
		Random random = new Random();
		for (int i = 0; i < (qtdePares*2); i+=2) {
			paresMutacao[i] = random.nextInt(N);
			paresMutacao[i+1] = random.nextInt(L);
		}
		return paresMutacao;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que que realiza a mutacao na populacao
	 * a mutacao ocorre trocando o gene do individuo por um outro gerado randomicamente 
	 * @return populacao com mutacao
	 * */
	private Populacao mutacao(Populacao pares, int[] paresMutacao) {
		Random random = new Random();
		int novoCodigo = 0;
		for(int i = 0; i < paresMutacao.length; i+=2) {
			do { novoCodigo = random.nextInt(QE); }
			while(pares.getIndividuo().get(paresMutacao[i]).getCenario()[paresMutacao[i+1]+1] == novoCodigo);
			pares.getIndividuo().get(paresMutacao[i]).setValorCenario(paresMutacao[i+1]+1, novoCodigo);
		}		
		return pares;
	}
}