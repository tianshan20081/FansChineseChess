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
package cn.fansunion.chinesechess.print.part;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.ColorUtil;
import cn.fansunion.chinesechess.core.ChessBoard;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessRule;
import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.core.MoveStep;

/**
 * 棋盘类
 * 
 * @author 雷文 2010/11/26修改棋子移动的方法 有拖动改为点击
 * @since 2.0
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 */
public class PrintPartBoard extends ChessBoard {

	private static final long serialVersionUID = 1L;

	public JList manualList;

	public PrintPartGUI boardOwner;// 打谱时，棋盘的拥有者

	// 闪烁的棋子所在的位置
	private boolean winkPieceAtBoard = false;

	public PrintPartBoard(PrintPartGUI owner) {
		super();
		boardOwner = owner;
		playerName = RED_NAME;// 初始化时，红方
		manualList = chessManual.manualList;

	}

	/**
	 * 
	 * 鼠标适配器
	 */
	private class PartialMouseAdapter extends MouseAdapter {
		PrintPartBoard board;

		public PartialMouseAdapter(PrintPartBoard board) {
			this.board = board;
		}

		/**
		 * 鼠标移动到棋子上面
		 */

		public void mouseEntered(MouseEvent e) {
			if (e.getSource() instanceof ChessPiece) {
				ChessPiece piece = (ChessPiece) e.getSource();
				Point point = isPieceAtBoard(piece);

				if (boardOwner.isLock) {
					// 棋局已经锁定,棋盘中的本方棋子手形鼠标
					if (point != null && piece.getName().equals(playerName)) {
						piece.setCursor(handCursor);
					} else {
						piece.setCursor(defaultCursor);
					}

				} else {
					// 棋局没有锁定,所有棋子都可以选中,手形鼠标
					piece.setCursor(handCursor);
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			// 玩家点击棋子时，当然是为了移动棋子

			boardOwner.last();
			if (!isSelected) {
				ChessPiece piece = null;

				// 第一次点击了棋盘，忽略不计
				if (e.getSource() == board) {
					isSelected = false;
					System.out.println("第一次点击了棋盘！忽略！");

				}

				// 第一次点击了棋子
				if (e.getSource() instanceof ChessPiece) {
					piece = (ChessPiece) e.getSource();

					// 必须每次都初始化
					winkPieceAtBoard = false;
					// 判断棋子是在棋盘中还是备用棋子面板中
					Point point = isPieceAtBoard(piece);
					if (point != null) {
						winkPieceAtBoard = true;
						startI = (int) point.getX();
						startJ = (int) point.getY();
					}

					if (winkPieceAtBoard) {

						if (!boardOwner.isLock) {
							System.out.println(piece.getCategory()
									+ "在备用棋子面板中！");
							System.out.println("(" + startI + "," + startJ
									+ ")");
							System.out.println("第一次选中" + piece.getCategory());

							isSelected = true;
							ChessUtil.playSound("select.wav");

							winkPiece = piece;
							needWink = true;
						} else {
							if (piece.getName().equals(playerName)) {
								isSelected = true;
								ChessUtil.playSound("select.wav");

								winkPiece = piece;
								needWink = true;
							} else {
								isSelected = false;
								return;
							}
						}

					} else {
						// 锁定棋局点击备用棋子无效
						if (!boardOwner.isLock) {
							startI = 0;
							startJ = 0;
							isSelected = true;
							ChessUtil.playSound("select.wav");

							winkPiece = piece;
							needWink = true;

						} else {
							return;
						}
					}

				}
			}

			// 第二次点击
			else {
				boolean canMove = true;
				ChessPiece pieceRemoved = null;

				int endI = 0, endJ = 0;

				// 第二次点击了棋盘
				if (e.getSource() == board) {
					// 绝对坐标转化为gamePoints[][]的坐标
					double x1 = e.getX();
					double y1 = e.getY();
					for (int i = 1; i <= X; i++) {
						for (int j = 1; j <= Y; j++) {
							double x0 = chessPoints[i][j].getX();
							double y0 = chessPoints[i][j].getY();

							if ((Math.abs(x0 - x1) <= PIECE_WIDTH / 2)
									&& (Math.abs(y0 - y1) <= PIECE_HEIGHT / 2)) {
								// 终点坐标
								endI = i;
								endJ = j;
								break;
							}
						}
					}
					System.out.println("第2次点击棋盘时,终点坐标为：(" + endI + "," + endJ
							+ ")");

					// 没有点击在棋盘范围内，删除棋子
					if (endI == 0 || endJ == 0) {
						if (boardOwner.isLock) {
							return;
						}
						System.out.println("第2次点击时,没有点击到棋盘范围内！");
						board.remove(winkPiece);
						boardOwner.piecesPanel.add(winkPiece);
						needWink = false;
						winkPiece.setVisible(true);
						isSelected = false;
						if (winkPieceAtBoard) {
							chessPoints[startI][startJ].setHasPiece(false);
						}

					}
					// 第二次点击在棋盘范围内
					else {

						// 打谱时棋子移动规则与正常对战时有所不同
						canMove = ChessRule.partialRule(winkPiece, startI,
								startJ, endI, endJ, chessPoints);

						if (canMove) {
							winkPiece.setPosition(new Point(endI, endJ));
							if (winkPieceAtBoard) {
								chessPoints[startI][startJ].setHasPiece(false);
							}

							chessPoints[endI][endJ].setPiece(winkPiece, board);

							needWink = false;
							winkPiece.setVisible(true);
							isSelected = false;
							boardOwner.piecesPanel.remove(winkPiece);
							ChessUtil.playSound("eat.wav");

							// 如果棋局已经锁定，需要构造移动记录，生成棋谱，初始化移动点
							if (boardOwner.isLock) {
								moveFlag = true;
								movePoints[0] = new Point(startI, startJ);
								movePoints[1] = new Point(endI, endJ);

								ManualItem moveRecord = new ManualItem();
								moveRecord.setMovePieceId(winkPiece.getId());
								moveRecord.setEatedPieceId(null);
								moveRecord
										.setMoveStep(new MoveStep(new Point(
												startI, startJ), new Point(
												endI, endJ)));
								chessManual.addManualItem(moveRecord);

								// 修改当前索引
								boardOwner.curIndex++;
								changeSide();

							}

						} else {
							System.out.println("第2次点击时,不符合棋子移动规则！");

						}
					}
				}

				// 第二次点击了棋子
				else if (e.getSource() instanceof ChessPiece) {
					ChessPiece piece = (ChessPiece) e.getSource();

					boolean flag = false;// 第二次点击的对方棋子是否在棋盘中
					for (int i = 1; i <= 9; i++) {
						for (int j = 1; j <= 10; j++) {
							ChessPiece tempPiece = chessPoints[i][j].getPiece();
							if (chessPoints[i][j].hasPiece()
									&& tempPiece.equals(piece)) {
								endI = i;
								endJ = j;
								System.out.println(piece.getCategory()
										+ "在棋盘中！");
								flag = true;
								break;
							}
						}
					}

					// 第2次点击了备用栏中的棋子
					if (!flag) {
						if (!boardOwner.isLock) {
							startI = 0;
							startJ = 0;
							System.out.println("第二次点击了备用棋子栏中的！"
									+ piece.getCategory());
							winkPiece.setVisible(true);
							winkPiece = piece;
							needWink = true;
							isSelected = true;
							winkPieceAtBoard = false;
						} else {
							return;
						}
					} else {
						System.out.println("第二次点击了棋盘中的！" + piece.getCategory());
						System.out.println("第二次点击棋子时，终点坐标为m=" + endI + "n="
								+ endJ);

						// 第二次点击了棋盘中同一方的棋子
						if (piece.getName().equals(winkPiece.getName())) {
							winkPiece.setVisible(true);
							winkPiece = piece;
							needWink = true;
							startI = endI;
							startJ = endJ;
							isSelected = true;
							winkPieceAtBoard = true;
							System.out.println("第二次点击了棋盘中同一方的棋子,选中了"
									+ piece.getCategory());
						}

						// 第二次点击了棋盘中的对方棋子
						else {
							// 打谱时棋子移动规则与正常对战时有所不同
							System.out.println("第二次点击对方棋子时，终点坐标为m=" + endI
									+ "n=" + endJ);

							pieceRemoved = chessPoints[endI][endJ].getPiece();

							// 第一个棋子在棋盘中
							if (winkPieceAtBoard) {
								canMove = ChessRule
										.partialRule(winkPiece, startI, startJ,
												endI, endJ, chessPoints);
								if (canMove) {
									// 如果棋局已经锁定，需要构造移动记录，生成棋谱，初始化移动点
									if (boardOwner.isLock) {
										board.addChessRecord(winkPiece, startI,
												startJ, endI, endJ);
										boardOwner.curIndex++;
										changeSide();

									}

									// 棋子在棋盘中移动
									movePiece(winkPiece, startI, startJ, endI,
											endJ);

									boardOwner.piecesPanel.add(pieceRemoved);
									needWink = false;
									winkPiece.setVisible(true);
									isSelected = false;
									ChessUtil.playSound("eat.wav");

								}
							}

							// 第一个棋子在备用栏中
							else {
								canMove = ChessRule
										.partialRule(winkPiece, startI, startJ,
												endI, endJ, chessPoints);
								if (canMove) {
									board.remove(pieceRemoved);

									chessPoints[endI][endJ].setPiece(winkPiece,
											board);
									chessPoints[endI][endJ].setHasPiece(true);

									boardOwner.piecesPanel.add(pieceRemoved);
									needWink = false;
									winkPiece.setVisible(true);
									isSelected = false;
									ChessUtil.playSound("eat.wav");
								}
							}

						}
					}

				}

			}
			setCursor(defaultCursor);
			validateAll();
		}
	}

	/**
	 * 红黑方交替
	 * 
	 */
	public void changeSide() {
		if (playerName.equals(RED_NAME)) {
			playerName = BLACK_NAME;
		} else if (playerName.equals(BLACK_NAME)) {
			playerName = RED_NAME;
		}
	}

	public void validateAll() {
		System.out.println("刷新！");
		validate();
		repaint();
		boardOwner.validate();
	}

	/**
	 * 判断一个棋子是否在棋盘中,
	 * 
	 * @param piece
	 *            棋子
	 * @return 如果在返回棋子所在的坐标，否则返回null
	 */
	private Point isPieceAtBoard(ChessPiece piece) {
		for (int r = 1; r <= 9; r++) {
			for (int s = 1; s <= 10; s++) {
				ChessPiece p = chessPoints[r][s].getPiece();
				if (p != null && chessPoints[r][s].hasPiece()
						&& p.equals(piece)) {

					return new Point(r, s);
				}
			}
		}
		return null;
	}

	@Override
	protected Color getBackgroundColor() {
		return ColorUtil.getDefaultBgcolor();
	}

	@Override
	protected MouseAdapter getMouseAdapter() {
		return new PartialMouseAdapter(this);
	}

	@Override
	protected BoardType getBoardType() {
		return BoardType.printPartial;
	}

}
