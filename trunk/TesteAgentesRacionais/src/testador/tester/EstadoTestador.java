package testador.thestes;

import testador.thestes.Populacao;

public class EstadoTestador {
	
	//Par�metro de entrada do teste, um inteiro que identifica a intera��o inicial entre o agente testado e seu ambiente 
	private int T;
	
	//Par�metro de entrada do teste, um inteiro que identifica a intera��o final entre o agente testado e o agente que representa seu ambiente 
	private int N;
	
	//Identifica��o do programa Agente
	private final String AGENTE = "Agente";
	
	//Identifica��o do programa Ambiente
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
