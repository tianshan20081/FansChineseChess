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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;

import cn.fansunion.chinesechess.ChessUtil;


/**
 * 高级打谱容纳备用棋子的面板
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class PiecesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private String imgName = "";

	public PiecesPanel(String imgName) {
		this.imgName = imgName;
	}

	public PiecesPanel(GridLayout layout) {
		super(layout);
	}

	protected void paintComponent(Graphics g) { // 重写此方法，可加入自己的图片
		super.paintComponent(g);
		Dimension size = new Dimension(super.getWidth(), super.getHeight());
		g.drawImage(ChessUtil.getImage(imgName), 0, 0, size.width,
				size.height, null);
		//g.drawString("fans", 200, 20);
	}

}
