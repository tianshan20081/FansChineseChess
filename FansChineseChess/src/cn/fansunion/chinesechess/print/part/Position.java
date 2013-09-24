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

import java.awt.Point;
import java.io.Serializable;

import cn.fansunion.chinesechess.core.ChessPiece.PieceId;


/**
 * 残局打谱保存棋谱时，需要记录棋子的初始位置
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class Position implements Serializable {

	private static final long serialVersionUID = 1L;

	private PieceId id;//棋子的id

	private Point point;//棋子的坐标

	public PieceId getId() {
		return id;
	}

	public void setId(PieceId id) {
		this.id = id;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

}
