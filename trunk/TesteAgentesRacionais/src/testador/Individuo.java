package testador;
import java.util.ArrayList;
import java.util.Arrays;

import ambiente.Estado;

public class Individuo implements Cloneable, Comparable<Individuo> {
		
	public Individuo(int qtdeElementos){
		cenario = new Integer[qtdeElementos];
	}
	
	private Integer[] cenario;
	private Integer avaliacao;
	private Integer fitnessAcumulado;
	private ArrayList<Estado> historia = new ArrayList<Estado>();
		
	public void setCenario(Integer[] cenario) {
		this.cenario = cenario;
	}
	
	public void setValorCenario(int indice, int valor) {
		this.cenario[indice] = valor;
	}
	
	public Integer[] getCenario() {
		return cenario;
	}
	
	public void setFitnessAcumulado(Integer fitnessAcumulado) {
		this.fitnessAcumulado = fitnessAcumulado;
	}
	
	public Integer getFitnessAcumulado() {
		return fitnessAcumulado; 
	}
	
	public void setHistoria(ArrayList<Estado> historia) {
		this.historia = historia;
	}
	
	public ArrayList<Estado> getHistoria() {
		return historia;
	}
	
	public Integer getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Integer avaliacao) {
		this.avaliacao = avaliacao;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(cenario) + (getAvaliacao() != null ? " Fitness: " + getAvaliacao() : "");
	}
	
	@Override
	protected Individuo clone() throws CloneNotSupportedException {
		Individuo clone = (Individuo)super.clone();
		clone.cenario = clone.cenario.clone();
		return clone;
	}
	
	@Override
	public int compareTo(Individuo o) {
		return o.getAvaliacao().compareTo(getAvaliacao());
	}
}