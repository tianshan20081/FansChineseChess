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
package cn.fansunion.chinesechess.net.common;

import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author 雷文 2011-12-20
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class PlayerGroupModel extends DefaultTableModel {
	private static final long serialVersionUID = 2;

	public static String[] columnNames = { "玩家名", "角色", "IP地址", "颜色" };

	/*
	 * String[] role = { "玩家", "观察者" };
	 * 
	 * JComboBox box = new JComboBox();
	 * 
	 * private Object[][] data = { { "", box, "", "" }, { "", box, "", "" }, {
	 * "", box, "", "" }, { "", box, "", "" } };
	 */

	public PlayerGroupModel(String[] tableHead, int column) {
		super(tableHead, column);
	}

	public PlayerGroupModel(Vector data, Vector columns) {
		super(data, columns);
	}

	public Class<?> getColumnClass(int c) {

		if (c == 1) {
			return JComboBox.class;
		} else if (c == 3) {
			return Icon.class;
		} else {
			return String.class;
		}
	}

	/**
	 * 只有组合框所在的列是可编辑的
	 */
	public boolean isCellEditable(int row, int col) {
		if (col != 1) {
			return false;
		}
		return true;
	}

	/*
	 * public void setValueAt(Object value, int row, int col) { data[row][col] =
	 * value; fireTableCellUpdated(row, col); }
	 */
}
