import java.util.Random;
import java.util.concurrent.Semaphore;

import robot.RobotLegoEV3;

/**
 * Classe que define o comportamento "Fugir". <br>
 * Este comportamento permite que o robot se afaste de obstáculo 
 * detectado pelo sensor.
 */
public class Fugir extends Thread {

	// Objeto que controla os movimentos do robot
	private RobotEV3 robot;

	// Semáforo que controla a execução do comportamento Fugir
	private Semaphore fugirAFuncionar;

	// Instância para manipular os dados compartilhados entre as classes
	private Dados dados;

	// Objeto para gerar valores aleatórios
	private Random random;

	// Estados possíveis
	private static final int TERMINAR = 0;
	private static final int LER_SENSOR = 1;
	private static final int FAZER_NADA = 2;

	// Estado Inicial
	private int estado;

	/**
	 * Construtor da classe Fugir, que inicializa os parâmetros necessários para o
	 * comportamento.
	 *
	 * @param gui_TP1 (GUI_TP1): Interface gráfica usada para interagir com o robot
	 *                e enviar dados.
	 * @param dados   (Dados): Objeto que contém dados compartilhados entre
	 *                diferentes comportamentos.
	 * @param robot   (RobotEV3): Objeto que representa o robot e permite
	 *                controlá-lo.
	 */
	public Fugir(GUI_TP1 gui_TP1, Dados dados, RobotEV3 robot) {
		// Verificações e tratamento de excepções para evitar NullPointerException
		if (gui_TP1 == null) {
			throw new IllegalArgumentException("FUGIR: gui_TP1 não pode ser nulo.");
		}
		if (dados == null) {
			throw new IllegalArgumentException("FUGIR: dados não pode ser nulo.");
		}

		if (robot == null) {
			throw new IllegalArgumentException("FUGIR: robot não pode ser nulo.");
		}

		this.dados = gui_TP1.dados;

		// Objeto para gerar valores aleatórios
		this.random = new Random();

		// Semáforo que controla o comportamento "fugir"
		this.fugirAFuncionar = new Semaphore(0);

		this.robot = robot;

		this.estado = FAZER_NADA;
	}

	/**
	 * Método principal executado quando a thread Fugir é iniciada. Controla a
	 * execução do comportamento de fuga, alternando entre diferentes estados.
	 */
	@Override
	public void run() {
		while (estado != TERMINAR) {
			executarMaquinaDeEstados();
		}
	}

	/**
	 * Método que implementa a máquina de estados do comportamento Fugir. Dependendo do
	 * estado atual, executa ações como verificar o sensor ou realizar movimentos de
	 * fuga.
	 */
	public void executarMaquinaDeEstados() {
		long t = 0;
		switch (estado) {

		case FAZER_NADA:
			try {
				
				//dados.enviarParaConsola("Fugir: Aguarda permissão para fugir...");
				// System.out.println("Fugir: Aguarda permissão para fugir...");
				
				//dados.enviarParaConsola("Fugir: Permissões de Fugir antes do ACQUIRE(): " + fugirAFuncionar.availablePermits());
				// System.out.println("Fugir: Permissões de Fugir antes do ACQUIRE(): " + fugirAFuncionar.availablePermits());
				fugirAFuncionar.acquire();
				
				
				//dados.enviarParaConsola("Fugir: Permissão de Fugir adquirida, em execução");
				// System.out.println("Fugir: Permissão de Fugir adquirida, em execução");
				
				//dados.enviarParaConsola("Fugir: Permissões de Fugir após ACQUIRE(): " + fugirAFuncionar.availablePermits());
				// System.out.println("Fugir: Permissões de Fugir após ACQUIRE(): " + fugirAFuncionar.availablePermits());
				estado = LER_SENSOR;
				
			} catch (InterruptedException e) {
				System.err.println("Fugir: A thread foi interrompida enquanto aguardava permissão para fugir.");
				e.printStackTrace();
			}
			break;

		case LER_SENSOR:
			// Verifica se o sensor detecta um obstáculo e muda o estado para EXECUTAR
			
				
				if(robot.metodoFugir()) {
					t = tempoFugir();
					}
					else {t = 500;}
					//System.out.println("A dormir " + t + " millis");
					dormir(t);
					break;
			

		case TERMINAR:

			// Não faz nada, loop será encerrado
			estado = TERMINAR; // Transição para o estado TERMINAR
			//dados.enviarParaConsola("Fugir: TERMINAR");
			// System.out.println("Fugir: TERMINAR");
			break;

		default:
			// Em caso de erro, define o estado como TERMINAR
			estado = TERMINAR;
			break;
		}
	}

	/**
	 * Método que inicia o comportamento "Fugir", alterando o estado para LERSENSOR
	 * e liberta o semáforo para permitir a execução.
	 */
	public void funcionar() {
	
			//dados.enviarParaConsola("Fugir: Permissões iniciais: " + fugirAFuncionar.availablePermits());
			// System.out.println("Fugir: Permissões iniciais: " + fugirAFuncionar.availablePermits());

			estado = LER_SENSOR;
		
			fugirAFuncionar.release();
			//dados.enviarParaConsola("Fugir: Permissões após release do funcionar: " + fugirAFuncionar.availablePermits());
			// System.out.println("Fugir: Permissões após release do funcionar: " + fugirAFuncionar.availablePermits());
	}

	/**
	 * Método que para o comportamento "Fugir", mudando o estado para FAZER_NADA e
	 * liberta o semáforo para permitir a execução.
	 */
	
	public void parar() {
		dados.enviarParaConsola("Fugir: METODO PARAR FUGIR");
		// System.out.println("Fugir: METODO PARAR FUGIR");

		estado = FAZER_NADA;
		//dados.enviarParaConsola("Fugir: Estado após parar: " + estado);
		// System.out.println("Fugir: Estado após parar: " + estado);

		//dados.enviarParaConsola("Fugir: Permissões após release do parar: " + fugirAFuncionar.availablePermits());
		// System.out.println("Fugir: Permissões após release do parar: " +
		// fugirAFuncionar.availablePermits());

	}

	/**
	 * Para o robot e interrompe a execução do comportamento "Fugir". Define o
	 * estado como TERMINAR e liberta o semáforo para sinalizar a paragem.
	 */
	public void pararRobot() {
		robot.Parar(true); // Paragem Sincrona

			estado = TERMINAR; // Define o estado como TERMINAR
			fugirAFuncionar.release(); // Liberta o semáforo para parar a execução

			dados.enviarParaConsola("Fugir: Parar Robot");
		}

	
	// Outros Métodos

	/**
	 * Faz a thread dormir por um tempo específico, em milissegundos. Se a thread
	 * for interrompida, a interrupção é tratada e a thread é marcada como
	 * interrompida novamente.
	 *
	 * @param tempoMillis (long): O tempo em milissegundos para a pausa.
	 */
	private void dormir(long tempoMillis) {
		try {
			Thread.sleep(tempoMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	public long tempoFugir() {
		double t = 0;
		double velocidade = 20;
		double raio = 20;
		double angulo = Math.PI / 2; 
		double distanciaCurva = (raio * angulo);
		
		t = (t + ((distanciaCurva / velocidade)*2));
		
		double distanciaReta = 20;
		t = (t + (distanciaReta / velocidade));
		
		return (long)(t * 1000);
	}
}