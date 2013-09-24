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

/**
 * 棋子点
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public class ChessPoint{

	public static final long serialVersionUID = 261L;

	private int x, y;// 棋子点的坐标

	private ChessPiece piece = null;// 棋子的引用

	public ChessPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public ChessPoint(int x, int y, ChessPiece piece, ChessBoard board) {
		this(x, y);
		setPiece(piece, board);
	}

	public ChessPoint() {
	}

	public boolean hasPiece() {
		return piece != null;
	}

	public void setHasPiece(boolean hasPiece) {
		// 很重要
		if (!hasPiece) {
			piece = null;
		}
	}

	public Point getPoint() {
		return new Point(x, y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * 
	 * @param piece
	 *            被删除的棋子
	 * @param board
	 *            棋盘接口
	 */
	public void setPiece(ChessPiece piece, ChessBoard board) {
		if (piece == null) {
			return;
		}
		this.piece = piece;

		if (board == null) {
			return;
		}

		board.addPiece(piece);
		piece.setBounds(x - ChessPiece.UNIT_WIDTH / 2, y
				- ChessPiece.UNIT_HEIGHT / 2, ChessPiece.UNIT_WIDTH,
				ChessPiece.UNIT_HEIGHT);
		if (piece != null && piece.getPosition() != null) {
			System.out.println(piece.getCategory() + "setPiece:"
					+ piece.getPosition().getX() + piece.getPosition().getY());
		}
	}

	// 不更新棋盘界面
	public void setPiece(ChessPiece piece) {
		this.piece = piece;
	}

	/**
	 * 删除棋子
	 * 
	 * @param piece
	 *            被删除的棋子
	 * @param board
	 *            棋盘接口
	 */
	public void removePiece(ChessPiece piece, ChessBoard board) {
		if (piece == null) {
			return;
		}
		if (board == null) {
			return;
		}
		board.removePiece(piece);
		piece = null;
	}

	public ChessPiece getPiece() {
		return piece;
	}

	public Object clone() throws CloneNotSupportedException {
		ChessPoint cp = new ChessPoint();
		cp.x = this.x;
		cp.y = this.y;
		cp.piece = (ChessPiece) this.piece.clone();
		return cp;
	}
}
