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
package cn.fansunion.chinesechess;

import java.awt.Color;
/**
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 *
 */
public final class ColorUtil {

	private ColorUtil() {

	}

	// 棋盘的背景色
	public static Color DEFAULT_BGCOLOR = new Color(232, 220, 184);
	
	public static Color getNetworkBgcolor(){
		return DEFAULT_BGCOLOR;
	}
	
	public static Color getPrintWholeBgcolor(){
		return DEFAULT_BGCOLOR;
	}
	
	public static Color getPrintPartialBgcolor(){
		return DEFAULT_BGCOLOR;
	}
	
	public static Color getManMachineBgcolor(){
		return DEFAULT_BGCOLOR;
	}
	
	public static Color getEmpressBgcolor(){
		return DEFAULT_BGCOLOR;
	}
	
	public static Color getHorseMazeBgcolor(){
		return DEFAULT_BGCOLOR;
	}

	public static Color getDefaultBgcolor() {
		return DEFAULT_BGCOLOR;
	}
}
