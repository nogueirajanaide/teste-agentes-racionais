package testador.GA;

import java.util.ArrayList;
import java.util.Random;

import testador.thestes.Estrategia;
import testador.thestes.Individuo;
import testador.thestes.Populacao;

import ambiente.Estado;

public class EstrategiaGA extends Estrategia {
		
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem uma populacao para ser testada
	 * @param populacao testada
	 * @return nova populacao
	 * */
	@Override
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