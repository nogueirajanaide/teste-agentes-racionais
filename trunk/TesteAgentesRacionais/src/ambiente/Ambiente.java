package ambiente;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import testador.thestes.Individuo;

import agente.Acao;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class Ambiente extends Agent{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	public static Individuo estadoInterno = null;
	public static int maxInteracoes = 0;
	public static int interacoes = 0;
	public static int posicaoAgenteL = 0, posicaoAgenteC = 0; //L = Linha; C = Coluna
	private ACLMessage msg = null;
	
	
	protected void setup() {
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
								
				Acao acao = ver(myAgent, this);
				proximo(acao);
				acao(myAgent);
			}
		});
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.1
	 * Este metodo obtem a mensagem enviada pelo agente
	 * @param myAgent - representa o agente
	 * @param behaviour - representa o comportamento executado pelo agente
	 * @return a acao enviada pelo agente 
	 */
	private Acao ver(Agent myAgent, Behaviour behaviour) {
		
		Acao acao = null;
		try {
			msg = myAgent.receive();
			if (msg != null) {
				acao = (Acao)msg.getContentObject();
			}
			else {
				behaviour.block();
			}	
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		return acao;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Atualiza o estado interno de acordo com a acao enviada pelo agente
	 * @param acao - representa a acao executada pelo agente
	 */
	private void proximo(Acao acao) {
		 
		if (msg != null && acao != null) {
			alteraEstadoInterno(acao);
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que modifica o estado interno do ambiente e responde ao testador a acao realizada
	 */
	private void acao(Agent myAgent) {
		
		try {
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				
				if (interacoes >= maxInteracoes || analisaAmbiente()){
					reply.setContentObject(new Estado(posicaoAgenteL, posicaoAgenteC, null, estadoInterno.getCenario().length));
				} else { 
					reply.setContentObject(new Estado(posicaoAgenteL, posicaoAgenteC, estadoInterno.getCenario().clone(), estadoInterno.getCenario().length));
				}
				
				interacoes++;
				myAgent.send(reply);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que altera o estado interno do ambiente
	 * @param acao do agente
	 **/
	private void alteraEstadoInterno(Acao acao) {
		switch (acao) {
			case ASPIRAR:
				{ estadoInterno.getCenario()[posicaoAgenteL][posicaoAgenteC] = 0; break; }
			case DIREITA:
				{ posicaoAgenteC += 1; break; }
			case ESQUERDA:
				{ posicaoAgenteC -= 1; break; }
			case PARA_CIMA:
				{ posicaoAgenteL -= 1; break;}
			case PARA_BAIXO:
				{ posicaoAgenteL += 1; break; }
		}
	}
	
	/**
	 * Metodo que verifica se acao executada pelo agente eh uma boa acao
	 * @param acao - representa a acao executada pelo agente
	 */
	public static boolean verificaAcao(Estado estado) {
	
		boolean penalizaAcao = false;
		
		switch (estado.getAcao()) {
			case DIREITA: case ESQUERDA: case PARA_CIMA: case PARA_BAIXO:
			{
				List<Posicao> posicoesSujas = Ambiente.identificaSalasSujas(estado);
				
				penalizaAcao = true;
				if (posicoesSujas != null && posicoesSujas.size() > 0) {
					int melhorQtdePassos = posicoesSujas.get(0).qtdePassos;
					
					for(Posicao pos : posicoesSujas)
						if (pos.qtdePassos <= melhorQtdePassos)
							melhorQtdePassos = pos.qtdePassos;
					
					for(Posicao pos : posicoesSujas) 
						if (pos.qtdePassos == melhorQtdePassos) {
							if (Ambiente.identificaMelhorAcao(pos, estado.getAcao(), estado.getPosicaoLinha(), estado.getPosicaoColuna()))
							{
								penalizaAcao = false;
								break;
							}
						}
				}
				break;
			}
			case ASPIRAR:
			{
				penalizaAcao = estado.getCenario()[estado.getPosicaoLinha()][estado.getPosicaoColuna()] == 0;
				//System.out.println("A Agent: " + estado.getAcao().toString() + " " + penalizaAcao);
				break;
			}
			case NAO_OP:
			{
				penalizaAcao = true;
				//System.out.println("A Agent: " + estado.getAcao().toString() + " " + penalizaAcao);
				break;
			}
		}
		return penalizaAcao;
	}
	
	/**
	 * Metodo que identifica se a acao executada pelo ambiente leva a uma sala suja
	 * @param estadoAmbiente
	 * @param pos - representa a posicao de uma sala suja
	 * @param acao - representa a acao executada pelo agente
	 * @return
	 */
	public static boolean identificaMelhorAcao(Posicao pos, Acao acao, int posL, int posC) {
		
		List<Acao> acoesPossiveis = new ArrayList<Acao>();
		if (pos.linha > posL) acoesPossiveis.add(Acao.PARA_BAIXO);
		if (pos.linha < posL) acoesPossiveis.add(Acao.PARA_CIMA);
		if (pos.coluna > posC) acoesPossiveis.add(Acao.DIREITA);
		if (pos.coluna < posC) acoesPossiveis.add(Acao.ESQUERDA);
		
		//System.out.println("A Agent: " + acao.toString() + " A pos: " + acoesPossiveis.toString());
		return acoesPossiveis.contains(acao);
	}
	
	/**
	 * Metodo que identifica as salas sujas no ambiente
	 * @param estadoAmbiente
	 * @return
	 */
	public static ArrayList<Posicao> identificaSalasSujas(Estado estado) {
		ArrayList<Posicao> posicoesSujas = new ArrayList<Posicao>();
		for (int i = 0; i < estado.getCenario().length; i++)
			for (int j = 0; j < estado.getCenario().length; j++)
			{
				if (estado.getCenario()[i][j] == 1) //1: Sala suja
					posicoesSujas.add(new Posicao(i, j, Math.abs(estado.getPosicaoLinha() - i) + Math.abs(estado.getPosicaoColuna() - j)));
			}
		return posicoesSujas;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que verifica se todo o ambiente estah limpo
	 * @return true - se o ambiente estiver limpo
	 **/
	private Boolean analisaAmbiente() {
		
		for (int i = 0; i < estadoInterno.getCenario().length; i++) {
			for (int j = 0; j < estadoInterno.getCenario().length; j++) {
				if (estadoInterno.getCenario()[i][j] != 0) 
					return false;
			}
		}
		return true;
	}
}

class Posicao {
	int linha, coluna, qtdePassos;
	
	Posicao(int linha, int coluna, int qtdePassos) {
		this.linha = linha;
		this.coluna = coluna;
		this.qtdePassos = qtdePassos;
	}
}

