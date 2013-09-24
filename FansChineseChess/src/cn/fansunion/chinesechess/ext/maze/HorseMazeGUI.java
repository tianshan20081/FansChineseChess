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
package cn.fansunion.chinesechess.ext.maze;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.core.ChessManual;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessRule;
import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.core.MoveStep;


/**
 * 中国象棋中馬的迷宫求解主界面
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class HorseMazeGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 266L;

	private JButton prev, next, auto, first, last;

	private JButton reprint, save, maze, set, exit, help, undo;

	private JButton manualBox;

	private JTextField startToEnd = new JTextField(9);

	private ArrayList<ManualItem> records;

	private HorseMazeBoard board;// 棋盘

	private ChessManual chessManual;// 棋谱

	private JPanel gameStatusPanel;// 游戏状态面板

	private JPanel manualTools;//

	public String tips;

	public MyThread mt = new MyThread();

	// 游戏状态
	private JLabel gameStatusContent;

	private JLabel gameStatusIcon;

	private JScrollPane manualScroll;

	private JList manual;

	private Vector descs;

	// 按钮共用的手形图标
	private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * 构造函数
	 */
	public HorseMazeGUI() {

		board = new HorseMazeBoard();
		board.initChess(true);
		board.horseMazePath = this;

		chessManual = board.chessManual;
		manualScroll = chessManual.manualScroll;
		manual = chessManual.manualList;
		descs = chessManual.descs;
		records = chessManual.getManualItems();

		initButtons();
		initPanels();

		setSize(660, 640);
		setTitle("楚汉棋兵__扩展练习__迷宫求解 --雷文-http://FansUnion.cn");
		setResizable(false);
		setIconImage(ChessUtil.getAppIcon());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			// 响应默认的退出事件
			public void windowClosing(WindowEvent e) {
				handleExitGame();
			}

		});
	}

	/**
	 * 初始化面板
	 * 
	 */
	private void initPanels() {
		// 构造右边的面板
		JPanel rightPanel = new JPanel(new BorderLayout());

		// 棋谱描述面板
		JPanel recordsPanel = new JPanel(new BorderLayout());

		TitledBorder recordsBorder = new TitledBorder(PropertyReader
				.get("CHESS_MESSAGE_TOOLTIP"));
		recordsPanel.setBorder(recordsBorder);
		recordsPanel.setPreferredSize(new Dimension(240, 330));
		recordsPanel.add(BorderLayout.CENTER, chessManual);

		// 工具栏
		manualTools = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// manualTools.setPreferredSize(new Dimension(220, 20));
		manualTools.add(first);
		manualTools.add(prev);
		manualTools.add(auto);
		manualTools.add(next);
		manualTools.add(last);

		recordsPanel.add(BorderLayout.SOUTH, manualTools);

		JPanel controlPanel = new JPanel(new GridLayout(3, 1));

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		JPanel one = new JPanel(layout);
		JPanel two = new JPanel(layout);
		JPanel three = new JPanel(layout);

		one.add(startToEnd);
		one.add(maze);

		two.add(reprint);
		two.add(undo);
		two.add(manualBox);

		three.add(help);
		three.add(set);
		three.add(exit);

		controlPanel.add(one);
		controlPanel.add(two);
		controlPanel.add(three);
		// controlPanel.add(four);
		rightPanel.add(BorderLayout.NORTH, recordsPanel);
		rightPanel.add(BorderLayout.CENTER, controlPanel);

		JSplitPane splitH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board,
				rightPanel);
		splitH.setDividerSize(5);
		splitH.setDividerLocation(450);
		add(splitH, BorderLayout.CENTER);

		// 游戏状态面板
		gameStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		gameStatusPanel.setPreferredSize(new Dimension(660, 120));
		// gameStatusPanel.setBackground(new Color(122, 146, 170));
		gameStatusIcon = new JLabel(ChessUtil.getImageIcon("hongshuai.png"));
		// 游戏状态栏
		gameStatusContent = new JLabel("红方先走棋");
		gameStatusContent.setFont(new Font("宋体", Font.PLAIN, 16));

		TitledBorder gameStatusBorder = new TitledBorder("游戏状态");
		gameStatusBorder.setTitleColor(Color.RED);
		gameStatusBorder.setTitleFont(new Font("宋体", Font.PLAIN, 16));
		gameStatusPanel.setToolTipText("游戏状态");
		gameStatusPanel.setBorder(gameStatusBorder);

		gameStatusPanel.add(gameStatusIcon);
		gameStatusPanel.add(gameStatusContent);

		add(BorderLayout.SOUTH, gameStatusPanel);

	}

	/**
	 * 初始化按钮
	 * 
	 */
	private void initButtons() {
		Dimension iconSize = new Dimension(16, 16);
		prev = new JButton(ChessUtil.getImageIcon("prev.gif"));
		prev.addActionListener(this);
		prev.setToolTipText("上一步");
		prev.setCursor(new Cursor(Cursor.HAND_CURSOR));
		prev.setPreferredSize(iconSize);

		next = new JButton(ChessUtil.getImageIcon("next.gif"));
		next.addActionListener(this);
		next.setToolTipText("下一步");
		next.setCursor(new Cursor(Cursor.HAND_CURSOR));
		next.setPreferredSize(iconSize);

		first = new JButton(ChessUtil.getImageIcon("first.gif"));
		first.addActionListener(this);
		first.setToolTipText("第一步");
		first.setCursor(new Cursor(Cursor.HAND_CURSOR));
		first.setPreferredSize(iconSize);

		last = new JButton(ChessUtil.getImageIcon("last.gif"));
		last.addActionListener(this);
		last.setToolTipText("最后一步");
		last.setCursor(new Cursor(Cursor.HAND_CURSOR));
		last.setPreferredSize(iconSize);

		auto = new JButton(ChessUtil.getImageIcon("auto.gif"));
		auto.addActionListener(this);
		auto.setToolTipText("自动演示");
		auto.setPreferredSize(iconSize);
		auto.setCursor(new Cursor(Cursor.HAND_CURSOR));

		Insets insets = new Insets(1, 1, 1, 1);

		reprint = new JButton("重打", ChessUtil.getImageIcon("reprint.gif"));
		reprint.setToolTipText("重新打谱");
		reprint.addActionListener(this);
		// reprint.setPreferredSize(dimension);
		reprint.setCursor(handCursor);
		reprint.setMargin(insets);

		save = new JButton("保存", ChessUtil.getImageIcon("save.gif"));
		save.addActionListener(this);
		save.setToolTipText("保存棋谱");
		// save.setPreferredSize(dimension);
		save.setCursor(handCursor);
		save.setMargin(insets);

		maze = new JButton("迷宫求解", ChessUtil.getImageIcon("saveas.gif"));
		maze.addActionListener(this);
		maze.setToolTipText("迷宫求解");
		// saveAs.setPreferredSize(dimension);
		maze.setCursor(handCursor);
		maze.setMargin(insets);

		undo = new JButton("悔棋", ChessUtil.getImageIcon("undo.gif"));
		undo.addActionListener(this);
		undo.setToolTipText("悔棋");
		undo.setCursor(handCursor);
		undo.setMargin(insets);

		set = new JButton("设置", ChessUtil.getImageIcon("welcome.gif"));
		set.setToolTipText("设置");
		set.addActionListener(this);
		set.setCursor(handCursor);
		set.setMargin(insets);

		help = new JButton("帮助", ChessUtil.getImageIcon("help.gif"));
		help.setToolTipText("帮助");
		help.addActionListener(this);
		help.setCursor(handCursor);
		help.setMargin(insets);

		exit = new JButton("退出", ChessUtil.getImageIcon("stop.gif"));
		exit.setToolTipText("退出");
		exit.addActionListener(this);
		exit.setCursor(handCursor);
		exit.setMargin(insets);

		manualBox = new JButton("棋谱", ChessUtil.getImageIcon("stop.gif"));
		manualBox.setToolTipText("棋谱");
		manualBox.addActionListener(this);
		manualBox.setCursor(handCursor);
		manualBox.setMargin(insets);

	}

	/**
	 * 响应事件
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// 重新打谱
		if (source == reprint) {
			// 销毁旧的打谱窗口，然后新建一个打谱窗口
			dispose();
			HorseMazeGUI newPrint = new HorseMazeGUI();
			newPrint.setVisible(true);

		}

		if (source == maze) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			String movePath = startToEnd.getText();
			movePath = movePath.trim();
			String[] path = movePath.split(",");
			if (path.length != 4) {
				System.out.println("坐标点不正确！");
				return;
			}

			int[] startToEnd = new int[4];
			for (int i = 0; i < path.length; i++) {
				startToEnd[i] = Integer.valueOf(path[i]);
			}
			IntPoint start = new IntPoint(startToEnd[0], startToEnd[1]);
			IntPoint end = new IntPoint(startToEnd[2], startToEnd[3]);

			HorseMazeModel maze = new HorseMazeModel(board, start, end);
			new Thread(maze).start();

			mt.start();

		} else if (source == exit) {
			dispose();
		} else if (source == manualBox) {
			String manual = JOptionPane.showInputDialog(this, "请输入棋谱", "请输入棋谱",
					JOptionPane.OK_CANCEL_OPTION);
			if (manual != null) {
				manual = manual.trim();// 去掉前导空白和尾部空白
			}
			movePieceByManual(manual);

		}

	}

	/**
	 * 根据棋谱，如馬八进七，移动棋子
	 * 
	 * @param manual
	 */
	private void movePieceByManual(String manual) {
		if (manual != null && manual.length() == 4) {
			ChessPiece movePiece = board.getMovePiece(manual);
			if (movePiece == null) {
				return;
			}
			ChessPiece removedPiece = board.getRemovedPiece(manual);
			Point pStart = board.getStartPosition(manual);
			Point pEnd = board.getEndPosition(manual);

			int startX = (int) pStart.getX();
			int startY = (int) pStart.getY();
			int endX = (int) pEnd.getX();
			int endY = (int) pEnd.getY();
			System.out.println(movePiece.getCategory() + " :" + startX + startY
					+ endX + endY);
			boolean rule = ChessRule.allRule(movePiece, startX, startY, endX,
					endY, board.chessPoints);
			System.out.println("能否移动棋子：" + rule);
			if (rule) {
				// 一定要先删除棋子，再设置新的棋子的位置
				board.removePiece(removedPiece);
				board.chessPoints[endX][endY].setPiece(movePiece, board);
				(board.chessPoints[startX][startY]).setHasPiece(false);
				movePiece.setPosition(pEnd);

				board.setMoveFlag(true);
				board.movePoints[0] = new Point(endX, endY);
				board.movePoints[1] = new Point(startX, startY);
				board.clearTipPoints();

				/**
				 * 构造移动记录
				 */
				ManualItem record = new ManualItem();
				MoveStep moveStep = new MoveStep(new Point(startX, startY),
						new Point(endX, endY));
				record.setMoveStep(moveStep);
				if (removedPiece != null) {
					record.setEatedPieceId(removedPiece.getId());
				} else {
					record.setEatedPieceId(null);
				}
				record.setMovePieceId(movePiece.getId());
				board.chessManual.addManualItem(record);

				// 播放棋子被吃声音
				ChessUtil.playSound("eat.wav");

				validate();
				repaint();

				// 更换己方名字，实现一人轮流走红黑2方的棋子
				board.reverseName();

			}
		}
	}

	private void handleExitGame() {
		if (board.getWinkThread() != null) {
			board.getWinkThread().interrupt();
			System.out.println("关闭中！");
		}
		dispose();
	}

	/**
	 * 更新游戏状态
	 * 
	 * @param state
	 *            图标标识
	 * @param content
	 *            游戏状态文字描述
	 */
	public void updateGameStatus(int state, String content) {

		switch (state) {
		case 1:
			gameStatusIcon.setIcon(ChessUtil.getImageIcon("hongshuai.png"));
			gameStatusIcon.setToolTipText("轮到红方走喽！");
			break;
		case 2:
			gameStatusIcon.setIcon(ChessUtil.getImageIcon("heijiang.png"));
			gameStatusIcon.setToolTipText("轮到黑方走喽！");
			break;
		default:
			break;
		}

		if (content != null && !content.equals("")) {
			gameStatusContent.setText(content);
		}
	}

	public void showTips(String tips) {
		this.tips = tips;
	}

	/**
	 * 全局打谱测试程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		HorseMazeGUI maze = new HorseMazeGUI();
		maze.setVisible(true);
	}

	class MyThread extends Thread {
		public void run() {
			while (true) {
				if (gameStatusContent == null) {
					gameStatusContent = new JLabel();
				}
				if (tips != null && tips.equals("exit")) {
					System.out.println("exit...");
					break;
				}
				gameStatusContent.setText(tips);
				validate();
				repaint();
			}
		}
	}

}
