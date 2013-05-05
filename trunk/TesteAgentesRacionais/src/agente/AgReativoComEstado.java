package agente;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ambiente.Estado;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class AgReativoComEstado extends Agent{

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = -2385358617391625935L;
	
	private Hashtable regras = null;
	
	//Representa as posicoes do ambiente ja visitadas pelo agente
	private ArrayList<Estado> estadoInterno = new ArrayList<Estado>();
	
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
							
							atualizaEstado(estadoAmbiente);
							regras = obtemRegras();
							Acao acao = obtemAcao(estadoAmbiente, regras);
							
							estadoAmbiente.setAcao(acao);
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.INFORM);
							reply.setContentObject(estadoAmbiente);
							myAgent.send(reply);
				
							if (estadoAmbiente.getAcao() == Acao.PARAR) { 
								block(); 
								estadoInterno.clear();
							}
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
	private Acao obtemAcao(Estado estadoAmbiente, Hashtable regras) {
		
		//System.out.println(Arrays.deepToString(estadoAmbiente.getCenario()));
		Acao acao = null;
		
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
			if (estadoAmbiente.getPosicaoColuna() == estadoAmbiente.getQtdeLinhaColuna()-1) //-1 é adicionado pois aqui considera-se os indices da matriz
				movimentos.remove(Acao.DIREITA);
			if (estadoAmbiente.getPosicaoLinha() == estadoAmbiente.getQtdeLinhaColuna()-1) //-1 é adicionado pois aqui considera-se os indices da matriz
				movimentos.remove(Acao.PARA_BAIXO);
			
			acao = obtemDirecao(estadoAmbiente, movimentos);
			//System.out.println(acao);
		}
		return acao;
	}
	
	/**
	 * @author raquel silveira
	 * metodo que obtem a direcao que o agente vai tomar
	 * @param movimentos
	 * @return acao
	 */
	public Acao obtemDirecao(Estado estadoAmbiente, ArrayList<Acao> movimentos) {
		
		Acao melhorAcao = null;
		int melhorPontuacao = 0, pontuacao = 0;
		
		for (Acao acao : movimentos) {
		
			int novaLinha = estadoAmbiente.getPosicaoLinha();
			int novaColuna = estadoAmbiente.getPosicaoColuna();
			switch (acao) {
				case DIREITA:
				{ novaColuna +=1; break; }
				case ESQUERDA:
				{ novaColuna -=1; break; }
				case PARA_BAIXO:
				{ novaLinha += 1; break; }
				case PARA_CIMA:
				{ novaLinha -= 1; break; }
			}
			
			pontuacao = estadoAmbiente.getCenario()[novaLinha][novaColuna];
			
			boolean posicaoVisitada = false;
			for (Estado estado : estadoInterno) {
				if (estado.getPosicaoLinha() == novaLinha && estado.getPosicaoColuna() == novaColuna)
				{ posicaoVisitada = true; break; }
			}
			
			if (!posicaoVisitada && pontuacao > melhorPontuacao) {
				melhorPontuacao = pontuacao;
				melhorAcao = acao;
			}
		}
		
		if (melhorAcao == null)
			melhorAcao = movimentos.get(new Random().nextInt(movimentos.size()));
				
		return melhorAcao;
	}
	
	private void atualizaEstado(Estado estadoAmbiente) {
		
		estadoInterno.add(estadoAmbiente);
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