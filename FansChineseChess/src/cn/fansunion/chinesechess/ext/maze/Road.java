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
package cn.fansunion.chinesechess.ext.maze;

/**
 * 迷宫求解--记录一个位置的信息
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class Road {

	public IntPoint seat;

	// 默认8个方向都可以走，方向为 从右上到左上，顺时针方向
	public boolean[] directions = { false, false, false, false, true, true,
			true, true };

	public Road(IntPoint curPosition) {
		this.seat = curPosition;
	}

	public Road() {

	}

	public boolean hasDirection() {
		return (directions[0] || directions[1] || directions[2]
				|| directions[3] || directions[4] || directions[5]
				|| directions[6] || directions[7]);
	}
}
