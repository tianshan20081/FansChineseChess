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
package cn.fansunion.chinesechess.print.all;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import cn.fansunion.chinesechess.ChessGUI;
import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.core.ChessManual;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessRule;
import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.core.MoveStep;
import cn.fansunion.chinesechess.core.PieceUtil;
import cn.fansunion.chinesechess.save.GameRecord;
import cn.fansunion.chinesechess.save.ISaveManual;
import cn.fansunion.chinesechess.save.SaveAsDialog;
import cn.fansunion.chinesechess.save.SaveDialog;
import cn.fansunion.chinesechess.save.GameRecord.ManualType;

/**
 * 全局打谱主界面
 * 
 * @author 雷文 2010/11/26
 * @since 2.0
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 */
public class PrintAllGUI extends JFrame implements ActionListener, ISaveManual,
		NAME {

	private static final long serialVersionUID = 266L;

	private JMenu fileMenu, manualMenu, settingMenu, helpMenu;

	private JMenuItem newManual, saveManual, saveManualAs, exitGame,
			openMainGUI;

	private JMenuItem reprintManual, inputManual, undoManual;

	private JMenuItem setting, helpContent, aboutGame, welcome;

	private JCheckBoxMenuItem bgSound;

	private JButton prev, next, auto, first, last;

	private JButton newButton, inputManualButton, reprint, save, saveAs, set,
			undo;

	private JButton openMainGUIButton;

	private ArrayList<ManualItem> records;

	private PrintAllBoard board;// 棋盘

	private ChessManual chessManual;// 棋谱

	private JPanel gameStatusPanel;// 游戏状态面板

	private JPanel manualTools;//

	// 游戏状态
	private JLabel gameStatusContent, gameStatusIcon;

	private JPanel toolBar = new JPanel();

	/**
	 * 当前的索引
	 */
	public int curIndex = -1;

	private JScrollPane manualScroll;

	private int time = 1000;

	private JList manual;

	private Vector descs;

	// 按钮共用的手形图标
	private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * 构造函数
	 */
	public PrintAllGUI() {

		board = new PrintAllBoard(this);
		board.initChess(true);

		chessManual = board.chessManual;
		manualScroll = chessManual.manualScroll;
		manual = chessManual.manualList;
		descs = chessManual.descs;
		records = chessManual.getManualItems();

		initMenuBar();
		initButtons();
		initPanels();
		handleKeyEvent();

		setSize(670, 660);
		setTitle("楚汉棋兵__全局打谱 --雷文-http://FansUnion.cn");
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
	 * 初始化菜单
	 * 
	 */
	private void initMenuBar() {
		// 构造菜单
		JMenuBar bar = new JMenuBar();

		// 构造文件菜单
		fileMenu = new JMenu("文件(G)");

		newManual = new JMenuItem("新建", ChessUtil.getImageIcon("newManual.gif"));
		saveManual = new JMenuItem("保存", ChessUtil.getImageIcon("save.gif"));
		saveManualAs = new JMenuItem("另存", ChessUtil.getImageIcon("saveas.gif"));
		exitGame = new JMenuItem("退出", ChessUtil.getImageIcon("exit.gif"));
		openMainGUI = new JMenuItem("主界面",
				ChessUtil.getImageIcon("mainGUI.gif"));
		// 向文件菜单中增加菜单项
		fileMenu.add(newManual);
		fileMenu.add(saveManual);
		fileMenu.add(saveManualAs);
		fileMenu.add(exitGame);
		fileMenu.add(openMainGUI);

		// 构造棋谱菜单
		manualMenu = new JMenu("棋谱(M)");
		reprintManual = new JMenuItem("重新打谱",
				ChessUtil.getImageIcon("reprint.gif"));
		inputManual = new JMenuItem("输入棋谱",
				ChessUtil.getImageIcon("inputManual.gif"));
		undoManual = new JMenuItem("悔棋", ChessUtil.getImageIcon("undo.gif"));
		// 向棋谱菜单中添加菜单项
		manualMenu.add(reprintManual);
		manualMenu.add(inputManual);
		manualMenu.add(undoManual);

		// 构造帮助菜单
		helpMenu = new JMenu("帮助(H)");
		welcome = new JMenuItem("欢迎", ChessUtil.getImageIcon("welcome.gif"));
		helpContent = new JMenuItem("帮助内容", ChessUtil.getImageIcon("help.gif"));
		aboutGame = new JMenuItem("关于楚汉棋兵", ChessUtil.getImageIcon("info.gif"));

		helpMenu.add(welcome);
		helpMenu.add(helpContent);
		helpMenu.add(aboutGame);

		// 构造设置菜单
		settingMenu = new JMenu("设置(S)");

		setting = new JMenuItem("常用设置");
		bgSound = new JCheckBoxMenuItem("背景音乐");
		bgSound.setSelected(true);

		settingMenu.add(bgSound);
		settingMenu.add(setting);

		// 向菜单条中添加菜单
		bar.add(fileMenu);
		bar.add(manualMenu);
		bar.add(settingMenu);
		bar.add(helpMenu);

		setJMenuBar(bar);

		// 设置快捷键
		newManual.setAccelerator(KeyStroke.getKeyStroke('N',
				InputEvent.CTRL_DOWN_MASK));
		saveManual.setAccelerator(KeyStroke.getKeyStroke('S',
				InputEvent.CTRL_DOWN_MASK));
		saveManualAs.setAccelerator(KeyStroke.getKeyStroke('U',
				InputEvent.CTRL_DOWN_MASK));

		exitGame.setAccelerator(KeyStroke.getKeyStroke('E',
				InputEvent.CTRL_DOWN_MASK));
		openMainGUI.setAccelerator(KeyStroke.getKeyStroke('M',
				InputEvent.CTRL_DOWN_MASK));

		reprintManual.setAccelerator(KeyStroke.getKeyStroke('P',
				InputEvent.CTRL_DOWN_MASK));
		inputManual.setAccelerator(KeyStroke.getKeyStroke('I',
				InputEvent.CTRL_DOWN_MASK));
		undoManual.setAccelerator(KeyStroke.getKeyStroke('U',
				InputEvent.CTRL_DOWN_MASK));

		welcome.setAccelerator(KeyStroke.getKeyStroke('W',
				InputEvent.CTRL_DOWN_MASK));
		helpContent.setAccelerator(KeyStroke.getKeyStroke('H',
				InputEvent.CTRL_DOWN_MASK));
		aboutGame.setAccelerator(KeyStroke.getKeyStroke('A',
				InputEvent.CTRL_DOWN_MASK));

		bgSound.setAccelerator(KeyStroke.getKeyStroke('B',
				InputEvent.CTRL_DOWN_MASK));
		setting.setAccelerator(KeyStroke.getKeyStroke('T',
				InputEvent.CTRL_DOWN_MASK));

		// 设置助记符
		fileMenu.setMnemonic(KeyEvent.VK_G);
		manualMenu.setMnemonic(KeyEvent.VK_M);
		settingMenu.setMnemonic(KeyEvent.VK_S);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		// 注册监听器
		newManual.addActionListener(this);
		saveManual.addActionListener(this);
		saveManualAs.addActionListener(this);
		exitGame.addActionListener(this);
		openMainGUI.addActionListener(this);

		inputManual.addActionListener(this);
		reprintManual.addActionListener(this);
		undoManual.addActionListener(this);

		setting.addActionListener(this);
		bgSound.addActionListener(this);

		welcome.addActionListener(this);
		helpContent.addActionListener(this);
		aboutGame.addActionListener(this);

		setJMenuBar(bar);
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

		TitledBorder recordsBorder = new TitledBorder(
				PropertyReader.get("CHESS_MESSAGE_TOOLTIP"));
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
		rightPanel.add(BorderLayout.CENTER, recordsPanel);

		JSplitPane splitH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board,
				rightPanel);
		splitH.setDividerSize(5);
		splitH.setDividerLocation(450);

		// 游戏状态面板
		gameStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		gameStatusPanel.setPreferredSize(new Dimension(660, 80));
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

		initToolBar();

		add(BorderLayout.NORTH, toolBar);
		add(BorderLayout.CENTER, splitH);
		add(BorderLayout.SOUTH, gameStatusPanel);

	}

	/**
	 * 初始化工具条
	 * 
	 */
	private void initToolBar() {
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		toolBar.setLayout(flowLayout);

		toolBar.add(newButton);
		toolBar.add(save);
		toolBar.add(saveAs);

		toolBar.add(reprint);
		toolBar.add(undo);
		toolBar.add(inputManualButton);

		toolBar.add(set);
		toolBar.add(openMainGUIButton);

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

		reprint = new JButton(ChessUtil.getImageIcon("reprint.gif"));
		reprint.setToolTipText("重新打谱");
		reprint.addActionListener(this);
		// reprint.setPreferredSize(dimension);
		reprint.setCursor(handCursor);
		reprint.setMargin(insets);

		save = new JButton(ChessUtil.getImageIcon("save.gif"));
		save.addActionListener(this);
		save.setToolTipText("保存棋谱");
		// save.setPreferredSize(dimension);
		save.setCursor(handCursor);
		save.setMargin(insets);
		save.setOpaque(true);

		saveAs = new JButton(ChessUtil.getImageIcon("saveas.gif"));
		saveAs.addActionListener(this);
		saveAs.setToolTipText("另存棋谱");
		// saveAs.setPreferredSize(dimension);
		saveAs.setCursor(handCursor);
		saveAs.setMargin(insets);

		undo = new JButton(ChessUtil.getImageIcon("undo.gif"));
		undo.addActionListener(this);
		undo.setToolTipText("悔棋");
		undo.setCursor(handCursor);
		undo.setMargin(insets);

		set = new JButton(ChessUtil.getImageIcon("welcome.gif"));
		set.setToolTipText("设置");
		set.addActionListener(this);
		set.setCursor(handCursor);
		set.setMargin(insets);

		newButton = new JButton(ChessUtil.getImageIcon("newManual.gif"));
		newButton.setToolTipText("新建棋谱");
		newButton.addActionListener(this);
		newButton.setCursor(handCursor);
		newButton.setMargin(insets);

		inputManualButton = new JButton(
				ChessUtil.getImageIcon("inputManual.gif"));
		inputManualButton.setToolTipText("输入棋谱");
		inputManualButton.addActionListener(this);
		inputManualButton.setCursor(handCursor);
		inputManualButton.setMargin(insets);

		openMainGUIButton = new JButton(ChessUtil.getImageIcon("mainGUI.gif"));
		openMainGUIButton.setToolTipText("打开主界面");
		openMainGUIButton.addActionListener(this);
		openMainGUIButton.setCursor(handCursor);
		openMainGUIButton.setMargin(insets);

	}

	/**
	 * 响应事件
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// 下一步
		if (source == next) {
			next();
		} else if (source == prev) {
			prev();
		} else if (source == first) {
			first();
		} else if (source == last) {
			last();
		} else if (source == auto) {
			auto();
		} else if (source == helpContent) {
			ChessUtil.showHelpDialog();
		} else if (source == welcome) {
			ChessUtil.showWelcomeDialog();
		} else if (source == aboutGame) {
			ChessUtil.showAboutDialog();
		} else if (source == set || source == setting) {

		}

		// 重新打谱
		else if (source == reprint || source == reprintManual) {
			int result = JOptionPane.showConfirmDialog(this, "您需要保存当前的棋谱吗？",
					"您需要保存当前的棋谱吗？", JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				SaveDialog dialog = new SaveDialog(this);
				dialog.setVisible(true);
			}
			int size = chessManual.getManualItems().size();
			for (int index = 0; index < size; index++) {
				undo();
			}
		}

		// 悔棋
		else if (source == undo || source == undoManual) {
			last();
			undo();

		} else if (source == save || source == saveManual) {
			SaveDialog dialog = new SaveDialog(this);
			dialog.setVisible(true);

		} else if (source == saveAs || source == saveManualAs) {
			SaveAsDialog dialog = new SaveAsDialog(this);
			dialog.setVisible(true);
		} else if (source == exitGame) {
			handleExitGame();

		} else if (source == inputManualButton || source == inputManual) {
			String manual = JOptionPane.showInputDialog(this, "请输入棋谱", "请输入棋谱",
					JOptionPane.OK_CANCEL_OPTION);
			if (manual != null) {
				manual = manual.trim();// 去掉前导空白和尾部空白
			}
			movePieceByManual(manual);

		} else if (source == newButton || source == newManual) {
			PrintAllGUI newPrint = new PrintAllGUI();
			newPrint.setVisible(true);
		} else if (source == openMainGUI || source == openMainGUIButton) {
			ChessGUI mainGUI = new ChessGUI();
			mainGUI.setVisible(true);
		}

	}

	/**
	 * 后退一步
	 */
	private void undo() {
		boolean flag = chessManual.removeManualItem();
		if (flag) {
			if (board.getPlayerName().equals(RED_NAME)) {
				board.changeSide();
				updateGameStatus(2, "轮到黑方走喽！");
			} else {
				board.changeSide();
				updateGameStatus(1, "轮到红方走喽！");
			}
			curIndex--;
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
				board.movePiece(movePiece, startX, startY, endX, endY);

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
				// 打谱类当前索引+1
				curIndex++;

				// 播放棋子被吃声音
				ChessUtil.playSound("eat.wav");

				validate();
				repaint();

				// 更换己方名字，实现一人轮流走红黑2方的棋子
				board.changeSide();

			}
		}
	}

	private void auto() {
		DemoThread auto = new DemoThread();
		auto.start();
	}

	/**
	 * 移动到第一步
	 * 
	 */
	private void first() {
		while (curIndex != -1) {
			prev();
		}
	}

	/**
	 * 演示index指定的一步
	 * 
	 * @param index
	 */
	private void step(int index) {
		if (index < 0) {
			return;
		}

		ManualItem moveRecord = records.get(index);
		MoveStep step = moveRecord.getMoveStep();
		Point pStart = step.start;
		Point pEnd = step.end;
		int startI = pStart.x;
		int startJ = pStart.y;
		int endI = pEnd.x;
		int endJ = pEnd.y;

		ChessPiece piece = board.chessPoints[startI][startJ].getPiece();
		board.movePiece(piece, startI, startJ, endI, endJ);

		// 演示时不能移动棋子的
		repaint();
		validate();
	}

	/**
	 * 上一步
	 * 
	 */

	private void prev() {
		if (curIndex == -1) {
			return;
		}

		int size = records.size();

		// 更新棋盘界面
		ManualItem record = new ManualItem();
		MoveStep moveStep;

		ChessPiece eatedPiece;
		int startI = 0;
		int startJ = 0;
		int endI = 0;
		int endJ = 0;

		if (size > 0 && curIndex < size && curIndex >= 0) {
			// 获得指定的元素
			record = records.get(curIndex);
			eatedPiece = PieceUtil.createPiece(record.getEatedPieceId());

			moveStep = record.getMoveStep();
			startI = moveStep.start.x;
			startJ = moveStep.start.y;
			endI = moveStep.end.x;
			endJ = moveStep.end.y;

			// 上一步没有吃棋子
			if (eatedPiece == null) {
				System.out.println("没吃棋子");

				ChessPiece piece = board.chessPoints[endI][endJ].getPiece();
				board.chessPoints[startI][startJ].setPiece(piece, board);
				board.chessPoints[endI][endJ].setHasPiece(false);

			}
			// 上一步吃了棋子
			else {
				ChessPiece piece = board.chessPoints[endI][endJ].getPiece();
				board.chessPoints[startI][startJ].setPiece(piece, board);
				board.chessPoints[endI][endJ].setPiece(eatedPiece, board);

			}
		}

		// 后退时，画标记与正常移动时不同
		if (curIndex >= 1) {
			record = (ManualItem) records.get(curIndex - 1);
			moveStep = record.getMoveStep();
			startI = moveStep.start.x;
			startJ = moveStep.start.y;
			endI = moveStep.end.x;
			endJ = moveStep.end.y;
		}

		// 如果移动，应该画2条提示框
		board.setMoveFlag(true);
		board.movePoints[0] = new Point(endI, endJ);
		board.movePoints[1] = new Point(startI, startJ);

		curIndex--;
		scrollToView();

		repaint();
		validate();
	}

	/**
	 * 把棋谱列表滚动到当前行
	 */
	private void scrollToView() {
		if (curIndex >= 0 && curIndex < descs.size()) {
			// 选中当前行，提示玩家
			System.out.println("应该选中第N行：" + curIndex);
			manual.setSelectedIndex(curIndex);

			int lastIndex = curIndex;
			Rectangle rect = manual.getCellBounds(lastIndex, lastIndex);
			manualScroll.getViewport().scrollRectToVisible(rect);

		}
		if (curIndex == -1) {
			// 退回到没有任何棋子移动的状态时，不用画移动标记
			board.setMoveFlag(false);
			/*
			 * 当curIndex == -1时，应该不选中任何一样,
			 * 而manual.setSelectedIndex(-1)不能时列表框不选中任何一行
			 * manual.setListData(descs);可以恢复到默认状态不选中任何一行
			 */
			System.out.println("没有选中任何一行：" + curIndex);
			manual.setListData(descs);
		}
	}

	/**
	 * 响应键盘事件
	 * 
	 */
	private void handleKeyEvent() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				if (((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED) {
					int code = ((KeyEvent) event).getKeyCode();
					if (code == KeyEvent.VK_F1) {
						ChessUtil.showHelpDialog();
					} else if (code == KeyEvent.VK_DOWN) {
						next();
						System.out.println("next");
					} else if (code == KeyEvent.VK_UP) {
						prev();
						System.out.println("prev");
					} else if (code == KeyEvent.VK_HOME) {
						first();
						System.out.println("first");
					} else if (code == KeyEvent.VK_END) {
						last();
						System.out.println("last");
					} else if (code == KeyEvent.VK_ENTER) {
						auto();
					}

				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

	}

	/**
	 * 处理关闭事件
	 * 
	 */
	private void handleExitGame() {
		int result = JOptionPane.showConfirmDialog(this, "您确定要退出么？", "确认退出",
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			if (board.getWinkThread() != null) {
				board.getWinkThread().interrupt();
				System.out.println("关闭中...");
			}
			dispose();
		}
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

	private class DemoThread extends Thread {

		public DemoThread() {
			System.out.println("刚刚写的新线程构造完成了");
		}

		public void run() {
			System.out.println("刚刚写的新线程运行了");
			int size = records.size();

			while (curIndex < size - 1) {
				try {
					Thread.sleep(time);

					next();
					if (curIndex >= size - 1) {
						this.join(100);
						break;
					}
				} catch (InterruptedException ie) {
					break;
				}
			}
		}

	}

	/**
	 * 移动到最后一步
	 * 
	 */
	public void last() {
		while (curIndex != descs.size() - 1) {
			next();
		}
	}

	/**
	 * 演示当前步的下一步
	 * 
	 */
	public void next() {
		if (curIndex == descs.size() - 1) {
			return;
		}
		curIndex++;
		if (curIndex < records.size()) {
			step(curIndex);
		}
		scrollToView();
	}

	/**
	 * 全局打谱测试程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PrintAllGUI printManual = new PrintAllGUI();
		printManual.setVisible(true);

		/*
		 * String arrayToFenString = FenUtil.arrayToFenString(
		 * printManual.board.chessPoints, printManual.board .getPlayerName());
		 * System.out.println(arrayToFenString); ChessBoard board =
		 * FenUtil.fenStringToArray(arrayToFenString);
		 * PieceUtil.printPieceLoations(board.chessPoints);
		 */
	}

	/**
	 * （非 Javadoc）
	 * 
	 * @see com.fans.chess.common.ISaveManual#getSavePaths()
	 */
	@Override
	public ArrayList<String> getSavePaths() {
		ArrayList<String> paths = new ArrayList<String>();
		String path = "manuals/whole/";
		String path2 = "manuals/whole/";

		paths.add(path);
		paths.add(path2);
		return paths;
	}

	@Override
	public GameRecord getGameRecord() {
		GameRecord gameRecord = new GameRecord(ManualType.PRINT_WHOLE,
				ChessUtil.getDateAndTime(), "",
				board.chessManual.getManualItems(), board.chessManual.descs,
				null);
		return gameRecord;
	}

}
