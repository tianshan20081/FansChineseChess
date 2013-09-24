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
package cn.fansunion.chinesechess.load;

import javax.swing.Icon;

/**
 * 装载列表项
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class IconListItem {
	// 列表项的图标
	private Icon icon;

	// 列表项的文本内容
	private String text;

	/**
	 * @param icon
	 *            列表项的图标
	 * @param text
	 *            列表项的文本
	 */
	public IconListItem(Icon icon, String text) {
		this.icon = icon;
		this.text = text;
	}

	/**
	 * @return 列表项的图标
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @return 列表项的文本
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param icon
	 *            列表项的图标
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * @param text
	 *            列表项的文本内容
	 */
	public void setText(String text) {
		this.text = text;
	}
}