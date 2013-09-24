/**
 * 项目名称: FansChineseChess
 * 版本号：2.0
 * 名字：雷文
 * 博客: http://FansUnion.cn
 * CSDN:http://blog.csdn.net/FansUnion
 * 邮箱: leiwen@FansUnion.cn
 * QQ：240-370-818
 * 版权所有: 2011-2013,leiwen
 */
package cn.fansunion.chinesechess;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.fansunion.chinesechess.ai.ManMachineGUI;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.core.ManualUtil;
import cn.fansunion.chinesechess.ext.empress.EmpressGUI;
import cn.fansunion.chinesechess.ext.maze.HorseMazeGUI;
import cn.fansunion.chinesechess.load.ChessDemoGUI;
import cn.fansunion.chinesechess.load.ChessLoadingGUI;
import cn.fansunion.chinesechess.net.client.RoomGUI;
import cn.fansunion.chinesechess.print.all.PrintAllGUI;
import cn.fansunion.chinesechess.print.part.PrintPartGUI;
import cn.fansunion.chinesechess.save.GameRecord;


/**
 * 客户端主界面，包含单机游戏和联网对战两个选项卡
 * 
 * 单机游戏选项卡: 全局打谱，残局打谱，装载游戏，高级装载，人机对弈，迷宫求解，八皇后 联网对战选项卡： 输入用户名、密码和服务器地址登录服务器
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class ChessGUI extends JFrame implements ActionListener, NAME {

	private static final long serialVersionUID = -4285888351596327876L;
	private JTabbedPane tabbedPane;

	private JPanel networkPanel;

	private JPanel localPanel;

	private JLabel petName = new JLabel("用户名:  ");

	private JLabel password = new JLabel("密  码:  ");

	private JTextField petNameField = new JTextField(20);

	private JTextField passwordField = new JTextField(20);

	private JLabel serverIP = new JLabel("服务器IP:");

	private JTextField serverIPField = new JTextField(20);

	private JButton login, exit;

	private JButton load, loadAs, partialManual, wholeManual, manMachine, fen,
			maze, eightEmpress;

	public ChessGUI() {
		initButtons();
		initPanels();

		serverIPField.setText("localhost");
		setTitle("楚汉棋兵--雷文-http://FansUnion.cn");
		setIconImage(ChessUtil.getAppIcon());
		setSize(370, 340);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);

		// serverIPField.setText("localhost");
	}

	private void initButtons() {
		int w = ChessUtil.getImageIcon("login.png").getIconWidth()+10;
		int h = ChessUtil.getImageIcon("login.png").getIconHeight();
		login = new JButton("登录");
		login.addActionListener(this);
		login.setToolTipText("登录");
		login.setCursor(new Cursor(Cursor.HAND_CURSOR));
		login.setPreferredSize(new Dimension(w, h));

		exit = new JButton("退出");
		exit.addActionListener(this);
		exit.setToolTipText("退出");
		exit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		exit.setPreferredSize(new Dimension(w, h));

		int width = ChessUtil.getImageIcon("load.png").getIconWidth()+10;
		int height = ChessUtil.getImageIcon("load.png").getIconHeight();

		load = new JButton("装载游戏");
		load.addActionListener(this);
		load.setToolTipText("装载游戏");
		load.setCursor(new Cursor(Cursor.HAND_CURSOR));
		load.setPreferredSize(new Dimension(width, height));

		loadAs = new JButton("高级装载");
		loadAs.addActionListener(this);
		loadAs.setToolTipText("高级装载");
		loadAs.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loadAs.setPreferredSize(new Dimension(width, height));

		fen = new JButton("FEN串→棋局");
		fen.addActionListener(this);
		fen.setToolTipText("根据FEN串生成棋局");
		fen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		fen.setPreferredSize(new Dimension(width + 20, height));

		partialManual = new JButton("残局打谱");
		partialManual.addActionListener(this);
		partialManual.setToolTipText("残局打谱");
		partialManual.setCursor(new Cursor(Cursor.HAND_CURSOR));
		partialManual.setPreferredSize(new Dimension(width, height));

		wholeManual = new JButton("全局打谱");
		wholeManual.addActionListener(this);
		wholeManual.setToolTipText("全局打谱");
		wholeManual.setCursor(new Cursor(Cursor.HAND_CURSOR));
		wholeManual.setPreferredSize(new Dimension(width, height));

		manMachine = new JButton("人机对弈");
		manMachine.addActionListener(this);
		manMachine.setToolTipText("人机对弈");
		manMachine.setCursor(new Cursor(Cursor.HAND_CURSOR));
		manMachine.setPreferredSize(new Dimension(width, height));

		maze = new JButton("迷宫求解");
		maze.addActionListener(this);
		maze.setToolTipText("迷宫求解");
		maze.setCursor(new Cursor(Cursor.HAND_CURSOR));
		maze.setPreferredSize(new Dimension(width, height));

		eightEmpress = new JButton("八皇后");
		eightEmpress.addActionListener(this);
		eightEmpress.setToolTipText("八皇后");
		eightEmpress.setCursor(new Cursor(Cursor.HAND_CURSOR));
		eightEmpress.setPreferredSize(new Dimension(width, height));

		/*
		 * moneyCollect = new JButton("钱币收集");
		 * moneyCollect.addActionListener(this);
		 * moneyCollect.setToolTipText("钱币收集"); moneyCollect.setCursor(new
		 * Cursor(Cursor.HAND_CURSOR)); moneyCollect.setPreferredSize(new
		 * Dimension(width, height));
		 */
	}

	private void initPanels() {
		TitledBorder oneBorder = new TitledBorder("打谱提高");
		oneBorder.setTitleFont(new Font("宋体", Font.PLAIN, 16));
		oneBorder.setTitleColor(new Color(0, 0, 255));

		TitledBorder twoBorder = new TitledBorder("装载棋谱");
		twoBorder.setTitleFont(new Font("宋体", Font.PLAIN, 16));
		twoBorder.setTitleColor(new Color(26, 151, 34));

		TitledBorder threeBorder = new TitledBorder("人工智能");
		threeBorder.setTitleFont(new Font("宋体", Font.PLAIN, 16));
		threeBorder.setTitleColor(new Color(255, 0, 0));

		TitledBorder fourBorder = new TitledBorder("扩展应用");
		fourBorder.setTitleFont(new Font("宋体", Font.PLAIN, 16));
		fourBorder.setTitleColor(new Color(128, 128, 0));

		// 单机修行面板
		FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
		JPanel one = new JPanel(flow);
		one.setBorder(oneBorder);
		JPanel two = new JPanel(flow);
		two.setBorder(twoBorder);
		JPanel three = new JPanel(flow);
		three.setBorder(threeBorder);
		JPanel four = new JPanel(flow);
		four.setBorder(fourBorder);

		one.add(wholeManual);
		one.add(partialManual);
		one.add(fen);

		two.add(load);
		two.add(loadAs);

		four.add(maze);
		four.add(eightEmpress);
		// four.add(moneyCollect);

		three.add(manMachine);
		// three.add(exit);

		localPanel = new JPanel(new GridLayout(4, 3));
		localPanel.add(one);
		localPanel.add(two);
		localPanel.add(three);
		localPanel.add(four);

		// 联网对战面板
		FlowLayout flow2 = new FlowLayout(FlowLayout.LEFT);
		JPanel one2 = new JPanel(flow2);
		JPanel two2 = new JPanel(flow2);
		JPanel three2 = new JPanel(flow2);
		JPanel four2 = new JPanel(flow2);

		one2.add(petName);
		one2.add(petNameField);

		two2.add(password);
		two2.add(passwordField);

		three2.add(serverIP);
		three2.add(serverIPField);
		four2.add(login);
		four2.add(exit);

		networkPanel = new JPanel(new GridLayout(4, 2));
		networkPanel.add(one2);
		networkPanel.add(two2);
		networkPanel.add(three2);
		networkPanel.add(four2);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("单机修行", ChessUtil.getImageIcon("hongshuai.gif"),
				localPanel, "单机修行，逐步提高");
		tabbedPane.addTab("联网对战", ChessUtil.getImageIcon("heijiang.gif"),
				networkPanel, "联网对战，切磋棋艺");
		add(tabbedPane);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == load) {
			ChessLoadingGUI test = new ChessLoadingGUI();
			test.setVisible(true);
		} else if (source == loadAs) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					EXTENSION_NAME2, EXTENSION_NAME2);
			fileChooser.setFileFilter(filter);

			int state = fileChooser.showOpenDialog(this);
			File openFile = fileChooser.getSelectedFile();
			if (openFile != null && state == JFileChooser.APPROVE_OPTION) {
				// 读取当前目录下的棋谱文件

				GameRecord gameRecord = ManualUtil.readManual(openFile);

				ChessDemoGUI demo = new ChessDemoGUI(gameRecord);

				demo.setVisible(true);
				// dispose();

			}
		} else if (source == fen) {

		} else if (source == manMachine) {
			ManMachineGUI ai = new ManMachineGUI();
			ai.setVisible(true);
		} else if (source == partialManual) {
			PrintPartGUI partialManual = new PrintPartGUI();
			partialManual.setVisible(true);

		} else if (source == wholeManual) {
			PrintAllGUI wholeManual = new PrintAllGUI();
			wholeManual.setVisible(true);
		} else if (source == login) {
			check();
		} else if (source == exit) {
			dispose();
		} else if (source == maze) {
			HorseMazeGUI maze = new HorseMazeGUI();
			maze.setVisible(true);
		} else if (source == eightEmpress) {
			EmpressGUI empress = new EmpressGUI();
			empress.setVisible(true);
		} else if (source == fen) {

		}
	}

	/**
	 * 验证是否能连接到服务器，如果成功，则显示主界面，否则提示有错误
	 * 
	 */
	private void check() {
		String name = petNameField.getText();
		String host = serverIPField.getText();
		RoomGUI room = new RoomGUI(name);
		System.out.println(host);
		boolean flag = false;

		if (host.equals("")) {
			flag = false;
		} else {
			flag = room.connectToServer(host);
			System.out.println(flag);
		}

		if (flag) {
			this.dispose();
			room.setLocationRelativeTo(null);
			room.setVisible(true);
			room.work();

		} else {
			JOptionPane.showMessageDialog(null, "连接服务器失败");
		}
	}

	/**
	 * 客户端应用程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

	protected static void createAndShowGUI() {
		/* 首先设置GUI外观样式：本地系统样式 */
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ChessGUI chineseChess = new ChessGUI();

		chineseChess.setVisible(true);

	}
}
