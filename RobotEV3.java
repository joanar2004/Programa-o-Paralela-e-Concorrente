import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ficheiro: RobotEV3.java
 * Descrição: Classe que representa o Robot EV3 em ambiente headless (simulado). 
 * Concentra a lógica de sincronização concorrente (synchronized) e gere os 
 * comportamentos de alto nível do robô (vaguear, evitar, fugir).
 */
public class RobotEV3 {

    // Constantes para os estados do comportamento Vaguear
    private static final int FRENTE = 0;
    private static final int CURVAR_DIREITA = 1;
    private static final int CURVAR_ESQUERDA = 2;

    // Constantes para as direções do comportamento Fugir
    private static final int DIREITA = 0;
    private static final int ESQUERDA = 1;

    // Definição das portas dos sensores na simulação headless
    private static final int S_1 = 1; // Porta do Sensor de Toque
    private static final int S_2 = 2; // Porta do Sensor de Ultrassons

    // Interface que define as operações de baixo nível do robô (simulado)
    private IRobotEV3 robot;

    // Instância de dados partilhados para controlo e envio de mensagens para a consola
    private Dados dados;

    // Gerador de números aleatórios para decisões comportamentais
    private Random random;

    private final Lock lock = new ReentrantLock();

    /**
     * Construtor da classe RobotEV3. Inicializa o robô em modo puramente simulado/virtual,
     * configurando as instâncias de apoio e registando o arranque na consola.
     * 
     * @param dados (Dados): Objeto que contém os dados e configurações globais do sistema.
     */
    public RobotEV3(Dados dados) {
        this.dados = dados;
        // Atribui a implementação simulada (RobotEV3Simulado) à interface IRobotEV3
        this.robot = new RobotEV3Simulado(dados);
        
        // Regista o estado inicial do robô simulado na consola de depuração
        dados.enviarParaConsola(">>> RobotEV3: Robot Simulado (Virtual) "
                + (dados.getNomeRobot() != null && !dados.getNomeRobot().isEmpty() ? dados.getNomeRobot()
                        : "Nome não definido"));
        
        this.random = new Random();
    }



    /**
     * MÉTODO CHAVE DO ESTUDO COMPARATIVO:
     * Envolve qualquer ação do robô no tipo de mecanismo de bloqueio selecionado nas configurações.
     * 
     * @param acao Bloco de código (Runnable) a ser executado sob exclusão mútua.
     */
    private void executarComBloqueio(Runnable acao) {
        if (dados.isUseReentrantLock()) {
            // CENÁRIO A: Utilização de ReentrantLock (bloqueio explícito)
            // Adquire o lock. Se outra thread estiver a usar, a atual aguarda sem prender a carrier thread.
            lock.lock();
            try {
                acao.run();
            } finally {
                // O bloco finally garante rigorosamente a libertação do lock, prevenindo deadlocks
                // mesmo em caso de exceções imprevistas durante a simulação.
                lock.unlock();
            }
        } else {
            // CENÁRIO B: Utilização de monitor intrínseco (synchronized)
            // Utiliza a própria instância (this) como monitor. No JDK 21, se a thread adormecer 
            // ou bloquear aqui dentro, provocará 'VirtualThreadPinned' na carrier thread de suporte.
            synchronized (this) {
                acao.run();
            }
        }
    }

    /**
     * Abre a comunicação com o robô simulado de forma sincronizada.
     * 
     * @param nomeRobot (String): Nome identificador do robô.
     * @return boolean: Retorna true se a ligação foi estabelecida com sucesso.
     */
    public synchronized boolean OpenEV3(String nomeRobot) {
        String dadosRobot = dados.getNomeRobot();
        dados.enviarParaConsola(
                ">>> RobotEV3: Passou por OpenEV3. Verificação de nome: " + nomeRobot + " | " + dadosRobot);
        return robot.OpenEV3(dadosRobot);
    }

    /**
     * Move o robô em linha reta (para a frente se positivo, para trás se negativo) de forma sincronizada.
     * 
     * @param distancia (int): Distância em centímetros a percorrer.
     */
    public synchronized void Reta(int distancia) {
        robot.Reta(distancia);
        dados.enviarParaConsola(">>> RobotEV3: Movimento de reta " + (distancia > 0 ? "para frente" : "para trás")
                + " com " + Math.abs(distancia) + " cm.");
    }

    /**
     * Executa uma curva para a esquerda com base no raio e ângulo especificados, de forma sincronizada.
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
     * Executa uma curva para a direita com base no raio e ângulo especificados, de forma sincronizada.
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
     * Lê o estado do sensor de toque de forma sincronizada.
     * 
     * @param port (int): Número da porta do sensor.
     * @return int: Retorna 1 se ativado, 0 caso contrário.
     */
    public synchronized int SensorToque(int port) {
        dados.enviarParaConsola(">>> RobotEV3: Sensor de toque ativado com porta " + port + ".\n");
        return robot.SensorToque(port);
    }

    /**
     * Lê a distância medida pelo sensor de ultrassons de forma sincronizada, 
     * validando possíveis erros ou leituras inválidas.
     * 
     * @param port (int): Número da porta do sensor de ultrassons.
     * @return float: Distância em centímetros ou -1 em caso de erro.
     */
    public synchronized float SensorUS(int port) {
        try {
            float leituraSensorUS = robot.SensorUS(port);
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
     * Para o movimento do robô de forma sincronizada.
     * 
     * @param sincrono (boolean): Define se a paragem deve ser síncrona ou assíncrona.
     */
    public synchronized void Parar(boolean sincrono) {
        robot.Parar(sincrono);
        dados.enviarParaConsola(">>> RobotEV3: Robot parado.");
    }

    /**
     * Fecha a comunicação com o robô simulado de forma sincronizada.
     */
    public synchronized void CloseEV3() {
        robot.CloseEV3();
        dados.enviarParaConsola(">>> RobotEV3: CloseEV3");
    }

    /**
     * Encerra as operações do robô de forma sincronizada e controlada.
     */
    public synchronized void Terminar() {
        robot.Terminar();
        dados.enviarParaConsola(">>> RobotEV3: Terminado");
    }

    /**
     * Retorna uma representação textual detalhada do estado atual do objeto RobotEV3.
     * 
     * @return String: Descrição formatada com o nome, modo e estado do robô.
     */
    @Override
    public String toString() {
        return "RobotEV3 {" + "Nome do Robot: "
                + (dados.getNomeRobot() != null && !dados.getNomeRobot().isEmpty() ? dados.getNomeRobot()
                        : "Nome não definido")
                + ", Modo: Simulado, Status: " + (robot != null ? "Ativo" : "Inativo") + "}";
    }

    /**
     * Comportamento autónomo "Vaguear": escolhe aleatoriamente se o robô avança 
     * em linha reta ou efetua uma curva (à direita ou à esquerda). Método sincronizado.
     */
    public synchronized void metodoVaguear() {
        dados.enviarParaConsola(">>> RobotEV3 - metodoVaguear: ENTROU");

        int movimento = random.nextInt(3); // 0 = Frente, 1 = Direita, 2 = Esquerda

        int distancia;
        int raio;
        int angulo;

        switch (movimento) {
        case FRENTE:
            distancia = random.nextInt(41) + 10; // Distância aleatória entre 10 e 50 cm
            robot.Reta(distancia);
            dados.enviarParaConsola(
                    ">>> RobotEV3 - metodoVaguear: PARA A FRENTE com " + distancia + " cm de distancia");
            System.out.println("VAGUEAR: RETA");
            break;
        case CURVAR_DIREITA:
            raio = random.nextInt(36) + 15;     // Raio entre 15 e 50 cm
            angulo = random.nextInt(161) + 20;  // Ângulo entre 20 e 180 graus
            robot.CurvarDireita(raio, angulo);
            dados.enviarParaConsola(">>> RobotEV3 - metodoVaguear: CURVAR para DIREITA com " + raio + " cm de raio e "
                    + angulo + " de ângulo.");
            System.out.println("VAGUEAR: CURVA DIREITA");
            break;
        case CURVAR_ESQUERDA:
            raio = random.nextInt(36) + 15;     // Raio entre 15 e 50 cm
            angulo = random.nextInt(161) + 20;  // Ângulo entre 20 e 180 graus
            robot.CurvarEsquerda(raio, angulo);
            dados.enviarParaConsola(">>> RobotEV3 - metodoVaguear: CURVAR para ESQUERDA com " + raio + " cm de raio e "
                    + angulo + " de ângulo.");
            System.out.println("VAGUEAR: CURVA ESQUERDA");
            break;
        }
    }

    /**
     * Comportamento autónomo "Evitar": acionado quando o sensor de toque deteta um obstáculo.
     * Faz o robô parar, recuar, curvar para contornar o obstáculo e retomar o estado. Método sincronizado.
     * 
     * @return boolean: Retorna true se executou a manobra de evasão, false caso contrário.
     */
    public synchronized boolean metodoEvitar() {
        if (verificarsensorToque()) {
            dados.enviarParaConsola(">>> RobotEV3 - metodoEvitar: ENTROU. Obstáculo detectado!");
            System.out.println("EVITAR: Obstáculo detectado!");
            robot.Parar(true);

            // Recua 15 cm
            robot.Reta(-15);
            dados.enviarParaConsola(">>> RobotEV3 - metodoEvitar: RETA PARA TRAS ");
            System.out.println("EVITAR: RETA PARA TRAZ ");
            dormir(750);
            
            // Faz curva à esquerda de 90 graus
            robot.CurvarEsquerda(0, 90);
            dados.enviarParaConsola(">>> RobotEV3 - metodoEvitar: CURVA ESQUERDA");
            System.out.println("EVITAR: CURVA ESQUERDA");
            dormir(1000);
            
            dados.enviarParaConsola(">>> RobotEV3 - metodoEvitar: ACABOU MOVIMENTO");
            System.out.println("EVITAR: ACABOU MOVIMENTO");
            robot.Parar(false);

            // Se o comportamento Vaguear não estiver ativo, para completamente o robô
            if (!dados.getVaguear()) {
                robot.Parar(true);
                dados.enviarParaConsola(">>> RobotEV3 - metodoEvitar: PARAR POIS O VAGUEAR NÃO ESTA ATIVO");
                System.out.println("EVITAR: PARAR POIS VAGUEAR NAO ESTA ATIVO");
                dormir(1000);
            }
            return true;
        }
        return false;
    }

    /**
     * Comportamento autónomo "Fugir": acionado quando o sensor ultrassónico deteta um obstáculo/inimigo próximo.
     * Executa uma sequência evasiva de curvas e deslocações. Método sincronizado.
     * 
     * @return boolean: Retorna true se executou a fuga, false caso contrário.
     */
    public synchronized boolean metodoFugir() {
        if (verificarsensorUV()) {
            dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: Inimigo detetado!");
            System.out.println("FUGIR: Inimigo detetado!");

            // Escolhe aleatoriamente a direção inicial da fuga (Direita ou Esquerda)
            int curva = random.nextInt(2); // 0 = Direita | 1 = Esquerda

            if (curva == DIREITA) {
                robot.CurvarDireita(20, 90);
                dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: 1 DIREITA");
                System.out.println("FUGIR: 1 DIREITA");
            } else {
                robot.CurvarEsquerda(20, 90);
                dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: 1 ESQUERDA");
                System.out.println("FUGIR: 1 ESQUERDA");
            }

            dormir(1750);

            // Avança 20 cm em linha reta
            robot.Reta(20);
            dados.enviarParaConsola(">>> RobotEV3 - metodoFugir: MEIO RETA");
            System.out.println("FUGIR: MEIO RETA");
            dormir(1000);

            // Executa a curva compensatória no sentido oposto
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
            System.out.println("FUGIR: ACABOU MOVIMENTO");
            robot.Parar(true);
            return true;
        }
        return false;
    }

    /**
     * Método auxiliar privado para pausar a execução da thread atual por um determinado tempo.
     * 
     * @param tempoMillis (long): Tempo de pausa em milissegundos.
     */
    private void dormir(long tempoMillis) {
        try {
            Thread.sleep(tempoMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Verifica de forma sincronizada o sensor de toque na porta S_1.
     * 
     * @return boolean: true se o sensor foi acionado (valor 1), false caso contrário.
     */
    public synchronized boolean verificarsensorToque() {
        int leituraSensor = robot.SensorToque(S_1);
        if (leituraSensor == 1) {
            dados.enviarParaConsola("EVITAR: Obstáculo detectado!");
            return true;
        }
        return false;
    }

    /**
     * Verifica de forma sincronizada o sensor ultrassónico na porta S_2.
     * 
     * @return boolean: true se a distância medida for inferior a 50 cm, false caso contrário.
     */
    public synchronized boolean verificarsensorUV() {
        float distancia = robot.SensorUS(S_2);
        if (distancia < 50) {
            dados.enviarParaConsola("FUGIR: Inimigo detectado!");
            return true;
        }
        return false;
    }
}