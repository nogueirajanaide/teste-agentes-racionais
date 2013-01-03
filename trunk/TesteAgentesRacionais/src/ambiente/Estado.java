package ambiente;

import java.io.Serializable;

import agente.Acao;

public class Estado implements Serializable {
	
	public Estado() {}
	
	public Estado(int posicao, Integer elemento) {
		this.posicao = posicao;
		this.elemento = elemento;
	}
	
	private int posicao;
	
	private Integer elemento;
	
	private Acao acao;
	
	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	public Integer getElemento() {
		return elemento;
	}

	public void setDescricao(Integer elemento) {
		this.elemento = elemento;
	}
	
	public Acao getAcao() {
		return acao;
	}
	
	public void setAcao(Acao acao) {
		this.acao = acao;
	}
	
	@Override
	public String toString() {
		return posicao + " - " + elemento + " --> " + acao;
	}
}
