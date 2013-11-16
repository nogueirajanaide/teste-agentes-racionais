package testador.thestes;

import java.util.Hashtable;
import agente.Acao;

public class FuncaoAvaliacao {
	private int elemento;
	private String acao;
	
	public class Avaliacao
	{
		public double pontuacao;
		public boolean problema;
		
		Avaliacao(double pontuacao, boolean problema) {
			this.pontuacao = pontuacao;
			this.problema = problema;
		}
	}
	
	/**
	 * O hashtable eh criado para proporcionar a inclusao da pontuacao por objetivo
	 * Chave: Objetivo, Valor: Avaliacao do objetivo (Pontuacao correspondente e Se o episodio eh problema)
	 */
	public Hashtable<String, Avaliacao> pontuacao = new Hashtable<String, Avaliacao>();
	
	public int getElemento() {
		return elemento;
	}
	
	public void setElemento(int elemento) {
		this.elemento = elemento;
	}
	
	public String getAcao() {
		return acao;
	}
	
	public void setAcao(String acao) {
		this.acao = acao;
	}
}
