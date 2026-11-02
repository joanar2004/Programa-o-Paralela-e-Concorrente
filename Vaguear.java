import java.util.Random;
import java.util.concurrent.Semaphore;
import robot.RobotLegoEV3;

/**
 * Classe que define o comportamento Vaguear, no qual usa o RobotEV3.
 */
public class Vaguear extends Thread {

	// Atributos

	private RobotEV3 robot;

	// Instância para manipular os dados compartilhados entre as classes
	private Dados dados;
	private Random random;
	private Semaphore vaguearAFuncionar;

	// Estados possíveis
	private static final int TERMINAR = 0;
	private static final int GERAR_RANDOM = 1;
	private static final int FAZER_NADA = 2;

	// Estado Inicial
	private int estado;

	/**
	 * Construtor da classe "Vaguear". Inicializa o robot e os atributos
	 * necessários.
	 *
	 * @param gui_TP1           Interface gráfica de controle.
	 * @param dados             Dados compartilhados.
	 * @param vaguearAFuncionar Semáforo para controlar o comportamento de
	 *                          "Vaguear".
	 */
	public Vaguear(GUI_TP1 gui_TP1, Dados dados, RobotEV3 robot) {
		this.dados = gui_TP1.dados;
		this.robot = robot;

		this.random = new Random();
		this.vaguearAFuncionar = new Semaphore(0);

		this.estado = FAZER_NADA;
	}

	/**
	 * Método que será executado quando a thread for iniciada.
	 */
	@Override
	public void run() {
		while (estado != TERMINAR) {
			executarMaquinaDeEstados();

		}
	}

	/**
	 * Método que implementa a máquina de estados do comportamento Vaguear.
	 * 
	 */
	public void executarMaquinaDeEstados() {

		switch (estado) {
		case FAZER_NADA:
			try {
				
				//dados.enviarParaConsola("Vaguear: Aguarda permissão para vaguear...");
				// System.out.println("Vaguear: Aguarda permissão para vaguear...");

				//dados.enviarParaConsola(
						//"Vaguear: Permissões antes do ACQUIRE(): " + vaguearAFuncionar.availablePermits());
				// System.out.println("Vaguear: Permissões antes do ACQUIRE(): " +
				// vaguearAFuncionar.availablePermits());

				vaguearAFuncionar.acquire();

				//dados.enviarParaConsola("Vaguear: Permissão adquirida, em execução");
				// System.out.println("Vaguear: Permissão adquirida, em execução");

				//dados.enviarParaConsola("Vaguear: Permissões após ACQUIRE(): " + vaguearAFuncionar.availablePermits());
				// System.out.println("Vaguear: Permissões após ACQUIRE(): " +
				// vaguearAFuncionar.availablePermits());

			} catch (InterruptedException e) {
				System.err.println("Vaguear: A thread foi interrompida enquanto aguardava permissão para vaguear.");
				e.printStackTrace();
			}
			break;

		case GERAR_RANDOM:
			gerarRandom();
			dormir(1500);
			break;

		case TERMINAR:
			dados.enviarParaConsola("Vaguear: TERMINAR");
			// System.out.println("Vaguear: TERMINAR");
			break;

		default:
			estado = TERMINAR; // Transição para o estado TERMINAR
			dados.enviarParaConsola("Vaguear: TERMINAR");
			break;
		}
	}

	/**
	 * Método que ativa o comportamento "Vaguear".
	 */
	public void funcionar() {

		
			//dados.enviarParaConsola("Vaguear: Permissões iniciais: " + vaguearAFuncionar.availablePermits());
			// System.out.println("Vaguear: Permissões iniciais: " +
			// vaguearAFuncionar.availablePermits());
			estado = GERAR_RANDOM;

			vaguearAFuncionar.release();

			//dados.enviarParaConsola(
					//"Vaguear: Permissões após release do funcionar: " + vaguearAFuncionar.availablePermits());
			// System.out.println("Vaguear: Permissões após release do funcionar: " +
			// vaguearAFuncionar.availablePermits());
	}

	/**
	 * Método para parar o comportamento de "Vaguear".
	 */
	public void parar() {
		estado = FAZER_NADA;

		//dados.enviarParaConsola("Vaguear: METODO PARAR VAGUEAR");
		// System.out.println("Vaguear: METODO PARAR VAGUEAR");


		//dados.enviarParaConsola("Vaguear: Estado após parar: " + estado);
		// System.out.println("Vaguear: Estado após parar: " + estado);

		//dados.enviarParaConsola("Vaguear: Permissões após release do parar: " + vaguearAFuncionar.availablePermits());
		// System.out.println("Vaguear: Permissões após release do parar: " +
		// vaguearAFuncionar.availablePermits());

		
	}

	/**
	 * Método para parar completamente o robot e encerrar a thread.
	 */
	public void pararRobot() {
		robot.Parar(true); // Parar o robot

			estado = TERMINAR; // Define o estado como TERMINAR
			vaguearAFuncionar.release(); // Liberta o semáforo para garantir que saia do acquire
	
	}

	// Outros Métodos

	/**
	 * Método que gera uma ação aleatória para o comportamento "Vaguear".
	 */
	private void gerarRandom() {
		robot.metodoVaguear();
	}

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

	
}