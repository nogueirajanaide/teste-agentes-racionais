package testador.NSGAII;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import testador.thestes.Estrategia;
import testador.thestes.Individuo;
import testador.thestes.Populacao;
import utils.jmetal.core.Operator;
import utils.jmetal.core.Problem;
import utils.jmetal.core.SolutionSet;
import utils.jmetal.metaheuristics.nsgaII.NSGAII;
import utils.jmetal.operators.crossover.CrossoverFactory;
import utils.jmetal.operators.mutation.MutationFactory;
import utils.jmetal.operators.selection.SelectionFactory;
import utils.jmetal.util.Configuration;
import utils.jmetal.util.JMException;


public class EstrategiaNSGAII extends Estrategia {

	public static Logger      logger_ ;      // Logger object
	public static FileHandler fileHandler_ ; // FileHandler object
	public static NSGAII algorithm = null;   //Algoritmo
	SolutionSet solutionSet = null;

	@Override
	protected void configuraParametros() {
		super.configuraParametros();
		
		try {
			// The problem to solve
			Problem problem = new AspiradorProblem(L);
			
			// The algorithm to use
			algorithm = new NSGAII(problem);
			algorithm.setInputParameter("populationSize", N); //Qtde de individuos na populacao
			algorithm.setInputParameter("maxEvaluations", max_gera); //Qtde maxima de geracoes
			
			HashMap parameters = null;
			
			// Crossover operator
			parameters = new HashMap();
			parameters.put("probability", 0.9);
			//parameters.put("distributionIndex",20.0);
			Operator  crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters); 
			
			// Mutation operator
			parameters = new HashMap() ;
			//parameters.put("probability", 0.1);
			parameters.put("probability", Pm/100.0); //TODO: Ou 1/Pm???
			//parameters.put("distributionIndex",20.0);
			Operator  mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters); 
			
			// Selection operator
			Operator  selection = SelectionFactory.getSelectionOperator("BinaryTournament", null); 
			
			// Add the operators to the algorithm
			algorithm.addOperator("crossover",crossover);
			algorithm.addOperator("mutation",mutation);
			algorithm.addOperator("selection",selection);
			
			// Logger object and file to store log messages
			logger_      = Configuration.logger_ ;
			fileHandler_ = new FileHandler("EstrategiaNSGAII.log"); 
			logger_.addHandler(fileHandler_) ;			 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gera a populacao inicial utilizando o NSGA-II
	 * @author: Raquel Silveira
	 */
	@Override
	public Populacao geraPopulacaoInicial() {
	
		return geraPopulacao(null);
	}
	
	@Override
	public Populacao obtemPopulacao(Populacao populacaoAnalisada) {
		try {
			return geraPopulacao(populacaoAnalisada);
			/*for (int i = 0; i < populacaoAnalisada.getIndividuo().size(); i++)
				System.out.println("Ind " + i + ": Fitness: " + populacaoAnalisada.getIndividuo().get(i).getAvaliacao());
			
			
			// Execute the Algorithm
		    long initTime = System.currentTimeMillis();
		    SolutionSet population = algorithm.execute(); //TODO: Aqui eh o ponto que executa o algoritmo!!!!
		    long estimatedTime = System.currentTimeMillis() - initTime;
		    
		    System.out.println("\nTamanho da pop: " +population.size());
		    for(int i = 0; i < population.size(); i++)
		    	System.out.println("Pop: " + Arrays.toString(population.get(i).getDecisionVariables()) + " Fitn: " + population.get(i).getFitness());
		    
		    
			// Result messages 
			System.out.println("Total execution time: "+estimatedTime + "ms");
			System.out.println("Variables values have been writen to file VAR");
			population.printVariablesToFile("VAR");    
			System.out.println("Objectives values have been writen to file FUN");
			population.printObjectivesToFile("FUN");*/

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Populacao geraPopulacao(Populacao populacaoAnalisada) {
		
		Populacao populacao = null;
		try {
			if (solutionSet != null && populacaoAnalisada != null) {
				avalia(solutionSet, populacaoAnalisada);
				System.out.println("Pop anterior: " + decode(solutionSet));
			}
			
			solutionSet = algorithm.execute();
			populacao = decode(solutionSet); 
			
			solutionSet.printVariablesToFile("VAR");    
			solutionSet.printObjectivesToFile("FUN");
		}
		catch (Exception e) 
		{ e.printStackTrace(); }
		
		//System.out.println(populacao.getIndividuo());
		return populacao;
	}
	
	/**
	 * Metodo que converte um SolutionSet para uma Populacao
	 * @author Raquel Silveira
	 * @param solucao
	 * @return
	 * @throws JMException
	 */
	public Populacao decode(SolutionSet solutionSet) throws JMException {
		
		Populacao populacao = new Populacao();
		for(int i = 0; i < solutionSet.size(); i++) {
			Individuo ind = new Individuo(L);
			for(int j = 0; j < solutionSet.get(i).getDecisionVariables().length; j++)
				ind.setValorCenario(j/qLinhaColuna, j%qLinhaColuna, (int)solutionSet.get(i).getDecisionVariables()[j].getValue());
				
			populacao.getIndividuo().add(ind);
		}
		return populacao;
	}
	
	/**
	 * Atribui a avaliacao da populacao para o solution
	 * @author Raquel Silveira
	 * @param solutionSet
	 * @param populacao
	 */
	public void avalia(SolutionSet solutionSet, Populacao populacao) {
		
		for(int i = 0; i < populacao.getIndividuo().size(); i++) {
			solutionSet.get(i).setObjective(0, -populacao.getIndividuo().get(i).getAvaliacao());
			//System.out.println(populacao.getIndividuo().get(i).getAvaliacao());
		}
	}
	
	@Override
	public Populacao selecionaMelhores(Populacao populacao,
			Populacao melhoresAnt, int geracao) {
		
		//System.out.println("Seleciona melhores: " + geracao + "  " + algorithm.evaluations);
		if (Estrategia.geracao == algorithm.evaluations)
			return super.selecionaMelhores(populacao, melhoresAnt, geracao);
		else
		{
			--Estrategia.geracao;
			selecionaIndividuo = false;
		}
		
		//System.out.println("Seleciona melhores: " + geracao + "  " + algorithm.evaluations);
		return melhoresAnt;
	}
}