import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Ficheiro: Fugir.java
 * Descrição: Classe que define o comportamento autónomo "Fugir" em ambiente headless.
 * Estende a classe Thread para execução concorrente e utiliza uma máquina de estados 
 * gerida por semáforos para permitir que o robô se afaste de obstáculos detetados 
 * pelo sensor ultrassónico.
 */
public class Fugir extends Thread {

    // Objeto responsável por controlar os movimentos do robô
    private RobotEV3 robot;

    // Semáforo de sincronização para controlar a ativação e suspensão do comportamento
    private Semaphore fugirAFuncionar;

    // Instância global para manipular dados partilhados de configuração e depuração
    private Dados dados;

    // Gerador de valores aleatórios (utilizado nas manobras de fuga)
    private Random random;

    // Constantes que definem os estados possíveis da máquina de estados
    private static final int TERMINAR = 0;    // Estado para encerrar a execução da thread
    private static final int LER_SENSOR = 1;  // Estado para ler o sensor e executar a fuga
    private static final int FAZER_NADA = 2;  // Estado de espera (bloqueado à escuta do semáforo)

    // Variável que guarda o estado atual da máquina de estados
    private int estado;

    /**
     * Construtor da classe Fugir (versão headless).
     * Valida os parâmetros de entrada e inicializa os atributos necessários.
     *
     * @param dados (Dados): Objeto com os dados partilhados do sistema.
     * @param robot (RobotEV3): Objeto que representa o robô e permite controlá-lo.
     */
    public Fugir(Dados dados, RobotEV3 robot) {
        // Validação defensiva contra NullPointerException
        if (dados == null) {
            throw new IllegalArgumentException("FUGIR: dados não pode ser nulo.");
        }
        if (robot == null) {
            throw new IllegalArgumentException("FUGIR: robot não pode ser nulo.");
        }

        this.dados = dados;
        this.random = new Random();
        
        // Inicializa o semáforo com 0 permissões para que a thread comece em pausa (bloqueada)
        this.fugirAFuncionar = new Semaphore(0);
        
        this.robot = robot;
        
        // Define o estado inicial como inativo/em espera
        this.estado = FAZER_NADA;
    }

    /**
     * Método principal executado quando a thread "Fugir" é iniciada.
     * Mantém o ciclo ativo, executando a máquina de estados até o estado passar a TERMINAR.
     */
    @Override
    public void run() {
        while (estado != TERMINAR) {
            executarMaquinaDeEstados();
        }
    }

    /**
     * Método que implementa a lógica da máquina de estados do comportamento Fugir.
     * Gere as transições entre `FAZER_NADA`, `LER_SENSOR` e `TERMINAR`.
     */
    public void executarMaquinaDeEstados() {
        long t = 0; // Armazena o tempo de duração da manobra de fuga
        
        switch (estado) {

        case FAZER_NADA:
            try {
                // Bloqueia a thread de forma síncrona até receber uma permissão (release)
                fugirAFuncionar.acquire();
                
                // Assim que obtém permissão, transita para a leitura de sensores
                estado = LER_SENSOR;
            } catch (InterruptedException e) {
                // Tratamento seguro da interrupção da thread
                System.err.println("Fugir: A thread foi interrompida enquanto aguardava permissão para fugir.");
                Thread.currentThread().interrupt();
            }
            break;

        case LER_SENSOR:
            // Verifica se o robô detetou algum obstáculo/inimigo através do sensor correspondente
            if (robot.metodoFugir()) {
                // Se detetou, calcula o tempo exato que a manobra de fuga irá demorar
                t = tempoFugir();
            } else {
                // Caso contrário, aguarda um intervalo curto padrão antes de nova verificação
                t = 500;
            }
            // Adormece a thread durante o tempo calculado para dar tempo ao robô de executar o movimento
            dormir(t);
            break;

        case TERMINAR:
            // Garante que o estado se mantém em terminar para encerrar o ciclo
            estado = TERMINAR;
            break;

        default:
            // Em caso de anomalia, força o encerramento da máquina de estados
            estado = TERMINAR;
            break;
        }
    }

    /**
     * Ativa o comportamento "Fugir", alterando o estado para LER_SENSOR 
     * e libertando o semáforo para destravar a execução da thread.
     */
    public void funcionar() {
        estado = LER_SENSOR;
        fugirAFuncionar.release(); // Liberta 1 permissão no semáforo
    }

    /**
     * Coloca o comportamento em pausa, mudando o estado atual para FAZER_NADA
     * e registando a ação na consola de depuração.
     */
    public void parar() {
        dados.enviarParaConsola("Fugir: METODO PARAR FUGIR");
        estado = FAZER_NADA;
    }

    /**
     * Para completamente o robô de forma síncrona, define o estado como TERMINAR 
     * e liberta o semáforo para garantir que a thread sai de qualquer bloqueio pendente.
     */
    public void pararRobot() {
        robot.Parar(true); // Comando de paragem síncrona ao hardware/simulador
        estado = TERMINAR;
        fugirAFuncionar.release(); // Destrava o acquire() caso esteja bloqueado
        dados.enviarParaConsola("Fugir: Parar Robot");
    }

    /**
     * Método auxiliar privado para adormecer a thread atual por um determinado período.
     * Trata exceções de interrupção em conformidade com as boas práticas de concorrência.
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
     * Calcula o tempo teórico em milissegundos necessário para o robô executar 
     * a manobra completa de fuga (curvas e deslocações retilíneas).
     * 
     * @return long: O tempo total da manobra de fuga em milissegundos.
     */
    public long tempoFugir() {
        double t = 0;
        double velocidade = 20;       // Velocidade estimada de deslocação (cm/s)
        double raio = 20;             // Raio das curvas de fuga (cm)
        double angulo = Math.PI / 2;  // Ângulo de rotação (90 graus em radianos)
        double distanciaCurva = (raio * angulo); // Comprimento do arco da curva
        
        // Adiciona o tempo necessário para as duas curvas executadas na manobra
        t = (t + ((distanciaCurva / velocidade) * 2));
        
        double distanciaReta = 20; // Distância percorrida em linha reta durante a fuga (cm)
        // Adiciona o tempo do percurso em linha reta
        t = (t + (distanciaReta / velocidade));
        
        // Converte o tempo total de segundos para milissegundos e devolve como long
        return (long)(t * 1000);
    }
}