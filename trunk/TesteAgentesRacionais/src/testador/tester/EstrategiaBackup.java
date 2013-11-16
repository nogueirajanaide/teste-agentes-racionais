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

import testador.thestes.FuncaoAvaliacao;
import testador.thestes.Individuo;
import testador.thestes.FuncaoAvaliacao.Avaliacao;
import utils.ArquivoUtils;

public class EstrategiaBackup{
		
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	public static int geracao = 0, max_gera, max_fit, repeticao, qLinhaColuna, max_interacoes;
	public static Double melhorFitness = 0.0;
	int N, L, Pm, CodAgente, QE;
	int[][] elementos = null;
	public static String path;
	static ArquivoUtils arquivo;
	static Populacao melhoresIndividuos =  new Populacao();
	static ArrayList<FuncaoAvaliacao> funcaoAvaliacao = new ArrayList<FuncaoAvaliacao>();
	//Representa os objetivos do agente (Chave: Objetivo; Valor: Peso)
	public static Hashtable<String, Double> objetivos = new Hashtable<String, Double>();
		
	public EstrategiaBackup() {
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
			CodAgente = Integer.parseInt(doc.getElementsByTagName("CodAgente").item(0).getTextContent());
			Pm = Integer.parseInt(doc.getElementsByTagName("Pm").item(0).getTextContent());
			max_gera = Integer.parseInt(doc.getElementsByTagName("Max_gera").item(0).getTextContent());
			max_fit = Integer.parseInt(doc.getElementsByTagName("Max_fit").item(0).getTextContent());
			max_interacoes = Integer.parseInt(doc.getElementsByTagName("Max_interacoes").item(0).getTextContent());
			path = doc.getElementsByTagName("Path").item(0).getTextContent();
			repeticao = Integer.parseInt(doc.getElementsByTagName("Repeticao").item(0).getTextContent());
			
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
					
					System.out.println(objetivo + " pont: " + Double.parseDouble(doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("pont").getNodeValue()) + " prob " +
							doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("problema").getNodeValue());
					
					System.out.println(avaliacao.pontuacao.get(objetivo).problema);
					System.out.println(Integer.parseInt(doc.getElementsByTagName(objetivo).item(i).getAttributes().getNamedItem("problema").getNodeValue()) == 1);
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
	public Populacao obtemPopulacao(Populacao populacaoAnalisada) {
		
		//System.out.println("obtem Populacao");
		Populacao populacao = null;
		  
		if (geracao == 0) {
			//Gera a populacao inicial
			System.out.println("Pop inicial");
			populacao = geraPopulacaoInicial();
			melhoresIndividuos = selecionaMelhores(populacao, null, geracao);
			//geracao++;
		} else {
			
			//System.out.println("Geracao > 0");
			//if (geracao < Max_gera && melhorFitness <= Max_fit) {
				
				//System.out.println("Entrou no if");
				//geracao++;
				populacao = sucessor(populacaoAnalisada);
				/*System.out.println("Populacao: (obtemPopulacao) " + populacao);
				melhoresIndividuos = selecionaMelhores(populacao, melhoresIndividuos, geracao);
				System.out.println("Melhores individuos: (obtemPopulacao) " + melhoresIndividuos);
				System.out.println("Qtde individuos: (obtemPopulacao) " + melhoresIndividuos.getIndividuo().size());
				melhorFitness = melhoresIndividuos.getIndividuo().get(0).getAvaliacao();*/
			//}		
		}
		
		//Acabou o teste
		if (populacao == null) {
			System.out.println("Melhores indivíduos = " + melhoresIndividuos);
			arquivo.imprime3(populacaoAnalisada, melhoresIndividuos);
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
		
		System.out.println("Entrou aqui!");
		
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
		
		Populacao melhoresIndividuos = new Populacao();
		Double maiorFitness = obtemMelhorFitness(populacao); //Maior fitness da populacao atual
		Double maiorFitnessAnt = obtemMelhorFitness(melhoresAnt); //Maior fitness da populacao anterior

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
								
			//arquivo.imprime2(populacaoPares, populacaoCruzada, paresMutacao, populacaoMutacao);
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
	/*private double obtemFitnessMedio(Populacao populacao) {
		int fitness = 0;
		for(int i = 0; i < populacao.getIndividuo().size(); i++) {
			fitness += (populacao.getIndividuo().get(i).getAvaliacao() != null ? populacao.getIndividuo().get(i).getAvaliacao() : 0);
		}
		return fitness/populacao.getIndividuo().size();
	}*/
	
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
			ind.setFitnessAcumulado(Math.abs(fitness)); //O abs eh adicionado para evitar problemas com valores negativos
		}		
		
		//Seleciona os individuos conforme metodo da roleta
		Populacao pares = new Populacao();
		try {
			for (int i = 0; i < N; i++) {
				
				int selecao = fitness == 0 ? 0 : new Random().nextInt(Math.abs(fitness)); //O abs eh adicionado para evitar problemas com valores negativos
				
				Individuo novoIndividuo = obtemIndividuoFitnessAcumulado(populacao, selecao).clone();
				novoIndividuo.setHistoria(new ArrayList<Estado>());
				novoIndividuo.listaHistorias = null;
				novoIndividuo.setAvaliacao(null);
				novoIndividuo.setHistoria(null);
				
				pares.getIndividuo().add(novoIndividuo);
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
		for (int i = 0; i < N; i+=2) { //N = Numero de individuos
			Integer[][] aux = pares.getIndividuo().get(i).getCenario().clone();
			int Pc = random.nextInt((int)Math.pow(qLinhaColuna,2)-1); //qLinhaColuna ao quadrado, representa a qtde de salas do ambiente
			int linhaFim = (Pc-1)/qLinhaColuna; //representa ate que linha sera percorrido
			int colunaFim = (Pc-1)%qLinhaColuna; //representa ate que coluna sera percorrido
			
			//System.out.println("L: " + linhaFim + " C: " + colunaFim);
			
			//Percorre a matriz para efetuar o cruzamento
			for (int j = 0; j < linhaFim; j++) {
				for (int z = 0; (z < qLinhaColuna && j < linhaFim-1) || (z <= colunaFim && j == linhaFim); z++) { 
					//System.out.println("i: " + i + " j" + j + " z " + z);
					pares.getIndividuo().get(i).getCenario()[j][z] = pares.getIndividuo().get(i+1).getCenario()[j][z];
					pares.getIndividuo().get(i+1).getCenario()[j][z] = aux[j][z];
				}
			}
			
			//System.arraycopy(pares.getIndividuo().get(i+1), 0, pares.getIndividuo().get(i), 0, Pc);
			//System.arraycopy(aux, 0, pares.getIndividuo().get(i+1), 0, Pc);
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
			int linha = (paresMutacao[i+1]-1)/qLinhaColuna;
			int coluna = paresMutacao[i+1]-1 < 0 ? 0 : (paresMutacao[i+1]-1)%qLinhaColuna; 
			
			do { novoCodigo = random.nextInt(QE); }
			while(pares.getIndividuo().get(paresMutacao[i]).getCenario()[linha][coluna] == novoCodigo);
			
			pares.getIndividuo().get(paresMutacao[i]).setValorCenario(linha, coluna, novoCodigo);
		}		
		return pares;
	}
}