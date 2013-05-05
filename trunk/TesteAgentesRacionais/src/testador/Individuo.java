package testador;
import java.util.ArrayList;
import java.util.Arrays;

import ambiente.Estado;

public class Individuo implements Cloneable, Comparable<Individuo> {
		
	public Individuo(int qtdeElementos){
		//Define-se que o cenario é uma matriz quadrada, ou seja, possui a mesma qtde de linhas e colunas
		int qtdeLinhaColuna = (int)Math.sqrt(qtdeElementos);
		cenario = new Integer[qtdeLinhaColuna][qtdeLinhaColuna];
	}
	
	/**
	 * Representa o cenario de teste do individuo
	 */
	private Integer[][] cenario;
	
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
		
	/**
	 * Representa a geracao que o individuo foi selecionado
	 * Atributo utilizado para obtencao dos resultados
	 */
	private Integer geracao = null;
	
	/**
	 * Representa se o individuo foi selecionado como melhor
	 * Atributo utilizado para obtencao dos resultados
	 */
	private boolean selecionado;
	
	public void setCenario(Integer[][] cenario) {
		this.cenario = cenario;
	}
	
	public void setValorCenario(int linha, int coluna,  int valor) {
		this.cenario[linha][coluna] = valor;
	}
	
	public Integer[][] getCenario() {
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
	
	public Integer getGeracao() {
		return geracao;
	}
	
	public void setGeracao(Integer geracao) {
		this.geracao = geracao;
	}
	
	public boolean getSelecionado() {
		return selecionado;
	}
	
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	@Override
	public String toString() {
		return Arrays.deepToString(cenario) + (getAvaliacao() != null ? " Fitness: " + getAvaliacao() : "");
	}
	
	@Override
	protected Individuo clone() throws CloneNotSupportedException {
		
		Individuo individuoClone = (Individuo)super.clone();
		individuoClone.cenario = cloneCenario(individuoClone.cenario);
		return individuoClone;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que faz um clone da matriz Integer[][]
	 * @param cenarioOriginal
	 * @return matriz clonada
	 */
	private Integer[][] cloneCenario(Integer[][] original) {  
	    Integer[][] resultado = (Integer[][]) original.clone();  
	    for (int i = 0; i < original.length; i++)  
	        resultado[i] = (Integer[]) original[i].clone();  
	    return resultado;  
	} 
	
	@Override
	public int compareTo(Individuo o) {
		
		return o.getAvaliacao().compareTo(getAvaliacao());
	}
}