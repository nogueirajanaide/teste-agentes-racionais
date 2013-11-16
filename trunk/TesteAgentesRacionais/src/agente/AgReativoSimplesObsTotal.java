package agente;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import ambiente.Estado;

public class AgReativoSimplesObsTotal extends AgReativoSimples {

	ArrayList<Acao> listaMovimentos = new ArrayList<Acao>();
	
	@Override
	public Acao obtemDirecao(Estado estadoAmbiente, ArrayList<Acao> movimentos) {
		
		PosicoesSujas melhorPosicao = null; //Representa a posicao da melhor sala a ser localizada
		ArrayList<PosicoesSujas> posicoesSujas = identificaSalasSujas(estadoAmbiente);
		
		if (posicoesSujas != null && posicoesSujas.size() > 0) {
			
			int melhorQtdePassos = Math.abs(estadoAmbiente.getPosicaoLinha() - posicoesSujas.get(0).linha) + Math.abs(estadoAmbiente.getPosicaoColuna() - posicoesSujas.get(0).coluna);
			int qtdePassos = 0;
			
			for(PosicoesSujas pos : posicoesSujas) {
				
				qtdePassos = Math.abs(estadoAmbiente.getPosicaoLinha() - pos.linha) + Math.abs(estadoAmbiente.getPosicaoColuna() - pos.coluna);
				
				if (qtdePassos <= melhorQtdePassos) {
					melhorPosicao = pos; 
					melhorQtdePassos = qtdePassos; 
				}
			}
			
			//System.out.println("Melhor qtde de passos: " + melhorQtdePassos);
		
			if (melhorPosicao != null) {
				if (melhorPosicao.linha > estadoAmbiente.getPosicaoLinha()) return Acao.PARA_BAIXO;
				if (melhorPosicao.linha < estadoAmbiente.getPosicaoLinha()) return Acao.PARA_CIMA;
				if (melhorPosicao.coluna > estadoAmbiente.getPosicaoColuna()) return Acao.DIREITA;
				if (melhorPosicao.coluna < estadoAmbiente.getPosicaoColuna()) return Acao.ESQUERDA;
			}
		}	
		System.out.println("Movimento randomico");
		return movimentos.get(new Random().nextInt(movimentos.size()));
	}
	
	ArrayList<PosicoesSujas> identificaSalasSujas(Estado estadoAmbiente) {
		ArrayList<PosicoesSujas> posicoesSujas = new ArrayList<PosicoesSujas>();
		for (int i = 0; i < estadoAmbiente.getCenario().length; i++)
			for (int j = 0; j < estadoAmbiente.getCenario().length; j++)
			{
				if (estadoAmbiente.getCenario()[i][j] == 1) //1: Sala suja
					posicoesSujas.add(new PosicoesSujas(i, j));
			}
		return posicoesSujas;
	}
}

class PosicoesSujas
{
	int linha, coluna;
	
	PosicoesSujas(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}
}
