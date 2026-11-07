import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Ficheiro Mian.java
 * 
 * Descrição:
 * Serve para testes e medição do desempenho para o estudo comparativo de concorrência.
 * Permite simular múltiplos robôs a correr em simulatâneo com Threads Virtuais (JDK 21+)
 * medindo a escalabilidade e permitindo detetar 'thread pinning' via JFR.
 */


public class Main {

    // Parâmetros de configuração
    private static final int NUM_ROBOS = 100;

    // Número de iterações de movimentos que cada robô vai usar no teste
    private static final int ITERACOES_POR_ROBO = 5;

    /**
     * ESTRATÉGIA DE BLOQUEIO:
     * true -> Usa ReeentrantLock (evita pinning nas Threads Vistuais)
     * false -> Usa synchronized (provoca pinning nas Threads Virtuais no JDK 21)
     */
    private static final boolean USAR_REENTRANT_LOCK = true;

    /**
     * ESTRATÉGIA DE THREADS:
     * true -> Usa Virtual Threads (uma thread por robô)
     * false -> Usa uma Thread Pool fixa de Platform Treads (threads do SO)
     */
    private static final boolean USAR_VIRTUAL_THREADS = true;


    private static void main (String[] args){
        System.out.println("=== INÍCIO DO SIMULADOR EV3 ===");
        System.out.println("Número de robôs em simultâneo: " + NUM_ROBOS);
        System.out.println("Estratégia de Lock: " + (USAR_REENTRANT_LOCK ? "ReentrantLock" : "synchronized"));
        System.out.println("Tipo de Threads: " + (USAR_VIRTUAL_THREADS ? "Virtual Threads" : "Platform Threads")); 
        System.out.println("-------------------------------------");

        // 1. Seleção do Executor de Threads
        ExecutorService executor;
        if(USAR_VIRTUAL_THREADS) {
            // Criar um executor que lança um nova Virtual Thread por cada tarefa;
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            // Criar uma pool fixa com um número limitado de threads físicas do sistema operativo
            int numCores = Runtime.getRuntime().availableProcessors();
            executor = Executors.newFixedThreadPool(numCores * 2);
        }

        List<Future<?>> tarefas = new ArrayList<>();
        long tempoInicio = System.nanoTime();



        // 2. Criação e submissão dos robôs para a Thread Pool / Virtual Threads
        for (int i = 1; i <= NUM_ROBOS; i++) {
            final int roboId = i;

            // Submete a simulaçáo do robô como uma tarefa assíncrona
            Future<?> tarefa = executor.submit(() -> {

                // Instancia o objeto Dados com a configuração de lock escolhida
                Dados dados = new Dados();
                dados.setNomeRobot("EV3_Robo_" + roboId);
                dados.setUseReentrantLock(USAR_REENTRANT_LOCK);

                // Cria o robô com a configuração de bloqueio
                RobotEV3 robot = new RobotEV3(dados);

                // Simulação da rotina de trabalho do robô (Vaguear / Evitar / Fugir)
                for (int inter = 0; inter < ITERACOES_POR_ROBO; inter++) {
                    robot.metodoVaguear();
                    robot.metodoEvitar();
                    robot.metodoFugir();
                }
            });

            tarefas.add(tarefa);
        }

        // 3. Aguardar por todos os robôs terminarem as suas tarefas
        for (Future<?> tarefa : tarefas) {
            try {
                tarefa.get(); // Bloqueia a thread principal do Main até a tarefa concluir
            } catch (Exception e) {
                System.err.println("Erro na execução de uma thread de robô: " + e.getMessage());
            }
        }

        // Encerrar o executor
        executor.shutdown();

        long tempoFim = System.nanoTime();
        double tempoTotalSegundos = (tempoFim - tempoInicio) / 1_000_000_000.0;

        // 4. Exibir os resultados do teste
        System.out.println("-------------------------------------");
        System.out.println("=== TESTE CONCLUÍDO COM SUCESSO ===");
        System.out.printf("Tempo total de execução: %.3f segundos%n", tempoTotalSegundos);
        System.out.printf("Throughput estimado: %.2f robôs/segundo\n", (NUM_ROBOS / tempoTotalSegundos));
    }
}