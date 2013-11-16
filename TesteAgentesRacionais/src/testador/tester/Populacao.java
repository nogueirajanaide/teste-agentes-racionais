package testador.thestes;

import java.util.ArrayList;
import java.util.Arrays;

import testador.thestes.Individuo;

public class Populacao implements Cloneable {
	
	private ArrayList<Individuo> individuo = new ArrayList<Individuo>(); 

	public void setIndividuo(ArrayList<Individuo> individuo) {
		this.individuo = individuo;
	}
	
	public void setValueIndividuo(int indice, Individuo valor) {
		this.individuo.add(indice, valor);
	}

	public ArrayList<Individuo> getIndividuo() {
		return individuo;
	}	
		
	@Override
	public String toString() {
		return Arrays.toString(individuo.toArray());
	}
	
	@Override
	public Populacao clone() throws CloneNotSupportedException {
		Populacao popClone = (Populacao)super.clone();
		popClone.individuo = cloneIndividuo(popClone.individuo);	
		return popClone;
	}
	
	private static ArrayList<Individuo> cloneIndividuo(ArrayList<Individuo> list) {
	    ArrayList<Individuo> clone = new ArrayList<Individuo>(list.size());
	    for(Individuo item: list)
			try {
				clone.add(item.clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    return clone;
	}
}