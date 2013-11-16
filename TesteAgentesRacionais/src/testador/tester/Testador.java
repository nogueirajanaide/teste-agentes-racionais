package testador.thestes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import testador.thestes.EstadoTestador;
import testador.thestes.Estrategia;
import testador.thestes.FuncaoAvaliacao;
import testador.thestes.Individuo;
import testador.GA.EstrategiaGA;
import testador.thestes.Individuo.Historias;
import testador.NSGAII.EstrategiaNSGAII;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

import graficos.frmMenuPrincipal;

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
	Estrategia estrategia = new EstrategiaNSGAII();
	private int repeticaoAtual = 0;
	ArrayList<Individuo> individuosSelecionados = new ArrayList<Individuo>();
	
	
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
		
		//System.out.println("Ver: " + Estrategia.geracao + " " + interacao);	
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Subsistema proximo
	 * Atualiza o estado interno e gera populacoes para a realizacao dos testes 
	 */
	private void proximo(){
		
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
		
		if (teste()) {
			utilidade();
			sucessor();
		}
		else {
			Estrategia.arquivo.imprime3(estadoInterno.getCasosTeste(), Estrategia.melhoresIndividuos);
			System.out.println("Melhores indivíduos = " + Estrategia.melhoresIndividuos);
			blockingReceive();
			mostraResultado();
		}
	}
	
	private void mostraResultado() {
	
		new frmMenuPrincipal(individuosSelecionados).setVisible(true);
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo que verifica se eh para continuar a geracao de casos de teste
	 * @return true - continuar a geracao de casos de teste
	 */
	private boolean teste() {
		//System.out.println("Teste: " + Estrategia.geracao + " " + interacao);
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
		
		//System.out.println("Initialise: " + Estrategia.geracao + " " + interacao + " Repeticao: " + repeticaoAtual);
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

				if (++repeticaoAtual <= Estrategia.repeticao) {		

					//System.out.println("Repeticao: " + repeticaoAtual);
					
					//Configura o teste com o caso de teste selecionado
					configuraTeste(estadoInterno.getCasosTeste().getIndividuo().get(interacao));

					//System.out.println(estadoInterno.getCasosTeste().getIndividuo().get(interacao).toString());
					
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
			
			//Verifica se o agente enviou a acao PARAR e se a ultima acao da historia foi PARAR
			if (((Estado)receptor.getContentObject()).getAcao() == Acao.PARAR && historia.get(historia.size()-1).getAcao() == Acao.PARAR){
								
				if (estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias == null) {
					estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias = new ArrayList<Historias>();
				}
									
				if (estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias.size() < repeticaoAtual){
					//Adiciona cada historia das repeticoes na lista de historias do caso de teste
					Historias h = estadoInterno.getCasosTeste().getIndividuo().get(interacao).new Historias();
					h.historia = (ArrayList<Estado>)historia.clone();
					h.salasSujas = verificaQtdeSalasSujas();
					estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias.add(h);
				}
				
				//System.out.println("Começou!");
				//System.out.println("geração: " + Estrategia.geracao + " - " + interacao + " Repeticao: " + repeticaoAtual);
				//System.out.println("caso de teste: " +estadoInterno.getCasosTeste().getIndividuo().get(interacao));
				//System.out.println("História: [h] " + historia);
				//System.out.println("História:     " + estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias.get(repeticaoAtual));
				//historia = new ArrayList<Estado>();
				//System.out.println("Qtde de histórias: " + estadoInterno.getCasosTeste().getIndividuo().get(interacao).listaHistorias.size());
				
				//Sera adicionada a historia na lista de historias do caso de teste
				//estadoInterno.getCasosTeste().getIndividuo().get(interacao).setHistoria((ArrayList<Estado>)historia.clone());	
				
				//Aqui ainda nao tem a avalicao da historia. A avaliacao eh obtida no metodo utilidade()
				//Estrategia.arquivo.imprimeHistoria(estadoInterno.getCasosTeste().getIndividuo().get(interacao));
				
				historia = new ArrayList<Estado>();
				behaviour.block();				
				proximo();
				
				//interacao++;
				//behaviour.block();
				return false;
			}
		}
		catch (Exception e) {
		}
		return true;
	}
	
	/**
	 * Metodo que verifica a qtde de salas que ficaram sujas no ambiente apos a realiacao dos testes
	 * @author raquel silveira
	 * @versao 1.0
	 * @data 26/05/2013
	 * @return qtde de salas sujas no ambiente
	 */
	private int verificaQtdeSalasSujas() {

		int qtde = 0;
		for(int i = 0; i < Math.sqrt(estrategia.L); i++)
		{
			for(int j = 0; j < Math.sqrt(estrategia.L); j++)
			{
				if (Ambiente.estadoInterno.getCenario()[i][j] == 1)
					qtde++;
			}
		}
		return qtde;
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
		
		try {
			if (receptor != null) {
				
				String destino = obtemDestinario(receptor.getSender().getLocalName());
				ACLMessage destinatario = new ACLMessage(ACLMessage.INFORM);
				destinatario.addReceiver(new AID(destino, AID.ISLOCALNAME));
				
				if (destino.equalsIgnoreCase(estadoInterno.getAGENTE())) {
					destinatario.setContentObject(receptor.getContentObject());
					myAgent.send(destinatario);
				}
				if (destino.equalsIgnoreCase(estadoInterno.getAMBIENTE())){
					historia.add((Estado)receptor.getContentObject());
					destinatario.setContentObject(((Estado)receptor.getContentObject()).getAcao());
					myAgent.send(destinatario);
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
		
		//Caso a populacao ja tenha sido testada, obtem uma nova populacao
		if (estadoInterno.getCasosTeste() == null || 
				interacao >= estadoInterno.getCasosTeste().getIndividuo().size()) {
			
			interacao = 0;
			Estrategia.geracao++;
			try {
				estadoInterno.setCasosTeste(estrategia.obtemPopulacao(estadoInterno.getCasosTeste()).clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		
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

		boolean avaliou = false;
		Estrategia.arquivo.imprimeGeracao(Estrategia.geracao);
		//Percorre cada caso de teste
		for (Iterator iterator = estadoInterno.getCasosTeste().getIndividuo().iterator(); iterator.hasNext();) {
			Individuo casoTeste = (Individuo) iterator.next();

			Estrategia.arquivo.imprimeCasoTeste(casoTeste);			
			casoTeste.setHistoria(null);
			casoTeste.fInad = new Hashtable<String, Double>();
			casoTeste.setAvaliacao(0.0);
			
			Hashtable<String, Double> avCasoTeste = new Hashtable<String, Double>();
			if (casoTeste.listaHistorias != null && casoTeste.listaHistorias.size() > 0) {
				//System.out.println("Caso teste: " + casoTeste);
				
				//Percorre cada historia do caso de teste
				int qtdeSalasSujas = 0;
				for (Iterator iterator2 = casoTeste.listaHistorias.iterator(); iterator2.hasNext();) {
					Historias historia = (Historias)iterator2.next();
					
					//Percorre cada episodio da historia e armazena a avaliacao obtida
					historia.avaliacao = new Hashtable<String, Double>();
					for (Iterator iterator3 = historia.historia.iterator(); iterator3.hasNext();) {
						Estado estado = (Estado) iterator3.next();
						
						//Obtem a avaliacao do episodio da historia
						obtemAvaliacao(estado);
						
						boolean episodioProblema = false;
						//Atribui a pontuacao do episodio para cada objetivo do agente
						if (estado.pontuacaoObjetivos != null) {
							Enumeration<String> e = estado.pontuacaoObjetivos.keys();
							while(e.hasMoreElements())
							{
								String chave = e.nextElement();
								double av = (historia.avaliacao.containsKey(chave) ? historia.avaliacao.get(chave) : 0) + estado.pontuacaoObjetivos.get(chave);
								
								if (estado.episodioProblema.get(chave)) {
									episodioProblema = true;
									atribuiEpisodioProblema(chave, historia);
								}
								
								if (historia.avaliacao.containsKey(chave)) 
									historia.avaliacao.remove(chave);
								
								historia.avaliacao.put(chave, av);
							}
							if (episodioProblema) {
								atribuiEpisodioProblema("Ambos", historia);
							}
							avaliou = true;
						}
					}
					
					//System.out.println("História: " + historia);
					//System.out.println("Avaliação: " + avaliacao);
					//System.out.println("Avaliação caso de teste: " + casoTeste.getAvaliacao());
					
					//Faz a multiplicacao 1/NInt pela avaliacao da historia [Av m(h(Caso t i))]
					Enumeration<String> e = historia.avaliacao.keys();
					while(e.hasMoreElements())
					{
						String chave = (String)e.nextElement();						
						//if (historia.avaliacao.containsKey(chave))
							//historia.avaliacao.put(chave, 1.0 / Estrategia.max_interacoes * (Double)historia.avaliacao.get(chave));
							
						//System.out.println(chave + historia.avaliacao.get(chave));
						
						//Realiza o somatorio das avaliacoes de cada historia do caso de teste, separado por objetivo							
						double av = (avCasoTeste.containsKey(chave) ? avCasoTeste.get(chave) : 0) + historia.avaliacao.get(chave);						
						
						if (avCasoTeste.containsKey(chave)) 
							avCasoTeste.remove(chave);
						
						avCasoTeste.put(chave, av);
						//System.out.println(chave + ": " + avCasoTeste.get(chave));
					}
					Estrategia.arquivo.imprimeAvaliacao(historia, historia.avaliacao);
			
					qtdeSalasSujas += historia.salasSujas;
				}
				
				//Calcula o percentual de salas sujas deixadas no ambiente apos a realizacao dos testes
				casoTeste.percentualSalaSuja = ((double)qtdeSalasSujas/(double)casoTeste.listaHistorias.size()) * 100.0 / estrategia.L;
				
				Enumeration<String> e = avCasoTeste.keys();
				while(e.hasMoreElements()) 
				{
					String chave = (String)e.nextElement();
					//Calculo do f m (H (CasosTEST t)) [por objetivo]
					double result = 1.0 / casoTeste.listaHistorias.size() * ((avCasoTeste.get(chave)) - qtdeSalasSujas / 2); //Adicionada a penalidade das salas sujas -> A divisao por 2 é para atribuir aos dois objetivos
					casoTeste.fInad.put(chave, -result);
					
					//Calculo da funcao utilidade [multiplicacao do peso do objetivo com o valor avaliado [por objetivo]]
					casoTeste.setAvaliacao(casoTeste.getAvaliacao() + Estrategia.objetivos.get(chave) * result);
				}
				//Apos a soma da avaliacao das historias, atribui sinal negativo para a avaliacao
				casoTeste.setAvaliacao(-casoTeste.getAvaliacao());
				//System.out.println("Utilidade: " + casoTeste.getAvaliacao());
				//System.out.println("");
			}
			
			//Atribui a avaliacao da melhor historia
			//Como a funcao eh de minimizacao eh atribuida a historia com avaliacao menor
			//TODO: Analisar essa comparacao para deixar mais geral
			/*
			 * ANALISAR CONFORME MUDANCAS NA ABORDAGEM!!!!!!!!
			 * 
			 * if (casoTeste.getAvaliacao() == null || casoTeste.getAvaliacao() > avaliacao) {						
				casoTeste.setAvaliacao(avaliacao);
				casoTeste.setHistoria((ArrayList<Estado>)historia.clone());
			}*/

			Estrategia.arquivo.imprimeAvaliacaoCasoTeste(casoTeste);
			//System.out.println("Obtendo a melhor historia....");
			//System.out.println(casoTeste.toString());
			//System.out.println("Historia: " + casoTeste.getHistoria());
			//System.out.println("Avaliacao: " + casoTeste.getAvaliacao());
		}
		
		if (avaliou) {
			//Obtem os melhores individuos avaliados
			Estrategia.melhoresIndividuos = estrategia.selecionaMelhores(estadoInterno.getCasosTeste(), Estrategia.melhoresIndividuos, Estrategia.geracao);
			System.out.println("Melhores individuos: " + Estrategia.melhoresIndividuos);
			//TODO: Analisar como fica o melhorFitness
			Estrategia.melhorFitness = (Estrategia.melhoresIndividuos.getIndividuo().size() > 0 && Estrategia.melhoresIndividuos.getIndividuo().get(0).getAvaliacao() != null ? Estrategia.melhoresIndividuos.getIndividuo().get(0).getAvaliacao() : 0.0);
			if (Estrategia.selecionaIndividuo)
				setIndividuosSelecionados();

			//System.out.println("Individuos selecionados: " + individuosSelecionados);
			System.out.println("Melhor fitness: " + Estrategia.melhorFitness);
		}
	}
	
	private void atribuiEpisodioProblema(String chave, Historias historia) {
		int qtde = 0;
		if (historia.qtdeEpisodioProblema.containsKey(chave)) {
			
			qtde = historia.qtdeEpisodioProblema.get(chave);
			historia.qtdeEpisodioProblema.remove(chave);
		}
		historia.qtdeEpisodioProblema.put(chave, qtde+1);
	}
	
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * metodo que armazena na variavel individuosSelecionados todos os individuos 
	 * e marca aqueles que foram selecionados como melhores
	 */
	private void setIndividuosSelecionados() {
		
		//Adiciona os melhores individuos para a variavel individuosSelecionados
		if (Estrategia.melhoresIndividuos.getIndividuo().size() > 0 &&
			(individuosSelecionados.size() == 0 ||
			(individuosSelecionados.size() > 0 &&
			individuosSelecionados.get(individuosSelecionados.size()-1).getGeracao() != Estrategia.geracao))) {
				for(Individuo i : Estrategia.melhoresIndividuos.getIndividuo()) {
					Individuo iSelecionado = null;
					try { iSelecionado = i.clone(); }
					catch (Exception e) { }
					iSelecionado.setGeracao(Estrategia.geracao);
					iSelecionado.setSelecionado(true);
					individuosSelecionados.add(iSelecionado);
				}
		}
		
		//Adiciona os demais individuos do caso de teste à variavel individuosSelecionados
		for (Individuo i : estadoInterno.getCasosTeste().getIndividuo()) {
			if (i.fInad.size() > 0) {
				i.setGeracao(Estrategia.geracao);
				try { individuosSelecionados.add(i.clone()); }
				catch (Exception e) { }
			}
		}
	}
	
	/**
	 * @author raquel silveira
	 * @version 1.0
	 * Metodo auxiliar para obter a pontuacao da avaliacao para o estado e a acao correspondente
	 * @param estado
	 * @return a pontuacao correspondente a avaliacao
	 */
	private void obtemAvaliacao(Estado estado) {
		
		//TODO: Identificar uma forma para nao pegar o elemento direto do estado do ambiente
		Hashtable<String, Double> avaliacao = new Hashtable();
		for (Iterator iterator = Estrategia.funcaoAvaliacao.iterator(); iterator.hasNext();) {
			FuncaoAvaliacao funcaoAvaliacao = (FuncaoAvaliacao) iterator.next();
						
			if (estado.getCenario() != null &&
				estado.getCenario()[estado.getPosicaoLinha()][estado.getPosicaoColuna()] != null &&
				funcaoAvaliacao.getElemento() == estado.getCenario()[estado.getPosicaoLinha()][estado.getPosicaoColuna()] && 
				funcaoAvaliacao.getAcao().equalsIgnoreCase(estado.getAcao().toString()))
			{
				Enumeration<String> e = funcaoAvaliacao.pontuacao.keys();
				while(e.hasMoreElements())
				{
					String chave = (String)e.nextElement();
					//Atribui a penalidade caso a acao executada seja incorreta
					estado.pontuacaoObjetivos.put(chave, funcaoAvaliacao.pontuacao.get(chave).pontuacao + (estado.isPenaliza() ? Estrategia.penalidade : 0));					
					estado.episodioProblema.put(chave, funcaoAvaliacao.pontuacao.get(chave).problema);
				}
				
				//System.out.println("[" + estado.getPosicaoLinha() + "," + estado.getPosicaoColuna() + "] Avaliacao: " + Arrays.deepToString(estado.getCenario()) + " " + estado.getAcao());
				estado.setPenaliza(Ambiente.verificaAcao(estado));
			}
		}
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
			//Ambiente.estadoInterno.setCenario(casoTeste.getCenario().clone());
			Ambiente.posicaoAgenteL = 0;
			Ambiente.posicaoAgenteC = 0;
			Ambiente.maxInteracoes = Estrategia.max_interacoes;
			Ambiente.interacoes = 0;
			historia = new ArrayList<Estado>();  
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
}