package agente;

import java.util.ArrayList;
import java.util.Random;

import ambiente.Estado;

public class AgReativoSimplesObsLocal extends AgReativoSimples {

	@Override
	public Acao obtemDirecao(Estado estadoAmbiente, ArrayList<Acao> movimentos) {
		
		return movimentos.get(new Random().nextInt(movimentos.size()));
	}
}
