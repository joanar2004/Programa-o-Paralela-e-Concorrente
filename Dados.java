/**
 * Classe Dados representa os dados de configuração e estado do robot. Ela
 * mantém informações sobre o nome do robot, a interface gráfica (GUI) associada
 * e o estado atual de várias funcionalidades do robot, como modo de debug, e
 * estados de operação (vaguear, evitar, fugir) e se este é ou não real.
 */
public class Dados {

	private String nomeRobot;

	// Interface gráfica (GUI) utilizada no projeto
	private GUI_TP1 gui;

	// Ligado/Desligado
	private boolean onOff;

	private boolean debug;

	// Comportamentos de vaguear, evitar e fugir
	private boolean vaguear;
	private boolean evitar;
	private boolean fugir;

	// Indica se o robot está no modo real ou simulado
	private boolean robotReal;

	/**
	 * Construtor da classe Dados Default
	 */
	public Dados() {
		nomeRobot = "EVA";
		gui = null;
		debug = false;
		onOff = false;
		robotReal = false;
		vaguear = false;
		evitar = false;
	}

	/**
	 * Construtor da classe Dados que permite associar uma GUI específica.
	 *
	 * @param g (GUI_TP1): Interface gráfica (GUI) utilizada.
	 */
	public Dados(GUI_TP1 g) {
		nomeRobot = "EVA";
		gui = g;
		onOff = false;
		debug = false;
		robotReal = false;
		vaguear = false;
		evitar = false;
	}

	// Setters and Getters

	/**
	 * Define a interface gráfica (GUI) associada.
	 *
	 * @param gui (GUI_TP1): Interface gráfica a ser associada.
	 */
	public void setGUI(GUI_TP1 gui) {
		this.gui = gui;
	}

	/**
	 * Retorna a interface gráfica associada.
	 *
	 * @return GUI_TP1: Retorna a interface gráfica associada ao objeto Dados.
	 */
	public GUI_TP1 getGUI() {
		return this.gui;
	}

	/**
	 * Retorna o nome do robot atual.
	 *
	 * @return String: Nome do robot.
	 */
	public String getNomeRobot() {
		return nomeRobot;
	}

	/**
	 * Define um novo nome para o robot.
	 *
	 * @param nomeRobot (String): Novo nome para o robot.
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
	 * @param debug (boolean): Retorna true para ativar o debug, false para desativar.
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
	 * @param onOff (boolean): Retorna true para ligar o robot, false para desligar.
	 */
	public void setOnOff(boolean onOff) {
		this.onOff = onOff;
	}

	/**
	 * Retorna o estado do modo real.
	 *
	 * @return boolean: Retorna true se o robot está no modo real, false caso contrário.
	 */
	public boolean getReal() {
		return robotReal;
	}

	/**
	 * Define o estado do modo real.
	 *
	 * @param real (boolean): Retorna true para ativar o modo real, false para ativar o modo
	 *             simulado.
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
     * @param vaguear (boolean): Retorna true para ativar o comportamento de vaguear, false para desativar.
     */
	public void setVaguear(boolean vaguear) {
		this.vaguear = vaguear;
	}

	/**
     * Retorna o estado do comportamento de evitar.
     *
     * @return boolean: Retorna true se o robot está em modo de evitar obstáculos, false caso contrário.
     */
	public boolean getEvitar() {
		return evitar;
	}

	/**
     * Define o estado do comportamento de evitar.
     *
     * @param evitar (boolean): Retorna true para ativar o comportamento de evitar, false para desativar.
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
	 * @param fugir (boolean): Retorna true para ativar o comportamento de fuga, false para
	 *              desativar.
	 */
	public void setFugir(boolean fugir) {
		this.fugir = fugir;
	}

	// Outros Métodos

	/**
	 * Envia uma mensagem para a consola se o modo de debug estiver ativo.
	 *
	 * @param texto (String): Texto a ser enviado para a consola.
	 */
	public void enviarParaConsola(String texto) {
		if (debug) { // Verifica se o modo debug está ativo
			gui.getConsola().append(texto + "\n");
			
			// Faz Scroll até ao final
			gui.getConsola().setCaretPosition(gui.getConsola().getDocument().getLength());
		}
	}

}