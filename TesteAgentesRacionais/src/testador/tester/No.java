package testador.thestes;

import java.util.ArrayList;
import ambiente.Estado;

/**
 * @author raquel silveira
 * @version 1.0
 * Estrutura de dados que contem: Cenario, Historia e Valor de Utilidade
 **/
public class No {
	
	private Integer[] cenario;
	private ArrayList<Estado> historia = new ArrayList<Estado>();
	private Integer Utilidade;
	
	public Integer[] getCenario() {
		return cenario;
	}
	public void setCenario(Integer[] cenario) {
		this.cenario = cenario;
	}
	public ArrayList<Estado> getHistoria() {
		return historia;
	}
	public void setHistoria(ArrayList<Estado> historia) {
		this.historia = historia;
	}
	public Integer getUtilidade() {
		return Utilidade;
	}
	public void setUtilidade(Integer utilidade) {
		Utilidade = utilidade;
	}
}
