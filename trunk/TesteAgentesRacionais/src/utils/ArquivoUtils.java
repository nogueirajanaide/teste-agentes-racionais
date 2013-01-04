package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import testador.Individuo;
import testador.Populacao;

public class ArquivoUtils {
	
	String path;
	
	public ArquivoUtils() {}
	
	public ArquivoUtils(String path) {
		this.path = path;
		//Remove o arquivo, caso exista
		File arquivo = new File(path);
		if (arquivo.exists())
			arquivo.delete();
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que imprime os dados iniciais do arquivo
	 *  */
	public void iniciaImpressao() {
		try {		
			BufferedWriter arquivo = new BufferedWriter(new FileWriter(this.path, true));
			arquivo.write("Teste realizado em " + new Date().toString());
			arquivo.newLine();
			arquivo.newLine();
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que imprime os resultados no arquivo log
	 * @param indice da geracao
	 * @param populacao
	 * @param fitness medio da populacao
	 * @param melhores individuos da populacao
	 * */
	public void imprime1(int geracao, Populacao populacao, double fitnessMedio, Populacao melhoresIndividuos) {
		try {
			BufferedWriter arquivo = new BufferedWriter(new FileWriter(this.path, true));
			arquivo.write("Geracao = " + geracao);
			arquivo.newLine();
			arquivo.write("LNós = " + populacao.toString() + "\n");
			arquivo.newLine();
			arquivo.write("Fitness_medio = " + fitnessMedio + "\n");
			arquivo.newLine();
			arquivo.write("Melhores = " + melhoresIndividuos.toString() + "\n");
			arquivo.newLine();
			arquivo.newLine();
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que imprime os resultados no arquivo log
	 * @param pares formados com os individuos da populacao
	 * @param populacao com individuos cruzados
	 * @param pares para mutacao (formado por IndiceIndividuo e IndiceGene)
	 * @param populacao apos mutacao
	 * */
	public void imprime2(Populacao populacaoPares, Populacao populacaoCruzada, int[] paresMutacao, Populacao populacaoMutada) {
		try {
			BufferedWriter arquivo = new BufferedWriter(new FileWriter(this.path, true));
			arquivo.write("Lpares = " + populacaoPares.toString() + "\n");
			arquivo.newLine();
			arquivo.write("Lcruza = " + populacaoCruzada.toString() + "\n");
			arquivo.newLine();
			arquivo.write("Lpares_muta = " + Arrays.toString(paresMutacao) + "\n");
			arquivo.newLine();
			arquivo.write("Lmuta = " + populacaoMutada.toString() + "\n");
			arquivo.newLine();
			arquivo.newLine();
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que imprime os resultados no arquivo
	 * @param populacao
	 * @param melhores individuos da populacao
	 * */
	public void imprime3(Populacao populacao, Populacao melhoresIndividuos) {
		try {
			BufferedWriter arquivo = new BufferedWriter(new FileWriter(this.path, true));
			arquivo.write("Encontrei uma solução !!!");
			arquivo.newLine();
			arquivo.write("A lista de nós na populacao atual é: ");
			arquivo.newLine();
			arquivo.write("LNós = " + populacao.toString());
			arquivo.newLine();
			arquivo.write("E a lista das melhores soluções é: ");
			arquivo.newLine();
			arquivo.write("ListaSolucoes =  " + melhoresIndividuos.toString());
			arquivo.newLine();
			arquivo.newLine();
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que imprime a historia de um determinado cenario de teste
	 * @param cenario
	 * */
	public void imprimeHistoria(Individuo cenario) {
		try {
			BufferedWriter arquivo = new BufferedWriter(new FileWriter(this.path, true));
			arquivo.write("Cenário de teste: ");
			arquivo.write(cenario.toString());
			arquivo.newLine();
			if (cenario != null && cenario.getHistoria() != null && cenario.getHistoria().size() > 0)
			{ arquivo.write("História: " + Arrays.toString(cenario.getHistoria().toArray())); }
			arquivo.newLine();
			arquivo.newLine();
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

