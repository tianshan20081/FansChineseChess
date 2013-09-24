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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.core.ChessPiece.PieceCategory;
import cn.fansunion.chinesechess.net.client.NetworkBoard;
import cn.fansunion.chinesechess.print.part.PrintPartBoard;


/**
 * 棋谱类
 * 
 * 职责：保存棋谱、删除棋谱、获取棋谱、设置棋谱
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public class ChessManual extends JPanel implements NAME {

	private static final long serialVersionUID = 1L;

	public JList manualList = null;// 棋谱列表

	public JScrollPane manualScroll = null;// 棋谱滚动条

	private ChessBoard board = null;// 棋盘

	private ChessPoint[][] chessPoints;// 棋子点二维数组

	private ArrayList<ManualItem> manualItems;// 移动记录

	public Vector<String> descs;// 棋谱对应的文字描述

	/**
	 * 构造函数，初始化界面
	 */
	public ChessManual(ChessBoard board) {
		this.board = board;
		this.chessPoints = board.getChessPoints();
		manualItems = new ArrayList<ManualItem>();
		descs = new Vector<String>();

		manualList = new JList();
		manualList.setFont(new Font("宋体", Font.PLAIN, 14));
		manualList.setToolTipText(PropertyReader.get("CHESS_MESSAGE_TOOLTIP"));
		// manual.setPreferredSize(new Dimension(160,180));
		manualList.setVisibleRowCount(12);
		manualList.setAutoscrolls(true);

		manualScroll = new JScrollPane(manualList);
		// manualScroll.setPreferredSize(new Dimension(200,210));
		setLayout(new BorderLayout());
		setSize(220, 260);
		add(manualScroll, BorderLayout.CENTER);
	}

	/**
	 * 记录移动棋子和被吃棋子的信息，以及移动信息
	 */
	public void addManualItem(ManualItem manualItem) {

		manualItems.add(manualItem);

		System.out.println("移动棋子的id：" + manualItem.getMovePieceId());
		ChessPiece piece = PieceUtil.searchPieceById(manualItem
				.getMovePieceId(), chessPoints);
		MoveStep moveStep = manualItem.getMoveStep();
		Point pStart = moveStep.getStart();
		Point pEnd = moveStep.getEnd();

		String name = piece.getName();
		PieceCategory category = piece.getCategory();
		int index = 1;
		if (name.equals(RED_NAME)) {
			index = manualItems.size() / 2 + 1;
		} else {
			index = manualItems.size() / 2;
		}

		// 只有高级打谱，黑方先走时，才需要修改
		if (board instanceof PrintPartBoard) {
			PrintPartBoard board2 = (PrintPartBoard) (board);
			if (!board2.boardOwner.redFirst) {
				if (name.equals(BLACK_NAME)) {
					index = manualItems.size() / 2 + 1;
				} else {
					index = manualItems.size() / 2;
				}
			}
		}

		int startX = (int) pStart.getX();
		int startY = (int) pStart.getY();
		int endX = (int) pEnd.getX();
		int endY = (int) pEnd.getY();
		String desc = name;
		// 棋谱描述4个字要对齐的，不能因为索引而没有对齐
		if (index < 10) {
			desc += "  " + index + ": ";
		} else {
			desc += " " + index + ": ";
		}

		// 第1个字
		desc += PieceUtil.catetoryToZi(category);

		// 第2个字
		if (name.equals(RED_NAME)) {
			desc += ChessUtil.numToZi(10 - startX);
		} else {
			desc += (" " + startX);
		}
		// 前2个字的特殊情况，前車进3、后炮退2等
		for (int j = 1; j <= 10; j++) {
			if (j == startY || j == endY) {
				continue;
			}

			if (chessPoints[startX][j].hasPiece()) {
				ChessPiece tempPiece = chessPoints[startX][j].getPiece();

				if (tempPiece.getCategory().equals(category)
						&& tempPiece.getName().equals(name)) {
					String category2 = PieceUtil.catetoryToZi(category);
					if (index < 10) {
						desc = name + "  " + index + ": ";
					} else {
						desc = name + " " + index + ": ";
					}

					if ((board instanceof NetworkBoard)) {
						if (board.getPlayerName().equals(name)) {
							if (startY > j) {
								desc += "后" + category2;
							} else if (startY < j) {
								desc += "前" + category2;
							}
						} else {
							if (startY > j) {
								desc += "前" + category2;
							} else if (startY < j) {
								desc += "后" + category2;
							}
						}
					} else {
						if (board.getPlayerName().equals(RED_NAME)) {
							if (startY > j) {
								desc += "后" + category2;
							} else if (startY < j) {
								desc += "前" + category2;
							}
						} else if (board.getPlayerName().equals(BLACK_NAME)) {
							if (startY > j) {
								desc += "前" + category2;
							} else if (startY < j) {
								desc += "后" + category2;
							}
						}
					}
				}
			}

		}

		// 生成棋谱的差别主要体现在第3个字上
		if (board instanceof NetworkBoard) {
			// 第3个字
			if (board.getPlayerName().equals(name)) {
				if (startY == endY) {
					desc += "平";
				} else if (startY > endY) {
					desc += "进";
				} else {
					desc += "退";
				}
			} else {
				if (startY == endY) {
					desc += "平";
				} else if (startY > endY) {
					desc += "退";
				} else {
					desc += "进";
				}
			}
		} else {
			if (name.equals(RED_NAME)) {
				if (startY == endY) {
					desc += "平";
				} else if (startY > endY) {
					desc += "进";
				} else {
					desc += "退";
				}
			} else if (name.equals(BLACK_NAME)) {
				if (startY == endY) {
					desc += "平";
				} else if (startY > endY) {
					desc += "退";
				} else {
					desc += "进";
				}
			}
		}

		// 第4个字
		if (startX == endX) {
			if (name.equals(RED_NAME)) {
				desc += ChessUtil.numToZi(Math.abs(startY - endY));
			} else {
				desc += (" " + Math.abs(startY - endY));
			}
		} else {
			if (name.equals(RED_NAME)) {
				desc += ChessUtil.numToZi(10 - endX);
			} else {
				desc += (" " + endX);
			}
		}

		// 保存棋局描述
		descs.add(desc);
		manualList.setListData(descs);
		// 滚动到最下面
		scrollToView();
	}

	/**
	 * 获得所有的棋子移动记录
	 * 
	 * @return 返回棋子移动记录的集合
	 */
	public ArrayList<ManualItem> getManualItems() {
		return manualItems;
	}

	public void setManualItems(ArrayList<ManualItem> manualItems) {
		this.manualItems = manualItems;
	}

	/**
	 * 悔棋，画标记与正常移动时不同
	 * 
	 */
	public boolean removeManualItem() {

		int size = manualItems.size();

		if (size <= 0) {
			return false;
		}
		// 更新列表信息
		descs.remove(size - 1);
		manualList.setListData(descs);

		// 更新棋盘界面
		ManualItem record = new ManualItem();
		MoveStep moveStep;

		ChessPiece eatedPiece;
		int startI = 0;
		int startJ = 0;
		int endI = 0;
		int endJ = 0;

		if (size > 0) {
			// 删除并返回最后一个元素
			record = (ManualItem) manualItems.remove(manualItems.size() - 1);
			eatedPiece = PieceUtil.createPiece(record.getEatedPieceId());

			moveStep = record.getMoveStep();
			startI = moveStep.start.x;
			startJ = moveStep.start.y;
			endI = moveStep.end.x;
			endJ = moveStep.end.y;

			ChessPiece piece = chessPoints[endI][endJ].getPiece();
			chessPoints[startI][startJ].setPiece(piece, board);
			if (eatedPiece == null) { // 上一步没有吃棋子
				chessPoints[endI][endJ].setHasPiece(false);
			} else {// 上一步吃了棋子
				chessPoints[endI][endJ].setPiece(eatedPiece, board);
				eatedPiece.addMouseListener(board.getMouseAdapter());
			}
		}

		// 后退时，画标记与正常移动时不同
		if (descs.size() >= 1) {
			record = (ManualItem) manualItems.get(descs.size() - 1);
			moveStep = record.getMoveStep();
			startI = moveStep.start.x;
			startJ = moveStep.start.y;
			endI = moveStep.end.x;
			endJ = moveStep.end.y;
		}

		// 如果移动，应该画2条提示框
		Point start = new Point(endI, endJ);
		Point end = new Point(startI, startJ);

		if (descs.size() > 0) {
			board.setMoveFlag(true);
			board.setMoveFlagPoints(start, end);
		} else {
			board.setMoveFlag(false);
		}
		// 滚动
		scrollToView();

		board.validate();
		board.repaint();

		return true;
	}

	private void scrollToView() {
		int lastIndex = descs.size();
		// 选中最后一行，提示玩家
		manualList.setSelectedIndex(lastIndex - 1);

		Rectangle rect = manualList.getCellBounds(lastIndex - 1, lastIndex - 1);
		if (rect == null) {
			return;
		}
		System.out.println(rect == null);
		if ((manualScroll != null) && (manualScroll.getViewport() != null)
				&& (rect != null)) {
			// 如果rect==null，则出现空指针异常
			manualScroll.getViewport().scrollRectToVisible(rect);
		}
	}
}
