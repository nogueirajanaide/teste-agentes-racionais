package agente;

import java.util.ArrayList;
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
				
				try {	
					ACLMessage msg = myAgent.receive();
										
					if (msg != null) {						
					
						if (msg.getContentObject().getClass().isEnum() && (Acao)msg.getContentObject() == Acao.INICIAR) {
										
							ACLMessage reply = msg.createReply();
							Estado estado = new Estado();
							estado.setAcao(Acao.OBTER_ESTADO);
							reply.setContentObject(estado);
							myAgent.send(reply);
						} else {
							
							Estado estadoAmbiente = (Estado)msg.getContentObject();
							estadoAmbiente.setAcao(obtemAcao(estadoAmbiente));					
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.INFORM);
							reply.setContentObject(estadoAmbiente);
							myAgent.send(reply);
				
							if (estadoAmbiente.getAcao() == Acao.PARAR) { block(); }
						}
					} else {
						
						block();
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
		
		if (estadoAmbiente.getCenario() == null)
			return Acao.PARAR;
		
		if (regras.containsKey(estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna()])) 
			acao = (Acao)regras.get(estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna()]);	
		
		if (acao == Acao.MOVER) {
			ArrayList<Acao> movimentos = obtemMovimentosPossiveis();
			if (estadoAmbiente.getPosicaoLinha() == 0) 
				movimentos.remove(Acao.PARA_CIMA);
			if (estadoAmbiente.getPosicaoColuna() == 0)
				movimentos.remove(Acao.ESQUERDA);
			if (estadoAmbiente.getPosicaoColuna() == estadoAmbiente.getQtdeLinhaColuna()-1) //-1 � adicionado pois aqui considera-se os indices da matriz
				movimentos.remove(Acao.DIREITA);
			if (estadoAmbiente.getPosicaoLinha() == estadoAmbiente.getQtdeLinhaColuna()-1) //-1 � adicionado pois aqui considera-se os indices da matriz
				movimentos.remove(Acao.PARA_BAIXO);
			
			acao = movimentos.get(new Random().nextInt(movimentos.size()));
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
		regras.put(0, Acao.MOVER); //0 = Limpo
		regras.put(1, Acao.ASPIRAR); //1 = Sujo
		return regras;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que obtem os movimentos possiveis do agente
	 * @return os movimentos possiveis do agente
	 */
	private ArrayList<Acao> obtemMovimentosPossiveis() {
		ArrayList<Acao> movimentos = new ArrayList<Acao>();
		movimentos.add(Acao.DIREITA);
		movimentos.add(Acao.ESQUERDA);
		movimentos.add(Acao.PARA_CIMA);
		movimentos.add(Acao.PARA_BAIXO);
		return movimentos;
	}
}