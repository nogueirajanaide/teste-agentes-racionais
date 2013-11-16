package testador.thestes;

import testador.thestes.Populacao;

public class EstadoTestador {
	
	//Parâmetro de entrada do teste, um inteiro que identifica a interação inicial entre o agente testado e seu ambiente 
	private int T;
	
	//Parâmetro de entrada do teste, um inteiro que identifica a interação final entre o agente testado e o agente que representa seu ambiente 
	private int N;
	
	//Identificação do programa Agente
	private final String AGENTE = "Agente";
	
	//Identificação do programa Ambiente
	private final String AMBIENTE = "Ambiente";
	
	//Um conjunto de casos de teste
	private Populacao CasosTeste;

	public int getT() {
		return T;
	}

	public void setT(int t) {
		T = t;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public Populacao getCasosTeste() {
		return CasosTeste;
	}

	public void setCasosTeste(Populacao casosTeste) {
		CasosTeste = casosTeste;
	}

	public String getAGENTE() {
		return AGENTE;
	}

	public String getAMBIENTE() {
		return AMBIENTE;
	}
}
