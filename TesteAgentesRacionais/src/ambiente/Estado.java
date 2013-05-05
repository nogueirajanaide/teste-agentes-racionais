package ambiente;

import java.io.Serializable;

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
	
	private Integer[][] cenario;
	
	private Acao acao;
	
	private int pontuacao;
	
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
	
	public int getPontuacao() {
		return pontuacao;
	}
	
	public void setPontuacao(int pontuacao) {
		this.pontuacao = pontuacao;
	}
	
	@Override
	public String toString() {
		if (cenario != null)
			return posicaoLinha + "," + posicaoColuna + " - " + cenario[posicaoLinha][posicaoColuna] + " --> " + acao + " [" + pontuacao + "] ";
		return "";
	}
}
