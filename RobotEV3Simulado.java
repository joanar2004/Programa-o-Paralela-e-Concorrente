import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Classe que simula o comportamento do robot EV3. Implementa a interface
 * IRobotEV3 e fornece métodos para controle do robot, simulando os movimentos e
 * respostas dos sensores.
 */
public class RobotEV3Simulado implements IRobotEV3 {

	// Indica se há um processo
	private AtomicBoolean haProcesso;

	// Indica se o robot está ativo
	private boolean estaAtivo;

	/**
	 * Construtor da classe RobotEV3Simulado. Inicializa o estado do robot.
	 *
	 * @param dados (Dados): Dados que podem ser utilizados pela simulação.
	 */
	public RobotEV3Simulado(Dados dados) {
		this.haProcesso = new AtomicBoolean(false);
		this.estaAtivo = true;
	}

	// Implementação dos métodos da interface

	/**
	 * Abre a conectividade com o robot.
	 * 
	 * @param nomeRobot (String): Nome do robot.
	 * @return boolean: true se sucesso, false caso contrário.
	 */
	@Override
	public boolean OpenEV3(String nomeRobot) {
		if (!this.haProcesso.getAndSet(true)) {
			// System.out.printf("RobotEV3Simulado: Robot %s está aberto.%n", dados.getNomeRobot());
			dormir(1000L);
			this.estaAtivo = true;
			return true;
		} else {
			// System.out.println(">>> Outro processo já está em andamento.");
			return false;
		}
	}

	/**
	 * Move o robot em linha reta.
	 *
	 * @param distancia (int): Distância em centímetros a ser percorrida.
	 */
	@Override
	public void Reta(int distancia) {
		if (estaOK()) {
			long quantoDorme = (long) (1.0 * ((distancia * 1000) / 30));
			dormir(Math.abs((long) (quantoDorme)));
			// System.out.println("RobotEV3Simulado: Reta " + Math.abs(distancia) + " cm");
		}
	}

	/**
	 * Faz o robot curvar à esquerda.
	 *
	 * @param raio   (float): Raio da curva.
	 * @param angulo (int): Ângulo em graus da curva.
	 */
	@Override
	public void CurvarEsquerda(float raio, int angulo) {
		if (estaOK()) {
			float quantoDorme = (float) ((angulo * Math.PI * raio) / 180);
			dormir((long) quantoDorme);
			// System.out.println("RobotEV3Simulado: Curva Esquerda -> raio: " + raio + " angulo: "
			// + angulo);
		}
	}

	/**
	 * Faz o robot curvar à direita.
	 *
	 * @param raio   (float): Raio da curva.
	 * @param angulo (int): Ângulo em graus da curva.
	 */
	@Override
	public void CurvarDireita(float raio, int angulo) {
		if (estaOK()) {
			float quantoDorme = (float) ((angulo * Math.PI * raio) / 180);
			dormir((long) quantoDorme);
			// System.out.println("RobotEV3Simulado: Curva Direita -> raio: " + raio + " angulo: " +
			// angulo);
		}
	}

	/**
	 * Sensor de Toque.
	 * 
	 * @param S_1(int): porta do sensor de toque.
	 * @return int: 1 se o sensor foi ativado, 0 se não, ou -1 se o robot não está
	 *         ativo.
	 */
	@Override
	public int SensorToque(int S_1) {
		if (estaOK()) {
			double probToque = Math.random();
		
			//System.out.printf("RobotEV3Simulado: Sensor de toque na porta %d ativado. Probabilidade: %.2f%n", S_1, probToque);
			
			// 10% probabilidade de toque
			return probToque < 0.10 ? 1 : 0; 
		}
		return -1;
	}

	/**
	 * Lê o sensor ultrasons.
	 *
	 * @param S_2 (int): Porta do sensor ultrasons.
	 * @return float: Distância medida pelo sensor ou -1 se o robot não está ativo.
	 */
	@Override
	public float SensorUS(int S_2) {
		if (estaOK()) {
			float distancia = (float) (Math.random() * 255);
			//System.out.printf("RobotEV3Simulado: Sensor de Ultrassons na porta %d mediu: %.2f cm%n", S_2, distancia);
			return distancia;
		}
		return -1;
	}

	/**
	 * Para o robot.
	 *
	 * @param sincrono Se a parada deve ser síncrona ou assíncrona (não utilizado na
	 *                 simulação).
	 */
	@Override
	public void Parar(boolean sincrono) {
		if (estaOK()) {
			// System.out.println("RobotEV3Simulado: Robot parado...");
			this.haProcesso.set(false);
			dormir(250L);
		}
	}

	/**
	 * Fecha o robot.
	 */
	@Override
	public void CloseEV3() {
		if (estaOK()) {
			// System.out.printf("RobotEV3Simulado: CloseEV3%n");
			this.haProcesso.set(false);
			dormir(1000L);
			Terminar();
			this.estaAtivo = false;
		}
	}

	/**
	 * Termina o processo do robot e encerra a aplicação.
	 */
	@Override
	public void Terminar() {
		if (estaOK()) {
			// System.out.print("RobotEV3Simulado: Terminar...\nRobotEV3Simulado: CloseEV3\n");
			dormir(250L);
			this.estaAtivo = false;
			System.exit(1);
		}
	}

	// Métodos auxiliares

	/**
	 * Verifica se o robot está ativo.
	 *
	 * @return true se o robot está ativo, false caso contrário.
	 */
	private boolean estaOK() {
		if (!this.estaAtivo) {
			// System.out.printf("%n>>> Canal de Comunicação não OK%n>>> Verifique robot e
			// ligação bluetooth...");
			return false;
		}
		return true;
	}

	/**
	 * Faz a thread dormir por um determinado tempo.
	 *
	 * @param tempoMillis (long): Tempo em milissegundos que a thread deve dormir.
	 */
	private void dormir(long tempoMillis) {
		try {
			Thread.sleep(tempoMillis);
		} catch (InterruptedException interruptedException) {
			Thread.currentThread().interrupt();
		}
	}
}
