import java.util.concurrent.Semaphore;

/**
 * Ficheiro: Evitar.java
 * Descrição: Classe que define o comportamento autónomo "Evitar" em ambiente headless.
 * Estende a classe Thread para correr de forma concorrente e utiliza uma máquina de 
 * estados gerida por semáforos para detetar obstáculos através do sensor de toque e 
 * executar manobras de desvio.
 */
public class Evitar extends Thread {

    // Objeto de alto nível que controla os movimentos físicos/simulados do robot
    private RobotEV3 robot;

    // Semáforo de sincronização que controla a ativação e suspensão do comportamento "Evitar"
    private Semaphore evitarAFuncionar;
    
    // Instância global para manipular os dados partilhados de configuração e depuração
    private Dados dados;

    // Constantes que definem os estados possíveis da máquina de estados do comportamento
    private static final int TERMINAR = 0;    // Estado para encerrar a execução da thread
    private static final int LER_SENSOR = 1;  // Estado para ler sensores e executar o desvio
    private static final int FAZER_NADA = 2;  // Estado de espera/pausa (bloqueado pelo semáforo)

    // Variável que armazena o estado atual da máquina de estados
    private int estado;

    /**
     * Construtor da classe Evitar (versão headless).
     * Valida os argumentos recebidos e inicializa os parâmetros do comportamento.
     *
     * @param dados (Dados): Objeto que armazena os dados partilhados do sistema.
     * @param robot (RobotEV3): Objeto responsável por comandar os movimentos do robot.
     */
    public Evitar(Dados dados, RobotEV3 robot) {
        // Validação defensiva para evitar exceções do tipo NullPointerException
        if (dados == null) {
            throw new IllegalArgumentException("EVITAR: dados não pode ser nulo.");
        }
        if (robot == null) {
            throw new IllegalArgumentException("EVITAR: robot não pode ser nulo.");
        }

        this.dados = dados;
        
        // Inicializa o semáforo com 0 permissões para que a thread comece em pausa (bloqueada)
        this.evitarAFuncionar = new Semaphore(0);
        
        this.robot = robot;
        
        // Define o estado inicial da máquina de estados como inativo/em espera
        this.estado = FAZER_NADA;
    }

    /**
     * Método principal executado quando a thread "Evitar" é iniciada.
     * Mantém o ciclo de vida ativo, invocando continuamente a máquina de estados
     * até que o estado passe a ser TERMINAR.
     */
    @Override
    public void run() {
        while (estado != TERMINAR) {
            executarMaquinaDeEstados();
        }
    }

    /**
     * Método que implementa a lógica da máquina de estados do comportamento Evitar.
     * Controla o fluxo de execução com base no estado atual (`FAZER_NADA`, `LER_SENSOR`, `TERMINAR`).
     */
    public void executarMaquinaDeEstados() {
        long t = 0; // Variável para armazenar o tempo de pausa (duração da manobra)
        
        switch (estado) {

        case FAZER_NADA:
            try {
                // Regista na consola que o comportamento está em espera de autorização
                dados.enviarParaConsola("Evitar: Aguarda Permissão para evitar...");
                
                // Bloqueia a thread de forma síncrona até receber uma permissão (release)
                evitarAFuncionar.acquire();
                
                // Após desbloquear, transita para o estado de leitura de sensores
                estado = LER_SENSOR;
            } catch (InterruptedException e) {
                // Trata a interrupção da thread de forma segura
                System.err.println("Evitar: A thread foi interrompida enquanto aguardava permissão para evitar.");
                Thread.currentThread().interrupt();
            }
            break;

        case LER_SENSOR:
            // Verifica se o sensor de toque detetou um obstáculo através do robot
            if (robot.metodoEvitar()) {
                // Se detetou, calcula o tempo exato necessário para concluir a manobra de desvio
                t = tempoEvitar();
            } else {
                // Caso contrário, define uma pausa curta padrão antes da próxima verificação
                t = 500;
            }
            // Adormece a thread durante o tempo calculado para permitir que o robot execute o movimento
            dormir(t);
            break;

        case TERMINAR:
            // Regista o encerramento do comportamento na consola
            dados.enviarParaConsola("Evitar: TERMINAR");
            break;

        default:
            // Em caso de estado corrompido ou inválido, força a transição para TERMINAR
            estado = TERMINAR;
            dados.enviarParaConsola("Evitar: TERMINAR");
            break;
        }
    }

    /**
     * Ativa o comportamento "Evitar", mudando o estado para LER_SENSOR 
     * e libertando o semáforo para destravar a execução da thread.
     */
    public void funcionar() {
        estado = LER_SENSOR;
        evitarAFuncionar.release(); // Liberta 1 permissão no semáforo
    }

    /**
     * Coloca o comportamento em pausa, alterando o estado atual para FAZER_NADA.
     */
    public void parar() {
        estado = FAZER_NADA;
    }

    /**
     * Para completamente o robot de forma síncrona, define o estado como TERMINAR 
     * e liberta o semáforo para garantir que a thread sai de qualquer bloqueio pendente.
     */
    public void pararRobot() {
        robot.Parar(true); // Comando de paragem síncrona ao hardware/simulador
        estado = TERMINAR;
        evitarAFuncionar.release(); // Destrava o acquire() caso esteja bloqueado
    }

    /**
     * Método auxiliar privado para adormecer a thread atual por um determinado período.
     * Captura e trata exceções de interrupção em conformidade com as boas práticas em Java.
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
     * a manobra completa de evasão (recuar em linha reta + curva de desvio).
     * 
     * @return long: O tempo total da manobra em milissegundos.
     */
    public long tempoEvitar() {
        double t = 0;
        double distanciaReta = 15;  // Distância de recuo em centímetros
        double velocidade = 20;     // Velocidade estimada de deslocação (cm/s)
        
        // Calcula o tempo gasto no movimento retilíneo para trás
        t = distanciaReta / velocidade;
        
        double raio = 4.5;          // Raio da curva em centímetros
        double angulo = Math.PI / 2; // Ângulo de rotação (90 graus em radianos)
        double distanciaCurva = raio * angulo; // Comprimento do arco da curva
        
        // Adiciona o tempo necessário para executar a curva
        t += distanciaCurva / velocidade;
        
        // Converte o tempo total de segundos para milissegundos e retorna como long
        return (long)(t * 1000);
    }
}