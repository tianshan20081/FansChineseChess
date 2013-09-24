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
package cn.fansunion.chinesechess.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

import javax.swing.JPanel;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.ColorUtil;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.core.ChessPiece.PieceCategory;
import cn.fansunion.chinesechess.core.ChessPiece.PieceId;


/**
 * 
 * 楚汉棋兵__抽象棋盘类
 * 
 * 用到了模版方法设计模式
 * 
 * 子类如果需要更改竖线标记的画法，需要覆盖drawShuXianFlag方法
 * 
 * 子类如果需要绘制棋盘的背景图片，需要覆盖getBackgroundImage方法。
 * 
 * 子类必须实现getMouseAdapter、getBoardType、getBackgroundColor方法
 * 
 * 
 * 职责：绘制棋盘
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 * @see NetworkBoard
 */
public abstract class ChessBoard extends JPanel implements NAME, Runnable {

	private static final long serialVersionUID = 1L;

	// 棋盘的水平方向可容纳棋子的点的个数
	public static final int X = 9;

	// 棋盘的垂直方向可容纳棋子的点的个数
	public static final int Y = 10;

	// 棋盘单元格的宽度和高度
	public static int UNIT_WIDTH = ChessPiece.UNIT_WIDTH;

	public static int UNIT_HEIGHT = ChessPiece.UNIT_HEIGHT;

	public static int PIECE_WIDTH = ChessPiece.PIECE_WIDTH;

	public static int PIECE_HEIGHT = ChessPiece.PIECE_HEIGHT;

	/**
	 * 棋盘的类型
	 * 
	 * @author 雷文
	 * 
	 */
	public enum BoardType {
		printWhole, printPartial, network, manMachine, eightEmpress, horseMaze
	}

	// 卒、兵和炮的位置的标记
	protected ArrayList<Point> sidePoints = new ArrayList<Point>(14);

	// 当前棋子的可选走法
	protected ArrayList<Point> tipPoints = new ArrayList<Point>();

	// 棋子点，共90个，横9*纵10
	public ChessPoint chessPoints[][];

	// 移动棋子时是否需要提示
	protected boolean moveFlag = false;

	// 移动棋子提示的2个点，可以简化为MoveStep
	public Point[] movePoints = new Point[] { new Point(), new Point() };

	// 红方16个棋子
	public ChessPiece 红車1, 红車2, 红馬1, 红馬2, 红相1, 红相2, 红帥, 红仕1, 红仕2, 红兵1, 红兵2,
			红兵3, 红兵4, 红兵5, 红炮1, 红炮2;

	// 黑方16个棋子
	public ChessPiece 黑車1, 黑車2, 黑馬1, 黑馬2, 黑象1, 黑象2, 黑將, 黑士1, 黑士2, 黑卒1, 黑卒2,
			黑卒3, 黑卒4, 黑卒5, 黑炮1, 黑炮2;

	// 手形光标
	public static Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

	// 默认光标
	public static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	// 闪烁的棋子
	protected ChessPiece winkPiece;

	// 默认没有选中棋子
	protected boolean isSelected = false;

	// 是否需要闪烁
	protected boolean needWink = false;

	// 是否可以移动棋子
	protected boolean move = false;

	// 闪烁的线程
	protected Thread winkThread;

	// 闪烁棋子的坐标
	protected int startI, startJ;

	public ChessManual chessManual = null;

	// 战斗方的名字，红方or黑方
	protected String playerName;

	/**
	 * 构造函数
	 * 
	 */
	public ChessBoard() {
		super();
		setLayout(null);// 不可忽视
		setBackground(getBackgroundColor());// 设置棋盘的背景色
		addMouseListener(getMouseAdapter());// 鼠标适配器

		// 初始化棋子点
		chessPoints = new ChessPoint[X + 1][Y + 1];
		for (int i = 1; i <= X; i++) {
			for (int j = 1; j <= Y; j++) {
				chessPoints[i][j] = new ChessPoint(i * UNIT_WIDTH, j
						* UNIT_HEIGHT);
			}
		}
		// 为32个棋子分配空间
		init32Pieces();
		// 初始化32个棋子的工具提示
		init32PiecesTooltip();

		// 棋谱类
		chessManual = new ChessManual(this);

		// 初始化炮和兵的位置，以便画出标记
		initPBFlag();

		winkThread = new Thread(this);
		winkThread.start();
	}

	// 默认使用默认背景色
	protected Color getBackgroundColor() {
		return ColorUtil.getDefaultBgcolor();
	}

	/**
	 * 初始化32个棋子的工具提示
	 * 
	 */
	private void init32PiecesTooltip() {
		红車1.setToolTipText(PropertyReader.get("JU_TOOLTIP"));
		红車2.setToolTipText(PropertyReader.get("JU_TOOLTIP"));
		黑車1.setToolTipText(PropertyReader.get("JU_TOOLTIP"));
		黑車2.setToolTipText(PropertyReader.get("JU_TOOLTIP"));

		红炮1.setToolTipText(PropertyReader.get("PAO_TOOLTIP"));
		红炮2.setToolTipText(PropertyReader.get("PAO_TOOLTIP"));
		黑炮1.setToolTipText(PropertyReader.get("PAO_TOOLTIP"));
		黑炮2.setToolTipText(PropertyReader.get("PAO_TOOLTIP"));

		红馬1.setToolTipText(PropertyReader.get("MA_TOOLTIP"));
		红馬2.setToolTipText(PropertyReader.get("MA_TOOLTIP"));
		黑馬1.setToolTipText(PropertyReader.get("MA_TOOLTIP"));
		黑馬2.setToolTipText(PropertyReader.get("MA_TOOLTIP"));

		红兵1.setToolTipText(PropertyReader.get("BING_TOOLTIP"));
		红兵2.setToolTipText(PropertyReader.get("BING_TOOLTIP"));
		红兵3.setToolTipText(PropertyReader.get("BING_TOOLTIP"));
		红兵4.setToolTipText(PropertyReader.get("BING_TOOLTIP"));
		红兵5.setToolTipText(PropertyReader.get("BING_TOOLTIP"));

		黑卒1.setToolTipText(PropertyReader.get("ZU_TOOLTIP"));
		黑卒2.setToolTipText(PropertyReader.get("ZU_TOOLTIP"));
		黑卒3.setToolTipText(PropertyReader.get("ZU_TOOLTIP"));
		黑卒4.setToolTipText(PropertyReader.get("ZU_TOOLTIP"));
		黑卒5.setToolTipText(PropertyReader.get("ZU_TOOLTIP"));

		红仕1.setToolTipText(PropertyReader.get("HONGSHI_TOOLTIP"));
		红仕2.setToolTipText(PropertyReader.get("HONGSHI_TOOLTIP"));

		黑士1.setToolTipText(PropertyReader.get("HEISHI_TOOLTIP"));
		黑士2.setToolTipText(PropertyReader.get("HEISHI_TOOLTIP"));

		黑象1.setToolTipText(PropertyReader.get("HEIXIANG_TOOLTIP"));
		黑象2.setToolTipText(PropertyReader.get("HEIXIANG_TOOLTIP"));

		红相1.setToolTipText(PropertyReader.get("HONGXIANG_TOOLTIP"));

		红相2.setToolTipText(PropertyReader.get("HONGXIANG_TOOLTIP"));

		红帥.setToolTipText(PropertyReader.get("SHUAI_TOOLTIP"));
		黑將.setToolTipText(PropertyReader.get("JIANG_TOOLTIP"));

	}

	/**
	 * 无论是对战棋盘还是打谱棋盘，都需要初始化32颗棋子，
	 * 
	 * 初始化32个棋子
	 * 
	 */
	private void init32Pieces() {
		红車1 = PieceUtil.createPiece(PieceId.HONGJU1);
		红車2 = PieceUtil.createPiece(PieceId.HONGJU2);
		红馬1 = PieceUtil.createPiece(PieceId.HONGMA1);
		红馬2 = PieceUtil.createPiece(PieceId.HONGMA2);
		红炮1 = PieceUtil.createPiece(PieceId.HONGPAO1);
		红炮2 = PieceUtil.createPiece(PieceId.HONGPAO2);
		红相1 = PieceUtil.createPiece(PieceId.HONGXIANG1);
		红相2 = PieceUtil.createPiece(PieceId.HONGXIANG2);
		红仕1 = PieceUtil.createPiece(PieceId.HONGSHI1);
		红仕2 = PieceUtil.createPiece(PieceId.HONGSHI2);
		红帥 = PieceUtil.createPiece(PieceId.SHUAI);
		红兵1 = PieceUtil.createPiece(PieceId.BING1);
		红兵2 = PieceUtil.createPiece(PieceId.BING2);
		红兵3 = PieceUtil.createPiece(PieceId.BING3);
		红兵4 = PieceUtil.createPiece(PieceId.BING4);
		红兵5 = PieceUtil.createPiece(PieceId.BING5);

		黑將 = PieceUtil.createPiece(PieceId.JIANG);
		黑士1 = PieceUtil.createPiece(PieceId.HEISHI1);
		黑士2 = PieceUtil.createPiece(PieceId.HEISHI2);
		黑車1 = PieceUtil.createPiece(PieceId.HEIJU1);
		黑車2 = PieceUtil.createPiece(PieceId.HEIJU2);
		黑炮1 = PieceUtil.createPiece(PieceId.HEIPAO1);
		黑炮2 = PieceUtil.createPiece(PieceId.HEIPAO2);
		黑象1 = PieceUtil.createPiece(PieceId.HEIXIANG1);
		黑象2 = PieceUtil.createPiece(PieceId.HEIXIANG2);
		黑馬1 = PieceUtil.createPiece(PieceId.HEIMA1);
		黑馬2 = PieceUtil.createPiece(PieceId.HEIMA2);
		黑卒1 = PieceUtil.createPiece(PieceId.ZU1);
		黑卒2 = PieceUtil.createPiece(PieceId.ZU2);
		黑卒3 = PieceUtil.createPiece(PieceId.ZU3);
		黑卒4 = PieceUtil.createPiece(PieceId.ZU4);
		黑卒5 = PieceUtil.createPiece(PieceId.ZU5);

		红帥.addMouseListener(getMouseAdapter());
		红仕1.addMouseListener(getMouseAdapter());
		红仕2.addMouseListener(getMouseAdapter());
		红相1.addMouseListener(getMouseAdapter());
		红相2.addMouseListener(getMouseAdapter());
		红車1.addMouseListener(getMouseAdapter());
		红車2.addMouseListener(getMouseAdapter());
		红馬1.addMouseListener(getMouseAdapter());
		红馬2.addMouseListener(getMouseAdapter());
		红炮1.addMouseListener(getMouseAdapter());
		红炮2.addMouseListener(getMouseAdapter());
		红兵1.addMouseListener(getMouseAdapter());
		红兵2.addMouseListener(getMouseAdapter());
		红兵3.addMouseListener(getMouseAdapter());
		红兵4.addMouseListener(getMouseAdapter());
		红兵5.addMouseListener(getMouseAdapter());

		黑將.addMouseListener(getMouseAdapter());
		黑士1.addMouseListener(getMouseAdapter());
		黑士2.addMouseListener(getMouseAdapter());
		黑象1.addMouseListener(getMouseAdapter());
		黑象2.addMouseListener(getMouseAdapter());
		黑炮1.addMouseListener(getMouseAdapter());
		黑炮2.addMouseListener(getMouseAdapter());
		黑車1.addMouseListener(getMouseAdapter());
		黑車2.addMouseListener(getMouseAdapter());
		黑馬1.addMouseListener(getMouseAdapter());
		黑馬2.addMouseListener(getMouseAdapter());
		黑卒1.addMouseListener(getMouseAdapter());
		黑卒2.addMouseListener(getMouseAdapter());
		黑卒3.addMouseListener(getMouseAdapter());
		黑卒4.addMouseListener(getMouseAdapter());
		黑卒5.addMouseListener(getMouseAdapter());
	}

	/**
	 * 绘制棋盘：10条横线、9条纵线、炮兵卒14个标记、九宫格、楚河漢界
	 * 
	 * 根据需要还绘制棋子移动的标记
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBackgroundImage(g);

		Graphics2D g2 = (Graphics2D) g;
		// 兵、卒、炮标记笔画
		BasicStroke bsFlag = new BasicStroke(2);
		// 楚河汉界、棋盘边框笔画
		BasicStroke bsLine = new BasicStroke(2);

		// 棋盘线笔画
		BasicStroke bs1 = new BasicStroke(1);

		//绘制直线
		drawLines(g2, bsLine, bs1);
		//绘制九宫格
		drawJiuGongLines(g2, bs1);
		//绘制楚河漢界
		drawChuheHanjieString(g2);
		//绘制炮和兵标记
		drawPaoBingFlag(g2, bsFlag);

		// 如果有棋子移动，画出2个提示框，每个提示框由8条线组成
		drawMoveFlag(g2);
		drawWillMoveFlag(g2);

		// 设置字体和线宽，为画坐标做准备
		BasicStroke bsOld = new BasicStroke(1);
		g2.setStroke(bsOld);
		g2.setFont(new Font("宋体", Font.PLAIN, 14));
		g2.setColor(new Color(0, 0, 0));
		drawShuXianFlag(g2);
	}

	private void drawBackgroundImage(Graphics g) {
		// 默认不绘制背景图片
		Image image = getBackgroundImage();
		if (image != null) {
			Dimension size = new Dimension(super.getWidth(), super.getHeight());
			g.drawImage(image, 0, 0, size.width, size.height, null);
		}

	}

	protected Image getBackgroundImage() {
		return null;
	}

	// 默认，竖线标记1到9，一到九，是按照红方在下，黑方在上绘制的。如果子类不应该这样话，应该重新绘制。
	protected void drawShuXianFlag(Graphics2D g2) {
		// 显示横坐标
		for (int i = 1; i <= X; i++) {
			g2.drawString("" + i, i * UNIT_WIDTH - 4, UNIT_HEIGHT / 2 - 4);
		}

		for (int i = 1; i <= X; i++) {
			g2.drawString("" + ChessUtil.numToZi(10 - i), i * UNIT_WIDTH - 4,
					10 * UNIT_HEIGHT + 34);
		}
	}

	/**
	 * @param g2
	 */
	private void drawWillMoveFlag(Graphics2D g2) {
		// 如果有棋子将要移动，在走的位置处绘制标记
		int tipSize = tipPoints.size();
		int distance = PIECE_WIDTH - 6;

		for (int i = 0; i < tipSize; i++) {
			int a = (int) tipPoints.get(i).getX();
			int b = (int) tipPoints.get(i).getY();

			// 坐标转换
			int boardX = chessPoints[a][b].getX();
			int boardY = chessPoints[a][b].getY();

			Color color;

			color = new Color(82, 72, 255);
			BasicStroke bs = new BasicStroke(2);
			g2.setStroke(bs);
			ChessPiece piece = chessPoints[a][b].getPiece();
			if (piece != null && !piece.getName().equals(playerName)) {
				if (playerName.equals(RED_NAME)) {
					// 用我方的颜色×标识可以杀死的棋子
					color = new Color(255, 0, 0);
				} else {
					// 用我方的颜色×标识可以杀死的棋子
					color = new Color(0, 0, 0);
				}
				drawMoveTips2(g2, boardX, boardY, color);
			} else {
				// 用我方的颜色×标识可以杀死的棋子
				drawMoveTips(g2, distance, boardX, boardY, color);
			}
		}
	}

	/**
	 * @param g2
	 */
	private void drawMoveFlag(Graphics2D g2) {
		if (moveFlag) {
			int d = PIECE_WIDTH - 6;
			int a = 0, b = 0;
			for (int i = 0; i < movePoints.length; i++) {
				// 绘制移动标记，8条短线
				a = (int) movePoints[i].getX();
				b = (int) movePoints[i].getY();
				// 坐标转换
				int boardX = chessPoints[a][b].getX();
				int boardY = chessPoints[a][b].getY();
				Color c = new Color(0, 128, 0);
				BasicStroke bs = new BasicStroke(2);
				g2.setStroke(bs);
				drawMoveTips(g2, d, boardX, boardY, c);
			}
		}
	}

	/**
	 * @param g2
	 * @param bsFlag
	 */
	private void drawPaoBingFlag(Graphics2D g2, BasicStroke bsFlag) {
		// 画炮和兵的位置的标记
		int size = sidePoints.size();
		double x = PIECE_WIDTH / 9;// 棋子中心点到标记直角边交点的水平距离
		double side = PIECE_WIDTH / 6;// 标记的长度
		for (int i = 0; i < size; i++) {
			double a = sidePoints.get(i).getX();
			double b = sidePoints.get(i).getY();
			g2.setStroke(bsFlag);
			if (i >= 0 && i <= 9) {
				drawPBMiddle(g2, x, side, a, b);
			} else if (i == 10 || i == 11) {
				drawPBRight(g2, x, side, a, b);
			} else if (i == 12 || i == 13) {
				drawPBLeft(g2, x, side, a, b);
			}

		}
	}

	/**
	 * @param g2
	 */
	private void drawChuheHanjieString(Graphics2D g2) {
		// 楚河、汉界
		g2.setFont(new Font("宋体", Font.PLAIN, 32));
		g2.drawString("漢 界", chessPoints[2][5].getX(), chessPoints[2][5].getY()
				+ 2 * UNIT_HEIGHT / 3 + 2);
		g2.drawString("楚 河", chessPoints[6][5].getX(), chessPoints[2][5].getY()
				+ 2 * UNIT_HEIGHT / 3 + 2);
	}

	/**
	 * @param g2
	 * @param bs1
	 */
	private void drawJiuGongLines(Graphics2D g2, BasicStroke bs1) {
		// 九宫格
		g2.setStroke(bs1);
		g2.drawLine(chessPoints[4][1].getX(), chessPoints[4][1].getY(),
				chessPoints[6][3].getX(), chessPoints[6][3].getY());
		g2.drawLine(chessPoints[6][1].getX(), chessPoints[6][1].getY(),
				chessPoints[4][3].getX(), chessPoints[4][3].getY());
		g2.drawLine(chessPoints[4][8].getX(), chessPoints[4][8].getY(),
				chessPoints[6][Y].getX(), chessPoints[6][Y].getY());
		g2.drawLine(chessPoints[4][Y].getX(), chessPoints[4][Y].getY(),
				chessPoints[6][8].getX(), chessPoints[6][8].getY());
	}

	/**
	 * @param g2
	 * @param bsLine
	 * @param bs1
	 */
	private void drawLines(Graphics2D g2, BasicStroke bsLine, BasicStroke bs1) {
		// 10条横线
		for (int j = 1; j <= Y; j++) {
			if (j == 1 || j == 5 || j == 6 || j == 10) {
				g2.setStroke(bsLine);
				g2.drawLine(chessPoints[1][j].getX(), chessPoints[1][j].getY(),
						chessPoints[X][j].getX(), chessPoints[X][j].getY());
			} else {
				g2.setStroke(bs1);
				g2.drawLine(chessPoints[1][j].getX(), chessPoints[1][j].getY(),
						chessPoints[X][j].getX(), chessPoints[X][j].getY());
			}
		}

		// 9条纵线
		for (int i = 1; i <= X; i++) {
			if (i != 1 && i != X) {
				// 中间的纵线
				g2.setStroke(bs1);
				g2.drawLine(chessPoints[i][1].getX(), chessPoints[i][1].getY(),
						chessPoints[i][Y - 5].getX(),
						chessPoints[i][Y - 5].getY());
				g2.drawLine(chessPoints[i][Y - 4].getX(),
						chessPoints[i][Y - 4].getY(), chessPoints[i][Y].getX(),
						chessPoints[i][Y].getY());
			} else {
				// 两边的纵线
				g2.setStroke(bsLine);
				g2.drawLine(chessPoints[i][1].getX(), chessPoints[i][1].getY(),
						chessPoints[i][Y].getX(), chessPoints[i][Y].getY());
			}
		}
	}

	private void drawMoveTips2(Graphics2D g2, int boardX, int boardY,
			Color color) {
		BasicStroke bs = new BasicStroke(2);
		g2.setStroke(bs);
		g2.setColor(color);
		g2.drawLine(boardX - PIECE_WIDTH / 2, boardY - PIECE_HEIGHT / 2, boardX
				+ PIECE_WIDTH / 2, boardY + PIECE_HEIGHT / 2);
		g2.drawLine(boardX - PIECE_WIDTH / 2, boardY + PIECE_HEIGHT / 2, boardX
				+ PIECE_WIDTH / 2, boardY - PIECE_HEIGHT / 2);

	}

	/**
	 * 初始化一个棋子可以走的位置，第1种方案：
	 * 
	 * 优点：代码简洁
	 * 
	 * 缺点：效率不高
	 * 
	 * @param piece
	 *            将要移动的棋子
	 * @param startX
	 *            起点横坐标
	 * @param startY
	 *            起点纵坐标
	 */
	protected void initTipPoints(ChessPiece piece, int startX, int startY) {
		BoardType boardType = getBoardType();
		if (boardType == null) {
			return;
		}
		/*
		 * 使用2重循环，代码最简介 如果为了提高效率，可以根据棋子的类别，分别编写相应的算法，工作量很大
		 */
		boolean rule = false;
		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 10; j++) {
				if (boardType == BoardType.printWhole
						|| boardType == BoardType.manMachine
						|| boardType == BoardType.horseMaze) {
					rule = ChessRule.allRule(piece, startX, startY, i, j,
							chessPoints);
				} else if (boardType == BoardType.printPartial) {
					rule = ChessRule.partialRule(piece, startX, startY, i, j,
							chessPoints);
				} else if (boardType == BoardType.network) {
					rule = ChessRule.networkRule(piece, startX, startY, i, j,
							chessPoints);
				}

				if (rule) {
					boolean flag = ChessRule.isWillDangerous(startX, startY, i,
							j, chessPoints, this);
					if (!flag) {
						tipPoints.add(new Point(i, j));
					}
				}
			}
		}

		repaint();
		validate();
	}

	protected abstract BoardType getBoardType();

	/**
	 * 初始化一个棋子可以走的位置，第二种方案：根据棋子类别分别计算
	 * 
	 * 优点：效率较高
	 * 
	 * 缺点：代码复杂
	 * 
	 * @param piece
	 *            将要移动的棋子
	 * @param startX
	 *            起点横坐标
	 * @param startY
	 *            起点纵坐标
	 */
	@Deprecated
	protected void initTipPoints2(ChessPiece piece, int startX, int startY) {
		BoardType boardType = getBoardType();
		PieceCategory pc = piece.getCategory();
		System.out.println("绘制棋子提示标记：" + pc + startX + "," + startY);
		boolean rule = false;

		switch (pc) {
		case JU:
			// 水平方向
			for (int i = 1; i <= 9; i++) {
				if (boardType == BoardType.printWhole
						|| boardType == BoardType.manMachine
						|| boardType == BoardType.horseMaze) {
					rule = ChessRule.allRule(piece, startX, startY, i, startY,
							chessPoints);
				} else if (boardType == BoardType.printPartial) {
					rule = ChessRule.partialRule(piece, startX, startY, i,
							startY, chessPoints);
				} else if (boardType == BoardType.network) {
					rule = ChessRule.networkRule(piece, startX, startY, i,
							startY, chessPoints);
				}
				if (rule) {
					tipPoints.add(new Point(i, startY));
				}

			}
			// 垂直方向
			for (int j = 1; j <= 10; j++) {
				if (boardType == BoardType.printWhole) {
					rule = ChessRule.allRule(piece, startX, startY, startX, j,
							chessPoints);
				} else if (boardType == BoardType.printPartial) {
					rule = ChessRule.partialRule(piece, startX, startY, startX,
							j, chessPoints);
				} else if (boardType == BoardType.network) {
					rule = ChessRule.networkRule(piece, startX, startY, startX,
							j, chessPoints);
				}

				if (rule) {
					tipPoints.add(new Point(startX, j));
				}
			}
			break;
		case MA:
			// 垂直方向
			for (int endX = startX - 2; endX <= startX + 2; endX++) {
				for (int endY = startY - 2; endY <= startY + 2; endY++) {
					if (boardType == BoardType.printWhole) {
						rule = ChessRule.allRule(piece, startX, startY, endX,
								endY, chessPoints);
					} else if (boardType == BoardType.printPartial) {
						rule = ChessRule.partialRule(piece, startX, startY,
								endX, endY, chessPoints);
					} else if (boardType == BoardType.network) {
						rule = ChessRule.networkRule(piece, startX, startY,
								endX, endY, chessPoints);
					}

					if (rule) {
						tipPoints.add(new Point(startX, endX));
					}
				}
			}
			break;
		case PAO:
			// 水平方向
			for (int i = 1; i <= 9; i++) {
				if (boardType == BoardType.printWhole) {
					rule = ChessRule.allRule(piece, startX, startY, i, startY,
							chessPoints);
				} else if (boardType == BoardType.printPartial) {
					rule = ChessRule.partialRule(piece, startX, startY, i,
							startY, chessPoints);
				} else if (boardType == BoardType.network) {
					rule = ChessRule.networkRule(piece, startX, startY, i,
							startY, chessPoints);
				}

				if (rule) {
					tipPoints.add(new Point(i, startY));
				}
			}
			// 垂直方向
			for (int j = 1; j <= 10; j++) {
				if (boardType == BoardType.printWhole) {
					rule = ChessRule.allRule(piece, startX, startY, startX, j,
							chessPoints);
				} else if (boardType == BoardType.printPartial) {
					rule = ChessRule.partialRule(piece, startX, startY, startX,
							j, chessPoints);
				} else if (boardType == BoardType.network) {
					rule = ChessRule.networkRule(piece, startX, startY, startX,
							j, chessPoints);
				}

				if (rule) {
					tipPoints.add(new Point(startX, j));
				}
			}
			break;
		case HONGXIANG:
			break;
		case HEIXIANG:
			break;
		case HONGSHI:
			break;
		case HEISHI:
			break;
		case BING:
			// 水平方向

			// 垂直方向
			break;
		case ZU:
			// 水平方向

			// 垂直方向
			break;
		case JIANG:
			// 水平方向

			// 垂直方向
			break;
		case SHUAI:
			// 水平方向
			for (int endX = startX - 1; endX <= startX + 1; endX++) {
				for (int endY = startY - 1; endY <= startY + 1; endY++) {
					if (boardType == BoardType.printWhole) {
						rule = ChessRule.allRule(piece, startX, startY, endX,
								endY, chessPoints);
					} else if (boardType == BoardType.printPartial) {
						rule = ChessRule.partialRule(piece, startX, startY,
								endX, endY, chessPoints);
					} else if (boardType == BoardType.network) {
						rule = ChessRule.networkRule(piece, startX, startY,
								endX, endY, chessPoints);
					}

					if (rule) {
						tipPoints.add(new Point(endX, startY));
					}
				}
			}

		}
	}

	/**
	 * 绘制移动提示标记
	 * 
	 * @param g2
	 * @param d
	 * @param a
	 * @param b
	 */
	private void drawMoveTips(Graphics2D g2, int d, double a, double b,
			Color color) {
		g2.setColor(color);
		// 左上角的2条线
		g2.drawLine((int) (a - d / 2), (int) (b - d / 2), (int) (a - d / 2),
				(int) (b - d / 4));
		g2.drawLine((int) (a - d / 2), (int) (b - d / 2), (int) (a - d / 4),
				(int) (b - d / 2));
		// 右上角的2条线
		g2.drawLine((int) (a + d / 2), (int) (b - d / 2), (int) (a + d / 4),
				(int) (b - d / 2));
		g2.drawLine((int) (a + d / 2), (int) (b - d / 2), (int) (a + d / 2),
				(int) (b - d / 4));
		// 左下角的2条线
		g2.drawLine((int) (a - d / 2), (int) (b + d / 2), (int) (a - d / 2),
				(int) (b + d / 4));
		g2.drawLine((int) (a - d / 2), (int) (b + d / 2), (int) (a - d / 4),
				(int) (b + d / 2));
		// 右下角的2条线
		g2.drawLine((int) (a + d / 2), (int) (b + d / 2), (int) (a + d / 4),
				(int) (b + d / 2));
		g2.drawLine((int) (a + d / 2), (int) (b + d / 2), (int) (a + d / 2),
				(int) (b + d / 4));
	}

	private void drawPBLeft(Graphics2D g2, double x, double side, double a,
			double b) {
		// 左上角
		g2.drawLine((int) (a - x), (int) (b - x), (int) (a - x),
				(int) (b - x - side));
		g2.drawLine((int) (a - x), (int) (b - x), (int) (a - x - side),
				(int) (b - x));
		// 左下角
		g2.drawLine((int) (a - x), (int) (b + x), (int) (a - x),
				(int) (b + x + side));
		g2.drawLine((int) (a - x), (int) (b + x), (int) (a - x - side),
				(int) (b + x));
	}

	private void drawPBRight(Graphics2D g2, double x, double side, double a,
			double b) {
		// 右上角
		g2.drawLine((int) (a + x), (int) (b - x), (int) (a + x),
				(int) (b - x - side));
		g2.drawLine((int) (a + x), (int) (b - x), (int) (a + x + side),
				(int) (b - x));
		// 右下角
		g2.drawLine((int) (a + x), (int) (b + x), (int) (a + x),
				(int) (b + x + side));
		g2.drawLine((int) (a + x), (int) (b + x), (int) (a + x + side),
				(int) (b + x));
	}

	private void drawPBMiddle(Graphics2D g2, double x, double side, double a,
			double b) {
		// 左上角
		g2.drawLine((int) (a - x), (int) (b - x), (int) (a - x),
				(int) (b - x - side));
		g2.drawLine((int) (a - x), (int) (b - x), (int) (a - x - side),
				(int) (b - x));
		// 左下角
		g2.drawLine((int) (a - x), (int) (b + x), (int) (a - x),
				(int) (b + x + side));
		g2.drawLine((int) (a - x), (int) (b + x), (int) (a - x - side),
				(int) (b + x));

		// 右上角
		g2.drawLine((int) (a + x), (int) (b - x), (int) (a + x),
				(int) (b - x - side));
		g2.drawLine((int) (a + x), (int) (b - x), (int) (a + x + side),
				(int) (b - x));
		// 右下角
		g2.drawLine((int) (a + x), (int) (b + x), (int) (a + x),
				(int) (b + x + side));
		g2.drawLine((int) (a + x), (int) (b + x), (int) (a + x + side),
				(int) (b + x));
	}

	/**
	 * 初始化炮和兵、卒的标记的位置
	 * 
	 */
	private void initPBFlag() {
		// 4个炮的位置
		sidePoints.add(new Point(chessPoints[2][3].getX(), chessPoints[2][3]
				.getY()));
		sidePoints.add(new Point(chessPoints[8][3].getX(), chessPoints[8][3]
				.getY()));
		sidePoints.add(new Point(chessPoints[2][8].getX(), chessPoints[2][8]
				.getY()));
		sidePoints.add(new Point(chessPoints[8][8].getX(), chessPoints[8][8]
				.getY()));

		// 3个兵、3个卒
		sidePoints.add(new Point(chessPoints[3][4].getX(), chessPoints[3][4]
				.getY()));
		sidePoints.add(new Point(chessPoints[5][4].getX(), chessPoints[5][4]
				.getY()));
		sidePoints.add(new Point(chessPoints[7][4].getX(), chessPoints[7][4]
				.getY()));
		sidePoints.add(new Point(chessPoints[3][7].getX(), chessPoints[3][7]
				.getY()));
		sidePoints.add(new Point(chessPoints[5][7].getX(), chessPoints[5][7]
				.getY()));
		sidePoints.add(new Point(chessPoints[7][7].getX(), chessPoints[7][7]
				.getY()));

		// 左边：1个兵+ 1个卒；右边：1个兵+ 1个卒
		sidePoints.add(new Point(chessPoints[1][4].getX(), chessPoints[1][4]
				.getY()));
		sidePoints.add(new Point(chessPoints[1][7].getX(), chessPoints[1][7]
				.getY()));
		sidePoints.add(new Point(chessPoints[9][4].getX(), chessPoints[9][4]
				.getY()));
		sidePoints.add(new Point(chessPoints[9][7].getX(), chessPoints[9][7]
				.getY()));

	}

	/**
	 * 查找棋子的纵坐标
	 * 
	 * @param x
	 *            棋子的横坐标
	 * @param pc
	 *            棋子的类别
	 * @return 返回棋子的纵坐标，否则返回 0
	 */
	protected int getPieceYByCategory(int x, PieceCategory pc) {
		if (pc == null) {
			System.out.println("getPieceYByCategory:pc == null");
			return 0;
		}
		int y = 0;
		if (x < 1 || x > 9) {
			return y;
		}

		for (int j = 1; j <= 10; j++) {
			ChessPiece piece = chessPoints[x][j].getPiece();
			if (piece != null && pc.equals(piece.getCategory())
					&& piece.getName().equals(playerName)) {
				System.out.println(playerName + piece.getCategory() + x + ","
						+ +j);
				y = j;
				break;
			}
		}
		return y;

	}

	/**
	 * 根据棋谱，如馬八进七，获得移动的棋子
	 * 
	 * @param manual
	 *            棋谱，如馬八进七
	 * @return 移动的棋子。
	 */
	public ChessPiece getMovePiece(String manual) {
		if (manual == null || manual.length() != 4) {
			return null;
		}

		Point point = getStartPosition(manual);
		int startX = (int) point.getX();
		int startY = (int) point.getY();

		if (startX < 1 || startX > 9 || startY < 1 || startY > 10) {
			return null;
		}
		ChessPiece piece = chessPoints[startX][startY].getPiece();
		System.out.println("移动的棋子：" + piece.getName() + " "
				+ piece.getCategory());
		return piece;

	}

	/**
	 * 根据棋谱，如馬八进七，获得被吃的棋子
	 * 
	 * @param manual棋谱
	 *            ，如馬八进七
	 * @return 被吃的棋子，如果没有棋子被吃，返回null。
	 */
	public ChessPiece getRemovedPiece(String manual) {
		Point point = getEndPosition(manual);

		int endX = (int) point.getX();
		int endY = (int) point.getY();
		if (endX < 1 || endX > 9 || endY < 1 || endY > 10) {
			return null;
		}
		return chessPoints[endX][endY].getPiece();

	}

	/**
	 * 根据棋谱，如馬八进七，获得移动棋子的起始坐标
	 * 
	 * @param manual棋谱
	 *            ，如馬八进七
	 * @return 移动棋子的起始坐标。
	 */
	public Point getStartPosition(String manual) {
		char name = manual.charAt(0);
		// 获得棋子的类别
		PieceCategory pc = getPieceCategory(manual);
		// 特殊情况
		Point p1 = null;
		Point p2 = null;
		if (name == '前' || name == '后') {
			for (int i = 1; i <= 9; i++) {
				for (int j = 1; j <= 10; j++) {
					ChessPiece piece = chessPoints[i][j].getPiece();
					if (piece != null && piece.getCategory().equals(pc)
							&& piece.getName().equals(playerName)) {
						if (p1 == null) {
							p1 = new Point(i, j);
						} else {
							p2 = new Point(i, j);
						}
					}
				}
			}
			// 只考虑了打谱的情况，联网对战没有考虑
			if (name == '前' && playerName.equals(RED_NAME)) {
				return p1;
			} else if (name == '后' && playerName.equals(RED_NAME)) {
				return p2;
			} else if (name == '前' && playerName.equals(BLACK_NAME)) {
				return p2;
			} else if (name == '后' && playerName.equals(BLACK_NAME)) {
				return p1;
			}

		}
		int startX = ChessUtil.ziToNum(manual.substring(1, 2));

		if (playerName.equals(RED_NAME)) {
			startX = 10 - startX;
		}
		int startY = getPieceYByCategory(startX, pc);
		System.out.println(pc + "getStartPosition起点坐标：" + startX + startY);

		return new Point(startX, startY);
	}

	/**
	 * 根据棋谱，如馬八进七，获得移动棋子的终点坐标
	 * 
	 * @param manual
	 *            棋谱，如馬八进七
	 * @return 移动棋子的终点坐标。
	 */
	public Point getEndPosition(String manual) {
		Point pStart = getStartPosition(manual);
		String third = manual.substring(2, 3);// 第3个字
		String fourth = String.valueOf((manual.charAt(3)));// 第4个字
		PieceCategory pc = getPieceCategory(manual);

		int endX = 0;
		int endY = 0;

		int jbzpjs = ChessUtil.ziToNum(fourth);
		int msx = ChessUtil.ziToNum(fourth);
		if (playerName.equals(RED_NAME)) {
			msx = 10 - msx;
		}

		if (third.equals("进")) {
			switch (pc) {
			case JU:
			case ZU:
			case BING:
			case PAO:
			case JIANG:
			case SHUAI:
				// 横坐标不变，纵坐标加或减
				endX = (int) pStart.getX();
				if (playerName.equals(RED_NAME)) {
					endY = (int) (pStart.getY() - jbzpjs);
				} else {
					endY = (int) (pStart.getY() + jbzpjs);
				}
				break;

			// 横坐标和纵坐标都变化
			case MA:
				endX = msx;
				int startX = (int) pStart.getX();
				int xDistance = Math.abs(startX - endX);
				if (playerName.equals(RED_NAME)) {
					if (xDistance == 1) {
						endY = (int) (pStart.getY() - 2);
					} else {
						endY = (int) (pStart.getY() - 1);
					}
				} else {
					if (xDistance == 1) {
						endY = (int) (pStart.getY() + 2);
					} else {
						endY = (int) (pStart.getY() + 1);
					}
				}
				break;

			case HONGSHI:
				endX = msx;
				endY = (int) (pStart.getY() - 1);

				break;
			case HEISHI:
				endX = msx;
				endY = (int) (pStart.getY() + 1);
				break;
			case HEIXIANG:
				endX = msx;
				endY = (int) (pStart.getY() + 2);
				break;
			case HONGXIANG:
				endX = msx;
				endY = (int) (pStart.getY() - 2);
				break;

			}
		} else if (third.equals("退")) {
			switch (pc) {
			case JU:
			case ZU:
			case BING:
			case PAO:
			case JIANG:
			case SHUAI:
				// 横坐标不变，纵坐标加或减
				endX = (int) pStart.getX();
				if (playerName.equals(RED_NAME)) {
					endY = (int) (pStart.getY() + jbzpjs);
				} else {
					endY = (int) (pStart.getY() - jbzpjs);
				}
				break;

			// 横坐标和纵坐标都变化
			case MA:
				endX = msx;
				int startX = (int) pStart.getX();
				int xDistance = Math.abs(startX - endX);
				if (playerName.equals(RED_NAME)) {
					if (xDistance == 1) {
						endY = (int) (pStart.getY() + 2);
					} else {
						endY = (int) (pStart.getY() + 1);
					}
				} else {
					if (xDistance == 1) {
						endY = (int) (pStart.getY() - 2);
					} else {
						endY = (int) (pStart.getY() - 1);
					}
				}
				break;

			case HONGSHI:
				endX = msx;
				endY = (int) (pStart.getY() + 1);

				break;
			case HEISHI:
				endX = msx;
				endY = (int) (pStart.getY() - 1);
				break;
			case HEIXIANG:
				endX = msx;
				endY = (int) (pStart.getY() - 2);
				break;
			case HONGXIANG:
				endX = msx;
				endY = (int) (pStart.getY() + 2);
				break;
			}
		} else if (third.equals("平")) {
			endX = msx;
			endY = (int) pStart.getY();
		}
		System.out.println("getEndPosition" + pStart.getX() + pStart.getY()
				+ endX + endY);
		return new Point(endX, endY);

	}

	/**
	 * 根据棋谱，如馬八进七，获取移动棋子的类型
	 * 
	 * @param manual
	 *            棋谱，如馬八进七
	 * @return 移动棋子的类型
	 */
	private PieceCategory getPieceCategory(String manual) {
		String name = manual.substring(0, 1);

		if (name.equals("前") || name.equals("后")) {
			name = manual.substring(1, 2);
		}

		return PieceUtil.getPieceCategory(name);
	}

	/**
	 * 棋子闪烁线程
	 */
	public void run() {
		while (true) {
			try {
				if (needWink) {
					winkPiece.setVisible(false);
					Thread.sleep(600);
					winkPiece.setVisible(true);
					Thread.sleep(600);
				} else {
					Thread.sleep(500);
				}
			} catch (InterruptedException ex) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void clearTipPoints() {
		tipPoints.clear();
	}

	public String getPlayerName() {
		return playerName;
	}

	public Thread getWinkThread() {
		return winkThread;
	}

	public ChessPoint[][] getChessPoints() {
		return chessPoints;
	}

	public void addPiece(ChessPiece piece) {
		if (piece == null) {
			return;
		}
		add(piece);
	}

	public void removePiece(ChessPiece piece) {
		if (piece == null) {
			return;
		}
		remove(piece);

	}

	public void setMoveFlag(boolean moveFlag) {
		this.moveFlag = moveFlag;

	}

	public void setMoveFlagPoints(Point start, Point end) {
		movePoints[0] = start;
		movePoints[1] = end;
	}

	protected abstract MouseAdapter getMouseAdapter();

	/**
	 * 移动棋子
	 * 
	 * @param movePiece
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void movePiece(ChessPiece movePiece, int startX, int startY,
			int endX, int endY) {
		System.out.println("想要移动棋子，坐标：(" + startX + "," + startY + "),(" + endX
				+ "," + endY + ")");
		ChessPiece pieceRemoved = chessPoints[endX][endY].getPiece();
		if (pieceRemoved != null) {
			chessPoints[endX][endY].removePiece(pieceRemoved, this);
		}
		chessPoints[endX][endY].setPiece(movePiece, this);
		chessPoints[startX][startY].setHasPiece(false);
		System.out.println("移动：" + movePiece.getCategory());

		setMoveFlag(true);
		movePoints[0] = new Point(endX, endY);
		movePoints[1] = new Point(startX, startY);
		clearTipPoints();

		validate();
		repaint();

	}

	/**
	 * 增加一条棋谱记录
	 * 
	 * @param movePiece
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void addChessRecord(ChessPiece movePiece, int startX, int startY,
			int endX, int endY) {

		if (movePiece == null) {
			System.out.println("movePiece == null");
			return;
		}
		ManualItem moveRecord = new ManualItem();
		ChessPiece pieceRemoved = chessPoints[endX][endY].getPiece();

		moveRecord.setMovePieceId(movePiece.getId());
		if (pieceRemoved != null) {
			moveRecord.setEatedPieceId(pieceRemoved.getId());
		} else {
			moveRecord.setEatedPieceId(null);
		}

		moveRecord.setMoveStep(new MoveStep(new Point(startX, startY),
				new Point(endX, endY)));
		chessManual.addManualItem(moveRecord);

	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setNeedWink(boolean needWink) {
		this.needWink = needWink;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
