//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package utils.jmetal.metaheuristics.nsgaII;

import java.util.Arrays;

import utils.jmetal.core.*;
import utils.jmetal.util.comparators.CrowdingComparator;
import utils.jmetal.qualityIndicator.QualityIndicator;
import utils.jmetal.util.*;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

/**
 * Esta classe foi modificada para atender a especificacao da abordagem
 */
public class NSGAII extends Algorithm {

	//Parameters
	int populationSize;
	public int evaluations = 0;
	int maxEvaluations;
	
	//Operators
	Operator mutationOperator;
	Operator crossoverOperator;
	Operator selectionOperator;
	
	//Populations
	SolutionSet population;
	SolutionSet offspringPopulation;
	SolutionSet union;
	
	/**
	 * Constructor
	 * @param problem Problem to solve
	 */
	public NSGAII(Problem problem) {
		super (problem) ;
	} // NSGAII

	
	private void readParameters() {
			
		//Read the parameters
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
		
		//Read the operators
		mutationOperator = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		selectionOperator = operators_.get("selection");
	}
	
	/**
	 * Obtem a populacao inicial
	 * @author Raquel Silveira
	 * @return
	 */
	private SolutionSet getInitialSolutionSet() throws ClassNotFoundException {
		
		readParameters();
		if (population == null)
			population = new SolutionSet(populationSize);
		
		// Create the initial solutionSet
		for (int i = 0; i < populationSize; i++)
			population.add(new Solution(problem_));
		
		return population;
	}
	
	/**   
	 * Runs the NSGA-II algorithm.
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 * as a result of the algorithm execution
	 * @throws JMException 
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {

		if (evaluations == 0) {
			population = getInitialSolutionSet();
			evaluations++;
			
			System.out.println("Populacao inicial: ");
			imprimeSolution(population);
		}
		else {
			Distance distance = new Distance();

			// Generations
			if (evaluations < maxEvaluations) {
				
				//The offspring is created if it not to exist
				if (offspringPopulation == null) {
					// Create the offSpring solutionSet      
					offspringPopulation = new SolutionSet(populationSize);
					Solution[] parents = new Solution[2];
					for (int i = 0; i < (populationSize / 2); i++) {
						//obtain parents
						parents[0] = (Solution) selectionOperator.execute(population);
						parents[1] = (Solution) selectionOperator.execute(population);
						Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
						mutationOperator.execute(offSpring[0]);
						mutationOperator.execute(offSpring[1]);
						offspringPopulation.add(offSpring[0]);
						offspringPopulation.add(offSpring[1]);
					}
					
					System.out.println("Prole: ");
					imprimeSolution(offspringPopulation);
					
					return offspringPopulation;
				}
				else {

					// Create the solutionSet union of solutionSet and offSpring
					union = ((SolutionSet) population).union(offspringPopulation);

					System.out.println("Populacao: ");
					imprimeSolution(population);
					
					System.out.println("Prole: ");
					imprimeSolution(offspringPopulation);
					
					System.out.println("Uniao: ");
					imprimeSolution(union);
					
					// Ranking the union
					Ranking ranking = new Ranking(union);

					int remain = populationSize;
					int index = 0;
					SolutionSet front = null;
					population.clear();

					// Obtain the next front
					front = ranking.getSubfront(index);

					while ((remain > 0) && (remain >= front.size())) {
						//Assign crowding distance to individuals
						distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
						
						//Add the individuals of this front
						for (int k = 0; k < front.size(); k++)
							population.add(front.get(k));

						//Decrement remain
						remain = remain - front.size();

						//Obtain the next front
						index++;
						if (remain > 0)
							front = ranking.getSubfront(index);
						
					}

					// Remain is less than front(index).size, insert only the best one
					if (remain > 0) {  // front contains individuals to insert                        
						distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
						front.sort(new CrowdingComparator());
						for (int k = 0; k < remain; k++)
							population.add(front.get(k));
						
						remain = 0;
					}                               
				}

				// Return as output parameter the required evaluations
				setOutputParameter("evaluations", evaluations);

				// Return the first non-dominated front
				Ranking ranking = new Ranking(population);
				//return ranking.getSubfront(0);
				
				System.out.println("Populacao resultante: ");
				imprimeSolution(population);
				
				offspringPopulation = null;
				evaluations++;
			}
		}	
		return population;
	}
	
	//TODO: REMOVER -> metodo de teste
	private void imprimeSolution(SolutionSet solut) {
		for(int i = 0; i < solut.size(); i++) {
			System.out.println("");
			for (int j = 0; solut.get(i).getDecisionVariables() != null && j < solut.get(i).getDecisionVariables().length; j++)
				System.out.print(solut.get(i).getDecisionVariables()[j]);
			System.out.print(" -> "  + solut.get(i).getObjective(0));
		}		
	}
}
