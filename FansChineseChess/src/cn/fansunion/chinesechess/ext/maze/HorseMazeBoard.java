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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.ColorUtil;
import cn.fansunion.chinesechess.core.ChessBoard;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessRule;
import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.core.MoveStep;
import cn.fansunion.chinesechess.core.PieceUtil;

/**
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class HorseMazeBoard extends ChessBoard {

	private static final long serialVersionUID = 1L;

	public JList manualList;

	public JLabel gameStatusContent;

	public JTextArea msgArea;

	public HorseMazeGUI horseMazePath;// 打谱时，棋盘的拥有者

	public HorseMazeBoard() {
		manualList = chessManual.manualList;
	}

	/**
	 * 初始化红方、蓝方棋子的宽度，高度和前景色
	 * 
	 */
	public void initChess(boolean isRed) {

		if (isRed) {
			// 红方先走棋
			playerName = RED_NAME;

			if (gameStatusContent != null) {
				gameStatusContent.setText("  " + "红方先走棋哟！");
			}

			chessPoints[1][10].setPiece(红車1, this);
			红車1.setPosition(new Point(1, 10));
			chessPoints[2][10].setPiece(红馬1, this);
			红馬1.setPosition(new Point(2, 10));
			chessPoints[3][10].setPiece(红相1, this);
			红相1.setPosition(new Point(3, 10));
			chessPoints[4][10].setPiece(红仕1, this);
			红仕1.setPosition(new Point(4, 10));
			chessPoints[5][10].setPiece(红帥, this);
			红帥.setPosition(new Point(5, 10));
			chessPoints[6][10].setPiece(红仕2, this);
			红仕2.setPosition(new Point(6, 10));
			chessPoints[7][10].setPiece(红相2, this);
			红相2.setPosition(new Point(7, 10));
			chessPoints[8][10].setPiece(红馬2, this);
			红馬2.setPosition(new Point(8, 10));
			chessPoints[9][10].setPiece(红車2, this);
			红車2.setPosition(new Point(9, 10));
			chessPoints[2][8].setPiece(红炮1, this);
			红炮1.setPosition(new Point(2, 8));
			chessPoints[8][8].setPiece(红炮2, this);
			红炮2.setPosition(new Point(8, 8));
			chessPoints[1][7].setPiece(红兵1, this);
			红兵1.setPosition(new Point(1, 7));
			chessPoints[3][7].setPiece(红兵2, this);
			红兵2.setPosition(new Point(3, 7));
			chessPoints[5][7].setPiece(红兵3, this);
			红兵3.setPosition(new Point(5, 7));
			chessPoints[7][7].setPiece(红兵4, this);
			红兵4.setPosition(new Point(7, 7));
			chessPoints[9][7].setPiece(红兵5, this);
			红兵5.setPosition(new Point(9, 7));

			chessPoints[1][1].setPiece(黑車1, this);
			黑車1.setPosition(new Point(1, 1));
			chessPoints[2][1].setPiece(黑馬1, this);
			黑馬1.setPosition(new Point(2, 1));
			chessPoints[3][1].setPiece(黑象1, this);
			黑象1.setPosition(new Point(3, 1));
			chessPoints[4][1].setPiece(黑士1, this);
			黑士1.setPosition(new Point(4, 1));
			chessPoints[5][1].setPiece(黑將, this);
			黑將.setPosition(new Point(5, 1));
			chessPoints[6][1].setPiece(黑士2, this);
			黑士2.setPosition(new Point(6, 1));
			chessPoints[7][1].setPiece(黑象2, this);
			黑象2.setPosition(new Point(7, 1));
			chessPoints[8][1].setPiece(黑馬2, this);
			黑馬2.setPosition(new Point(8, 1));
			chessPoints[9][1].setPiece(黑車2, this);
			黑車2.setPosition(new Point(9, 1));
			chessPoints[2][3].setPiece(黑炮1, this);
			黑炮1.setPosition(new Point(2, 3));
			chessPoints[8][3].setPiece(黑炮2, this);
			黑炮2.setPosition(new Point(8, 3));
			chessPoints[1][4].setPiece(黑卒1, this);
			黑卒1.setPosition(new Point(1, 4));
			chessPoints[3][4].setPiece(黑卒2, this);
			黑卒2.setPosition(new Point(3, 4));
			chessPoints[5][4].setPiece(黑卒3, this);
			黑卒3.setPosition(new Point(5, 4));
			chessPoints[7][4].setPiece(黑卒4, this);
			黑卒4.setPosition(new Point(7, 4));
			chessPoints[9][4].setPiece(黑卒5, this);
			黑卒5.setPosition(new Point(9, 4));

		} else {//
			playerName = BLACK_NAME;
			if (gameStatusContent != null) {
				gameStatusContent.setText("   " + "等待红方走棋哟！");
			}

			chessPoints[1][10].setPiece(黑車1, this);
			黑車1.setPosition(new Point(1, 10));
			chessPoints[2][10].setPiece(黑馬1, this);
			黑馬1.setPosition(new Point(2, 10));
			chessPoints[3][10].setPiece(黑象1, this);
			黑象1.setPosition(new Point(3, 10));
			chessPoints[4][10].setPiece(黑士1, this);
			黑士1.setPosition(new Point(4, 10));

			chessPoints[5][10].setPiece(黑將, this);
			黑將.setPosition(new Point(5, 10));

			chessPoints[6][10].setPiece(黑士2, this);
			黑士2.setPosition(new Point(6, 10));
			chessPoints[7][10].setPiece(黑象2, this);
			黑象2.setPosition(new Point(7, 10));
			chessPoints[8][10].setPiece(黑馬2, this);
			黑馬2.setPosition(new Point(8, 10));
			chessPoints[9][10].setPiece(黑車2, this);
			黑車2.setPosition(new Point(9, 10));
			chessPoints[2][8].setPiece(黑炮1, this);
			黑炮1.setPosition(new Point(2, 8));
			chessPoints[8][8].setPiece(黑炮2, this);
			黑炮2.setPosition(new Point(8, 8));
			chessPoints[1][7].setPiece(黑卒1, this);
			黑卒1.setPosition(new Point(1, 7));
			chessPoints[3][7].setPiece(黑卒2, this);
			黑卒2.setPosition(new Point(3, 7));
			chessPoints[5][7].setPiece(黑卒3, this);
			黑卒3.setPosition(new Point(5, 7));
			chessPoints[7][7].setPiece(黑卒4, this);
			黑卒4.setPosition(new Point(7, 7));
			chessPoints[9][7].setPiece(黑卒5, this);
			黑卒5.setPosition(new Point(9, 7));

			chessPoints[1][1].setPiece(红車1, this);
			红車1.setPosition(new Point(1, 1));
			chessPoints[2][1].setPiece(红馬1, this);
			红馬1.setPosition(new Point(2, 1));
			chessPoints[3][1].setPiece(红相1, this);
			红相1.setPosition(new Point(3, 1));
			chessPoints[4][1].setPiece(红仕1, this);
			红仕1.setPosition(new Point(4, 1));
			chessPoints[5][1].setPiece(红帥, this);
			红帥.setPosition(new Point(5, 1));
			chessPoints[6][1].setPiece(红仕2, this);
			红仕2.setPosition(new Point(6, 1));
			chessPoints[7][1].setPiece(红相2, this);
			红相2.setPosition(new Point(7, 1));
			chessPoints[8][1].setPiece(红馬2, this);
			红馬2.setPosition(new Point(8, 1));
			chessPoints[9][1].setPiece(红車2, this);
			红車2.setPosition(new Point(9, 1));
			chessPoints[2][3].setPiece(红炮1, this);
			红炮1.setPosition(new Point(2, 3));
			chessPoints[8][3].setPiece(红炮2, this);
			红炮2.setPosition(new Point(8, 3));
			chessPoints[1][4].setPiece(红兵1, this);
			红兵1.setPosition(new Point(1, 4));
			chessPoints[3][4].setPiece(红兵2, this);
			红兵2.setPosition(new Point(3, 4));
			chessPoints[5][4].setPiece(红兵3, this);
			红兵3.setPosition(new Point(5, 4));
			chessPoints[7][4].setPiece(红兵4, this);
			红兵4.setPosition(new Point(7, 4));
			chessPoints[9][4].setPiece(红兵5, this);
			红兵5.setPosition(new Point(9, 4));
		}

	}

	/**
	 * 绘制棋盘
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		// 显示横坐标
		for (int i = 1; i <= X; i++) {
			g2.drawString("" + i, i * UNIT_WIDTH - 4, UNIT_HEIGHT / 2 - 4);
		}

		for (int i = 1; i <= X; i++) {
			g2.drawString("" + ChessUtil.numToZi(10 - i), i * UNIT_WIDTH - 4,
					10 * UNIT_HEIGHT + 34);
		}

	}

	private void visibleOrNot() {
		if (winkPiece != null) {
			winkPiece.setVisible(true);
		}

	}

	/**
	 * 玩家名交换
	 * 
	 */
	public void reverseName() {
		if (playerName.equals(RED_NAME)) {
			playerName = BLACK_NAME;
			horseMazePath.updateGameStatus(2, "轮到黑方走喽！");

		} else {
			playerName = RED_NAME;
			horseMazePath.updateGameStatus(1, "轮到红方走喽！");
		}
	}

	private class HorseMazeMouseAdapter extends MouseAdapter {
		HorseMazeBoard board;

		public HorseMazeMouseAdapter(HorseMazeBoard board) {
			this.board = board;
		}

		/**
		 * 鼠标移动到棋盘或棋子上面
		 */
		public void mouseEntered(MouseEvent e) {
			ChessPiece piece = null;
			// 轮到己方走棋，移动到己方棋子上面
			if (e.getSource() instanceof ChessPiece) {
				piece = (ChessPiece) e.getSource();
				if (piece.getName().equals(playerName)) {
					piece.setCursor(handCursor);
				} else {
					piece.setCursor(defaultCursor);
				}
			}

		}

		/**
		 * 按下鼠标
		 */
		public void mousePressed(MouseEvent e) {
			// 第一次选中棋子
			if (!isSelected) {
				ChessPiece piece = null;
				Rectangle rect = null;

				// 第一次点击了棋盘，忽略不计
				if (e.getSource() == this) {
					isSelected = false;
					System.out.println("第一次没有选中");
				}

				// 第一次点击了棋子
				if (e.getSource() instanceof ChessPiece) {
					piece = (ChessPiece) e.getSource();
					// 第一次点击本方的棋子
					if (piece.getName().equals(playerName)) {
						isSelected = true;

						System.out.println("第一次选中" + playerName
								+ piece.getCategory());

						rect = piece.getBounds();
						for (int i = 1; i <= X; i++) {
							for (int j = 1; j <= Y; j++) {
								int x = chessPoints[i][j].getX();
								int y = chessPoints[i][j].getY();
								if (rect.contains(x, y)) {
									// 保存棋子的起始坐标
									visibleOrNot();

									winkPiece = piece;
									needWink = true;

									if (winkThread == null) {
										winkThread = new Thread(board);
										winkThread.start();
									}

									startI = i;
									startJ = j;
									board.initTipPoints(piece, startI, startJ);
									break;
								}

							}
						}
					} else {
						System.out.println("第一次点击了对方的棋子，无效！");
					}
				}
			}

			// 第二次点击
			else {
				boolean rule = false;
				ChessPiece pieceRemoved = null;
				// 修改图标为默认图标
				setCursor(defaultCursor);

				int m = 0, n = 0;

				// 第二次点击了棋盘
				if (e.getSource() == board) {
					// 绝对坐标转化为gamePoints[][]的坐标
					double x1 = e.getX();
					double y1 = e.getY();
					System.out.println("x1=" + x1 + "y1=" + y1);
					for (int i = 1; i <= X; i++) {
						for (int j = 1; j <= Y; j++) {
							double x0 = chessPoints[i][j].getX();
							double y0 = chessPoints[i][j].getY();

							if ((Math.abs(x0 - x1) <= PIECE_WIDTH / 2)
									&& (Math.abs(y0 - y1) <= PIECE_HEIGHT / 2)) {
								// 终点坐标
								m = i;
								n = j;
								break;
							}
						}
					}
					System.out.println("第2次点击时(" + m + "," + n + ")");

					if (m == 0 || n == 0) {
						System.out.println("第2次点击时,没有点击到棋盘范围内！");
						return;
					}

					// 打谱时棋子移动规则与正常对战时有所不同

					System.out.println("打谱移动规则1");
					rule = ChessRule.allRule(winkPiece, startI, startJ, m, n,
							chessPoints);

					if (rule) {
						winkPiece.setPosition(new Point(m, n));
						(chessPoints[startI][startJ]).setHasPiece(false);

						chessPoints[m][n].setPiece(winkPiece, board);
						System.out.println("没有吃子，然后查看棋子：");
						// ChessRule.printPieceLoations(chessPoints);
						isSelected = false;

					} else {
						System.out.println("第2次点击时,不符合棋子移动规则！");
						return;
					}
				}

				// 第二次点击了棋子
				else if (e.getSource() instanceof ChessPiece) {
					ChessPiece piece2 = (ChessPiece) e.getSource();

					Rectangle rect2 = piece2.getBounds();
					for (int i = 1; i <= X; i++) {
						for (int j = 1; j <= Y; j++) {
							int x = chessPoints[i][j].getX();
							int y = chessPoints[i][j].getY();
							if (rect2.contains(x, y)) {
								// 保存棋子的起始坐标
								m = i;
								n = j;
								break;
							}
						}
					}
					System.out.println("第二次点击棋子时，终点坐标为m=" + m + "n=" + n);
					// 第二次点击了本方棋子
					if (piece2.getName().equals(playerName)) {
						needWink = true;

						visibleOrNot();
						winkPiece = piece2;
						startI = m;
						startJ = n;
						isSelected = true;

						// 清除上次的提示标记
						tipPoints.clear();
						validate();
						board.initTipPoints(piece2, startI, startJ);
						System.out.println("选中了" + playerName
								+ piece2.getCategory());
						return;
					}

					System.out.println("打谱移动规则2");
					rule = ChessRule.allRule(winkPiece, startI, startJ, m, n,
							chessPoints);
					System.out.println("全局打谱是否可以移动:" + rule);
					if (rule) {
						// 符合规则吃掉对方的棋子
						pieceRemoved = chessPoints[m][n].getPiece();
						winkPiece.setPosition(new Point(m, n));

						movePiece(winkPiece, startI, startJ, m, n);

						System.out.println("查看棋子位置22：");
						PieceUtil.printPieceLoations(chessPoints);
						isSelected = false;

					} else {
						System.out.println("第2次点击对方棋子时,不符合规则！");
						return;
					}

				}
				if (rule) {// 如果能够移动，应该画2条提示框

					needWink = false;
					winkPiece.setVisible(true);

					tipPoints.clear();// 清空提示标记
					/**
					 * 构造移动记录
					 */
					ManualItem record = new ManualItem();
					MoveStep moveStep = new MoveStep(new Point(startI, startJ),
							new Point(m, n));
					record.setMoveStep(moveStep);
					if (pieceRemoved != null) {
						record.setEatedPieceId(pieceRemoved.getId());
					} else {
						record.setEatedPieceId(null);
					}
					record.setMovePieceId(winkPiece.getId());
					chessManual.addManualItem(record);

					// 播放棋子被吃声音
					ChessUtil.playSound("eat.wav");

					validate();
					repaint();

					// 更换己方名字，实现一人轮流走红黑2方的棋子
					reverseName();

				}
			}
		}

	}

	@Override
	protected Color getBackgroundColor() {
		return ColorUtil.getHorseMazeBgcolor();
	}

	@Override
	protected BoardType getBoardType() {
		return BoardType.printWhole;
	}

	@Override
	protected MouseAdapter getMouseAdapter() {
		return new HorseMazeMouseAdapter(this);
	}

}
