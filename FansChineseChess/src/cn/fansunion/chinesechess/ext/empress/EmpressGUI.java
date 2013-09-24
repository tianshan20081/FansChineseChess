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
package cn.fansunion.chinesechess.ext.empress;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.PieceUtil;
import cn.fansunion.chinesechess.core.ChessPiece.PieceId;

/**
 * N皇后界面
 * 
 * @author 雷文 2010/11/26 leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class EmpressGUI extends JFrame implements ActionListener, Runnable {

	private static final long serialVersionUID = 266L;

	private JButton prev, next, auto, first, last;

	private JButton save, advancedSave, empress, set, exit, help, undo;

	private JTextField numField = new JTextField(5);

	private EmpressBoard board;// 棋盘

	private JPanel gameStatusPanel;// 游戏状态面板

	private JPanel manualTools;//

	public String tips;

	// 游戏状态
	private JTextArea gameStatusContent;

	private JScrollPane manualScroll;

	private JList manual = new JList();

	private Vector<String> descs = new Vector<String>();

	private ArrayList<ArrayList<Point>> lists = new ArrayList<ArrayList<Point>>();

	private ArrayList<int[][]> advancedLists = new ArrayList<int[][]>();

	// 按钮共用的手形图标
	private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

	private ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();

	private Thread autoThread = new Thread(this);

	private int curIndex = -1;

	private TitledBorder recordsBorder;

	/**
	 * 构造函数
	 */
	public EmpressGUI() {

		board = new EmpressBoard();
		// board.owner = this;

		initButtons();
		initPanels();

		// 最多能解决9皇后问题
		for (int index = 0; index < 9; index++) {
			pieces.add(PieceUtil.createPiece(PieceId.SHUAI));
		}

		setSize(660, 620);
		setTitle("楚汉棋兵__扩展练习__N皇后问题 --雷文-http://FansUnion.cn");
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

		recordsBorder = new TitledBorder("所有布局");
		recordsPanel.setBorder(recordsBorder);
		recordsPanel.setPreferredSize(new Dimension(240, 330));
		manualScroll = new JScrollPane(manual);
		recordsPanel.add(BorderLayout.CENTER, manualScroll);

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

		one.add(numField);
		one.add(empress);

		two.add(save);
		two.add(advancedSave);
		// two.add(undo);

		three.add(help);
		three.add(set);
		three.add(exit);

		controlPanel.add(one);
		controlPanel.add(two);
		controlPanel.add(three);

		rightPanel.add(BorderLayout.NORTH, recordsPanel);
		rightPanel.add(BorderLayout.CENTER, controlPanel);

		JSplitPane splitH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board,
				rightPanel);
		splitH.setDividerSize(5);
		splitH.setDividerLocation(450);
		add(splitH, BorderLayout.CENTER);

		// 游戏状态面板
		gameStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		gameStatusPanel.setPreferredSize(new Dimension(660, 90));

		// 游戏状态栏
		gameStatusContent = new JTextArea("  求N(1<=N<=9)皇后的所有合法布局:\n"
				+ "  3个约束条件：任何2个棋子都不能占居棋盘上的同一行、或者同一列、" + "或者同一对角线.");
		gameStatusContent.setFont(new Font("宋体", Font.PLAIN, 16));

		TitledBorder gameStatusBorder = new TitledBorder("游戏状态");
		gameStatusBorder.setTitleColor(Color.RED);
		gameStatusBorder.setTitleFont(new Font("宋体", Font.PLAIN, 16));
		gameStatusPanel.setToolTipText("算法说明");
		gameStatusPanel.setBorder(gameStatusBorder);
		gameStatusPanel.add(gameStatusContent);

		add(BorderLayout.SOUTH, gameStatusPanel);

		// 列表框响应单击和双击事件
		manual.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int count = e.getClickCount();
				// 单击事件
				if (count == 1 || count == 2) {
					int index = manual.getSelectedIndex();
					if (index != -1) {
						curIndex = index;
						displayLayout(curIndex);
					}
				}

			}

		});
	}

	/**
	 * 显示第index个布局
	 * 
	 * @param index
	 *            布局的索引
	 */
	private void displayLayout(int index) {
		if (index < 0 || index >= lists.size()) {
			return;
		}

		ArrayList<Point> arrayList = lists.get(index);
		int size2 = arrayList.size();
		for (int j = 0; j < size2; j++) {
			Point point = arrayList.get(j);
			ChessPiece piece = pieces.get(j);
			int x = (int) point.getX();
			int y = (int) point.getY();
			board.chessPoints[x][y].setPiece(piece, board);
			validate();
			repaint();
		}
	}

	/**
	 * 初始化按钮
	 * 
	 */
	private void initButtons() {
		Dimension iconSize = new Dimension(16, 16);
		prev = new JButton(ChessUtil.getImageIcon("prev.gif"));
		prev.addActionListener(this);
		prev.setToolTipText("上一个布局");
		prev.setCursor(new Cursor(Cursor.HAND_CURSOR));
		prev.setPreferredSize(iconSize);

		next = new JButton(ChessUtil.getImageIcon("next.gif"));
		next.addActionListener(this);
		next.setToolTipText("下一个布局");
		next.setCursor(new Cursor(Cursor.HAND_CURSOR));
		next.setPreferredSize(iconSize);

		first = new JButton(ChessUtil.getImageIcon("first.gif"));
		first.addActionListener(this);
		first.setToolTipText("第一个布局");
		first.setCursor(new Cursor(Cursor.HAND_CURSOR));
		first.setPreferredSize(iconSize);

		last = new JButton(ChessUtil.getImageIcon("last.gif"));
		last.addActionListener(this);
		last.setToolTipText("最后一个布局");
		last.setCursor(new Cursor(Cursor.HAND_CURSOR));
		last.setPreferredSize(iconSize);

		auto = new JButton(ChessUtil.getImageIcon("auto.gif"));
		auto.addActionListener(this);
		auto.setToolTipText("自动演示");
		auto.setPreferredSize(iconSize);
		auto.setCursor(new Cursor(Cursor.HAND_CURSOR));

		Insets insets = new Insets(1, 1, 1, 1);

		advancedSave = new JButton("高级保存",
				ChessUtil.getImageIcon("reprint.gif"));
		advancedSave.setToolTipText("高级保存");
		advancedSave.addActionListener(this);
		// reprint.setPreferredSize(dimension);
		advancedSave.setCursor(handCursor);
		advancedSave.setMargin(insets);

		save = new JButton("保存", ChessUtil.getImageIcon("save.gif"));
		save.addActionListener(this);
		save.setToolTipText("保存棋谱");
		// save.setPreferredSize(dimension);
		save.setCursor(handCursor);
		save.setMargin(insets);

		empress = new JButton("N皇后布局", ChessUtil.getImageIcon("saveas.gif"));
		empress.addActionListener(this);
		empress.setToolTipText("N皇后布局");
		// saveAs.setPreferredSize(dimension);
		empress.setCursor(handCursor);
		empress.setMargin(insets);

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

	}

	/**
	 * 响应事件
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == save) {
			int size = lists.size();
			if (size == 0) {
				JOptionPane.showMessageDialog(this, "没有布局可以保存！");
				return;
			}
			JFileChooser fileChooser = new JFileChooser();
			int state = fileChooser.showSaveDialog(null);
			File saveFile = fileChooser.getSelectedFile();
			if (saveFile != null && state == JFileChooser.APPROVE_OPTION) {
				System.out.println(saveFile.getAbsolutePath());
				// 保存
				EmpressUtil.save(saveFile.getAbsolutePath() + ".txt", lists);
			}

		} else if (source == advancedSave) {
			int size = advancedLists.size();
			if (size == 0) {
				JOptionPane.showMessageDialog(this, "没有布局可以保存！");
				return;
			}
			// 高级保存
			// EightEmpressUtil.advancedSave(advancedLists);
			JFileChooser fileChooser = new JFileChooser();
			int state = fileChooser.showSaveDialog(null);
			File saveFile = fileChooser.getSelectedFile();
			if (saveFile != null && state == JFileChooser.APPROVE_OPTION) {
				System.out.println(saveFile.getAbsolutePath());
				// 保存
				EmpressUtil.advancedSave(saveFile.getAbsolutePath() + ".txt",
						advancedLists);
			}

		} else if (source == empress) {
			String number = numField.getText();
			int n = 3;
			try {
				n = Integer.valueOf(number);
				if (n < 1 || n > 9) {
					JOptionPane
							.showMessageDialog(this, "抱歉，只能求解N皇后问题（1<=N<=9）");
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			EmpressModel ee = new EmpressModel(n);
			ee.initAllLayout();
			lists = ee.getLists();
			advancedLists = ee.getAdvancedLists();
			int size = lists.size();

			descs.clear();
			for (int i = 0; i < size; i++) {
				descs.add("第" + i + "个布局");
			}
			manual.setListData(descs);

		} else if (source == exit) {
			dispose();
		} else if (source == first) {
			first();
		} else if (source == next) {
			next();
		} else if (source == prev) {
			prev();
		} else if (source == last) {
			last();
		} else if (source == auto) {
			auto();
		}

	}

	/**
	 * 
	 */
	private void auto() {
		if (autoThread == null) {
			autoThread = new Thread(this);
		}
		autoThread.start();
	}

	/**
	 * 
	 */
	private void last() {
		curIndex = lists.size() - 1;
		displayLayout(curIndex);
		scrollToView();
	}

	/**
	 * 
	 */
	private void first() {
		curIndex = 0;
		displayLayout(curIndex);
		scrollToView();
	}

	private void prev() {
		if (curIndex >= 0) {
			displayLayout(curIndex--);
			scrollToView();
		}

	}

	private void next() {
		if (curIndex < lists.size()) {
			displayLayout(curIndex++);
			scrollToView();
		}
	}

	private void handleExitGame() {
		if (board.getWinkThread() != null) {
			board.getWinkThread().interrupt();
			System.out.println("关闭中！");
		}
		dispose();
	}

	private void scrollToView() {
		if (curIndex >= 0 && curIndex < lists.size()) {
			int lastIndex = curIndex;
			Rectangle rect = manual.getCellBounds(lastIndex, lastIndex);
			manualScroll.getViewport().scrollRectToVisible(rect);
			// 选中当前行，提示玩家
			manual.setSelectedIndex(curIndex);
			validate();
			repaint();
		}

	}

	/**
	 * 全局打谱测试程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EmpressGUI empress = new EmpressGUI();
		empress.setVisible(true);
	}

	public void run() {
		while (curIndex >= -1 && curIndex < lists.size()) {
			next();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}
