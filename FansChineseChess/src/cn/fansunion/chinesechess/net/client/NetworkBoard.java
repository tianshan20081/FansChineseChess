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
package cn.fansunion.chinesechess.net.client;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.core.ChessBoard;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessRule;
import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.core.MoveStep;
import cn.fansunion.chinesechess.core.PieceUtil;
import cn.fansunion.chinesechess.core.ChessPiece.PieceId;
import cn.fansunion.chinesechess.net.common.MsgPacket;
import cn.fansunion.chinesechess.net.common.MsgPacket.MsgType;
import cn.fansunion.chinesechess.net.common.MsgPacket.PlayerRole;

/**
 * 联机对战用的 棋盘类
 * 
 * @author 雷文 2010/11/26修改棋子移动的方法 有拖动改为点击
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public class NetworkBoard extends ChessBoard implements Runnable {

	private static final long serialVersionUID = 1L;

	public JLabel gameStatusContent;

	// 游戏是否就绪
	// public int running = RUNNING;

	// 我是否暂停了游戏
	public boolean isPause = false;

	// 对方是否暂停了游戏
	public boolean otherIsPause = false;

	public boolean myTurn = false;// 是否轮到己方走棋

	public MatchGUI client;// 多人联机对战时，棋盘的拥有者

	public String userName;// 玩家的名字

	public static String SPACE = "    ";

	public NetworkBoard() {
	}

	/**
	 * 初始化红方、蓝方棋子
	 * 
	 * @param isRed
	 *            红方还是蓝方
	 * 
	 */
	public void initChess(boolean isRed) {

		// 红方在下方和黑方在下方的主要区别时：坐标不同
		if (isRed) {
			// 红方先走棋
			playerName = RED_NAME;

			myTurn = true;
			if (gameStatusContent != null) {
				gameStatusContent.setText(SPACE + "红方先走棋哟！");
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
				gameStatusContent.setText(SPACE + "等待红方走棋哟！");
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

	private void visibleOrNot() {
		if (winkPiece != null) {
			winkPiece.setVisible(true);
		}

	}

	/**
	 * 玩家之间的坐标需要转换，关于中心线对称，x不变，y之和为11
	 * 
	 * @param packet
	 *            收到的数据包
	 */
	// 获取另一个玩家的移动,并修改界面来实现同步
	public void handleMoveMessage(MsgPacket sp) {
		// 播放棋子被吃声音
		ChessUtil.playSound("eat.wav");

		System.out.println("正在处理移动消息");
		// DataPacket packet = sp.getDataPacket();
		ManualItem chessRecord = sp.chessRecord;
		MoveStep moveStep = chessRecord.getMoveStep();
		int startX, startY, endX, endY;

		// 默认需要转换坐标
		startX = (int) moveStep.getStart().getX();
		startY = 11 - (int) moveStep.getStart().getY();
		endX = (int) moveStep.getEnd().getX();
		endY = 11 - (int) moveStep.getEnd().getY();

		System.out.println("角色：" + client.parent.role);
		// 创建者或玩家，才能走棋,并且需要转换坐标
		if (client.parent.role == PlayerRole.ROLE_RED
				|| client.parent.role == PlayerRole.ROLE_BLACK) {
			myTurn = true;
			// 创建者或玩家收到对方的移动消息，则轮到自己走棋
			if (playerName.equals(RED_NAME)) {
				client.updateGameStatus(RED_TURN_GAME_STATUS, "轮到红方走棋喽！");
			} else if (playerName.equals(BLACK_NAME)) {
				client.updateGameStatus(BLACK_TURN_GAME_STATUS, "轮到黑方走棋喽！");
			}

		} else if (client.parent.role == PlayerRole.ROLE_OBSERVER
				|| client.parent.role == PlayerRole.ROLE_JUDGMENT) {
			if (sp.getRole() == PlayerRole.ROLE_RED) {
				// 只有观察者或裁判接收到创建者的移动消息时才不转换坐标
				startX = (int) moveStep.getStart().getX();
				startY = (int) moveStep.getStart().getY();
				endX = (int) moveStep.getEnd().getX();
				endY = (int) moveStep.getEnd().getY();

				// 观察者不能走棋
				String playerName = sp.getPlayerName();
				if (playerName.equals(RED_NAME)) {// 红方发送的移动消息
					client.updateGameStatus(BLACK_TURN_GAME_STATUS, "轮到黑方走棋喽！");
				} else if (playerName.equals(BLACK_NAME)) {
					client.updateGameStatus(RED_TURN_GAME_STATUS, "轮到红方走棋喽！");
				}
			}
		}

		// 能够移动消息，应该画2条提示框
		moveFlag = true;
		movePoints[0] = new Point((int) (chessPoints[startX][startY].getX()),
				(int) (chessPoints[startX][startY].getY()));
		movePoints[1] = new Point((int) (chessPoints[endX][endY].getX()),
				(int) (chessPoints[endX][endY].getY()));

		movePoints[0] = new Point(startX, startY);
		movePoints[1] = new Point(endX, endY);

		// 根据棋子id获取移动的棋子
		ChessPiece piece = PieceUtil.searchPieceById(
				chessRecord.getMovePieceId(), chessPoints);

		PieceId delPieceId = chessRecord.getEatedPieceId();
		ManualItem record = new ManualItem();
		moveStep.setStart(new Point(startX, startY));
		moveStep.setEnd(new Point(endX, endY));
		record.setMoveStep(moveStep);
		record.setMovePieceId(piece.getId());

		// 有棋子被删除
		if (delPieceId != null) {
			this.remove(PieceUtil.searchPieceById(delPieceId, chessPoints));
		}

		record.setEatedPieceId(delPieceId);

		chessPoints[endX][endY].setPiece(piece, this);
		piece.setPosition(new Point(endX, endY));
		chessPoints[startX][startY].setHasPiece(false);

		chessManual.addManualItem(record);
		// 判断对方是否将军
		boolean flag = playerName.equals(RED_NAME);
		if (ChessRule.isDangerous(flag, playerName, chessPoints)) {
			gameStatusContent.setText(SPACE + gameStatusContent.getText()
					+ ("对方将军啦！"));
			ChessUtil.playSound("jiangjun.wav");
		}

		// 更新
		validate();
		repaint();
	}

	class BoardMouseAdapter extends MouseAdapter {
		NetworkBoard board;

		/**
		 * @param board
		 */
		public BoardMouseAdapter(NetworkBoard board) {
			this.board = board;
		}

		/**
		 * 鼠标移动到棋子上面
		 * 
		 * @param e
		 */
		public void mouseEntered(MouseEvent e) {
			ChessPiece piece = null;
			boolean flag = false;

			if (e.getSource() instanceof ChessPiece) {
				piece = (ChessPiece) e.getSource();
				if (myTurn && piece.getName().equals(playerName)) {
					flag = true;
				}
			}
			// 双方都没有暂停游戏
			flag = flag && (!isPause) && (!otherIsPause);
			if (flag) {
				board.setCursor(handCursor);
			} else {
				board.setCursor(defaultCursor);
			}
		}

		public void mousePressed(MouseEvent e) {
			// 没有轮到自己走，有暂停，不处理
			if ((!myTurn) || (isPause) || (otherIsPause)) {
				return;
			}

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
						ChessUtil.playSound("select.wav");
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
									break;
								}

							}
						}
					}
				} else {
					System.out.println("第一次点击了对方的棋子，无效！");
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

					rule = ChessRule.networkRule(winkPiece, startI, startJ, m,
							n, chessPoints);

					if (rule) {
						winkPiece.setPosition(new Point(m, n));
						movePiece(winkPiece, startI, startJ, m, n);

						isSelected = false;
						myTurn = false;
					} else {
						System.out.println("第2次点击时,不符合棋子移动规则！");
						return;
					}
				}

				// 第二次点击了棋子
				else if (e.getSource() instanceof ChessPiece) {
					ChessPiece piece = (ChessPiece) e.getSource();

					Rectangle rect = piece.getBounds();
					for (int i = 1; i <= X; i++) {
						for (int j = 1; j <= Y; j++) {
							int x = chessPoints[i][j].getX();
							int y = chessPoints[i][j].getY();
							if (rect.contains(x, y)) {
								// 保存棋子的起始坐标
								m = i;
								n = j;
								break;
							}
						}
					}
					System.out.println("第二次点击棋子时，终点坐标为m=" + m + "n=" + n);
					// 第二次点击了本方棋子
					if (piece.getName().equals(playerName)) {
						needWink = true;

						visibleOrNot();
						winkPiece = piece;
						startI = m;
						startJ = n;
						isSelected = true;
						ChessUtil.playSound("select.wav");

						System.out.println("选中了" + playerName
								+ piece.getCategory());

					}

					// 第二次点击了对方棋子
					else {

						rule = ChessRule.networkRule(winkPiece, startI, startJ,
								m, n, chessPoints);

						if (rule) {
							// 符合规则吃掉对方的棋子
							pieceRemoved = chessPoints[m][n].getPiece();
							winkPiece.setPosition(new Point(m, n));

							board.movePiece(winkPiece, startI, startJ, m, n);

							isSelected = false;
							myTurn = false;
						} else {
							System.out.println("第2次点击对方棋子时,不符合规则！");
							return;
						}
					}

				}

				if (rule) {// 如果能够移动，应该画2条提示框
					/*
					 * moveFlag = true; movePoints[0] = new
					 * Point(startI,startJ); movePoints[1] = new Point( m,n);
					 */

					needWink = false;
					if (winkPiece != null) {
						winkPiece.setVisible(true);
					}
					validate();
					repaint();

					/**
					 * 构造移动记录，存储并发送给其他玩家
					 */
					ManualItem chessRecord = new ManualItem();
					MoveStep moveStep = new MoveStep(new Point(startI, startJ),
							new Point(m, n));
					chessRecord.setMoveStep(moveStep);
					if (pieceRemoved != null) {
						chessRecord.setEatedPieceId(pieceRemoved.getId());
					} else {
						chessRecord.setEatedPieceId(null);
					}
					chessRecord.setMovePieceId(winkPiece.getId());
					chessManual.addManualItem(chessRecord);

					// 播放棋子被吃声音
					ChessUtil.playSound("eat.wav");

					MsgPacket sp = new MsgPacket();
					sp.chessRecord = chessRecord;
					sp.setMsgType(MsgType.PIECE_MOVING);
					sp.setName(userName);
					sp.setRole(client.parent.role);
					sp.setPlayerName(playerName);

					myTurn = false;

					if (playerName.equals(RED_NAME)) {
						client.updateGameStatus(BLACK_TURN_GAME_STATUS,
								"轮到黑方走棋喽！");
					} else if (playerName.equals(BLACK_NAME)) {
						client.updateGameStatus(RED_TURN_GAME_STATUS,
								"轮到红方走棋喽！");
					}

					client.sendPacket(sp);

				}
			}
		}

	}

	@Override
	protected void drawShuXianFlag(Graphics2D g2) {
		if (playerName.equals(RED_NAME)) {
			for (int i = 1; i <= X; i++) {
				g2.drawString("" + i, i * UNIT_WIDTH - 4, UNIT_HEIGHT / 2 - 4);
			}

			for (int i = 1; i <= X; i++) {
				g2.drawString("" + ChessUtil.numToZi(10 - i), i * UNIT_WIDTH
						- 4, 10 * UNIT_HEIGHT + 34);
			}
		} else if (playerName.equals(BLACK_NAME)) {
			for (int i = 1; i <= X; i++) {
				g2.drawString("" + i, i * UNIT_WIDTH - 4, 10 * UNIT_HEIGHT + 34);
			}

			for (int i = 1; i <= X; i++) {
				g2.drawString("" + ChessUtil.numToZi(10 - i), i * UNIT_WIDTH
						- 4, UNIT_HEIGHT / 2 - 4);
			}

		}
	}

	@Override
	protected MouseAdapter getMouseAdapter() {
		return new BoardMouseAdapter(this);
	}

	@Override
	protected BoardType getBoardType() {
		return BoardType.network;
	}

}
