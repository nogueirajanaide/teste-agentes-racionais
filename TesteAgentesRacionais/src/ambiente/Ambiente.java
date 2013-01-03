package ambiente;

import java.io.IOException;

import agente.Acao;
import testador.Individuo;
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
	public static int posicaoAgente = 0;
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
				if (analisaAmbiente()){
					reply.setContentObject(new Estado(posicaoAgente, -1));
				} else {
					reply.setContentObject(new Estado(posicaoAgente, estadoInterno.getCenario()[posicaoAgente]));
				}
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
				{ estadoInterno.getCenario()[posicaoAgente] = 0; break; }
			case DIREITA:
				{ posicaoAgente += 1; break; }
			case ESQUERDA:
				{ posicaoAgente -= 1; break; }
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que verifica se todo o ambiente estah limpo
	 * @return true - se o ambiente estiver limpo
	 **/
	private Boolean analisaAmbiente() {
		
		for(Integer item : estadoInterno.getCenario()) {
			if (item != 0)
				return false;
		}
		return true;
	}
}
