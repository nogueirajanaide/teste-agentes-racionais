package ambiente;

import agente.Acao;
import testador.Individuo;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Ambiente extends Agent{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	public static Individuo estadoInterno = null;
	public static int posicaoAgente = 0;
	
	protected void setup() {
		addBehaviour(new CyclicBehaviour(this) {
			public void action() { 
				//System.out.println("AGENTE AMBIENTE - RECEBE MSG");
				try {
					ACLMessage msg = myAgent.receive();
					if (msg != null) {
						ACLMessage reply = msg.createReply();
						Acao acao = (Acao)msg.getContentObject();
						alteraEstadoInterno(acao);
					
						if (analisaAmbiente()){
							reply.setContentObject(new Estado(posicaoAgente, -1));
						} else {
							reply.setContentObject(new Estado(posicaoAgente, estadoInterno.getCenario()[posicaoAgente]));
						}
					
						myAgent.send(reply);
						//System.out.println("AMBIENTE ENVIA MSG PARA " + msg.getSender().getLocalName());
						
					} else
						block();
						//System.out.println("AMBIENTE BLOQUEADO!");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}) ;
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
