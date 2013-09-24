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

import java.awt.Point;

import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.core.ChessPiece.PieceCategory;
import cn.fansunion.chinesechess.core.ChessPiece.PieceId;

/**
 * 游戏规则
 * 
 * 联网对战时，打全谱时，高级打谱棋盘中移动时，車馬炮的移动规则完全一致
 * 
 * 基本打谱和高级打谱棋盘中移动时， 兵(卒)、帥(將)、仕(士)、相(象)的规则完全相同
 * 
 * 高级打谱从备用棋子面板移动棋子到棋盘中，所有棋子的规则与 其他情况都不同，兵(卒)、帥(將)、仕(士)、相(象)的终点有限制，独有的规则
 * 
 * 联网对战时，红方、黑方的兵(卒)、帥(將)、仕(士)、相(象)都在下方，8类棋子与其他情况不同，独有的规则
 * 
 * 游戏规则，需要考虑到是否被将军和老将对脸2种特殊情况。
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public final class ChessRule implements NAME {

	private ChessRule() {

	}

	/**
	 * 联网对战时，移动棋子的规则
	 * 
	 * 正常下棋时，违反象棋规则的走法会很少
	 * 
	 * 大概率事件放在if的前边，小概率事件尽可能靠后
	 * 
	 * @param piece
	 *            将要移动的棋子
	 * @param startX
	 *            数学意义上的起点横坐标
	 * @param startY
	 *            数学意义上的起点纵坐标
	 * @param endX
	 *            数学意义上的终点横坐标
	 * @param endY
	 *            数学意义上的终点纵坐标
	 * @param gamePoints
	 *            棋子所在棋盘的棋子点
	 * @return 合法返回true，不合法返回false
	 */
	public static boolean networkRule(ChessPiece piece, int startX, int startY,
			int endX, int endY, ChessPoint chessPoints[][]) {
		// 检查下标
		if (!rangeCheck(startX, startY, endX, endY)) {
			return false;
		}

		// 起点和终点为同一方棋子，不能移动
		if (isSameColor(chessPoints, startX, startY, endX, endY)) {
			return false;
		}
		// 是否可以移动
		boolean canMove = false;

		// 棋子的类别
		PieceCategory category = piece.getCategory();
		// 重用車、馬、炮的移动规则
		if (category.equals(PieceCategory.JU)
				|| category.equals(PieceCategory.MA)
				|| category.equals(PieceCategory.PAO)) {
			return jmpRule(category, startX, startY, endX, endY, chessPoints);
		}

		// 象的规则： 象飞田，横坐标和纵坐标得变化绝对值必须是2
		else if (category.equals(PieceCategory.HEIXIANG)
				|| category.equals(PieceCategory.HONGXIANG)) {
			int centerI = (startX + endX) / 2;
			int centerJ = (startY + endY) / 2;
			int xAxle = Math.abs(startX - endX);
			int yAxle = Math.abs(startY - endY);

			if (xAxle == 2 && yAxle == 2 && endY >= 6) {
				// 卡象眼
				if (chessPoints[centerI][centerJ].hasPiece()) {
					canMove = false;
				} else {
					canMove = true;
				}
			} else {
				canMove = false;
			}
		}

		// 兵的移动规则：
		else if (category.equals(PieceCategory.BING)
				|| category.equals(PieceCategory.ZU)) {
			int xAxle = Math.abs(startX - endX);

			// 兵没有过河
			if (endY >= 6) {
				if (startY - endY == 1 && xAxle == 0) {
					canMove = true;
				} else {
					canMove = false;
				}
			}

			// 兵过河了，过河的兵不回头
			else if (endY <= 5) {
				if ((startY - endY == 1) && (xAxle == 0)) {
					canMove = true;
				} else if ((endY - startY == 0) && (xAxle == 1)) {
					canMove = true;
				} else {
					canMove = false;
				}
			}
		}

		/*
		 * 士的规则：在一定范围内，坐标限制如下
		 * 
		 * 士划斜
		 */
		else if (category.equals(PieceCategory.HEISHI)
				|| category.equals(PieceCategory.HONGSHI)) {
			int xAxle = Math.abs(startX - endX);
			int yAxle = Math.abs(startY - endY);
			if (xAxle == 1 && yAxle == 1 && endX <= 6 && endX >= 4) {
				if (endY >= 8 && endY <= 10) {
					canMove = true;
				}
			} else {
				canMove = false;
			}
		}

		/*
		 * 帅和将的规则：在一定范围内，坐标限制如下
		 * 
		 * 4<=x<=6,8<=y<=10 一步一步地直走
		 */
		else if ((category.equals(PieceCategory.SHUAI))
				|| (category.equals(PieceCategory.JIANG))) {
			int xAxle = Math.abs(startX - endX);
			int yAxle = Math.abs(startY - endY);
			// 横坐标的限制条件
			if (endX <= 6 && endX >= 4) {
				// 一次只能移动一步
				if ((xAxle == 1 && yAxle == 0) || (xAxle == 0 && yAxle == 1)) {
					// 帅的纵坐标的限制条件
					if (endY >= 8 && endY <= 10) {
						// 没有对脸
						if (!ChessRule
								.isDuiLian(piece, endX, endY, chessPoints)) {
							canMove = true;
						}
					}
				} else {
					canMove = false;
				}
			} else {
				canMove = false;
			}
		}

		return canMove;

	}

	/**
	 * 打全谱时用的棋子移动规则
	 * 
	 * @param piece
	 *            将要移动的棋子
	 * @param startX
	 *            数学意义上的 起点横坐标
	 * @param startY
	 *            数学意义上的 起点纵坐标
	 * @param endX
	 *            数学意义上的 终点横坐标
	 * @param endY
	 *            数学意义上的 终点纵坐标
	 * @param chessPoints
	 *            棋子所在棋盘的棋子点
	 * @return 合法返回true，不合法返回false
	 */
	public static boolean allRule(ChessPiece piece, int startX, int startY,
			int endX, int endY, ChessPoint[][] chessPoints) {

		// 检查下标
		if (!rangeCheck(startX, startY, endX, endY)) {
			return false;
		}

		// 起点和终点为同一方棋子，不能移动
		if (isSameColor(chessPoints, startX, startY, endX, endY)) {
			return false;
		}

		// 将帅对脸，不能移动
		if (isDuiLian(piece, endY, endY, chessPoints)) {
			return false;
		}

		// 默认不可以移动，只需处理符合规则的(canMove = true)
		boolean canMove = false;

		// 棋子的类别
		PieceCategory category = piece.getCategory();

		// 重用車、馬、炮的移动规则
		if (category == PieceCategory.JU || category == PieceCategory.MA
				|| category == PieceCategory.PAO) {
			return jmpRule(category, startX, startY, endX, endY, chessPoints);
		}

		// 基本打谱、高级打谱共用的帥、仕、兵、相的移动规则
		if (category.equals(PieceCategory.SHUAI)
				|| category.equals(PieceCategory.HONGSHI)
				|| category.equals(PieceCategory.BING)
				|| category.equals(PieceCategory.HONGXIANG)
				|| category.equals(PieceCategory.JIANG)
				|| category.equals(PieceCategory.HEISHI)
				|| category.equals(PieceCategory.ZU)
				|| (category.equals(PieceCategory.HEIXIANG))) {
			return ssxbRule(piece, startX, startY, endX, endY, chessPoints);
		}

		return canMove;
	}

	/**
	 * 移动一步之后，己方是否被将军
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param chessPoints
	 * @param board
	 * @return
	 */
	public static boolean isWillDangerous(int startX, int startY, int endX,
			int endY, ChessPoint[][] chessPoints, ChessBoard board) {

		// System.out.println("暂时移动棋子之前：");
		// PieceUtil.printPieceLoations(chessPoints);

		ChessPiece pieceRemoved = chessPoints[endX][endY].getPiece();
		PieceId id = null;
		if (pieceRemoved != null) {
			id = pieceRemoved.getId();
			// System.out.println("被吃棋子的id：" + id);
		}
		ChessPiece movePiece = chessPoints[startX][startY].getPiece();

		chessPoints[endX][endY].removePiece(pieceRemoved, board);
		chessPoints[endX][endY].setPiece(movePiece, board);
		chessPoints[startX][startY].setHasPiece(false);

		// System.out.println("暂时移动棋子之后：");
		// PieceUtil.printPieceLoations(chessPoints);
		boolean flag = isDangerous(true, movePiece.getName(), chessPoints);

		// System.out.println(movePiece.getId() + "是否被将军：(" + startX + ","+
		// startY + "),(" + endX + "),(" + endY + ")" + flag);

		ChessPiece piece = chessPoints[endX][endY].getPiece();
		chessPoints[startX][startY].setPiece(piece, board);
		// 撤销前一步的临时走法
		if (pieceRemoved == null) {
			chessPoints[endX][endY].setHasPiece(false);
		} else {
			ChessPiece pieceRemoved2 = PieceUtil.createPiece(id);
			chessPoints[endX][endY].setPiece(pieceRemoved2, board);
			pieceRemoved2.addMouseListener(board.getMouseAdapter());
			// System.out.println("被吃棋子的id22：" + pieceRemoved2.getId());
		}

		// System.out.println("恢复移动棋子之前：");
		// PieceUtil.printPieceLoations(chessPoints);

		return flag;
	}

	/**
	 * 高级打谱时用的棋子移动规则
	 * 
	 * @param piece
	 *            将要移动的棋子
	 * @param startX
	 *            数学意义上的 起点横坐标
	 * @param startY
	 *            数学意义上的 起点纵坐标
	 * @param endX
	 *            数学意义上的 终点横坐标
	 * @param endY
	 *            数学意义上的 终点纵坐标
	 * @param chessPoints
	 *            棋子所在棋盘的棋子点
	 * @return 合法返回true，不合法返回false
	 */
	public static boolean partialRule(ChessPiece piece, int startX, int startY,
			int endX, int endY, ChessPoint[][] chessPoints) {

		// 起点和终点为同一方棋子，不能移动
		if (isSameColor(chessPoints, startX, startY, endX, endY)) {
			return false;
		}

		System.out.println("startX=" + startX + " startY= " + startY
				+ " endX= " + endX + " endY= " + endY);
		PieceCategory category = piece.getCategory();

		// 从备用棋子栏中选择棋子到棋盘中,只能将仕、相、帥放在合法的位置
		if (startX == 0 && startY == 0) {

			// 仕、相、帥的终点位置有限制
			if (category.equals(PieceCategory.HONGSHI)) {
				if (endX == 4 || endX == 6) {
					if (endY == 8 || endY == 10) {
						return true;
					}
				} else if (endX == 5 && endY == 9) {
					return true;
				}
				return false;
			}

			else if (category.equals(PieceCategory.HEISHI)) {
				if (endX == 4 || endX == 6) {
					if (endY == 1 || endY == 3) {
						return true;
					}
				} else if (endX == 5 && endY == 2) {
					return true;
				}
				return false;
			}

			else if (category.equals(PieceCategory.HEIXIANG)) {
				if (endX == 1 || endX == 5 || endX == 9) {
					if (endY == 3) {
						return true;
					}
				} else if (endX == 3 || endX == 7) {
					if (endY == 1 || endY == 5) {
						return true;
					}
				}
				return false;
			}

			else if (category.equals(PieceCategory.HONGXIANG)) {
				if (endX == 1 || endX == 5 || endX == 9) {
					if (endY == 8) {
						return true;
					}
				} else if (endX == 3 || endX == 7) {
					if (endY == 6 || endY == 10) {
						return true;
					}
				}
				return false;
			}

			else if (category.equals(PieceCategory.JIANG)) {
				if (endX >= 4 && endX <= 6) {
					if (endY >= 1 && endY <= 3) {
						// 老将对脸
						if (ChessRule.isDuiLian(piece, endX, endY, chessPoints)) {
							return false;
						}
						return true;
					}
				}
				return false;
			}

			else if (category.equals(PieceCategory.SHUAI)) {
				if (endX >= 4 && endX <= 6) {
					if (endY >= 8 && endY <= 10) {
						// 老将对脸
						if (ChessRule.isDuiLian(piece, endX, endY, chessPoints)) {
							return false;
						}
						return true;
					}
				}
				return false;
			}

			// 兵的终点位置有限制
			else if (category.equals(PieceCategory.BING)) {
				if (endY == 6 || endY == 7) {
					if (endX == 1 || endX == 3 || endX == 5 || endX == 7
							|| endX == 9) {
						return true;
					}
					return false;
				} else if (endY < 6) {
					return true;
				} else {
					return false;
				}

			}
			// 卒的终点位置有限制
			else if (category.equals(PieceCategory.ZU)) {
				if (endY == 4 || endY == 5) {
					if (endX == 1 || endX == 3 || endX == 5 || endX == 7
							|| endX == 9) {
						return true;
					}
					return false;
				} else if (endY > 5) {
					return true;
				} else {
					return false;
				}

			}

			else {
				// 車、馬、炮可以放在任何位置
				return true;
			}
		}
		// 重用車、馬、炮的移动规则
		if (category.equals(PieceCategory.JU)
				|| category.equals(PieceCategory.MA)
				|| category.equals(PieceCategory.PAO)) {
			return jmpRule(category, startX, startY, endX, endY, chessPoints);
		}

		// 高级打谱在棋盘中移动时的规则，movePieceRule5只适用与此种情况，不能被其他情况时调用
		else if (category.equals(PieceCategory.HONGSHI)
				|| category.equals(PieceCategory.HEISHI)
				|| category.equals(PieceCategory.HONGXIANG)
				|| category.equals(PieceCategory.BING)
				|| category.equals(PieceCategory.HEIXIANG)
				|| category.equals(PieceCategory.ZU)
				|| category.equals(PieceCategory.JIANG)
				|| (category.equals(PieceCategory.SHUAI))) {
			return ssxbRule(piece, startX, startY, endX, endY, chessPoints);
		}
		return false;
	}

	/**
	 * 車、馬、炮的移动规则，棋盘中移动时通用的规则
	 * 
	 * 重用3次
	 * 
	 * @param startX
	 *            数学意义上的 起点横坐标
	 * @param startY
	 *            数学意义上的 起点纵坐标
	 * @param endX
	 *            数学意义上的 终点横坐标
	 * @param endY
	 *            数学意义上的 终点纵坐标
	 * @param chessPoints
	 *            棋子所在棋盘的棋子点
	 * @return 合法返回true，不合法返回false
	 */
	public static boolean jmpRule(PieceCategory category, int startX,
			int startY, int endX, int endY, ChessPoint[][] chessPoints) {
		/*
		 * System.out.println("車馬炮是否将军：(" + startX + "," + startY + "),(" + endX
		 * + "," + endY + ")");
		 */
		int minX = Math.min(startX, endX);
		int maxX = Math.max(startX, endX);
		int minY = Math.min(startY, endY);
		int maxY = Math.max(startY, endY);

		// 車的规则：車走直
		if (category.equals(PieceCategory.JU)) {
			// 竖着走
			if (startX == endX) {
				int j = 0;
				for (j = minY + 1; j <= maxY - 1; j++) {
					// 中间不能有棋子
					if (chessPoints[startX][j].hasPiece()) {
						return false;
					}
				}
				if (j == maxY) {
					return true;
				}
			}
			// 横着走
			else if (startY == endY) {
				int i = 0;
				for (i = minX + 1; i <= maxX - 1; i++) {
					if (chessPoints[i][startY].hasPiece()) {
						return false;
					}
				}
				if (i == maxX) {
					return true;
				}
			} else {
				return false;
			}
		}

		// 马的规则：马踏日,别马腿有4种情况
		else if (category.equals(PieceCategory.MA)) {
			int xAxle = Math.abs(startX - endX);
			int yAxle = Math.abs(startY - endY);

			// X方向移动2步，Y方向移动1步
			if (xAxle == 2 && yAxle == 1) {
				if (endX > startX) {
					// 别马腿
					if (chessPoints[startX + 1][startY].hasPiece()) {
						return false;
					}
					return true;
				}
				if (endX < startX) {
					// 别马腿
					if (chessPoints[startX - 1][startY].hasPiece()) {
						return false;
					}
					return true;
				}

			}

			// X方向移动1步，Y方向移动2步
			else if (xAxle == 1 && yAxle == 2) {
				if (endY > startY) {
					// 别马腿
					if (chessPoints[startX][startY + 1].hasPiece()) {
						return false;
					}
					return true;
				}
				if (endY < startY) {
					// 别马腿
					if (chessPoints[startX][startY - 1].hasPiece()) {
						return false;
					}
					return true;
				}

			} else {
				return false;
			}
		}

		// 炮的规则：
		else if (category.equals(PieceCategory.PAO)) {
			// 如果吃子，中间只能隔着一个棋子
			int number = 0;
			// 垂直方向移动
			if (startX == endX) {
				int j = 0;
				for (j = minY + 1; j <= maxY - 1; j++) {
					if (chessPoints[startX][j].hasPiece()) {
						number++;
					}
				}

				// 中间没有棋子，不能吃子
				if (number == 0 && !chessPoints[endX][endY].hasPiece()) {
					return true;
				}

				// 中间有一个棋子，必须吃一个子
				else if (number == 1) {
					if (chessPoints[endX][endY].hasPiece()) {
						return true;
					}
				}

				// 中间多于一个棋子，不能移动
				else if (number > 1) {
					return false;
				}

			}

			// 水平方向移动
			else if (startY == endY) {
				int i = 0;
				for (i = minX + 1; i <= maxX - 1; i++) {
					if (chessPoints[i][startY].hasPiece()) {
						number++;
					}
				}

				if (number > 1) {
					return false;
				} else if (number == 1) {
					if (chessPoints[endX][endY].hasPiece()) {
						return true;
					}
				}

				else if (number == 0 && !chessPoints[endX][endY].hasPiece()) {
					return true;
				}
			} else {
				return false;
			}
		}
		return false;

	}

	/**
	 * 
	 * 红方在下，黑方在上，
	 * 
	 * 仕（士）、相（象）、帥（將）、兵（卒）
	 * 
	 * 基本打谱、高级打谱用的规则，重用2次
	 * 
	 * @param piece
	 *            将要移动的棋子
	 * @param startX
	 *            数学意义上的 起点横坐标
	 * @param startY
	 *            数学意义上的 起点纵坐标
	 * @param endX
	 *            数学意义上的 终点横坐标
	 * @param endY
	 *            数学意义上的 终点纵坐标
	 * @param chessPoints
	 *            棋子所在棋盘的棋子点
	 * @return 合法返回true，不合法返回false
	 */
	private static boolean ssxbRule(ChessPiece piece, int startX, int startY,
			int endX, int endY, ChessPoint[][] chessPoints) {
		PieceCategory category = piece.getCategory();

		if (category.equals(PieceCategory.HEIXIANG)
				|| category.equals(PieceCategory.HONGXIANG)) {
			int centerI = (startX + endX) / 2;
			int centerJ = (startY + endY) / 2;
			int xAxle = Math.abs(startX - endX);
			int yAxle = Math.abs(startY - endY);

			if (xAxle == 2 && yAxle == 2) {
				// 卡象眼
				if (!chessPoints[centerI][centerJ].hasPiece()) {
					if (category.equals(PieceCategory.HEIXIANG)) {
						if (endY <= 5) {
							return true;
						}
					} else if (category.equals(PieceCategory.HONGXIANG)) {
						if (endY >= 6) {
							return true;
						}
					}
				}
			}
		}

		// 兵的移动规则：
		else if (category.equals(PieceCategory.BING)
				|| category.equals(PieceCategory.ZU)) {
			int xAxle = Math.abs(startX - endX);

			// 兵在下方, 兵没有过河
			if (category.equals(PieceCategory.BING)) {
				if (endY >= 6) {
					if (startY - endY == 1 && xAxle == 0) {
						return true;
					}
				}

				// 兵过河了，过河的兵不回头
				else if (endY <= 5) {
					if ((startY - endY == 1) && (xAxle == 0)) {
						return true;
					} else if ((endY - startY == 0) && (xAxle == 1)) {
						return true;
					}
				}
			}

			// 卒在上方
			else {
				if (endY < 6) {
					if (startY - endY == -1 && xAxle == 0) {
						return true;
					}
				}

				// 兵过河了，过河的兵不回头
				else if (endY >= 6) {
					if ((startY - endY == -1) && (xAxle == 0)) {
						return true;
					} else if ((endY - startY == 0) && (xAxle == 1)) {
						return true;
					}
				}
			}
		}

		/*
		 * 士的规则：在一定范围内，坐标限制如下
		 * 
		 * 士划斜
		 */
		else if (category.equals(PieceCategory.HEISHI)
				|| category.equals(PieceCategory.HONGSHI)) {
			int xAxle = Math.abs(startX - endX);
			int yAxle = Math.abs(startY - endY);
			if (xAxle == 1 && yAxle == 1 && endX <= 6 && endX >= 4) {
				if (category.equals(PieceCategory.HONGSHI)
						&& (endY >= 8 && endY <= 10)) {
					return true;
				} else if (category.equals(PieceCategory.HEISHI)
						&& (endY >= 1 && endY <= 3)) {
					return true;
				}
			}
		}

		/*
		 * 帅和将的规则：在一定范围内，坐标限制如下
		 * 
		 * 4<=x<=6,8<=y<=10 一步一步地直走
		 */
		else if ((category.equals(PieceCategory.SHUAI))
				|| (category.equals(PieceCategory.JIANG))) {
			int xAxle = Math.abs(startX - endX);
			int yAxle = Math.abs(startY - endY);
			// 横坐标的限制条件

			// 一次只能移动一步
			if ((xAxle == 1 && yAxle == 0) || (xAxle == 0 && yAxle == 1)) {
				if (endX <= 6 && endX >= 4) {
					// 老将对脸
					if (ChessRule.isDuiLian(piece, endX, endY, chessPoints)) {
						return false;
					}

					// 帅的纵坐标的限制条件
					if (category.equals(PieceCategory.SHUAI) && endY >= 8
							&& endY <= 10) {
						return true;
					}
					if (category.equals(PieceCategory.JIANG) && endY >= 1
							&& endY <= 3) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 判断己方是否被将军 只有車馬炮卒兵才有将军的可能
	 * 
	 * 只要有一个棋子可以将军则将军为真，并且立即返回
	 * 
	 * 
	 * @param flag
	 *            在己方棋盘中，红方是否在下方
	 * @param playerName
	 *            我方的名字 红方或者黑方
	 * @param chessPoints
	 *            棋子点集
	 * @return 对方将军返回true，否则返回false
	 */
	public static boolean isDangerous(boolean flag, String playerName,
			ChessPoint[][] chessPoints) {
		Point leaderPosition;// 己方將或帥的位置
		Point[] paoPositions;// 对方炮的位置
		Point[] juPositions;// 对方車的位置
		Point[] maPositions;// 对方马的位置
		Point[] zuPositions;// 对方卒或兵的位置
		// System.out.println("判断" + playerName + "是否被将军！");
		// PieceUtil.printPieceLoations(chessPoints);

		// 我方是红方，判断黑方是否将军
		if (playerName.equals(RED_NAME)) {
			leaderPosition = PieceUtil.getLeaderPosition(PieceCategory.SHUAI,
					chessPoints);

			if (leaderPosition == null) {
				System.out.println("没有找到帥！");
				return false;
			}

			juPositions = PieceUtil.getPositionByCategory(BLACK_NAME,
					PieceCategory.JU, chessPoints);
			paoPositions = PieceUtil.getPositionByCategory(BLACK_NAME,
					PieceCategory.PAO, chessPoints);
			maPositions = PieceUtil.getPositionByCategory(BLACK_NAME,
					PieceCategory.MA, chessPoints);
			zuPositions = PieceUtil.getPositionByCategory(BLACK_NAME,
					PieceCategory.ZU, chessPoints);

			int leaderX = (int) leaderPosition.getX();
			int leaderY = (int) leaderPosition.getY();

			// 判断黑方的車是否将军
			for (int i = 0; i < juPositions.length; i++) {
				int x = (int) juPositions[i].getX();
				int y = (int) juPositions[i].getY();
				// 如果将军直接返回
				if (ChessRule.jmpRule(PieceCategory.JU, x, y, leaderX, leaderY,
						chessPoints)) {
					return true;
				}
			}

			// 判断黑方的炮是否将军
			for (int i = 0; i < paoPositions.length; i++) {
				int x = (int) paoPositions[i].getX();
				int y = (int) paoPositions[i].getY();

				if (ChessRule.jmpRule(PieceCategory.PAO, x, y, leaderX,
						leaderY, chessPoints)) {
					return true;
				}
			}

			// 判断黑方的马是否将军
			for (int i = 0; i < maPositions.length; i++) {
				int x = (int) maPositions[i].getX();
				int y = (int) maPositions[i].getY();

				if (ChessRule.jmpRule(PieceCategory.MA, x, y, leaderX, leaderY,
						chessPoints)) {
					return true;
				}
			}

			// 判断黑方的卒是否将军
			for (int i = 0; i < zuPositions.length; i++) {
				int x = (int) zuPositions[i].getX();
				int y = (int) zuPositions[i].getY();

				int xAxle = Math.abs(x - leaderX);

				if ((leaderY - y == 0) && (xAxle == 1)) {
					return true;
				} else if ((y - leaderY == -1) && (xAxle == 0) && flag) {
					return true;
				} else if ((y - leaderY == 1) && (xAxle == 0) && !flag) {
					return true;
				}

			}
		}

		// 我方是黑方，判断红方是否将军
		else if (playerName.equals(BLACK_NAME)) {
			leaderPosition = PieceUtil.getLeaderPosition(PieceCategory.JIANG,
					chessPoints);

			if (leaderPosition == null) {
				System.out.println("没有找到將！");
				return false;
			}

			juPositions = PieceUtil.getPositionByCategory(RED_NAME,
					PieceCategory.JU, chessPoints);
			paoPositions = PieceUtil.getPositionByCategory(RED_NAME,
					PieceCategory.PAO, chessPoints);
			maPositions = PieceUtil.getPositionByCategory(RED_NAME,
					PieceCategory.MA, chessPoints);
			zuPositions = PieceUtil.getPositionByCategory(RED_NAME,
					PieceCategory.BING, chessPoints);

			int leaderX = (int) leaderPosition.getX();
			int leaderY = (int) leaderPosition.getY();

			// 判断红方的車是否将军
			for (int i = 0; i < juPositions.length; i++) {
				int x = (int) juPositions[i].getX();
				int y = (int) juPositions[i].getY();
				// 如果将军直接返回
				if (ChessRule.jmpRule(PieceCategory.JU, x, y, leaderX, leaderY,
						chessPoints)) {
					return true;
				}
			}

			// 判断红方的炮是否将军
			for (int i = 0; i < paoPositions.length; i++) {
				int x = (int) paoPositions[i].getX();
				int y = (int) paoPositions[i].getY();

				if (ChessRule.jmpRule(PieceCategory.PAO, x, y, leaderX,
						leaderY, chessPoints)) {
					return true;
				}
			}

			// 判断红方的马是否将军
			for (int i = 0; i < maPositions.length; i++) {
				int x = (int) maPositions[i].getX();
				int y = (int) maPositions[i].getY();

				if (ChessRule.jmpRule(PieceCategory.MA, x, y, leaderX, leaderY,
						chessPoints)) {
					return true;
				}
			}

			// 判断红方的兵是否将军
			for (int i = 0; i < zuPositions.length; i++) {
				int x = (int) zuPositions[i].getX();
				int y = (int) zuPositions[i].getY();

				int xAxle = Math.abs(x - leaderX);

				if ((leaderY - y == 0) && (xAxle == 1)) {
					return true;
				} else if ((y - leaderY == 1) && (xAxle == 0) && flag) {
					return true;
				} else if ((y - leaderY == -1) && (xAxle == 0) && !flag) {
					return true;
				}

			}
		}
		return false;

	}

	/**
	 * 判断起点和终点处的2个棋子是否为同一方的
	 * 
	 * @param chessPoints
	 *            棋子点集
	 * @param startX
	 *            起点横坐标
	 * @param startY
	 *            起点纵坐标
	 * @param endX
	 *            终点横坐标
	 * @param endY
	 *            起点纵坐标
	 * @return 同一方则返回true，否则返回false。
	 */
	private static boolean isSameColor(ChessPoint[][] chessPoints, int startX,
			int startY, int endX, int endY) {

		if (startX < 1 || startX > 9 || startY < 1 || startY > 10) {
			return false;
		}

		if (endX < 1 || endX > 10 || endY < 1 || endY > 10) {
			return false;
		}
		/*
		 * System.out.println("是否为同一方棋子:" + startX + "," + startY + "," + endX +
		 * "," + endY);
		 */
		ChessPiece startPiece = chessPoints[startX][startY].getPiece();
		ChessPiece endPiece = chessPoints[endX][endY].getPiece();

		if (startPiece != null && endPiece != null) {
			String startPlayerName = startPiece.getName();
			String endPlayerName = endPiece.getName();
			if (startPlayerName.equals(endPlayerName)) {
				/*
				 * System.out.println("是否为同一方棋子：" + startPiece.getCategory() +
				 * "" + startX + "," + startY + "," + endX + "," + endY);
				 */
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查指定的索引，是否在合法的范围内
	 * 
	 * @param startX
	 *            棋子的起点横坐标
	 * @param startY
	 *            棋子的起点纵坐标
	 * @param endX
	 *            棋子的终点横坐标
	 * @param endY
	 *            棋子的终点纵坐标
	 */
	private static boolean rangeCheck(int startX, int startY, int endX, int endY) {
		if (startX < 1 || startX > 9 || startY < 1 || startY > 10) {
			return false;
		}

		if (endX < 1 || endX > 10 || endY < 1 || endY > 10) {
			return false;
		}

		return true;
	}

	/**
	 * 是否对脸
	 * 
	 * @param piece
	 *            移动的棋子
	 * @param endX
	 *            终点横坐标
	 * @param endY
	 *            终点纵坐标
	 * @param chessPoints
	 *            棋子点二维数组
	 * @return 如果对脸，返回true；否则，返回false。
	 */
	public static boolean isDuiLian(ChessPiece piece, int endX, int endY,
			ChessPoint[][] chessPoints) {

		int shuaiI = 0, shuaiJ = 0;
		int jiangI = 0, jiangJ = 0;
		PieceCategory pc = piece.getCategory();

		// 棋盘中黑將的位置
		for (int i = 4; i <= 6; i++) {
			for (int j = 1; j <= 10; j++) {
				if (chessPoints[i][j].hasPiece()) {
					if (chessPoints[i][j].getPiece().getCategory()
							.equals(PieceCategory.JIANG)) {
						jiangI = i;
						jiangJ = j;
						break;
					}
				}
			}
		}

		// 棋盘中红帥的位置
		for (int i = 4; i <= 6; i++) {
			for (int j = 1; j <= 10; j++) {
				if (chessPoints[i][j].hasPiece()) {
					if (chessPoints[i][j].getPiece().getCategory()
							.equals(PieceCategory.SHUAI)) {
						shuaiI = i;
						shuaiJ = j;
						break;
					}
				}
			}
		}
		// 移动的棋子是帥或將
		if (pc == PieceCategory.SHUAI || pc == PieceCategory.JIANG) {
			int startX = 0;
			int startY = 0;
			if (pc == PieceCategory.SHUAI) {
				startX = jiangI;
				startY = jiangJ;
			} else {
				startX = shuaiI;
				startY = shuaiJ;
			}
			boolean flag = false;
			// 横坐标相同才有可能对脸
			if (startX == endX) {
				int y2 = Math.max(endY, startY);
				int y1 = Math.min(endY, startY);
				for (int j = y1 + 1; j < y2; j++) {
					if (chessPoints[startX][j].hasPiece()) {
						flag = true;
					}
				}
				if (!flag) {
					return true;
				}
			}
		}
		// 移动的棋子不是將和帥
		else {
			// 横坐标相同才有可能对脸
			if (shuaiI == jiangI) {
				boolean flag = false;
				int y2 = Math.max(shuaiJ, jiangJ);
				int y1 = Math.min(shuaiJ, jiangJ);
				for (int j = y1 + 1; j < y2; j++) {
					// 將和帥之间是否有棋子，不包含移动的棋子，因为这个棋子将要离开
					if (j != endY && chessPoints[shuaiI][j].hasPiece()) {
						flag = true;
					}
				}
				if (!flag) {
					return true;
				}
			}
		}
		// System.out.println(piece.getCategory()+"Test" + shuaiI + shuaiJ + ","
		// + endX + endY);

		return false;

	}
}
