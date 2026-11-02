import java.util.concurrent.Semaphore;

import robot.RobotLegoEV3;

/**
 * Classe que define o comportamento "Evitar". <br>
 * Esse comportamento faz com que o robot detecte obstáculos usando um sensor de
 * toque e execute uma série de comandos para desviar do obstáculo.
 */
public class Evitar extends Thread {

	// Objeto que controla os movimentos do robot
	private RobotEV3 robot;

	// Semáforo que controla o comportamento "Evitar"
	private Semaphore evitarAFuncionar;
	
	// Instância para manipular os dados compartilhados entre as classes
	private Dados dados;

	// Estados possíveis
	private static final int TERMINAR = 0;
	private static final int LER_SENSOR = 1;
	private static final int FAZER_NADA = 2;

	// Estado Inicial
	private int estado;

	/**
	 * Construtor da classe Evitar, que inicializa os atributos necessários para o
	 * comportamento.
	 *
	 * @param gui_TP1 (GUI_TP1): Interface gráfica para controlar o robot, que
	 *                contém os dados compartilhados.
	 * @param dados   (Dados): Objeto que armazena dados compartilhados entre
	 *                diferentes comportamentos.
	 * @param robot   (RobotEV3): Objeto que representa o robot, permitindo o
	 *                controlo dos seus movimentos.
	 */
	public Evitar(GUI_TP1 gui_TP1, Dados dados, RobotEV3 robot) {
		// Verificações e tratamento de excepções para evitar NullPointerException
		if (gui_TP1 == null) {
			throw new IllegalArgumentException("EVITAR: gui_TP1 não pode ser nulo.");
		}
		if (dados == null) {
			throw new IllegalArgumentException("EVITAR: dados não pode ser nulo.");
		}

		if (robot == null) {
			throw new IllegalArgumentException("EVITAR: robot não pode ser nulo.");
		}

		this.dados = gui_TP1.dados;
		
		this.evitarAFuncionar = new Semaphore(0);
		
		this.robot = robot;

		this.estado = FAZER_NADA;
	}

	/**
	 * Método que executa o ciclo, no qual chama a máquina de estados até que o
	 * estado seja TERMINAR.
	 */
	@Override
	public void run() {
		while (estado != TERMINAR) {
			executarMaquinaDeEstados();
		}
	}

	/**
     * Método que implementa a máquina de estados do comportamento Evitar.
     * Dependendo do estado atual, executa ações como verificar o sensor e
     * executar o comportamento de evitar.
     */
	public void executarMaquinaDeEstados() {
		long t = 0;
		switch (estado) {

		case FAZER_NADA:
			try {
				dados.enviarParaConsola("Evitar: Aguarda Permissão para evitar...");
				// System.out.println("Evitar: Aguarda Permissão para evitar...");
				// System.out.println("Evitar: Permissões de evitar antes do ACQUIRE(): " + evitarAFuncionar.availablePermits());
				//dados.enviarParaConsola(
						//"Evitar: Permissões de evitar antes do ACQUIRE(): " + evitarAFuncionar.availablePermits());
				
				evitarAFuncionar.acquire();
				
				
				//dados.enviarParaConsola("Evitar: Permissão de evitar adquirida, em execução");
				// System.out.println("Evitar: Permissão de evitar adquirida, em execução");
				
				//dados.enviarParaConsola(
						//"Evitar: Permissões de evitar após ACQUIRE(): " + evitarAFuncionar.availablePermits());
				// System.out.println("Evitar: Permissões de evitar após ACQUIRE(): " + evitarAFuncionar.availablePermits());
			
				estado = LER_SENSOR;
			} catch (InterruptedException e) {
				System.err.println("Evitar: A thread foi interrompida enquanto aguardava permissão para evitar.");
				e.printStackTrace();
			}
			break;

		case LER_SENSOR:
			if(robot.metodoEvitar()){
			//CALCULAR TEMPO EVITAR
			t = tempoEvitar();
			}
			else {t = 500;}
			//System.out.println("A dormir " + t + " millis");
			dormir(t);
			break;

		case TERMINAR:
			dados.enviarParaConsola("Evitar: TERMINAR");
			// System.out.println("Evitar: TERMINAR");
			break;

		default:
			// Define o estado como TERMINAR
			estado = TERMINAR;
			dados.enviarParaConsola("Evitar: TERMINAR");
			break;
		}
	}

	/**
	 * Método que inicia o comportamento Evitar, alterando o estado para LERSENSOR
	 * e liberta o semáforo para permitir a execução.
	 */
	public void funcionar() {
		
			//dados.enviarParaConsola("Evitar: Permissões iniciais: " + evitarAFuncionar.availablePermits());
			// System.out.println("Evitar: Permissões iniciais: " + evitarAFuncionar.availablePermits());
	
			estado = LER_SENSOR;
	
			evitarAFuncionar.release();
	
			//dados.enviarParaConsola("Evitar: Permissões após release do funcionar: " + evitarAFuncionar.availablePermits());
			// System.out.println("Evitar: Permissões após release do funcionar: " + evitarAFuncionar.availablePermits());
			
	}

	/**
	 * Método que para o comportamento "Evitar", mudando o estado para FAZER_NADA e
	 * liberta o semáforo para permitir a execução.
	 */
	public void parar() {
	
			//dados.enviarParaConsola("Evitar: METODO PARAR EVITAR");
			// System.out.println("Evitar: METODO PARAR EVITAR");
	
			estado = FAZER_NADA;
	
			//dados.enviarParaConsola("Evitar: Estado após parar: " + estado);
			// System.out.println("Evitar: Estado após parar: " + estado);
	
			//dados.enviarParaConsola("Evitar: Permissões após release do parar: " + evitarAFuncionar.availablePermits());
			//System.out.println("Evitar: Permissões após release do parar: " + evitarAFuncionar.availablePermits());
			

	
	}

	/**
	 * Para o robot e encerra o comportamento Evitar definindo o estado como
	 * TERMINAR.
	 */
	public void pararRobot() {
		    robot.Parar(true);
            estado = TERMINAR;
            evitarAFuncionar.release();
	}

	// Outros Métodos


	/**
	 * Faz a thread dormir por um período de tempo especificado.
	 *
	 * @param tempoMillis (long): Tempo em milissegundos para o qual a thread deve
	 *                    dormir.
	 */
	private void dormir(long tempoMillis) {
		try {
			Thread.sleep(tempoMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	public long tempoEvitar() {
		double t = 0;
		
	    double distanciaReta = 15;  // em centímetros
	    double velocidade = 20;     // em centímetros por segundo
	    
	     t = distanciaReta / velocidade; // tempo para a primeira parte (em segundos)
	    
	    double raio = 4.5;          // em centímetros
	    double angulo = Math.PI / 2; 
	    double distanciaCurva = raio * angulo;
	    
	    t += distanciaCurva / velocidade; // tempo para a curva (em segundos)
	    
	    return (long)(t * 1000); // Convertendo para milissegundos
	}
}
