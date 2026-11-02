/**
 * Ficheiro: Dados.java
 * Descrição: Classe que representa os dados de configuração e estado do robot. 
 * Mantém informações sobre o nome do robot, o estado atual de várias funcionalidades 
 * (modo de debug, ligado/desligado, modo real/simulado) e os estados dos 
 * comportamentos (vaguear, evitar, fugir) em ambiente headless.
 */
public class Dados {

    // Nome identificador atribuído ao robot
    private String nomeRobot;

    // Estado geral de funcionamento (Ligado / Desligado)
    private boolean onOff;

    // Indicador de ativação do modo de depuração (debug)
    private boolean debug;

    // Estados operacionais dos comportamentos autónomos do robot
    private boolean vaguear;
    private boolean evitar;
    private boolean fugir;

    // Indica se o robot está a operar no modo real (físico) ou simulado
    private boolean robotReal;

    /**
     * Construtor por defeito da classe Dados.
     * Inicializa o robot com o nome padrão "EVA" e todas as flags desativadas (false).
     */
    public Dados() {
        nomeRobot = "EVA";
        debug = false;
        onOff = false;
        robotReal = false;
        vaguear = false;
        evitar = false;
        fugir = false;
    }

    // ==========================================
    // MÉTODOS GETTERS E SETTERS
    // ==========================================

    /**
     * Retorna o nome atual atribuído ao robot.
     *
     * @return String: Nome do robot.
     */
    public String getNomeRobot() {
        return nomeRobot;
    }

    /**
     * Define um novo nome para o robot.
     *
     * @param nomeRobot (String): Novo nome a atribuir.
     */
    public void setNomeRobot(String nomeRobot) {
        this.nomeRobot = nomeRobot;
    }

    /**
     * Retorna o estado atual do modo de debug.
     *
     * @return boolean: Retorna true se o modo debug está ativado, false caso contrário.
     */
    public boolean getDebug() {
        return debug;
    }

    /**
     * Define o estado do modo de debug.
     *
     * @param debug (boolean): true para ativar o modo debug, false para desativar.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Retorna o estado ligado/desligado do robot.
     *
     * @return boolean: Retorna true se o robot está ligado, false caso contrário.
     */
    public boolean isOnOff() {
        return onOff;
    }

    /**
     * Define o estado ligado/desligado do robot.
     *
     * @param onOff (boolean): true para ligar o robot, false para desligar.
     */
    public void setOnOff(boolean onOff) {
        this.onOff = onOff;
    }

    /**
     * Retorna o estado do modo real de operação.
     *
     * @return boolean: Retorna true se o robot está no modo real, false se estiver simulado.
     */
    public boolean getReal() {
        return robotReal;
    }

    /**
     * Define o estado do modo real de operação.
     *
     * @param real (boolean): true para ativar o modo real, false para ativar o modo simulado.
     */
    public void setReal(boolean real) {
        this.robotReal = real;
    }

    /**
     * Retorna o estado do comportamento de vaguear.
     *
     * @return boolean: Retorna true se o robot está em modo de vaguear, false caso contrário.
     */
    public boolean getVaguear() {
        return vaguear;
    }

    /**
     * Define o estado do comportamento de vaguear.
     *
     * @param vaguear (boolean): true para ativar o comportamento de vaguear, false para desativar.
     */
    public void setVaguear(boolean vaguear) {
        this.vaguear = vaguear;
    }

    /**
     * Retorna o estado do comportamento de evitar obstáculos.
     *
     * @return boolean: Retorna true se o robot está em modo de evitar, false caso contrário.
     */
    public boolean getEvitar() {
        return evitar;
    }

    /**
     * Define o estado do comportamento de evitar obstáculos.
     *
     * @param evitar (boolean): true para ativar o comportamento de evitar, false para desativar.
     */
    public void setEvitar(boolean evitar) {
        this.evitar = evitar;
    }

    /**
     * Retorna o estado do comportamento de fuga.
     *
     * @return boolean: Retorna true se o robot está em modo de fuga, false caso contrário.
     */
    public boolean getFugir() {
        return fugir;
    }

    /**
     * Define o estado do comportamento de fuga.
     *
     * @param fugir (boolean): true para ativar o comportamento de fuga, false para desativar.
     */
    public void setFugir(boolean fugir) {
        this.fugir = fugir;
    }

    // ==========================================
    // OUTROS MÉTODOS
    // ==========================================

    /**
     * Envia uma mensagem de texto para a consola padrão (System.out) se o modo de debug estiver ativo.
     *
     * @param texto (String): Texto descritivo a ser registado.
     */
    public void enviarParaConsola(String texto) {
        if (debug) { // Verifica se o modo debug está ativo antes de escrever
            System.out.println(texto);
        }
    }
}