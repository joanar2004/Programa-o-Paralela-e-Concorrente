import robot.RobotLegoEV3;

/**
 * A classe MyRobotEV3 implementa a interface IRobotEV3
 */
public class MyRobotEV3 implements IRobotEV3 {

	private RobotLegoEV3 robot;

	/**
	 * Construtor que inicializa uma instância do RobotLegoEV3.
	 * 
	 * @param dados (Dados): Instância de Dados a ser usada.
	 */
	public MyRobotEV3(Dados dados) {
		this.robot = new RobotLegoEV3();
	}

	/**
	 * Abre a conectividade com o robot.
	 * 
	 * @param nomeRobot (String): Nome do robot.
	 * @return boolean: Retorna true se sucesso, false caso contrário.
	 */
	public boolean OpenEV3(String nomeRobot) {
		return robot.OpenEV3(nomeRobot);
	}

	/**
	 * Move o robot em linha reta.
	 *
	 * @param distancia (int): Distância em centímetros a ser percorrida.
	 */
	public void Reta(int distancia) {
		robot.Reta(distancia);
	}

	/**
	 * Faz o robot curvar à esquerda.
	 *
	 * @param raio   (float): Raio da curva.
	 * @param angulo (int): Ângulo em graus da curva.
	 */
	public void CurvarEsquerda(float raio, int angulo) {
		robot.CurvarEsquerda(raio, angulo);
	}

	/**
	 * Faz o robot curvar à direita.
	 *
	 * @param raio   (float): Raio da curva.
	 * @param angulo (int): Ângulo em graus da curva.
	 */
	public void CurvarDireita(float raio, int angulo) {
		robot.CurvarDireita(raio, angulo);
	}

	/**
	 * Sensor de Toque.
	 * 
	 * @param S_1(int): porta do sensor de toque.
	 * @return int: 1 se o sensor foi ativado, 0 se não, ou -1 se o robot não está
	 *         ativo.
	 */
	public int SensorToque(int S_1) {
		return robot.SensorToque(S_1);
	}

	/**
	 * Lê o sensor ultrasons.
	 *
	 * @param S_2 (int): Porta do sensor ultrasons.
	 * @return float: Distância medida pelo sensor ou -1 se o robot não está ativo.
	 */
	public float SensorUS(int S_2) {
		return robot.SensorUS(S_2);
	}

	/**
	 * Pára o robot.
	 *
	 * @param sincrono Se a parada deve ser síncrona ou assíncrona (não utilizado na
	 *                 simulação).
	 */
	public void Parar(boolean sincrono) {
		robot.Parar(sincrono);
	}

	/**
	 * Fecha o robot.
	 */
	public void CloseEV3() {
		robot.CloseEV3();
	}

	/**
	 * Termina o processo do robot e encerra a aplicação.
	 */
	@Override
	public void Terminar() {
		robot.Terminar();

	}
}