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

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * 棋子
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public class ChessPiece extends JLabel {

	private static final long serialVersionUID = 263L;

	public static enum PieceCategory {
		JU, MA, PAO, HONGXIANG, HEIXIANG, HONGSHI, HEISHI, JIANG, SHUAI, ZU, BING
	}// 棋子的种类

	public static enum PieceId {
		HONGJU1, HONGJU2, HONGMA1, HONGMA2, HONGXIANG1, HONGXIANG2, HONGSHI1, HONGSHI2, SHUAI, HONGPAO1, HONGPAO2, BING1, BING2, BING3, BING4, BING5, HEIJU1, HEIJU2, HEIMA1, HEIMA2, HEIXIANG1, HEIXIANG2, HEISHI1, HEISHI2, JIANG, HEIPAO1, HEIPAO2, ZU1, ZU2, ZU3, ZU4, ZU5;
	}

	// 棋盘单元格的宽度和高度
	public static int UNIT_WIDTH = 44;

	public static int UNIT_HEIGHT = 44;

	public static int PIECE_WIDTH = 44;

	public static int PIECE_HEIGHT = 44;

	private PieceCategory category;// 棋子的类别

	private String name;// 黑方或红方

	private int width;// 宽度

	private int height;// 高度

	private PieceId id;// 棋子的id

	private Point position;// 棋子的坐标 1<=x<=9,1<=y<=10

	public ChessPiece() {

	}

	/** 构造函数，初始化棋子的信息 */
	public ChessPiece(PieceId id, String name, PieceCategory category,
			int width, int height) {

		this.id = id;
		this.category = category;
		ImageIcon imageIcon = PieceUtil.getImageIcon("jiang.png");
		this.width = imageIcon.getIconWidth();
		this.height = imageIcon.getIconHeight();
		this.name = name;

		this.setPreferredSize(new Dimension(width, height));

		switch (id) {
		case HONGJU1:
		case HONGJU2:
			setIcon(PieceUtil.getImageIcon("hongju.png"));
			break;

		case HONGMA1:
		case HONGMA2:
			setIcon(PieceUtil.getImageIcon("hongma.png"));
			break;
		case HONGPAO1:
		case HONGPAO2:
			setIcon(PieceUtil.getImageIcon("hongpao.png"));
			break;

		case HONGXIANG1:
		case HONGXIANG2:
			setIcon(PieceUtil.getImageIcon("hongxiang.png"));
			break;

		case HONGSHI1:
		case HONGSHI2:
			setIcon(PieceUtil.getImageIcon("hongshi.png"));
			break;

		case SHUAI:
			setIcon(PieceUtil.getImageIcon("shuai.png"));
			break;

		case BING1:
		case BING2:
		case BING3:
		case BING4:
		case BING5:
			setIcon(PieceUtil.getImageIcon("bing.png"));
			break;

		case JIANG:
			setIcon(PieceUtil.getImageIcon("jiang.png"));
			break;

		case HEISHI1:
		case HEISHI2:
			setIcon(PieceUtil.getImageIcon("heishi.png"));
			break;

		case HEIJU1:
		case HEIJU2:
			setIcon(PieceUtil.getImageIcon("heiju.png"));
			break;

		case HEIPAO1:
		case HEIPAO2:
			setIcon(PieceUtil.getImageIcon("heipao.png"));
			break;

		case HEIXIANG1:
		case HEIXIANG2:
			setIcon(PieceUtil.getImageIcon("heixiang.png"));
			break;

		case HEIMA1:
		case HEIMA2:
			setIcon(PieceUtil.getImageIcon("heima.png"));
			break;

		case ZU1:
		case ZU2:
		case ZU3:
		case ZU4:
		case ZU5:
			setIcon(PieceUtil.getImageIcon("zu.png"));
			break;
		}

		validate();
		repaint();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * 棋子的种类(車、兵、炮等)
	 * 
	 * @return 棋子的种类(車、兵、炮等)
	 */
	public PieceCategory getCategory() {
		return category;
	}

	public PieceId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(PieceId id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
