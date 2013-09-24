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
package cn.fansunion.chinesechess.ai;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.HelpDialog;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.core.ChessBoard;
import cn.fansunion.chinesechess.core.ChessManual;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessPoint;
import cn.fansunion.chinesechess.core.ChessRule;
import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.core.MoveStep;
import cn.fansunion.chinesechess.core.PieceUtil;
import cn.fansunion.chinesechess.core.ChessPiece.PieceId;
import cn.fansunion.chinesechess.save.GameRecord;
import cn.fansunion.chinesechess.save.ISaveManual;
import cn.fansunion.chinesechess.save.SaveAsDialog;
import cn.fansunion.chinesechess.save.SaveDialog;
import cn.fansunion.chinesechess.save.GameRecord.ManualType;


/**
 * 人机对弈图形用户界面
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class ManMachineGUI extends JFrame implements ActionListener,
		NAME, ISaveManual {

	private static final long serialVersionUID = 266L;

	private static final int MAX_VALUE = 3000;

	private int maxDepth;
	
	private ChessPoint[][] globalChessPoints;

	private ChessBoard globalBoard;

	private ManualItem bestChessMove;

	private JButton reprint, save, saveAs, set, exit, help, undo;

	private JButton manualBox;

	// private ArrayList<ChessMove> records;

	private ManMachineBoard board;// 棋盘

	private ChessManual chessManual;// 棋谱

	private ChessPoint[][] chessPoints;

	private JPanel gameStatusPanel;// 游戏状态面板

	// 走法栈
	private Stack<ManualItem> moveStack = new Stack<ManualItem>();

	// 游戏状态
	private JLabel gameStatusContent, gameStatusIcon;

	/**
	 * 当前的索引
	 */
	public int curIndex = -1;

	private JScrollPane manualScroll;

	private JList manual;

	private Vector descs;

	// 按钮共用的手形图标
	private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * 构造函数
	 */
	public ManMachineGUI() {

		board = new ManMachineBoard();
		board.initChess(true);
		board.boardOwner = this;

		chessManual = board.chessManual;
		manualScroll = chessManual.manualScroll;
		manual = chessManual.manualList;
		chessPoints = board.chessPoints;
		descs = chessManual.descs;
		// records = chessManual.getChessRecords();

		initButtons();
		initPanels();
		handleKeyEvent();

		setSize(660, 600);
		setTitle("楚汉棋兵-人机对弈 -雷文-http://FansUnion.cn");
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

		TitledBorder recordsBorder = new TitledBorder(PropertyReader.get("CHESS_MESSAGE_TOOLTIP"));
		recordsPanel.setBorder(recordsBorder);
		recordsPanel.setPreferredSize(new Dimension(240, 330));
		recordsPanel.add(BorderLayout.CENTER, chessManual);

		/*
		 * // 工具栏 manualTools = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
		 * manualTools.setPreferredSize(new Dimension(220, 20));
		 * manualTools.add(first); manualTools.add(prev); manualTools.add(auto);
		 * manualTools.add(next); manualTools.add(last);
		 * 
		 * recordsPanel.add(BorderLayout.SOUTH, manualTools);
		 */
		JPanel controlPanel = new JPanel(new GridLayout(3, 1));

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		JPanel one = new JPanel(layout);
		JPanel two = new JPanel(layout);
		JPanel three = new JPanel(layout);

		one.add(save);
		one.add(saveAs);

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

		add(BorderLayout.SOUTH, gameStatusPanel);

	}

	/**
	 * 初始化按钮
	 * 
	 */
	private void initButtons() {

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

		saveAs = new JButton("另存", ChessUtil.getImageIcon("saveas.gif"));
		saveAs.addActionListener(this);
		saveAs.setToolTipText("另存棋谱");
		// saveAs.setPreferredSize(dimension);
		saveAs.setCursor(handCursor);
		saveAs.setMargin(insets);

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

		// 下一步
		if (source == help) {
			HelpDialog help = new HelpDialog();
			help.setVisible(true);
		} else if (source == set) {

		}

		// 重新打谱
		else if (source == reprint) {
			int result = JOptionPane.showConfirmDialog(this, "您需要保存当前的棋谱吗？",
					"您需要保存当前的棋谱吗？", JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				SaveDialog dialog = new SaveDialog(this);
				dialog.setVisible(true);
			}

			// 销毁旧的打谱窗口，然后新建一个打谱窗口
			dispose();
			ManMachineGUI newPrint = new ManMachineGUI();
			newPrint.setVisible(true);

		}

		// 悔棋
		else if (source == undo) {

			boolean flag = chessManual.removeManualItem();
			if (flag) {
				if (board.getPlayerName().equals(RED_NAME)) {
					board.changeSide();
					updateGameStatus(2, "轮到黑方走喽！");
				} else {
					board.changeSide();
					updateGameStatus(1, "轮到红方走喽！");
				}
			}
			curIndex--;
		} else if (source == save) {
			SaveDialog dialog = new SaveDialog(this);
			dialog.setVisible(true);

		} else if (source == saveAs) {
			SaveAsDialog dialog = new SaveAsDialog(this);
			dialog.setVisible(true);
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

	/**
	 * 把棋谱列表滚动到当前行
	 */
	public void scrollToView() {
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
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

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

	/**
	 * 极大极小搜索算法
	 * 
	 * @param depth
	 *            深度搜索的深度
	 */
	private void minMaxSearch2(int depth) {
		if (board.getPlayerName().equals(RED_NAME)) {
			maxSearch(depth);
		} else {
			minSearch(depth);
		}
	}

	/**
	 * 极大点搜索算法
	 * 
	 * @param depth
	 * @return
	 */
	private int maxSearch(int depth) {
		int best = -MAX_VALUE;
		int value = 0;
		if (depth == 0) {
			return AIUtil.evaluate(board.getPlayerName(), globalChessPoints);
		}

		ArrayList<ManualItem> chessMoves = AIUtil.generateAllChessMove(board
				.getPlayerName(), globalChessPoints);
		int size = chessMoves.size();

		for (int index = 0; index < size; index++) {
			ManualItem chessMove = chessMoves.get(index);
			makeMove(chessMove);
			value = minSearch(depth - 1);
			unMakeMove();
			if (value > best) {
				best = value;
				if (depth == maxDepth) {
					bestChessMove = chessMove;
					System.out.println("maxSearch找到了最佳走法！");
				}
			}
		}
		return best;

	}

	/**
	 * 极小点搜索算法
	 * 
	 * @param depth
	 * @return
	 */
	private int minSearch(int depth) {
		int best = MAX_VALUE;
		int value = 0;
		if (depth == 0) {
			return AIUtil.evaluate(board.getPlayerName(), globalChessPoints);
		}
		ArrayList<ManualItem> chessMoves = AIUtil.generateAllChessMove(board
				.getPlayerName(), globalChessPoints);
		int size = chessMoves.size();
		// System.out.println("当前可行走法：" + size);
		for (int index = 0; index < size; index++) {
			ManualItem chessMove = chessMoves.get(index);
			makeMove(chessMove);
			value = maxSearch(depth - 1);
			unMakeMove();
			if (value < best) {
				best = value;
				if (depth == maxDepth) {
					System.out.println("minSearch找到了最佳走法！");
					bestChessMove = chessMove;
				}
			}
		}
		return best;

	}

	private void makeMove(ManualItem chessMove) {
		moveStack.add(chessMove);
		MoveStep moveStep = chessMove.getMoveStep();
		int startX = (int) moveStep.getStart().getX();
		int startY = (int) moveStep.getStart().getY();
		int endX = (int) moveStep.getEnd().getX();
		int endY = (int) moveStep.getEnd().getY();

		ChessPiece piece = globalChessPoints[startX][startY].getPiece();
		ChessPiece eatedPiece = globalChessPoints[endX][endY].getPiece();
		globalChessPoints[endX][endY].removePiece(eatedPiece, globalBoard);
		globalChessPoints[endX][endY].setPiece(piece, globalBoard);
		globalChessPoints[startX][startY].setHasPiece(false);

		board.changeSide();
	}

	private void unMakeMove() {
		ManualItem chessMove = moveStack.pop();
		MoveStep moveStep = chessMove.getMoveStep();
		PieceId eatedPieceId = chessMove.getEatedPieceId();

		int startX = (int) moveStep.getStart().getX();
		int startY = (int) moveStep.getStart().getY();
		int endX = (int) moveStep.getEnd().getX();
		int endY = (int) moveStep.getEnd().getY();

		ChessPiece piece = globalChessPoints[endX][endY].getPiece();
		ChessPiece eatedPiece = PieceUtil.createPiece(eatedPieceId);

		globalChessPoints[startX][startY].setPiece(piece, globalBoard);
		globalChessPoints[endX][endY].setPiece(eatedPiece, globalBoard);

		board.changeSide();
	}

	/**
	 * 全局打谱测试程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// int[][][][] is = AIUtil.positionValues;

		ManMachineGUI ai = new ManMachineGUI();
		ai.setVisible(true);
		// ai.computerThink();
		// ai.fenTest();

		// PieceUtil.printPieceLoations(ai.globalChessPoints);
	}

	/**
	 * @param printManual
	 */
	private void fenTest() {
		String arrayToFenString = FenUtil.arrayToFenString(board.chessPoints,
				board.getPlayerName());
		System.out.println(arrayToFenString + "\n");
		globalBoard = FenUtil.fenStringToArray(arrayToFenString);
		globalChessPoints = globalBoard.chessPoints;
		PieceUtil.printPieceLoations(globalBoard.chessPoints);

		ArrayList<ManualItem> chessMoves = AIUtil.generateAllChessMove(RED_NAME,
				globalBoard.chessPoints);
		printChessMove(chessMoves);
	}

	private void test() {
		String arrayToFenString = FenUtil.arrayToFenString(board.chessPoints,
				board.getPlayerName());
		System.out.println(arrayToFenString + "\n");
		globalBoard = FenUtil.fenStringToArray(arrayToFenString);
		globalChessPoints = globalBoard.chessPoints;
		PieceUtil.printPieceLoations(globalChessPoints);

	}

	private void printChessMove(ArrayList<ManualItem> chessMoves) {
		int size = chessMoves.size();
		for (int index = 0; index < size; index++) {
			ManualItem chessMove = chessMoves.get(index);

			MoveStep moveStep = chessMove.getMoveStep();
			Point start = moveStep.getStart();
			Point end = moveStep.getEnd();
			System.out.println("第" + index + "个走法：(" + start.getX() + ","
					+ start.getY() + ")->" + end.getX() + "," + end.getY()
					+ ")");
		}

	}

	/**
	 * （非 Javadoc）
	 * 
	 * @see com.fans.chess.common.ISaveManual#getSavePaths()
	 */
	public ArrayList<String> getSavePaths() {
		ArrayList<String> paths = new ArrayList<String>();
		String path = "manuals/ai/";
		String path2 = "manuals/ai/";

		paths.add(path);
		paths.add(path2);
		return paths;
	}

	public GameRecord getGameRecord() {
		GameRecord gameRecord = new GameRecord(ManualType.MAN_MACHINE, ChessUtil
				.getDateAndTime(), "", board.chessManual.getManualItems(),
				board.chessManual.descs, null);
		return gameRecord;
	}

	public void computerThink() {
		test();

		// globalChessPoints = chessPoints.clone();
		/*
		 * System.out.println("globalChessPoints == chessPoints " +
		 * (globalChessPoints == chessPoints));
		 */
		bestChessMove = null;
		maxDepth = 4;
		// negalMaxSearch(MAX_DEPTH);
		alphaBetaSearch(maxDepth, -MAX_VALUE, MAX_VALUE);
		if (bestChessMove == null) {
			System.out.println("没有找到最佳走法！");
			return;
		}
		MoveStep step = bestChessMove.getMoveStep();
		int startX = (int) step.getStart().getX();
		int startY = (int) step.getStart().getY();
		int endX = (int) step.getEnd().getX();
		int endY = (int) step.getEnd().getY();
		System.out.println("(" + startX + "," + startY + "),(" + endX + ","
				+ endY + ")");
		ChessPiece movePiece = chessPoints[startX][startY].getPiece();

		board.addChessRecord(movePiece, startX, startY, endX, endY);
		board.movePiece(movePiece, startX, startY, endX, endY);
		board.changeSide();

	}

	/**
	 * 
	 * 
	 * @param depth
	 * @return
	 */
	private int alphaBetaSearch(int depth, int alpha, int beta) {
		// int best = 0;
		int value = 0;

		// best = -MAX_VALUE;

		if (depth == 0) {
			return AIUtil.evaluate2(board.getPlayerName(), globalChessPoints);
		}

		ArrayList<ManualItem> chessMoves = AIUtil.generateAllChessMove(board
				.getPlayerName(), globalChessPoints);
		printChessMove(chessMoves);
		int size = chessMoves.size();

		for (int index = 0; index < size; index++) {
			ManualItem chessMove = chessMoves.get(index);
			makeMove(chessMove);
			value = -alphaBetaSearch(depth - 1, -beta, -alpha);
			unMakeMove();

			if (value >= beta) {
				return beta;
			}
			if (value > alpha) {
				alpha = value;
				if (depth == maxDepth) {
					bestChessMove = chessMove;
					System.out.println("maxSearch找到了最佳走法！");
				}
			}

		}
		return alpha;

	}

	/**
	 * 负极大值搜索算法
	 * 
	 * @param depth
	 * @return
	 */
	private int negalMaxSearch(int depth) {
		int best = 0;
		int value = 0;

		best = -MAX_VALUE;

		if (depth == 0) {
			return AIUtil.evaluate(board.getPlayerName(), globalChessPoints);
		}

		ArrayList<ManualItem> chessMoves = AIUtil.generateAllChessMove(board
				.getPlayerName(), globalChessPoints);
		printChessMove(chessMoves);
		int size = chessMoves.size();

		for (int index = 0; index < size; index++) {
			ManualItem chessMove = chessMoves.get(index);
			makeMove(chessMove);
			value = -negalMaxSearch(depth - 1);
			unMakeMove();

			if (value > best) {
				best = value;
				if (depth == maxDepth) {
					bestChessMove = chessMove;
					System.out.println("maxSearch找到了最佳走法！");
				}
			}

		}
		return best;

	}

	/**
	 * 极大点搜索算法
	 * 
	 * @param depth
	 * @return
	 */
	private int minMaxSearch22(int depth) {
		int best = 0;
		int value = 0;
		if (board.getPlayerName().equals(RED_NAME)) {
			best = MAX_VALUE;
		} else {
			best = -MAX_VALUE;
		}

		if (depth == 0) {
			return AIUtil.evaluate(board.getPlayerName(), globalChessPoints);
		}

		ArrayList<ManualItem> chessMoves = AIUtil.generateAllChessMove(board
				.getPlayerName(), globalChessPoints);
		printChessMove(chessMoves);
		int size = chessMoves.size();

		for (int index = 0; index < size; index++) {
			ManualItem chessMove = chessMoves.get(index);
			makeMove(chessMove);
			value = minSearch(depth - 1);
			unMakeMove();
			if (board.getPlayerName().equals(RED_NAME)) {
				if (value < best) {
					best = value;
					if (depth == maxDepth) {
						bestChessMove = chessMove;
						System.out.println("maxSearch找到了最佳走法！");
					}
				}
			} else {
				if (value > best) {
					best = value;
					if (depth == maxDepth) {
						bestChessMove = chessMove;
						System.out.println("maxSearch找到了最佳走法！");
					}
				}
			}
		}
		return best;

	}

}
