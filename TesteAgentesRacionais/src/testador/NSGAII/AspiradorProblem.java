package testador.NSGAII;

import testador.thestes.Estrategia;
import testador.thestes.Populacao;
import utils.jmetal.core.Problem;
import utils.jmetal.core.Solution;
import utils.jmetal.core.Variable;
import utils.jmetal.encodings.solutionType.ArrayRealSolutionType;
import utils.jmetal.encodings.solutionType.BinaryRealSolutionType;
import utils.jmetal.encodings.solutionType.IntSolutionType;
import utils.jmetal.encodings.solutionType.RealSolutionType;
import utils.jmetal.util.JMException;
import utils.jmetal.util.wrapper.XReal;

public class AspiradorProblem extends Problem {
	
	private static final String EXPORT_PATH = Estrategia.path;
	private static int casoTesteCont = 0;
	
	public AspiradorProblem(int qtdeElementos) throws ClassNotFoundException {
		
		numberOfVariables_  = qtdeElementos;
		numberOfObjectives_ = 1;    
		problemName_        = "AspiradorProblem";
		
		solutionType_ = new IntSolutionType(this);
	    length_       = new int[numberOfVariables_];
	    lowerLimit_ = new double[numberOfVariables_]; //TODO: Verificar se eh isso mesmo
		upperLimit_ = new double[numberOfVariables_]; //TODO: Verificar se eh isso mesmo
		
		for(int i=0;i<numberOfVariables_;i++) {
	    	lowerLimit_[i] = 0; //TODO: Verificar se eh isso mesmo: O valor do menor elemento
			upperLimit_[i] = 1; //TODO: Verificar se eh isso mesmo: O valor do maior elemento
	    }
	}

	@Override
	public void evaluate(Solution solution) throws JMException {
		
		//A avaliacao dos individuos eh realizada pelo testador e atribuida a solution em avalia - EstrategiaNSGAII
	}
}
