import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Ficheiro: Vaguear.java
 * Descrição: Classe que define o comportamento autónomo "Vaguear" em ambiente headless.
 * Estende a classe Thread para execução concorrente e utiliza uma máquina de estados 
 * gerida por semáforos para efetuar deslocamentos e rotações aleatórias contínuas.
 */
public class Vaguear extends Thread {

    // Objeto responsável por controlar os movimentos do robô
    private RobotEV3 robot;

    // Instância global para manipular dados partilhados de configuração e depuração
    private Dados dados;
    
    // Gerador de valores aleatórios para as direções do vaguear
    private Random random;
    
    // Semáforo de sincronização para controlar a ativação e suspensão do comportamento
    private Semaphore vaguearAFuncionar;

    // Constantes que definem os estados possíveis da máquina de estados
    private static final int TERMINAR = 0;     // Estado para encerrar a execução da thread
    private static final int GERAR_RANDOM = 1; // Estado para gerar movimento aleatório
    private static final int FAZER_NADA = 2;   // Estado de espera (bloqueado à escuta do semáforo)

    // Variável que guarda o estado atual da máquina de estados
    private int estado;

    /**
     * Construtor da classe Vaguear (versão headless).
     * Valida os parâmetros de entrada e inicializa os atributos necessários.
     *
     * @param dados (Dados): Objeto com os dados partilhados do sistema.
     * @param robot (RobotEV3): Objeto que representa o robô e permite controlá-lo.
     */
    public Vaguear(Dados dados, RobotEV3 robot) {
        // Validação defensiva contra NullPointerException
        if (dados == null) {
            throw new IllegalArgumentException("VAGUEAR: dados não pode ser nulo.");
        }
        if (robot == null) {
            throw new IllegalArgumentException("VAGUEAR: robot não pode ser nulo.");
        }

        this.dados = dados;
        this.robot = robot;

        this.random = new Random();
        
        // Inicializa o semáforo com 0 permissões para começar em pausa (bloqueado)
        this.vaguearAFuncionar = new Semaphore(0);

        // Define o estado inicial como inativo/em espera
        this.estado = FAZER_NADA;
    }

    /**
     * Método principal executado quando a thread "Vaguear" é iniciada.
     * Mantém o ciclo ativo, executando a máquina de estados até o estado passar a TERMINAR.
     */
    @Override
    public void run() {
        while (estado != TERMINAR) {
            executarMaquinaDeEstados();
        }
    }

    /**
     * Método que implementa a lógica da máquina de estados do comportamento Vaguear.
     * Gere as transições entre `FAZER_NADA`, `GERAR_RANDOM` e `TERMINAR`.
     */
    public void executarMaquinaDeEstados() {

        switch (estado) {
        case FAZER_NADA:
            try {
                // Bloqueia a thread de forma síncrona até receber uma permissão (release) via semáforo
                vaguearAFuncionar.acquire();
            } catch (InterruptedException e) {
                // Tratamento seguro da interrupção da thread
                System.err.println("Vaguear: A thread foi interrompida enquanto aguardava permissão para vaguear.");
                Thread.currentThread().interrupt();
            }
            break;

        case GERAR_RANDOM:
            // Executa o movimento aleatório e adormece durante 1.5 segundos antes da próxima iteração
            gerarRandom();
            dormir(1500);
            break;

        case TERMINAR:
            // Regista o término do comportamento na consola de depuração
            dados.enviarParaConsola("Vaguear: TERMINAR");
            break;

        default:
            // Em caso de anomalia, força a transição segura para o estado TERMINAR
            estado = TERMINAR;
            dados.enviarParaConsola("Vaguear: TERMINAR");
            break;
        }
    }

    /**
     * Ativa o comportamento "Vaguear", alterando o estado para GERAR_RANDOM
     * e libertando o semáforo para destravar a execução da thread.
     */
    public void funcionar() {
        estado = GERAR_RANDOM;
        
        // Liberta o semáforo para permitir que o acquire() prossiga
        vaguearAFuncionar.release();
    }

    /**
     * Coloca o comportamento em pausa, alterando o estado atual para FAZER_NADA.
     */
    public void parar() {
        estado = FAZER_NADA;
    }

    /**
     * Para completamente o robô de forma síncrona, define o estado como TERMINAR 
     * e liberta o semáforo para garantir que a thread sai de qualquer bloqueio pendente.
     */
    public void pararRobot() {
        robot.Parar(true); // Comando de paragem síncrona ao hardware/simulador
        estado = TERMINAR; // Define o estado como TERMINAR para sair do ciclo do run
        vaguearAFuncionar.release(); // Liberta o semáforo para destravar qualquer acquire pendente
    }

    // ==========================================
    // OUTROS MÉTODOS AUXILIARES
    // ==========================================

    /**
     * Invoca o método de movimento aleatório do robô (Vaguear).
     */
    private void gerarRandom() {
        robot.metodoVaguear();
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
}