package agente;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import ambiente.Estado;

public class AgReativoSimplesObsLocalRegra  extends AgReativoSimples {
	
	@Override
	public Acao obtemDirecao(Estado estadoAmbiente, ArrayList<Acao> movimentos) {
		
		return movimentos.get(new Random().nextInt(movimentos.size()));
	}
	
	@Override
	public Hashtable obtemRegras() {
		Hashtable regras = new Hashtable(); //Keys: percepcao; Value: acao;
		
		regras.put(0, mapeiaRegra(new Random().nextInt(3))); //0 = Limpo
		regras.put(1, mapeiaRegra(new Random().nextInt(3))); //1 = Sujo
		return regras;
	}
	
	/**
	 * Metodo que obtem um numero randomico e retorna uma acao
	 * @param acao
	 * @return
	 */
	private Acao mapeiaRegra(int acao)
	{
		Acao acaoResultante = Acao.MOVER;
		switch(acao)
		{
			case 0: { acaoResultante = Acao.MOVER; break;}
			case 1: { acaoResultante = Acao.ASPIRAR; break;}
			case 2: { acaoResultante = Acao.NAO_OP; break; }
		}
		return acaoResultante;
	}

}
