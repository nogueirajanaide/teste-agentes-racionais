package testador;
import java.util.ArrayList;
import java.util.Arrays;

import ambiente.Estado;

public class Individuo implements Cloneable, Comparable<Individuo> {
		
	public Individuo(int qtdeElementos){
		cenario = new Integer[qtdeElementos];
	}
	
	/**
	 * Representa o cenario de teste do individuo
	 */
	private Integer[] cenario;
	
	/**
	 * Representa o fitnessAcumulado entre os individuos
	 * Utilizado para o metodo da roleta no cruzamento
	 */
	private Integer fitnessAcumulado;
	
	/**
	 * Representa a melhor historia das repeticoes, ou seja, a historia melhor avaliada
	 */
	private ArrayList<Estado> historia = new ArrayList<Estado>();
	
	/**
	 * Representa a avaliacao da historia selecionada
	 */
	private Integer avaliacao;
	
	/**
	 * Representa as historias das repeticoes do caso de teste
	 */
	public ArrayList<ArrayList<Estado>> listaHistorias = null;
		
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