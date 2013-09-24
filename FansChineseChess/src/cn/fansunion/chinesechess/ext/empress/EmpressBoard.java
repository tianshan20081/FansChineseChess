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
package cn.fansunion.chinesechess.ext.empress;

import java.awt.Color;
import java.awt.event.MouseAdapter;

import cn.fansunion.chinesechess.ColorUtil;
import cn.fansunion.chinesechess.core.ChessBoard;


/**
 * N皇后棋盘
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class EmpressBoard extends ChessBoard {

	private static final long serialVersionUID = 4499247593357571375L;

	@Override
	protected Color getBackgroundColor() {
		return ColorUtil.getEmpressBgcolor();
	}

	@Override
	protected MouseAdapter getMouseAdapter() {
		return null;
	}

	@Override
	protected BoardType getBoardType() {
		return BoardType.eightEmpress;
	}

}
