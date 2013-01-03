package agente;

import java.io.Serializable;

public enum Acao implements Serializable {

	OBTER_ESTADO("Estado"),
	INICIAR("Iniciar"),
	PARAR("Parar"),
	MOVER("Mover"),
	ASPIRAR("Aspirar"),
	DIREITA("Direita"),
	ESQUERDA("Esquerda");
	
	public String acao;
	
	Acao(String acao) {
		this.acao = acao;
	}
}