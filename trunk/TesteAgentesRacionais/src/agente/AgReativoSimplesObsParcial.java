package agente;

import java.util.ArrayList;
import java.util.Random;

import ambiente.Estado;

public class AgReativoSimplesObsParcial extends AgReativoSimples {

	@Override
	public Acao obtemDirecao(Estado estadoAmbiente, ArrayList<Acao> movimentos) {
		int qtdeCelulas = (int)(((Math.pow(estadoAmbiente.getQtdeLinhaColuna(),2)-1) * raioVisao) / 100);
		int celulasAnalisadas = 0;
		Acao melhorAcao = null;
		int melhorPontuacao = 0, pontuacao = 0;
		
		for (Acao acao : movimentos) {
			if (celulasAnalisadas < qtdeCelulas) {
				System.out.println("Celulas visitadas: " + celulasAnalisadas);
				
				switch (acao) {
					case DIREITA:
						{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna()+1]; break; }
					case ESQUERDA:
						{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna()-1]; break; }
					case PARA_BAIXO:
						{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()+1][estadoAmbiente.getPosicaoColuna()]; break; }
					case PARA_CIMA:
						{ pontuacao = estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()-1][estadoAmbiente.getPosicaoColuna()]; break; }
				}
				if (pontuacao > melhorPontuacao) {
					melhorPontuacao = pontuacao;
					melhorAcao = acao;
				}
				celulasAnalisadas++;
			}
		}
		
		if (melhorAcao == null)
			melhorAcao = movimentos.get(new Random().nextInt(movimentos.size()));
				
		return melhorAcao;
	}
}
