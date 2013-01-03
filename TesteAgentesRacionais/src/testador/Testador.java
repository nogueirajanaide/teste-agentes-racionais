package testador;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Iterator;

import agente.Acao;
import ambiente.Ambiente;
import ambiente.Estado;

/**
 * @author raquel silveira
 * @version 1.0
 * Especifica o Agente Testador
 */
public class Testador extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	//TODO: Analisar as seguintes vari�veis
	// Action A��o em A, a��o mais recente, inicialmente nenhuma
	// Utilidade, fun��o que mede a utilidade de um estado
 
	//Percep��o em Pteste, uma percep��o
	EstadoTestador percepcao;
	
	// INFOefeito/Sucessor, informa��o suplementar para a tomada de decis�o
	Populacao sucessor;

	// Estado em E, descri��o de estado atual
	EstadoTestador estado;
	 */
	
	// Ei em EInterno, descri��o de estado interno
	EstadoTestador estadoInterno = null;
			
	ArrayList<Estado> historia = new ArrayList<Estado>();
	int interacao = 0;
	Estrategia estrategia = new Estrategia();
	
	/**
	 * @author raquel silveira
	 * @versio 1.0
	 * Metodo que corresponde ao inicio da execucao do testador
	 */
	protected void setup() {
		
		System.out.println("Setup: " + Estrategia.geracao + " " + interacao);
		//System.out.println("TESTADOR! - VER");
		ver();
		//System.out.println("TESTADOR! - PROXIMO");
		proximo();
		//System.out.println("TESTADOR! - ACAO");
		acao();
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Subsistema ver
	 * TODO: Analisar a implementacao - A principio nao faz nada
	 */
	private void ver() {
		System.out.println("Ver: " + Estrategia.geracao + " " + interacao);
				
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Subsistema proximo
	 * Atualiza o estado interno e gera populacoes para a realizacao dos testes 
	 */
	private void proximo(){
		
		System.out.println("Proximo: " + Estrategia.geracao + " " + interacao);
		//Inicia os dados do teste
		initialise();
		
		//Gera as historias
		activate_system();
		
		//Finaliza a execucao do proximo
		finalise();		
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Subsistema acao
	 * Metodo que testa a continuidade da geracao de novos cenarios de teste (se sim, gera novos casos de teste)
	 * TODO: Analisar a implementacao deste metodo
	 */
	private void acao() {
		
		System.out.println("Acao: " + Estrategia.geracao + " " + interacao);
		//System.out.println(Estrategia.geracao + " " + interacao);
		if (teste())
		{
			utilidade();
			sucessor();
		}
		else
		{ blockingReceive(); }
	}
	
	/**
	 * @author raquel silveia
	 * @version 1.0
	 * Metodo que verifica se eh para continuar a geracao de casos de teste
	 * A principio continua se a geracao for <= 4
	 * TODO: Falta implementar
	 * @return true - continuar a geracao de casos de teste
	 */
	private boolean teste() {
		System.out.println("Teste: " + Estrategia.geracao + " " + interacao);
		return (Estrategia.geracao <= Estrategia.Max_gera && 
				Estrategia.melhorFitness <= Estrategia.Max_fit);
	}
		
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que inicia os testes
	 */
	private void initialise() {
		
		System.out.println("Initialise: " + Estrategia.geracao + " " + interacao);
		try {
			
			//Caso seja o primeiro teste, obtem a populacao inicial
			if (estadoInterno == null) {
				estadoInterno = new EstadoTestador();
				estadoInterno.setCasosTeste(estrategia.geraPopulacaoInicial());
				interacao = 0;
				Estrategia.geracao = 1;
			}
			
			if (estadoInterno.getCasosTeste() != null && 
					estadoInterno.getCasosTeste().getIndividuo().size() > 0 &&
					interacao < estadoInterno.getCasosTeste().getIndividuo().size()) {		
				//Seleciona o caso de teste
				Individuo casoTeste = estadoInterno.getCasosTeste().getIndividuo().get(interacao);
				
				//Configura o ambiente
				Ambiente.estadoInterno = casoTeste.clone();
				Ambiente.posicaoAgente = 0;
				historia.clear();
				
				//Envia msg para agente iniciar
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM) ;
				msg.addReceiver(new AID(estadoInterno.getAGENTE(), AID.ISLOCALNAME));
				msg.setContentObject(Acao.INICIAR);
				send(msg);
			} else {
				takeDown();
				acao();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que verifica a continuidade dos testes de um caso de teste e gera as historias correspondentes ao teste
	 */
	private void activate_system() {
				
		System.out.println("Activate_system: " + Estrategia.geracao + " " + interacao);
		addBehaviour(new CyclicBehaviour(this) {
			
			public void action() { 
										
				ACLMessage receptor = myAgent.receive();
				if (test_for_termination(receptor, this)) {
					geracao_historias(myAgent, this, receptor);
				}
			}
		});
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que finaliza afuncao proximo
	 */
	private void finalise() {
		
		System.out.println("Finalise: " + Estrategia.geracao + " " + interacao);
		//acao();
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que testa a continuidade dos testes de um caso de teste
	 * TODO: A principio o teste para se o agente envia a msg PARAR
	 * @param receptor da msg
	 * @param behaviour (comportamento) do agente
	 * @return true - continuar os testes
	 */
	private boolean test_for_termination(ACLMessage receptor, Behaviour behaviour) {
		//System.out.println("Test_for_termination: " + Estrategia.geracao + " " + interacao);
		try {
			
			if (((Estado)receptor.getContentObject()).getAcao() == Acao.PARAR) {
				estadoInterno.getCasosTeste().getIndividuo().get(interacao).setHistoria((ArrayList<Estado>)historia.clone());
						
				Estrategia.arquivo.imprimeHistoria(estadoInterno.getCasosTeste().getIndividuo().get(interacao));	
				behaviour.block();
				interacao++;
				proximo();
				//setup(); //TODO: proximo() ???
				return false;
			}
		}
		catch (Exception e) {
		}
		return true;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que gera as historias correspondentes a um caso de teste
	 * @param myAgent (agente testador)
	 * @param behaviour (comportamento do agente)
	 * @param receptor (receptor da msg)
	 */
	private void geracao_historias(Agent myAgent, Behaviour behaviour, ACLMessage receptor) {
		
		//System.out.println("geracao_historias: " + Estrategia.geracao + " " + interacao);
		try {
			if (receptor != null) {
				
				//System.out.println("TESTADOR - RECEBE MSG");
				
				String destino = obtemDestinario(receptor.getSender().getLocalName());
				ACLMessage destinatario = new ACLMessage(ACLMessage.INFORM);
				destinatario.addReceiver(new AID(destino ,AID.ISLOCALNAME));
				
				if (destino.equalsIgnoreCase(estadoInterno.getAGENTE())) {
					destinatario.setContentObject(receptor.getContentObject());
					myAgent.send(destinatario);
					//System.out.println("TESTADOR ENVIA MSG PARA AGENTE");
				}
				if (destino.equalsIgnoreCase(estadoInterno.getAMBIENTE())){
					historia.add((Estado)receptor.getContentObject());				
					destinatario.setContentObject(((Estado)receptor.getContentObject()).getAcao());
					myAgent.send(destinatario);
					
					//System.out.println("TESTADOR ENVIA MSG PARA AMBIENTE");
				}						
			} else {
				behaviour.block();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que identifica o destinatario da mensagem
	 * @return nome do agente de destino
	 */
	private String obtemDestinario(String receptor) {
		
		if (receptor.equalsIgnoreCase(estadoInterno.getAGENTE()))
			return estadoInterno.getAMBIENTE();
		
		if (receptor.equalsIgnoreCase(estadoInterno.getAMBIENTE()))
			return estadoInterno.getAGENTE();		
		
		return null;
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que obtem o conjunto de casos de teste sucessor
	 */
	private void sucessor() {
		
		System.out.println("Sucessor: " + Estrategia.geracao + " " + interacao);
		//System.out.println("Sucessor - Casos teste: "  + estadoInterno.getCasosTeste());
		//System.out.println(interacao >= estadoInterno.getCasosTeste().getIndividuo().size());
		
		//System.out.println(interacao);
		//System.out.println(Estrategia.geracao);
		//System.out.println(estadoInterno.getCasosTeste().getIndividuo().size());
			
		//Caso a populacao ja tenha sido testada, obtem uma nova populacao
		if (estadoInterno.getCasosTeste() == null || interacao >= estadoInterno.getCasosTeste().getIndividuo().size()) {
			//System.out.println("Entrou no if do sucessor!");
			//System.out.println(estadoInterno.getCasosTeste());
			
			interacao = 0;
			Estrategia.geracao++;
			estadoInterno.setCasosTeste(estrategia.obtemPopulacao(estadoInterno.getCasosTeste()));
				
			System.out.println(Estrategia.geracao + " " + interacao);
			
			setup(); 
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que obtem a utilidade de um caso de teste
	 * A funcao utilidade eh calculada de acordo com a medida de avaliacao repassada pelo projetista
	 */
	private void utilidade() {
		//System.out.println("Utilidade: " + Estrategia.geracao + " " + interacao);
		//System.out.println("Entrou na utilidade");
		//System.out.println("Casos teste:" + estadoInterno.getCasosTeste());
		for (Iterator iterator = estadoInterno.getCasosTeste().getIndividuo().iterator(); iterator.hasNext();) {
			Individuo casoTeste = (Individuo) iterator.next();
			
			int avaliacao = 0;
			for (Iterator iterator2 = casoTeste.getHistoria().iterator(); iterator2.hasNext();) {
				Estado estado = (Estado) iterator2.next();
				//Obtem a avaliacao do estado da historia
				Integer pont = obtemAvaliacao(estado);
				if (pont != null) {
					avaliacao += pont;	
				}
			}
			casoTeste.setAvaliacao(avaliacao);
			
			//System.out.println("Geracao: " + Estrategia.geracao + " Interacao: " + interacao);
			//System.out.println(casoTeste.toString());
			//System.out.println("Historia: " + casoTeste.getHistoria());
			//System.out.println("Avaliacao: " + casoTeste.getAvaliacao());
		}
		//System.out.println("Saiu da utilidade");
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo auxiliar para obter a pontuacao da avaliacao para o estado e a acao correspondente
	 * @param estado
	 * @return a pontuacao correspondente a avaliacao
	 */
	private Integer obtemAvaliacao(Estado estado) {
		//System.out.println("ObtemAvaliacao: " + Estrategia.geracao + " " + interacao);
		Integer avaliacao = null;
		for (Iterator iterator = Estrategia.funcaoAvaliacao.iterator(); iterator.hasNext();) {
			FuncaoAvaliacao funcaoAvaliacao = (FuncaoAvaliacao) iterator.next();
						
			if (estado.getElemento() != null &&
					funcaoAvaliacao.getElemento() == estado.getElemento() && 
					funcaoAvaliacao.getAcao().equalsIgnoreCase(estado.getAcao().toString())) {
				avaliacao = funcaoAvaliacao.getPontuacao();
			}
		}
		return avaliacao;
	}
}