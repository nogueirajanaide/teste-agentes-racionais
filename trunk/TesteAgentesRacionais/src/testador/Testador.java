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
	//TODO: Analisar as seguintes variáveis
	// Action Ação em A, ação mais recente, inicialmente nenhuma
	// Utilidade, função que mede a utilidade de um estado
 
	//Percepção em Pteste, uma percepção
	EstadoTestador percepcao;
	
	// INFOefeito/Sucessor, informação suplementar para a tomada de decisão
	Populacao sucessor;

	// Estado em E, descrição de estado atual
	EstadoTestador estado;
	 */
	
	// Ei em EInterno, descrição de estado interno
	EstadoTestador estadoInterno = null;
			
	ArrayList<Estado> historia = new ArrayList<Estado>();
	int interacao = 0;
	Estrategia estrategia = new Estrategia();
	private int repeticaoAtual = 0;
	
	/**
	 * @author raquel silveira
	 * @versio 1.0
	 * Metodo que corresponde ao inicio da execucao do testador
	 */
	protected void setup() {
		
		System.out.println("Setup: " + Estrategia.geracao + " " + interacao);
		
		ver();
		proximo();
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
	 */
	private void acao() {
		
		System.out.println("Acao: " + Estrategia.geracao + " " + interacao);
		//System.out.println(Estrategia.geracao + " " + interacao);
		if (teste())
		{
			utilidade();
			sucessor();
		}
		else {
			
			Estrategia.arquivo.imprime3(estadoInterno.getCasosTeste(), Estrategia.melhoresIndividuos);
			System.out.println("Melhores indivíduos = " + Estrategia.melhoresIndividuos);
			blockingReceive();
		}
	}
	
	/**
	 * @author raquel silveia
	 * @version 1.0
	 * Metodo que verifica se eh para continuar a geracao de casos de teste
	 * @return true - continuar a geracao de casos de teste
	 */
	private boolean teste() {
		System.out.println("Teste: " + Estrategia.geracao + " " + interacao);
		return (Estrategia.geracao <= Estrategia.max_gera && 
				Estrategia.melhorFitness >= Estrategia.max_fit); 
		//Como a funcao eh de minimizacao, o melhor tem que ser maior que o maximo fitness
		//TODO: Analisar uma forma de deixar essa comparacao mais generica
	}
		
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que inicia os testes
	 */
	private void initialise() {
		
		System.out.println("Initialise: " + Estrategia.geracao + " " + interacao + " Repeticao: " + repeticaoAtual);
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

				if (repeticaoAtual++ < Estrategia.repeticao) {		

					System.out.println("Repeticao: " + repeticaoAtual);
					//Configura o teste com o caso de teste selecionado
					System.out.println("Repeticao: " + repeticaoAtual);
					configuraTeste(estadoInterno.getCasosTeste().getIndividuo().get(interacao));

					//Envia msg para agente iniciar
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM) ;
					msg.addReceiver(new AID(estadoInterno.getAGENTE(), AID.ISLOCALNAME));
					msg.setContentObject(Acao.INICIAR);
					send(msg);
				}
				else {

					interacao++;
					repeticaoAtual = 0;
					initialise();
				}
			} else {
				takeDown();
				acao();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean teste = true;
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que verifica a continuidade dos testes de um caso de teste e gera as historias correspondentes ao teste
	 */
	private void activate_system() {
		
		//System.out.println("Activate_system: " + Estrategia.geracao + " " + interacao);
		//System.out.println("Repeticao: " + i);
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
		
		//System.out.println("Finalise: " + Estrategia.geracao + " " + interacao);
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
				
				if (estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias == null) {
					estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias = new ArrayList<ArrayList<Estado>>();
				}
				//Adiciona cada historia das repeticoes na lista de historias do caso de teste
				estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias.add((ArrayList<Estado>)historia.clone());
				
				System.out.println("Geração: " + Estrategia.geracao + " - " + interacao);
				System.out.println("Caso de teste: " +estadoInterno.getCasosTeste().getIndividuo().get(interacao));
				System.out.println("História: " + historia);
				System.out.println("História: " + estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias.get(repeticaoAtual));
				System.out.println("Qtde de histórias: " + estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias.size());
				
				//Sera adicionada a historia na lista de historias do caso de teste
				//estadoInterno.getCasosTeste().getIndividuo().get(interacao).setHistoria((ArrayList<Estado>)historia.clone());	
				
				//Aqui ainda nao tem a avalicao da historia. A avaliacao eh obtida no metodo utilidade()
				//Estrategia.arquivo.imprimeHistoria(estadoInterno.getCasosTeste().getIndividuo().get(interacao));
				proximo();
				//interacao++;
				behaviour.block();
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
				destinatario.addReceiver(new AID(destino, AID.ISLOCALNAME));
				
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
		if (estadoInterno.getCasosTeste() == null || 
				interacao >= estadoInterno.getCasosTeste().getIndividuo().size()) {
			//System.out.println("Entrou no if do sucessor!");
			//System.out.println(estadoInterno.getCasosTeste());
			
			interacao = 0;
			Estrategia.geracao++;
			try {
				estadoInterno.setCasosTeste(estrategia.obtemPopulacao(estadoInterno.getCasosTeste()).clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Novos individuos gerados: " + estadoInterno.getCasosTeste().toString());
			for(int i = 0; i < estadoInterno.getCasosTeste().getIndividuo().size(); i++)
			{
				System.out.println("Individuo 1: " + (estadoInterno.getCasosTeste().getIndividuo().get(i).listaHistorias != null ? estadoInterno.getCasosTeste().getIndividuo().get(i).listaHistorias.size() : 0));
			}
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

		System.out.println("Utilidade: " + Estrategia.geracao + " " + interacao);

		//Percorre cada caso de teste
		for (Iterator iterator = estadoInterno.getCasosTeste().getIndividuo().iterator(); iterator.hasNext();) {
			Individuo casoTeste = (Individuo) iterator.next();

			System.out.println("Avaliando as historias....");
			
			casoTeste.setHistoria(null);
			casoTeste.setAvaliacao(null);
			
			if (casoTeste.listaHistorias != null) {
				//Percorre cada historia do caso de teste
				for (Iterator iterator2 = casoTeste.listaHistorias.iterator(); iterator2.hasNext();) {
					ArrayList<Estado> historia = (ArrayList<Estado>) iterator2.next();

					//Percorre cada estado da historia
					int avaliacao = 0;
					for (Iterator iterator3 = historia.iterator(); iterator3.hasNext();) {
						Estado estado = (Estado) iterator3.next();
						//Obtem a avaliacao do estado da historia
						Integer pont = obtemAvaliacao(estado);
						if (pont != null) {
							avaliacao += pont;	
						}
					}
					
					//Atribui a avaliacao da melhor historia
					//Como a funcao eh de minimizacao eh atribuida a historia com avaliacao menor
					//TODO: Analisar essa comparacao para deixar mais geral
					if (casoTeste.getAvaliacao() == null || casoTeste.getAvaliacao() > avaliacao) {
						if (casoTeste != null)
							System.out.println("Entrou no if - " + (casoTeste.getAvaliacao() == null) + " " + (casoTeste.getAvaliacao() != null ? casoTeste.getAvaliacao() > avaliacao: false));
						else
							System.out.println("Entrou no if - Caso de teste é nulo!");
						casoTeste.setAvaliacao(avaliacao);
						casoTeste.setHistoria((ArrayList<Estado>)historia.clone());
					}
					
					System.out.println("História: " + historia);
					System.out.println("Avaliação: " + avaliacao);
					System.out.println("Avaliação caso de teste: " + casoTeste.getAvaliacao());
				}				
			}

			Estrategia.arquivo.imprimeHistoria(casoTeste);
			System.out.println("Obtendo a melhor historia....");
			System.out.println(casoTeste.toString());
			System.out.println("Historia: " + casoTeste.getHistoria());
			System.out.println("Avaliacao: " + casoTeste.getAvaliacao());
		}
		
		
		
		/*
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
			
			Estrategia.arquivo.imprimeHistoria(casoTeste);
			//System.out.println("Geracao: " + Estrategia.geracao + " Interacao: " + interacao);
			//System.out.println(casoTeste.toString());
			//System.out.println("Historia: " + casoTeste.getHistoria());
			//System.out.println("Avaliacao: " + casoTeste.getAvaliacao());
		}*/
		
		//Obtem os melhores individuos avaliados
		Estrategia.melhoresIndividuos = estrategia.selecionaMelhores(estadoInterno.getCasosTeste(), Estrategia.melhoresIndividuos, Estrategia.geracao);
		System.out.println("Melhores individuos: " + Estrategia.melhoresIndividuos);
		Estrategia.melhorFitness = (Estrategia.melhoresIndividuos.getIndividuo().size() > 0 && Estrategia.melhoresIndividuos.getIndividuo().get(0).getAvaliacao() != null ? Estrategia.melhoresIndividuos.getIndividuo().get(0).getAvaliacao() : 0); 
		System.out.println("Melhor fitness: " + Estrategia.melhorFitness);
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
	
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que atribui as configuracoes iniciais para o inicio do teste
	 * @param casoTeste
	 */
	private void configuraTeste(Individuo casoTeste ) {
		//Configura o ambiente
		try {
			
			Ambiente.estadoInterno = casoTeste.clone();
			Ambiente.posicaoAgente = 0;
			historia.clear();
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
}