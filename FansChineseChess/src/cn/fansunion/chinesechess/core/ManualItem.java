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

import java.io.Serializable;

import cn.fansunion.chinesechess.core.ChessPiece.PieceId;


/**
 * 棋子走法 记录一条棋谱
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public class ManualItem implements Serializable {

	private static final long serialVersionUID = 259L;

	private PieceId eatedPieceId;// 被吃棋子的ID，悔棋时使用

	private MoveStep moveStep;// 从哪移动到哪

	private PieceId movePieceId;// 移动棋子的ID，暂时不使用

	public ManualItem() {
		moveStep = null;
		eatedPieceId = null;
	}

	public ManualItem(PieceId movePieceId, PieceId eatedPieceId,
			MoveStep moveStep) {
		this.movePieceId = movePieceId;
		this.eatedPieceId = eatedPieceId;
		this.moveStep = moveStep;
	}

	public MoveStep getMoveStep() {
		return moveStep;
	}

	public PieceId getEatedPieceId() {
		return eatedPieceId;
	}

	public void setEatedPieceId(PieceId eatedPieceId) {
		this.eatedPieceId = eatedPieceId;
	}

	public PieceId getMovePieceId() {
		return movePieceId;
	}

	public void setMovePieceId(PieceId movePieceId) {
		this.movePieceId = movePieceId;
	}

	public void setMoveStep(MoveStep moveStep) {
		this.moveStep = moveStep;
	}
}
