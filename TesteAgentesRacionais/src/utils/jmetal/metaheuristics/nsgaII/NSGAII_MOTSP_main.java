//  NSGAII_MOTSP_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro, Juan J. Durillo
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

import utils.jmetal.core.*;
import utils.jmetal.operators.crossover.*;
import utils.jmetal.operators.mutation.*;
import utils.jmetal.operators.selection.*;
import utils.jmetal.problems.*                  ;
import utils.jmetal.problems.DTLZ.*;
import utils.jmetal.problems.ZDT.*;
import utils.jmetal.problems.WFG.*;
import utils.jmetal.problems.LZ09.* ;

import utils.jmetal.util.Configuration;
import utils.jmetal.util.JMException;
import java.io.IOException;
import java.util.* ;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import utils.jmetal.qualityIndicator.QualityIndicator;

/** 
 * Class to configure and execute the NSGA-II algorithm. The settings are aimed
 * at solving the MOTSP problem.
 */

public class NSGAII_MOTSP_main {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

  /**
   * @param args Command line arguments.
   * @throws JMException 
   * @throws IOException 
   * @throws SecurityException 
   * Usage: 
   *      - jmetal.metaheuristics.nsgaII.NSGAII_MOTSP_main
   */
  public static void main(String [] args) throws 
                                  JMException, 
                                  SecurityException, 
                                  IOException, 
                                  ClassNotFoundException {
    Problem   problem   ; // The problem to solve
    Algorithm algorithm ; // The algorithm to use
    Operator  crossover ; // Crossover operator
    Operator  mutation  ; // Mutation operator
    Operator  selection ; // Selection operator
    
    HashMap  parameters ; // Operator parameters
    
    QualityIndicator indicators ; // Object to get quality indicators

    // Logger object and file to store log messages
    logger_      = Configuration.logger_ ;
    fileHandler_ = new FileHandler("NSGAII_main.log"); 
    logger_.addHandler(fileHandler_) ;
    
    indicators = null ;
    problem = new MOTSP("Permutation", "kroA150.tsp","kroB150.tsp");
    
    algorithm = new NSGAII(problem);
    //algorithm = new ssNSGAII(problem);
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize",100);
    algorithm.setInputParameter("maxEvaluations",10000000);
    
    /* Crossver operator */
    parameters = new HashMap() ;
    parameters.put("probability", 0.95) ;
    //crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover", parameters);
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);
    
    /* Mutation operator */
    parameters = new HashMap() ;
    parameters.put("probability", 0.2) ;
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);                    
  
    /* Selection Operator */
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                            

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    // Add the indicator object to the algorithm
    algorithm.setInputParameter("indicators", indicators) ;
    
    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    
    // Result messages 
    logger_.info("Total execution time: "+estimatedTime + "ms");
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");    
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
  
    if (indicators != null) {
      logger_.info("Quality indicators") ;
      logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
      logger_.info("GD         : " + indicators.getGD(population)) ;
      logger_.info("IGD        : " + indicators.getIGD(population)) ;
      logger_.info("Spread     : " + indicators.getSpread(population)) ;
      logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;  
     
      int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
      logger_.info("Speed      : " + evaluations + " evaluations") ;      
    } // if
  } //main
} // NSGAII_main
