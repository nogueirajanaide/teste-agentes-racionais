package agente;

import java.util.ArrayList;
import java.util.Random;

import ambiente.Estado;

public class AgReativoSimplesObsTotal extends AgReativoSimples {

	@Override
	public Acao obtemDirecao(Estado estadoAmbiente, ArrayList<Acao> movimentos) {
		
		Acao melhorAcao = null;
		int melhorPontuacao = 0, pontuacao = 0;
					
		for (Acao acao : movimentos) {
			switch (acao) {
				case DIREITA:
				{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna() + 1]; break; }
				case ESQUERDA:
				{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna() - 1]; break; }
				case PARA_BAIXO:
				{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()+1][estadoAmbiente.getPosicaoColuna()]; break; }
				case PARA_CIMA:
				{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()-1][estadoAmbiente.getPosicaoColuna()]; break; }
			}
			if (pontuacao > melhorPontuacao) {
				melhorPontuacao = pontuacao;
				melhorAcao = acao;
			}	
		}
		
		if (melhorAcao == null)
			melhorAcao = movimentos.get(new Random().nextInt(movimentos.size()));
				
		return melhorAcao;
	}
}
