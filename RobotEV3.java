import java.util.Random;

import robot.RobotLegoEV3;

/**
 * Classe que representa o Robot EV3, podendo ser controlado tanto com um robot
 * real quanto um robot simulado, dependendo da configuração inicial.
 */
public class RobotEV3 {

	// Estados de Vaguear
	private static final int FRENTE = 0;
	private static final int CURVAR_DIREITA = 1;
	private static final int CURVAR_ESQUERDA = 2;

	// Estado de Fugir
	private static final int DIREITA = 0;
	private static final int ESQUERDA = 1;

	// Interface que define as operações do robot
	private IRobotEV3 robot;

	// Dados para interação com o sistema e controlo do estado
	private Dados dados;

	// Objeto para gerar valores aleatórios
	private Random random;

	/**
	 * Construtor que recebe os dados e decide qual robot usar (simulado ou real)
	 * 
	 * @param dados (Dados): Contém os dados para controlar o robot.
	 */
	public RobotEV3(Dados dados) {
		this.dados = dados;

		if (dados.getReal()) {
			// Inicializa o robot real
			this.robot = new MyRobotEV3(dados);
			dados.enviarParaConsola(">>> RobotEV3: Robot Real "
					+ (dados.getNomeRobot() != null && !dados.getNomeRobot().isEmpty() ? dados.getNomeRobot()
							: "Nome não definido"));
			// System.out.println("ROBOT REAL");
		} else {
			this.robot = new RobotEV3Simulado(dados);
			dados.enviarParaConsola(">>> RobotEV3: Robot Simulado"
					+ (dados.getNomeRobot() != null && !dados.getNomeRobot().isEmpty() ? dados.getNomeRobot()
							: "Nome não definido"));
			// System.out.println("ROBOT SIMULADO");
		}
		this.random = new Random();
	}

	/**
	 * Método para abrir comunicação com o robot real ou simulado.
	 * 
	 * @param nomeRobot (String): Nome do robot a ser utilizado na comunicação.
	 * @return synchronized boolean: Retorna true se a comunicação foi estabelecida
	 *         com sucesso, false caso contrário.
	 */
	public synchronized boolean OpenEV3(String nomeRobot) {
		String dadosRobot = dados.getNomeRobot();
		dados.enviarParaConsola(
				">>> RobotEV3: Passou por OpenEV3. Verificação de nome: " + nomeRobot + " | " + dadosRobot);
		return robot.OpenEV3(dadosRobot);
	}

	/**
	 * Método para mover o robot em linha reta.
	 * 
	 * @param distancia (int): Distância em centímetros a ser percorrida pelo robot.
	 */
	public synchronized void Reta(int distancia) {
		robot.Reta(distancia);
		dados.enviarParaConsola(">>> RobotEV3: Movimento de reta " + (distancia > 0 ? "para frente" : "para trás")
				+ " com " + Math.abs(distancia) + " cm.");
	}

	/**
	 * Método para fazer o robot curvar à esquerda.
	 * 
	 * @param raio   (float): Raio da curva em centímetros.
	 * @param angulo (int): Ângulo da curva em graus.
	 */
	public synchronized void CurvarEsquerda(float raio, int angulo) {
		robot.CurvarEsquerda(raio, angulo);
		dados.enviarParaConsola(
				">>> RobotEV3: Curva para Esquerda com " + raio + " de raio" + " e " + angulo + " de ângulo.");
	}

	/**
	 * Método para fazer o robot curvar à direita
	 * 
	 * @param raio   (float): Raio da curva em centímetros.
	 * @param angulo (int): Ângulo da curva em graus.
	 */
	public synchronized void CurvarDireita(float raio, int angulo) {
		robot.CurvarDireita(raio, angulo);
		dados.enviarParaConsola(
				">>> RobotEV3: Curva para Direita com " + raio + " de raio" + " e " + angulo + " de ângulo.");
	}

	/**
	 * Método para ler o sensor de toque (do atuador botão).
	 * 
	 * @param S_1 (int): Número do sensor de toque.
	 * @return int: Retorna 1 se o sensor de toque foi acionado, 0 caso contrário.
	 */
	public synchronized int SensorToque(int S_1) {
		dados.enviarParaConsola(">>> RobotEV3: Sensor de toque ativado com " + S_1 + ".\n");
		return robot.SensorToque(S_1);
	}

	/**
	 * Método para ler o sensor ultrasons.
	 * 
	 * @param S_2 (int): Número do sensor ultrasons.
	 * @return synchronized float: Distância medida pelo sensor em centímetros.
	 */
	public synchronized float SensorUS(int S_2) {
		try {
			float leituraSensorUS = robot.SensorUS(S_2);
			if (leituraSensorUS < 0) {
				throw new RuntimeException(">>> RobotEV3 - Leitura inválida do sensor de ultrassom.");
			}
			dados.enviarParaConsola(">>> RobotEV3 - Leitura do sensor de ultrassom: " + leituraSensorUS);
			return leituraSensorUS;
		} catch (IllegalArgumentException e) {
			System.err.println(">>> RobotEV3 - Erro de argumento inválido no SensorUS: " + e.getMessage());
			dados.enviarParaConsola(">>> RobotEV3 - ERRO: Porta do sensor inválida.");
			return -1;
		} catch (Exception e) {
			System.err.println(">>> RobotEV3 - Erro ao ler o sensor de ultrassom: " + e.getMessage());
			dados.enviarParaConsola(">>> RobotEV3 - ERRO: Falha ao ler o sensor de ultrassom.");
			return -1;
		}
	}

	/**
	 * Método para parar o robot.
	 * 
	 * @param sincrono (boolean): Se o comando de parada deve ser síncrono
	 *                 (verdadeiro) ou assíncrono (falso).
	 */
	public synchronized void Parar(boolean sincrono) {
		robot.Parar(sincrono);
		dados.enviarParaConsola(">>> RobotEV3: Robot parado.");
	}

	/**
	 * Método para fechar a comunicação com o robot.
	 */
	public synchronized void CloseEV3() {
		robot.CloseEV3();
		dados.enviarParaConsola(">>> RobotEV3: CloseEV3");
	}

	/**
	 * Método para encerrar o robot e terminar o programa de forma controlada.
	 */
	public synchronized void Terminar() {
		robot.Terminar();
		dados.enviarParaConsola(">>> RobotEV3: Terminado");
	}

	/**
	 * Método que retorna uma representação em string do objeto RobotEV3.
	 * 
	 * @return String: Representação textual do estado atual do robot, incluindo o
	 *         nome do robot, o modo (real ou simulado), e o status (ativo ou
	 *         inativo).
	 */
	@Override
	public String toString() {

		return "RobotEV3 {" + "Nome do Robot: "
				+ (dados.getNomeRobot() != null && !dados.getNomeRobot().isEmpty() ? dados.getNomeRobot()
						: "Nome não definido")
				+ ", " + "Modo: " + (robot instanceof MyRobotEV3 ? "Real" : "Simulado") + ", " + "Status: "
				+ (robot != null ? "Ativo" : "Inativo") + "}";
	}

	/**
	 * Método que faz o robot vaguear aleatoriamente, movendo-se em direções
	 * diferentes (frente, direita, ou esquerda) com distâncias e ângulos
	 * aleatórios.
	 */
	public synchronized void metodoVaguear() {
		dados.enviarParaConsola(">>> RobotEV3 - metodoVaguear: ENTROU");

		int movimento = random.nextInt(3); // 0 = em frente, 1 = direita, 2 = esquerda

		int distancia;
		int raio;
		int angulo;

		switch (movimento) {
		case FRENTE:
			// Distância aleatória entre 10 e 50 cm
			distancia = random.nextInt(41) + 10;
			robot.Reta(distancia);
			dados.enviarParaConsola(
					">>> RobotEV3 - metodoVaguear: PARA A FRENTE com " + distancia + " cm de distancia");
			System.out.println("VAGUEAR: RETA");

			break;
		case CURVAR_DIREITA:
			raio = random.nextInt(36) + 15; // Raio aleatório entre 15 e 50 cm
			angulo = random.nextInt(161) + 20; // Ângulo aleatório entre 20 e 180 graus

			robot.CurvarDireita(raio, angulo);
			dados.enviarParaConsola(">>> RobotEV3 - metodoVaguear: CURVAR para DIREITA com " + raio + " cm de raio e "
					+ angulo + " de ângulo.");
			System.out.println("VAGUEAR: CURVA DIREITA");

			break;
		case CURVAR_ESQUERDA:
			raio = random.nextInt(36) + 15; // Raio aleatório entre 15 e 50 cm
			angulo = random.nextInt(161) + 20; // Ângulo aleatório entre 20 e 180 graus

			robot.CurvarEsquerda(raio, angulo);
			dados.enviarParaConsola(">>> RobotEV3 - metodoVaguear: CURVAR para ESQUERDA com " + raio + " cm de raio e "
					+ angulo + " de ângulo.");
			System.out.println("VAGUEAR: CURVA ESQUERDA");
			
			break;
		}
	}

	/**
	 * Método que faz o robot evitar obstáculos, movendo-se para trás e fazendo uma
	 * curva após detectar um obstáculo.
	 */
	public synchronized boolean metodoEvitar() {
		
		if (verificarsensorToque()) {
		dados.enviarParaConsola(">>> RobotEV3 - medotoEvitar: ENTROU. Obstáculo detectado!");
		System.out.println("EVITAR: Obstáculo detectado!");
		robot.Parar(true);

		// Robot anda 15 cm para trás
		robot.Reta(-15);
		dados.enviarParaConsola(">>> RobotEV3 - medotoEvitar: RETA PARA TRAS ");
		System.out.println("EVITAR: RETA PARA TRAZ ");
		dormir(750);
		
		// Faz uma curva à esquerda com raio 0 e ângulo de 90 graus
		robot.CurvarEsquerda(0, 90);
		dados.enviarParaConsola(">>> RobotEV3 - medotoEvitar: CURVA ESQUERDA");
		System.out.println("EVITAR: CURVA ESQUERDA");
		dormir(1000);
		
		dados.enviarParaConsola(">>> RobotEV3 - medotoEvitar: ACABOU MOVIMENTO");
		System.out.println("EVITAR: ACABOU MOVIMENTO");
		robot.Parar(false);

		// Se o comportamento "Vaguear" não estiver ativo, para o robot
		if (!dados.getVaguear()) {
			robot.Parar(true);
			dados.enviarParaConsola(">>> RobotEV3 - medotoEvitar: PARAR POIS O VAGUEAR NÃO ESTA ATIVO");
			System.out.println("EVITAR: PARAR POIS VAGUEAR NAO ESTA ATIVO");

			// Pausa para garantir que o robot conclua a ação
			dormir(1000);
		}
		//dados.enviarParaConsola(">>> RobotEV3 - metodoEvitar: Saiu.");
		return true;
		}
		return false;
	}

	/**
	 * Método que faz o robot fugir de um obstáculo, realiza uma série de movimentos
	 * e curvas.
	 */
	public synchronized boolean metodoFugir() {
		if (verificarsensorUV()) {
		dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: Inimigo detetado!");
		System.out.println("FUGIR: Inimigo detetado!");

		// Define a direção da primeira curva aleatoriamente
		int curva = random.nextInt(2); // 0 = direita | 1 = esquerda

		if (curva == DIREITA) {
			robot.CurvarDireita(20, 90);
			dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: 1 DIREITA");
			System.out.println("FUGIR: 1 DIREITA");
		} else {
			robot.CurvarEsquerda(20, 90);
			dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: 1 ESQUERDA");
			System.out.println("FUGIR: 1 ESQUERDA");
		}

		// Pausa para garantir que o robot conclua a ação
		dormir(1750);

		// Robot anda 20 cm para frente
		robot.Reta(20);
		dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: MEIO RETA");
		System.out.println("FUGIR: MEIO RETA");
		dormir(1000);
		// Pausa para garantir que o robot conclua a ação

		if (curva == DIREITA) {
			robot.CurvarEsquerda(20, 90);
			dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: 2 ESQUERDA");
			System.out.println("FUGIR: 2 ESQUERDA");
		} else {
			robot.CurvarDireita(20, 90);
			dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: 2 DIREITA");
			System.out.println("FUGIR: 2 DIREITA");
		}
		dormir(1750);
		// Parar o movimento após conclusão
		System.out.println("FUGIR: ACABOU MOVIMENTO");
		robot.Parar(true);
		return true;
		}
		//dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: SAIU");
		// System.out.println(">>> RobotEV3 - metodoFugir: SAIU");
		return false;
	}

	// Setters

	/**
	 * Método para alternar entre o modo de operação do robot, permitindo que ele
	 * funcione como um robot real ou simulado.
	 * 
	 * @param isReal (boolean): Se true, o robot será configurado para operar como
	 *               um robot real; se false, como um robot simulado.
	 */
	public void setRobotReal(boolean isReal) {
		if (isReal && !(robot instanceof MyRobotEV3)) {
			this.robot = new MyRobotEV3(dados);
		} else if (!isReal && !(robot instanceof RobotEV3Simulado)) {
			this.robot = new RobotEV3Simulado(dados);
		}
	} 

	// Outros métodos

	/**
	 * Método que pausa a execução do programa por um tempo especificado.
	 * 
	 * @param tempo (int): Tempo em milissegundos para pausar.
	 */
	private void dormir(long tempoMillis) {
		try {
			Thread.sleep(tempoMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	public synchronized boolean verificarsensorToque() {
		int leituraSensor = robot.SensorToque(RobotLegoEV3.S_1);
		if (leituraSensor == 1) {
			dados.enviarParaConsola("EVITAR: Obstáculo detectado!");
			return true;
		} else if (leituraSensor < 1E-10) {
			//dados.enviarParaConsola("EVITAR: Leitura do sensor muito baixa.");
			return false;
		}
		return false;
	}

	public synchronized boolean verificarsensorUV() {
		float distancia = robot.SensorUS(RobotLegoEV3.S_2);

		// Se estiver a menos de 50cm ativar comportamento
		if (distancia < 50) {
			dados.enviarParaConsola("FUGIR: Inimigo detectado!");
			//dados.enviarParaConsola("Fugir: Distância : " + distancia + " cm \n");

			return true;
		}
		
		return false;
	

}}