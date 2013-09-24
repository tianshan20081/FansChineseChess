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

import java.awt.Point;
import java.util.ArrayList;

import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessPoint;
import cn.fansunion.chinesechess.core.ChessRule;
import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.core.MoveStep;
import cn.fansunion.chinesechess.core.ChessPiece.PieceId;

/**
 * 人机对弈生成走法
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public final class AIUtil implements NAME, AIConstants {

	private AIUtil() {
	}

	/**
	 * 产生一个棋子的所有走法
	 * 
	 * @param piece
	 * @param startX
	 * @param startY
	 * @param chessPoints
	 * @return 一个棋子的所有走法
	 */
	public static ArrayList<Point> generateChessMoveByPiece(ChessPiece piece, int startX, int startY, ChessPoint[][] chessPoints) {
		ArrayList<Point> points = new ArrayList<Point>();
		/*
		 * 使用2重循环，代码最简介 如果为了提高效率，可以根据棋子的类别，分别编写相应的算法，工作量很大
		 */
		boolean rule = false;
		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 10; j++) {
				rule = ChessRule.allRule(piece, startX, startY, i, j, chessPoints);
				if (rule) {
					points.add(new Point(i, j));
				}
			}

		}
		return points;
	}

	/**
	 * 产生一个局面的全部走法
	 * 
	 * @param name
	 *            走棋一方，红方或黑方
	 * @param chessPoints
	 *            棋子点2维数组
	 * @return 一个局面的全部走法
	 */
	public static ArrayList<ManualItem> generateAllChessMove(String name, ChessPoint[][] chessPoints) {
		ArrayList<ManualItem> chessMoves = new ArrayList<ManualItem>();
		ArrayList<PiecePosition> lists = getPiecePositionByName(name, chessPoints);

		int size = lists.size();
		// System.out.println("棋子数：" + size);

		for (int index = 0; index < size; index++) {
			PiecePosition record = lists.get(index);
			ChessPiece piece = record.piece;
			Point pos = record.pos;
			/*
			 * System.out.print("棋子：" + piece.getId() + "(" + pos.getX() + "," +
			 * pos.getY() + ")");
			 */
			ArrayList<Point> points = AIUtil.generateChessMoveByPiece(piece, pos.x, pos.y, chessPoints);

			int size2 = points.size();
			// System.out.println("可行的走法有：" + size2 + "个");
			for (int j = 0; j < size2; j++) {
				MoveStep moveStep = new MoveStep(pos, points.get(j));
				ManualItem chessMove = new ManualItem();
				chessMove.setMoveStep(moveStep);
				chessMoves.add(chessMove);
			}
		}

		return chessMoves;
	}

	public static ArrayList<PiecePosition> getPiecePositionByName(String name, ChessPoint[][] chessPoints) {
		ArrayList<PiecePosition> lists = new ArrayList<PiecePosition>();

		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 10; j++) {
				ChessPiece piece = chessPoints[i][j].getPiece();
				if (piece != null && piece.getName().equals(name)) {
					PiecePosition record = new PiecePosition();
					record.piece = piece;
					record.pos = new Point(i, j);
					lists.add(record);
				}
			}
		}
		return lists;
	}

	/**
	 * 对当前局面进行估值
	 * 
	 * @param chessPoints
	 * @return 当前局面，红方相对黑方的优势
	 */
	public static int evaluate(String playerName, ChessPoint[][] chessPoints) {
		int wValue = 0;
		int bValue = 0;
		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 10; j++) {
				ChessPiece piece = chessPoints[i][j].getPiece();

				if (piece != null) {
					String color = piece.getName();
					int value = getPositionValue(piece, i, j);
					if (color.equals(RED_NAME)) {
						wValue += value;
					} else {
						bValue += value;
					}
				}
			}
		}
		int result = 0;
		if (playerName.equals(RED_NAME)) {
			result = wValue - bValue + getFlexibleValue(chessPoints);
		} else {
			result = bValue - wValue + getFlexibleValue(chessPoints);
		}
		return result;

	}

	/**
	 * 对当前局面进行估值
	 * 
	 * @param chessPoints
	 * @return 当前局面，红方相对黑方的优势
	 */
	public static int evaluate2(String playerName, ChessPoint[][] chessPoints) {
		int wValue = 0;
		int bValue = 0;
		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 10; j++) {
				ChessPiece piece = chessPoints[i][j].getPiece();

				if (piece != null) {
					String color = piece.getName();
					int pieceIdToIndex = pieceIdToIndex(piece.getId());
					if (color.equals(RED_NAME)) {

						wValue += pieceValues[pieceIdToIndex];
					} else {
						bValue += pieceIdToIndex;
					}
				}
			}
		}
		int result = 0;
		if (playerName.equals(RED_NAME)) {
			result = wValue - bValue;
		} else {
			result = bValue - wValue;
		}
		return result;

	}

	/**
	 * 將棋子的id转换成 棋子值（位置值、灵活性值）数组的索引
	 * 
	 * @param id
	 *            棋子的id
	 * @return 数组的索引
	 */
	public static int getPieceValue(PieceId id) {
		int index = pieceIdToIndex(id);
		if (index < 0 || index > 6) {
			return -1;
		}
		return pieceValues[index];
	}

	/**
	 * 根据棋子的位置，获取棋子的位置值
	 * 
	 * @param piece
	 * @param x
	 *            数学意义上的横坐标
	 * @param y
	 *            数学意义上的纵坐标
	 * @return 获取棋子的位置值
	 */
	public static int getPositionValue(ChessPiece piece, int x, int y) {
		String name = piece.getName();
		int redOrBlack = -1;
		if (name.equals(RED_NAME)) {
			redOrBlack = 0;
		} else {
			redOrBlack = 1;
		}
		int index = pieceIdToIndex(piece.getId());
		// System.out.println(redOrBlack+" "+index+" "+piece.getId()+"
		// getPositionValue"+"x="+x+"y="+y);
		// 此处需要注意，数学上的坐标和数组的坐标不一致
		return positionValues[redOrBlack][index][y][x];
	}

	/**
	 * 获取棋子的灵活性分值
	 * 
	 * @param chessPoints
	 * @return棋子的灵活性分值
	 */
	public static int getFlexibleValue(ChessPoint[][] chessPoints) {
		int redValue = 0;
		int blackValue = 0;
		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 9; j++) {
				ChessPiece piece = chessPoints[i][j].getPiece();
				if (piece != null) {
					// PieceCategory pc = piece.getCategory();
					String name = piece.getName();
					for (int m = 1; m <= 9; m++) {
						for (int n = 1; n <= 10; n++) {
							boolean rule = ChessRule.allRule(piece, i, j, m, n, chessPoints);
							if (rule) {
								int index = pieceIdToIndex(piece.getId());
								int value = flexibleValues[index];
								if (name.equals(RED_NAME)) {
									redValue += value;
								} else {
									blackValue += value;
								}
							}
						}
					}
				}
			}
		}
		return redValue - blackValue;

	}

	/**
	 * 將棋子的id转换成索引
	 * 
	 * @param id
	 * @return 將棋子的id转换成索引
	 */
	public static int pieceIdToIndex(PieceId id) {
		int index = 0;

		switch (id) {

		case SHUAI:

		case JIANG:
			index = 0;
			break;

		case HONGJU1:
		case HONGJU2:

		case HEIJU1:
		case HEIJU2:
			index = 1;
			break;

		case HONGPAO1:
		case HONGPAO2:

		case HEIPAO1:
		case HEIPAO2:
			index = 2;
			break;

		case HONGSHI1:
		case HONGSHI2:

		case HONGMA1:
		case HONGMA2:

		case HEIMA1:
		case HEIMA2:
			index = 3;
			break;

		case HONGXIANG1:
		case HONGXIANG2:

		case HEIXIANG1:
		case HEIXIANG2:
			index = 4;
			break;

		case HEISHI1:
		case HEISHI2:
			index = 5;
			break;

		case BING1:
		case BING2:
		case BING3:
		case BING4:
		case BING5:

		case ZU1:
		case ZU2:
		case ZU3:
		case ZU4:
		case ZU5:

			index = 6;
			break;
		}
		return index;

	}

}
