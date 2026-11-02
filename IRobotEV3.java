/**
 * A interface IRobotEV3 define as operações que podem ser realizadas por um
 * robot.
 */
public interface IRobotEV3 {

    /**
     * Método para abrir a comunicação com o robot.
     *
     * @param nomeRobot (String): Nome do robot a ser utilizado na comunicação.
     * @return (boolean): Retorna true se a comunicação foi estabelecida com sucesso,
     *                   false caso contrário.
     */
    boolean OpenEV3(String nomeRobot);

    /**
     * Método para mover o robot em linha reta.
     *
     * @param distancia (int): Distância em centímetros a ser percorrida pelo robot.
     */
    void Reta(int distancia);

    /**
     * Método para fazer o robot curvar à esquerda.
     *
     * @param raio   (float): Raio da curva em centímetros.
     * @param angulo (int): Ângulo da curva em graus.
     */
    void CurvarEsquerda(float raio, int angulo);

    /**
     * Método para fazer o robot curvar à direita.
     *
     * @param raio   (float): Raio da curva em centímetros.
     * @param angulo (int): Ângulo da curva em graus.
     */
    void CurvarDireita(float raio, int angulo);

    /**
     * Método para ler o sensor de toque (do atuador botão).
     *
     * @param S_1 (int): Número do sensor de toque.
     * @return (int): Retorna 1 se o sensor de toque foi acionado, 0 caso contrário.
     */
    int SensorToque(int S_1);

    /**
     * Método para ler o sensor de ultrassom.
     *
     * @param S_2 (int): Número do sensor de ultrassom.
     * @return (float): Distância medida pelo sensor em centímetros.
     *                  Retorna um valor negativo se a leitura for inválida.
     */
    float SensorUS(int S_2);

    /**
     * Método para parar o robot.
     *
     * @param sincrono (boolean): Se o comando de parada deve ser síncrono
     *                         (verdadeiro) ou assíncrono (falso).
     */
    void Parar(boolean sincrono);

    /**
     * Método para fechar a comunicação com o robot.
     */
    void CloseEV3();

    /**
     * Método para encerrar o robot e terminar o programa de forma controlada.
     */
    void Terminar();
}
