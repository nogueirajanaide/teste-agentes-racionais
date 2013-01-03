package agente;

import java.util.Random;

import ambiente.Estado;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AspiradorReativoSimples extends Agent {
	
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = -2385358617391625935L;
	
	private Hashtable regras = null;
	
	protected void setup() {
				
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				//System.out.println("AGENTE TESTADO - RECEBE MSG");
				try {	
					ACLMessage msg = myAgent.receive();
										
					if (msg != null) {						
					
						if (msg.getContentObject().getClass().isEnum() && (Acao)msg.getContentObject() == Acao.INICIAR) {
							
							System.out.println("AGENTE TESTADO - RECEBE MSG PARA INICIAR");
							
							ACLMessage reply = msg.createReply();
							Estado estado = new Estado();
							estado.setAcao(Acao.OBTER_ESTADO);
							reply.setContentObject(estado);
							myAgent.send(reply);
							
							//System.out.println("AGENTE TESTADO ENVIA MSG PARA " + msg.getSender().getLocalName());
							
						} else {
							Estado estadoAmbiente = (Estado)msg.getContentObject();
							estadoAmbiente.setAcao(obtemAcao(estadoAmbiente));					
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.INFORM);
							reply.setContentObject(estadoAmbiente);
							myAgent.send(reply);
				
							//System.out.println("AGENTE TESTADO ENVIA MSG PARA " + msg.getSender().getLocalName());
							if (estadoAmbiente.getAcao() == Acao.PARAR) { block(); }//System.out.println("AGENTE TESTADO BLOQUEADO!");}
						}
					} else {
						
						block();
						//System.out.println("AGENTE TESTADO BLOQUEADO!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}) ;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem a acao de acordo com a percepcao local do ambiente
	 * @param percepcao
	 * @return acao
	 * */
	private Acao obtemAcao(Estado estadoAmbiente) {
		Acao acao = null;
		regras = obtemRegras();
		if (regras.containsKey(estadoAmbiente.getElemento())) 
			acao = (Acao)regras.get(estadoAmbiente.getElemento());	
		
		if (acao == Acao.MOVER) {
			if (estadoAmbiente.getPosicao() == 0)
				acao = Acao.DIREITA;
			else {
				Random random = new Random();
				int direcao = random.nextInt(2);
				if(direcao == 0)
					acao = Acao.DIREITA;
				else
					acao = Acao.ESQUERDA;
			}
		}
		return acao;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem as regras e a acao correspondente do agente
	 * @return regras
	 * */
	private Hashtable obtemRegras() {
		Hashtable regras = new Hashtable(); //Keys: percepcao; Value: acao;
		regras.put(-1, Acao.PARAR);
		regras.put(0, Acao.MOVER); //0 = Limpo
		regras.put(1, Acao.ASPIRAR); //1 = Sujo
		regras.put(2, Acao.ASPIRAR); //TODO: Modificar acao
		regras.put(3, Acao.ASPIRAR); //TODO: Modificar acao
		regras.put(4, Acao.ASPIRAR); //TODO: Modificar acao
		regras.put(5, Acao.ASPIRAR); //TODO: Modificar acao
		return regras;
	}
}