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

import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.core.ChessBoard;
import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessPoint;
import cn.fansunion.chinesechess.core.PieceUtil;
import cn.fansunion.chinesechess.core.ChessPiece.PieceId;

/**
 * FEN格式串工具类，不能实例化，不能被继承
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public final class FenUtil implements NAME {
	private FenUtil() {

	}

	/**
	 * 將棋子点2维数组转换成FEN格式串
	 * 
	 * @param chessPoints
	 * @param name
	 * @return
	 */
	public static String arrayToFenString(ChessPoint[][] chessPoints,
			String name) {
		StringBuilder fen = new StringBuilder();
		int k = 0;
		for (int i = 1; i <= 10; i++) {
			k = 0;
			for (int j = 1; j <= 9; j++) {
				// 列优先
				ChessPiece piece = chessPoints[j][i].getPiece();
				if (piece != null) {
					// System.out.println(piece.getCategory());
					if (k > 0) {
						fen.append(k);
						k = 0;
					}
					fen.append(pieceToFenPiece(piece));
				} else {
					k++;
				}
			}
			if (k > 0) {
				fen.append(k);
			}
			fen.append("/");

		}
		fen.append(" ");

		if (name.equals(RED_NAME)) {
			fen.append("w");
		} else {
			fen.append("b");
		}
		return fen.toString();

	}

	/**
	 * 將楚汉棋兵的棋子转换成 FEN格式串对应的棋子表示
	 * 
	 * @param piece
	 * @return 將楚汉棋兵的棋子转换成 FEN格式串对应的棋子表示
	 */
	private static String pieceToFenPiece(ChessPiece piece) {
		String str = null;
		PieceId id = piece.getId();
		switch (id) {
		case BING1:
		case BING2:
		case BING3:
		case BING4:
		case BING5:
			str = "P";
			break;

		case ZU1:
		case ZU2:
		case ZU3:
		case ZU4:
		case ZU5:

			str = "p";
			break;

		case SHUAI:
			str = "K";
			break;
		case JIANG:
			str = "k";
			break;

		case HONGSHI1:
		case HONGSHI2:
			str = "A";
			break;
		case HEISHI1:
		case HEISHI2:
			str = "a";
			break;

		case HONGXIANG1:
		case HONGXIANG2:
			str = "B";
			break;
		case HEIXIANG1:
		case HEIXIANG2:
			str = "b";
			break;

		case HONGMA1:
		case HONGMA2:
			str = "N";
			break;

		case HEIMA1:
		case HEIMA2:
			str = "n";
			break;

		case HONGJU1:
		case HONGJU2:
			str = "R";
			break;

		case HEIJU1:
		case HEIJU2:
			str = "r";
			break;

		case HONGPAO1:
		case HONGPAO2:
			str = "C";
			break;

		case HEIPAO1:
		case HEIPAO2:
			str = "c";
			break;

		}
		return str;
	}

	/**
	 * 將FEN字符串转换为表示棋局局面的二维数组
	 * 
	 * @param fenString
	 *            FEN字符串
	 * @return 表示棋局局面的二维数组
	 */
	public static ChessBoard fenStringToArray(String fenString) {

		// 棋盘中的坐标和数学中的坐标不同
		ManMachineBoard board = new ManMachineBoard();
		ChessPoint[][] chessPoints = board.getChessPoints();
		int i = 1;// x坐标
		int j = 1;// y坐标
		char ch = 'q';
		for (int index = 0; index < fenString.length(); index++) {
			ch = fenString.charAt(index);
			System.out.print(ch + "");
			if (ch == '/') {
				// 换行
				i = 1;
				j++;
				;
			} else if (ch >= '1' && ch <= '9') {
				i += (ch - '0');
			} else if (ch >= 'A' && ch <= 'Z') {
				ChessPiece piece = fenPieceToPiece(ch, chessPoints);
				chessPoints[i][j] = new ChessPoint(i * ChessPiece.UNIT_WIDTH, j
						* ChessPiece.UNIT_HEIGHT, piece, board);
				i++;
			} else if (ch >= 'a' && ch <= 'z') {
				ChessPiece piece = fenPieceToPiece(ch, chessPoints);
				chessPoints[i][j] = new ChessPoint(i * ChessPiece.UNIT_WIDTH, j
						* ChessPiece.UNIT_HEIGHT, piece, board);
				i++;
			} else if (ch == ' ') {
				ch = fenString.charAt(index + 1);
				break;
			}
			// System.out.println("当前坐标："+i+","+j+")");
		}

		if (ch == 'w') {
			board.setPlayerName(RED_NAME);
		} else if (ch == 'b') {
			board.setPlayerName(BLACK_NAME);
		}
		return board;

	}

	/**
	 * 將FEN中的棋子字符标志转换为一个棋子
	 * 
	 * @param ch
	 *            FEN中的棋子字符标志
	 * @param chessPoints
	 *            转换过程中局面的二维数组
	 * @return 一个棋子
	 */
	private static ChessPiece fenPieceToPiece(char ch,
			ChessPoint[][] chessPoints) {
		ChessPiece piece = null;
		switch (ch) {
		case 'K':
			piece = PieceUtil.createPiece(PieceId.SHUAI);
			break;
		case 'k':
			piece = PieceUtil.createPiece(PieceId.JIANG);
			break;
		case 'A':
			ChessPiece hongSHi1 = PieceUtil.searchPieceById(PieceId.HONGSHI1,
					chessPoints);
			if (hongSHi1 == null) {
				piece = PieceUtil.createPiece(PieceId.HONGSHI1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HONGSHI2);
			}
			break;
		case 'a':
			ChessPiece heiShi1 = PieceUtil.searchPieceById(PieceId.HEISHI1,
					chessPoints);
			if (heiShi1 == null) {
				piece = PieceUtil.createPiece(PieceId.HEISHI1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HEISHI2);
			}
			break;
		case 'B':
			ChessPiece hongXiang1 = PieceUtil.searchPieceById(
					PieceId.HONGXIANG1, chessPoints);
			if (hongXiang1 == null) {
				piece = PieceUtil.createPiece(PieceId.HONGXIANG1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HONGXIANG2);
			}
			break;
		case 'b':
			ChessPiece heiXiang1 = PieceUtil.searchPieceById(PieceId.HEIXIANG1,
					chessPoints);
			if (heiXiang1 == null) {
				piece = PieceUtil.createPiece(PieceId.HEIXIANG1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HEIXIANG2);
			}
			break;
		case 'N':
			ChessPiece hongMa1 = PieceUtil.searchPieceById(PieceId.HONGMA1,
					chessPoints);
			if (hongMa1 == null) {
				piece = PieceUtil.createPiece(PieceId.HONGMA1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HONGMA2);
			}
			break;
		case 'n':
			ChessPiece heiMa1 = PieceUtil.searchPieceById(PieceId.HEIMA1,
					chessPoints);
			if (heiMa1 == null) {
				piece = PieceUtil.createPiece(PieceId.HEIMA1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HEIMA2);
			}
			break;
		case 'R':
			ChessPiece hongJu1 = PieceUtil.searchPieceById(PieceId.HONGJU1,
					chessPoints);
			if (hongJu1 == null) {
				piece = PieceUtil.createPiece(PieceId.HONGJU1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HONGJU2);
			}
			break;
		case 'r':
			ChessPiece heiJu1 = PieceUtil.searchPieceById(PieceId.HEIJU1,
					chessPoints);
			if (heiJu1 == null) {
				piece = PieceUtil.createPiece(PieceId.HEIJU1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HEIJU2);
			}
			break;
		case 'C':
			ChessPiece hongPao1 = PieceUtil.searchPieceById(PieceId.HONGPAO1,
					chessPoints);
			if (hongPao1 == null) {
				piece = PieceUtil.createPiece(PieceId.HONGPAO1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HONGPAO2);
			}
			break;

		case 'c':
			ChessPiece heiPao1 = PieceUtil.searchPieceById(PieceId.HEIPAO1,
					chessPoints);
			if (heiPao1 == null) {
				piece = PieceUtil.createPiece(PieceId.HEIPAO1);
			} else {
				piece = PieceUtil.createPiece(PieceId.HEIPAO2);
			}
			break;
		case 'P':
			ChessPiece bing1 = PieceUtil.searchPieceById(PieceId.BING1,
					chessPoints);
			ChessPiece bing2 = PieceUtil.searchPieceById(PieceId.BING2,
					chessPoints);
			ChessPiece bing3 = PieceUtil.searchPieceById(PieceId.BING3,
					chessPoints);
			ChessPiece bing4 = PieceUtil.searchPieceById(PieceId.BING4,
					chessPoints);
			ChessPiece bing5 = PieceUtil.searchPieceById(PieceId.BING5,
					chessPoints);
			if (bing1 == null) {
				piece = PieceUtil.createPiece(PieceId.BING1);
			} else if (bing2 == null) {
				piece = PieceUtil.createPiece(PieceId.BING2);
			} else if (bing3 == null) {
				piece = PieceUtil.createPiece(PieceId.BING3);
			} else if (bing4 == null) {
				piece = PieceUtil.createPiece(PieceId.BING4);
			} else if (bing5 == null) {
				piece = PieceUtil.createPiece(PieceId.BING5);
			}

			break;

		case 'p':
			ChessPiece zu1 = PieceUtil
					.searchPieceById(PieceId.ZU1, chessPoints);
			ChessPiece zu2 = PieceUtil
					.searchPieceById(PieceId.ZU2, chessPoints);
			ChessPiece zu3 = PieceUtil
					.searchPieceById(PieceId.ZU3, chessPoints);
			ChessPiece zu4 = PieceUtil
					.searchPieceById(PieceId.ZU4, chessPoints);
			ChessPiece zu5 = PieceUtil
					.searchPieceById(PieceId.ZU5, chessPoints);
			if (zu1 == null) {
				piece = PieceUtil.createPiece(PieceId.ZU1);
			} else if (zu2 == null) {
				piece = PieceUtil.createPiece(PieceId.ZU2);
			} else if (zu3 == null) {
				piece = PieceUtil.createPiece(PieceId.ZU3);
			} else if (zu4 == null) {
				piece = PieceUtil.createPiece(PieceId.ZU4);
			} else if (zu5 == null) {
				piece = PieceUtil.createPiece(PieceId.ZU5);
			}
			break;

		default:
			break;
		}
		// System.out.println(ch + "对应的棋子：" + piece.getId());
		return piece;
	}
}
