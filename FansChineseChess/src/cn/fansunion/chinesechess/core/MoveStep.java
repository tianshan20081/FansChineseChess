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
import java.io.Serializable;

/**
 * 移动一步
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public class MoveStep implements Serializable {

	private static final long serialVersionUID = 260L;

	// 移动的起点
	public Point start;

	// 移动的终点
	public Point end;

	public MoveStep(Point start, Point end) {
		this.start = start;
		this.end = end;
	}

	public MoveStep() {
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}
}
