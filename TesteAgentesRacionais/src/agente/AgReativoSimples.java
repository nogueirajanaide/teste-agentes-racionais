package agente;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ambiente.Estado;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class AgReativoSimples extends Agent{

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = -2385358617391625935L;
	
	private Hashtable regras = null;
	Double raioVisao; 
	
	protected void setup() {
				
		//Obtem o raio de visao (valor em percentual da observabilidade do ambiente)
		Object [] args = getArguments () ;
		if (args != null && args.length >0)
			raioVisao = Double.parseDouble(args[0].toString());
		
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
		
		//System.out.println(Arrays.deepToString(estadoAmbiente.getCenario()));
		Acao acao = null;
		regras = obtemRegras();
		
		if (estadoAmbiente.getCenario() == null)
			return Acao.PARAR;
				
		if (regras.containsKey(estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna()])) 
			acao = (Acao)regras.get(estadoAmbiente.getCenario()[estadoAmbiente.getPosicaoLinha()][estadoAmbiente.getPosicaoColuna()]);	
		
		if (acao == Acao.MOVER) {
			ArrayList<Acao> movimentos = obtemMovimentosPossiveis(estadoAmbiente.getPosicaoLinha(), estadoAmbiente.getPosicaoColuna(), estadoAmbiente.getQtdeLinhaColuna());
			acao = obtemDirecao(estadoAmbiente, movimentos);
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
		
		return movimentos.get(new Random().nextInt(movimentos.size()));
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0 
	 * metodo que obtem as regras e a acao correspondente do agente
	 * @return regras
	 * */
	public Hashtable obtemRegras() {
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
	protected ArrayList<Acao> obtemMovimentosPossiveis(int posicaoLinha, int posicaoColuna, int qtdeLinhaColuna) {
		ArrayList<Acao> movimentos = new ArrayList<Acao>();
		movimentos.add(Acao.DIREITA);
		movimentos.add(Acao.ESQUERDA);
		movimentos.add(Acao.PARA_CIMA);
		movimentos.add(Acao.PARA_BAIXO);
		
		if (posicaoLinha == 0) 
			movimentos.remove(Acao.PARA_CIMA);
		if (posicaoColuna == 0)
			movimentos.remove(Acao.ESQUERDA);
		if (posicaoColuna == qtdeLinhaColuna-1) //-1 � adicionado pois aqui considera-se os indices da matriz
			movimentos.remove(Acao.DIREITA);
		if (posicaoLinha == qtdeLinhaColuna-1) //-1 � adicionado pois aqui considera-se os indices da matriz
			movimentos.remove(Acao.PARA_BAIXO);
		
		return movimentos;
	}
}