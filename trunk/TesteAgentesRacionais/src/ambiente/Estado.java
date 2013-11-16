package ambiente;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import testador.thestes.Estrategia;

import agente.Acao;

public class Estado implements Serializable {
	
	public Estado() {}
	
	public Estado(int posicaoLinha, int posicaoColuna, Integer[][] cenario, int qtdeLinhaColuna) {
		this.posicaoLinha = posicaoLinha;
		this.posicaoColuna = posicaoColuna;
		this.cenario = cenario;
		this.qtdeLinhaColuna = qtdeLinhaColuna;
	}
	
	private int posicaoLinha;
	
	private int posicaoColuna;
	
	private int qtdeLinhaColuna;
	
	private boolean penaliza;

	private Integer[][] cenario;
	
	private Acao acao;
	
	public Hashtable<String, Double> pontuacaoObjetivos = new Hashtable<String, Double>();
	
	public Hashtable<String, Boolean> episodioProblema = new Hashtable<String, Boolean>();
	
	public int getPosicaoLinha() {
		return posicaoLinha;
	}

	public void setPosicaoLinha(int posicaoLinha) {
		this.posicaoLinha = posicaoLinha;
	}
	
	public int getPosicaoColuna(){
		return posicaoColuna;
	}
	
	public void setPosicaoColuna(int posicaoColuna){
		this.posicaoColuna = posicaoColuna;
	}

	public int getQtdeLinhaColuna() {
		return qtdeLinhaColuna;
	}
	
	public void setQtdeLinhaColuna(int qtdeLinhaColuna) {
		this.qtdeLinhaColuna = qtdeLinhaColuna;
	}
	
	public boolean isPenaliza() {
		return penaliza;
	}

	public void setPenaliza(boolean penaliza) {
		this.penaliza = penaliza;
	}
	
	public Integer[][] getCenario() {
		return cenario;
	}

	public void setCenario(Integer[][] cenario) {
		this.cenario = cenario;
	}
	
	public Acao getAcao() {
		return acao;
	}
	
	public void setAcao(Acao acao) {
		this.acao = acao;
	}	
	
	@Override
	public String toString() {
		if (cenario != null)
		{
			String result = posicaoLinha + "," + posicaoColuna + " - " + cenario[posicaoLinha][posicaoColuna] + " --> " + acao + " [";
			
			Enumeration<String> e = pontuacaoObjetivos.keys();
			while(e.hasMoreElements())
			{
				String chave = (String)e.nextElement();
				if (pontuacaoObjetivos.containsKey(chave))
					result += "(" + chave + " " + pontuacaoObjetivos.get(chave) + ") ";  
			}
			
			result += (penaliza ? "(Penalidade: " + Estrategia.penalidade + ") " : "") + "] ";
			return result;
		}
		return "";
	}
}
