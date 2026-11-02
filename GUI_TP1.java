import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;

/**
 * A classe GUI_TP1 é uma classe que representa a interface gráfica do
 * utilizador para o controlo de um robot EV3. Esta classe estende JFrame.
 */
public class GUI_TP1 extends JFrame {

	private static final long serialVersionUID = 1L;

	// Atributos

	private JPanel contentPanel;
	private JButton stopButton;
	private JLabel nomeRobotLabel;
	private JTextField robotTextField;
	private JTextArea consolaTextArea;
	private JScrollPane scrollPanel;

	// Instância para manipular os dados compartilhados entre as classes
	Dados dados;

	//
	private RobotEV3 robot;

	// Indica se o robot está no modo real ou simulado
	private RobotEV3 robotReal;

	// Nome do robot
	private String nomeRobot;

	// Comportamentos de vaguear, evitar e fugir
	private Vaguear vaguear;
	private Evitar evitar;
	private Fugir fugir;

	/**
	 * Lança a aplicação
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		GUI_TP1 guiTp1 = new GUI_TP1(100);
	}

	/**
	 * Criação da Frame
	 * 
	 * @param x (int): Valor da posição em pixel da janela na coordenada x.
	 */
	public GUI_TP1(int x) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				vaguear.pararRobot();
				evitar.pararRobot();
				fugir.pararRobot();
				robot.Terminar();
			}
		});

		dados = new Dados(this);
		dados.setGUI(this);

		// Inicialmente, cria como simulado
		this.robot = new RobotEV3(dados);
		this.robotReal = null;

		vaguear = new Vaguear(this, dados, robot);
		evitar = new Evitar(this, dados, robot);
		fugir = new Fugir(this, dados, robot);

		vaguear.start();
		evitar.start();
		fugir.start();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(x, 100, 600, 450);

		contentPanel = new JPanel();
		contentPanel.setForeground(Color.RED);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);

		nomeRobotLabel = new JLabel("Robot");
		nomeRobotLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		nomeRobotLabel.setBounds(139, 10, 45, 13);
		contentPanel.add(nomeRobotLabel);

		robotTextField = new JTextField(dados.getNomeRobot());
		robotTextField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		robotTextField.setColumns(10);
		robotTextField.setBounds(200, 7, 208, 20);
		robotTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nomeRobot = robotTextField.getText();
				dados.setNomeRobot(nomeRobot);
				dados.enviarParaConsola("GUI_TP1: Nome do robot é " + nomeRobot);
			}
		});
		contentPanel.add(robotTextField);

		// Botão ON/OFF
		JRadioButton onOffRadioButton = new JRadioButton("On / Off");
		onOffRadioButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		onOffRadioButton.setBounds(475, 7, 88, 20);
		onOffRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (onOffRadioButton.isSelected()) {
					dados.enviarParaConsola("GUI_TP1: " + nomeRobot + " OFF");
					System.out.println(nomeRobot);
					robot.OpenEV3(nomeRobot);
				} else {
					dados.enviarParaConsola("GUI_TP1: OFF");
					robot.CloseEV3();
				}
			}
		});
		contentPanel.add(onOffRadioButton);

		JRadioButton realRadioButton = new JRadioButton("Real");
		realRadioButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		realRadioButton.setBounds(475, 34, 80, 20);
		realRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (realRadioButton.isSelected()) {
					robot.setRobotReal(true);
					dados.setReal(true);
					setRobotReal(robot);
					dados.enviarParaConsola("GUI_TP1: REAL == TRUE");
				} else {
					robot.setRobotReal(false);
					dados.setReal(false);
					dados.enviarParaConsola("GUI_TP1: REAL == FALSE");
				}
			}
		});
		contentPanel.add(realRadioButton);

		// CheckBox Vaguear, Evitar e Fugir
		JCheckBox vaguearCheckBox = new JCheckBox("Vaguear");
		vaguearCheckBox.setFont(new Font("Tahoma", Font.BOLD, 14));
		vaguearCheckBox.setBounds(475, 66, 90, 20);
		contentPanel.add(vaguearCheckBox);

		JCheckBox fugirCheckBox = new JCheckBox("Fugir");
		fugirCheckBox.setFont(new Font("Tahoma", Font.BOLD, 14));
		fugirCheckBox.setBounds(475, 84, 90, 20);
		contentPanel.add(fugirCheckBox);

		JCheckBox evitarCheckBox = new JCheckBox("Evitar");
		evitarCheckBox.setFont(new Font("Tahoma", Font.BOLD, 14));
		evitarCheckBox.setBounds(475, 102, 90, 20);
		contentPanel.add(evitarCheckBox);

		// Botão Parar
		stopButton = new JButton("Parar");
		stopButton.setForeground(Color.BLACK);
		stopButton.setBackground(Color.RED);
		stopButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		stopButton.setOpaque(true);
		stopButton.setBorder(new LineBorder(Color.RED, 3, true));
		stopButton.setBounds(198, 82, 100, 25);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dados.enviarParaConsola("GUI_TP1: PARAR");
				robot.Parar(true);
			}
		});
		contentPanel.add(stopButton);

		// Botão Frente
		JButton frenteButton = new JButton("Frente");
		frenteButton.setBackground(new Color(0, 250, 154));
		frenteButton.setForeground(Color.BLACK);
		frenteButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		frenteButton.setOpaque(true);
		frenteButton.setBorder(new LineBorder(new Color(0, 250, 154), 3, true));
		frenteButton.setBounds(198, 52, 100, 25);
		frenteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.Parar(false);
				robot.Reta(50);
				dados.enviarParaConsola("GUI_TP1: FRENTE");
			}
		});
		contentPanel.add(frenteButton);

		// Botão Direita
		JButton direitaButton = new JButton("Direita");
		direitaButton.setBackground(new Color(70, 130, 180));
		direitaButton.setForeground(Color.BLACK);
		direitaButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		direitaButton.setBorder(new LineBorder(new Color(70, 130, 180), 3, true));
		direitaButton.setOpaque(true);
		direitaButton.setBounds(308, 82, 100, 25);
		direitaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.Parar(false);
				robot.CurvarDireita(0, 360);
				dados.enviarParaConsola("GUI_TP1: DIREITA");
			}
		});
		contentPanel.add(direitaButton);

		// Botão Esquerda
		JButton esquerdaButton = new JButton("Esquerda");
		esquerdaButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		esquerdaButton.setBackground(new Color(221, 160, 221));
		esquerdaButton.setBorder(new LineBorder(new Color(221, 160, 221), 3, true));
		esquerdaButton.setOpaque(true);
		esquerdaButton.setBounds(88, 82, 100, 25);
		esquerdaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.Parar(false);
				robot.CurvarEsquerda(0, 360);
				dados.enviarParaConsola("GUI_TP1: ESQUERDA");
			}
		});
		contentPanel.add(esquerdaButton);

		// Botão Trás
		JButton trasButton = new JButton("Trás");
		trasButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		trasButton.setBackground(new Color(255, 255, 0));
		trasButton.setForeground(Color.BLACK);
		trasButton.setBorder(new LineBorder(new Color(255, 255, 0), 3, true));
		trasButton.setOpaque(true);
		trasButton.setBounds(198, 112, 100, 25);
		trasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.Parar(false);
				robot.Reta(-50);
				dados.enviarParaConsola("GUI_TP1: TRÁS");
			}
		});
		contentPanel.add(trasButton);

		// Botão de Debug
		JRadioButton debugRadioButton = new JRadioButton("Debug");
		debugRadioButton.setBackground(new Color(255, 255, 224));
		debugRadioButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		debugRadioButton.setBounds(310, 159, 100, 25);
		debugRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (debugRadioButton.isSelected()) {
					dados.setDebug(true);
					dados.enviarParaConsola("GUI_TP1: DEBUG == TRUE");
				} else {
					dados.setDebug(false);
					dados.enviarParaConsola("GUI_TP1: DEBUG == FALSE");
				}
			}
		});
		contentPanel.add(debugRadioButton);

		// Botão Limpar
		JButton limparButton = new JButton("Limpar");
		limparButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		limparButton.setBackground(new Color(255, 255, 224));
		limparButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		limparButton.setBounds(475, 159, 100, 25);
		limparButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				consolaTextArea.setText("");
			}
		});
		contentPanel.add(limparButton);

		JLabel consolaLabel = new JLabel("Consola");
		consolaLabel.setOpaque(true);
		consolaLabel.setBackground(new Color(255, 255, 224));
		consolaLabel.setForeground(Color.BLACK);
		consolaLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		consolaLabel.setBorder(new LineBorder(new Color(255, 255, 224), 3, true));
		consolaLabel.setBounds(10, 159, 100, 25);
		contentPanel.add(consolaLabel);

		// Consola
		consolaTextArea = new JTextArea();
		consolaTextArea.setBackground(new Color(255, 255, 224));
		consolaTextArea.setEditable(false);
		consolaTextArea.setLineWrap(true);
		scrollPanel = new JScrollPane(consolaTextArea);
		scrollPanel.setBounds(10, 195, 564, 205);
		contentPanel.add(scrollPanel);

		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setOpaque(true);
		horizontalBox.setBackground(new Color(255, 255, 224));
		horizontalBox.setBounds(10, 159, 564, 25);
		contentPanel.add(horizontalBox);

		vaguearCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vaguearCheckBox.isSelected()) {
					dados.setVaguear(true);
					dados.enviarParaConsola("GUI_TP1: VAGUEAR");
					vaguear.funcionar();
				} else {
					dados.setVaguear(false);
					vaguear.parar();
					dados.enviarParaConsola("GUI_TP1: VAGUEAR PARADO");
				}
			}
		});

		evitarCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (evitarCheckBox.isSelected() && !dados.getVaguear()) {
					dados.enviarParaConsola("GUI_TP1: APENAS EVITAR");
					// System.out.println("GUI_TP1: APENAS EVITAR");
					dados.setEvitar(true);

					//dados.enviarParaConsola("GUI_TP1: Evitar ativado: " + (dados.getEvitar() ? "Sim" : "Não"));
					// System.out.println("GUI_TP1: Evitar ativado: " + (dados.getEvitar() ? "Sim" :
					// "Não"));
					fugir.parar();
					evitar.funcionar();
				} else if (evitarCheckBox.isSelected() && dados.getVaguear()) {
					// System.out.println("GUI_TP1: EVITAR E VAGUEAR");
					dados.setEvitar(true);

					//dados.enviarParaConsola(
							//"GUI_TP1: Evitar ativado com vaguear: " + (dados.getEvitar() ? "Sim" : "Não"));
					// System.out.println("GUI_TP1: Evitar ativado com vaguear: " +
					// dados.getEvitar());
					fugir.parar();
					evitar.funcionar();

				} else {
					dados.setEvitar(false);
					evitar.parar(); // Parar o comportamento de vaguear

					//dados.enviarParaConsola("GUI_TP1: Evitar desativado: " + (dados.getEvitar() ? "Sim" : "Não"));
					// System.out.println("GUI_TP1: Evitar desativado: " + (dados.getEvitar() ?
					// "Sim" : "Não"));
				}
			}
		});

		fugirCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fugirCheckBox.isSelected()) {
					dados.setFugir(true);
					dados.enviarParaConsola("GUI_TP1: FUGIR");
					fugir.funcionar(); // Inicia o comportamento de vaguear
				} else {
					dados.setFugir(false);
					fugir.parar(); // Parar o comportamento de vaguear
					dados.enviarParaConsola("GUI_TP1: FUGIR PARADO");
				}
			}
		});

		setVisible(true);
	}

	/**
	 * Retorna a área de texto da consola onde são exibidas as mensagens de log e
	 * informações de debug. Este método permite que outras classes acedam à consola
	 * para adicionar novas mensagens.
	 *
	 * @return JTextArea: A área de texto da consola.
	 */
	public JTextArea getConsola() {
		return consolaTextArea;
	}

	/**
	 * Retorna a instância do robot EV3 que está a ser controlada pela GUI. Este
	 * método pode ser usado para obter referências ao robot e executar comandos
	 * diretamente nele a partir de outras classes.
	 *
	 * @return RobotEV3: A instância do robot controlado.
	 */
	public RobotEV3 getRobot() {
		return robot;
	}

	/**
	 * Configura o robot para operar no modo real, se ainda não estiver configurado.
	 * Este método inicializa uma nova instância do robot real apenas se a variável
	 * "robotReal" ainda for nula. O uso desse método permite que o sistema permuta
	 * entre o modo simulado e o modo real de operação do robot.
	 *
	 * @param RobotEV3: A instância do RobotEV3 a ser configurada como robot real.
	 */
	public void setRobotReal(RobotEV3 robot) {
		if (robotReal == null) {
			robotReal = new RobotEV3(dados);
			robot = robotReal;
		}
	}
}