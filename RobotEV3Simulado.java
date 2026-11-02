import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Ficheiro: RobotEV3Simulado.java
 * Descrição: Classe que simula o comportamento do robot EV3 em ambiente headless. 
 * Implementa a interface IRobotEV3 e fornece métodos para o controlo simulado, 
 * gerando movimentos proporcionais e respostas aleatórias de sensores através de 
 * pausas (Thread.sleep) e probabilidades estatísticas.
 */
public class RobotEV3Simulado implements IRobotEV3 {

    // Variável atómica para gerir o controlo de exclusividade de processos em execução
    private AtomicBoolean haProcesso;

    // Indicador lógico do estado de atividade do robô simulado
    private boolean estaAtivo;

    /**
     * Construtor da classe RobotEV3Simulado. 
     * Inicializa os controlos de concorrência e define o robô como ativo por defeito.
     *
     * @param dados (Dados): Objeto de configuração (mantido por compatibilidade com a arquitetura).
     */
    public RobotEV3Simulado(Dados dados) {
        this.haProcesso = new AtomicBoolean(false);
        this.estaAtivo = true;
    }

    // ==========================================
    // IMPLEMENTAÇÃO DOS MÉTODOS DA INTERFACE IRobotEV3
    // ==========================================

    /**
     * Simula a abertura de conectividade com o robô de forma atómica.
     * 
     * @param nomeRobot (String): Nome identificador do robô.
     * @return boolean: true se obteve acesso com sucesso, false caso já exista um processo ativo.
     */
    @Override
    public boolean OpenEV3(String nomeRobot) {
        // Tenta adquirir o acesso exclusivo de forma atómica (false -> true)
        if (!this.haProcesso.getAndSet(true)) {
            dormir(1000L); // Simula o tempo de ligação
            this.estaAtivo = true;
            return true;
        } else {
            // Caso já esteja ocupado, rejeita a abertura
            return false;
        }
    }

    /**
     * Simula o movimento do robô em linha reta, calculando o tempo de espera 
     * proporcional à distância fornecida.
     *
     * @param distancia (int): Distância em centímetros a percorrer (positivo = frente, negativo = trás).
     */
    @Override
    public void Reta(int distancia) {
        if (estaOK()) {
            // Calcula o tempo de simulação de movimento proporcional à distância (velocidade de referência: 30 cm/s)
            long quantoDorme = (long) (1.0 * ((distancia * 1000) / 30));
            dormir(Math.abs((long) (quantoDorme)));
        }
    }

    /**
     * Simula uma curva para a esquerda com base no raio e ângulo especificados.
     *
     * @param raio   (float): Raio da curva em centímetros.
     * @param angulo (int): Ângulo de rotação em graus.
     */
    @Override
    public void CurvarEsquerda(float raio, int angulo) {
        if (estaOK()) {
            // Calcula o comprimento do arco da circunferência para determinar o tempo de pausa simulado
            float quantoDorme = (float) ((angulo * Math.PI * raio) / 180);
            dormir((long) quantoDorme);
        }
    }

    /**
     * Simula uma curva para a direita com base no raio e ângulo especificados.
     *
     * @param raio   (float): Raio da curva em centímetros.
     * @param angulo (int): Ângulo de rotação em graus.
     */
    @Override
    public void CurvarDireita(float raio, int angulo) {
        if (estaOK()) {
            // Calcula o comprimento do arco da circunferência para determinar o tempo de pausa simulado
            float quantoDorme = (float) ((angulo * Math.PI * raio) / 180);
            dormir((long) quantoDorme);
        }
    }

    /**
     * Simula a leitura do sensor de toque de forma probabilística.
     * 
     * @param S_1 (int): Número da porta do sensor de toque.
     * @return int: Retorna 1 se detetar obstáculo (10% de probabilidade), 0 caso contrário, ou -1 se inativo.
     */
    @Override
    public int SensorToque(int S_1) {
        if (estaOK()) {
            double probToque = Math.random();
            
            // Atribui 10% de probabilidade de simular a deteção de um obstáculo físico
            return probToque < 0.10 ? 1 : 0; 
        }
        return -1;
    }

    /**
     * Simula a leitura do sensor ultrassónico gerando uma distância aleatória.
     *
     * @param S_2 (int): Número da porta do sensor ultrassónico.
     * @return float: Distância simulada em centímetros (até 255 cm) ou -1 se inativo.
     */
    @Override
    public float SensorUS(int S_2) {
        if (estaOK()) {
            // Gera uma distância aleatória simulada até 255 cm
            float distancia = (float) (Math.random() * 255);
            return distancia;
        }
        return -1;
    }

    /**
     * Simula a paragem dos motores do robô.
     *
     * @param sincrono (boolean): Define se a paragem é síncrona ou assíncrona.
     */
    @Override
    public void Parar(boolean sincrono) {
        if (estaOK()) {
            this.haProcesso.set(false); // Liberta o controlo atómico de processos
            dormir(250L);                // Simula o tempo de resposta da paragem
        }
    }

    /**
     * Simula o fecho da sessão de comunicação com o robô.
     */
    @Override
    public void CloseEV3() {
        if (estaOK()) {
            this.haProcesso.set(false);
            dormir(1000L);
            Terminar();
            this.estaAtivo = false;
        }
    }

    /**
     * Termina o processo do robô de forma controlada. 
     * Nota: O System.exit(1) foi removido propositadamente para permitir testes contínuos em ambiente headless.
     */
    @Override
    public void Terminar() {
        if (estaOK()) {
            dormir(250L);
            this.estaAtivo = false;
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    /**
     * Verifica se o canal de simulação do robô se encontra operacional e ativo.
     *
     * @return boolean: true se o robô estiver ativo, false caso contrário.
     */
    private boolean estaOK() {
        if (!this.estaAtivo) {
            return false;
        }
        return true;
    }

    /**
     * Método auxiliar privado para adormecer a thread atual durante um período simulado.
     * Trata exceções de interrupção em conformidade com as boas práticas de concorrência.
     *
     * @param tempoMillis (long): Tempo de pausa em milissegundos.
     */
    private void dormir(long tempoMillis) {
        try {
            Thread.sleep(tempoMillis);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}