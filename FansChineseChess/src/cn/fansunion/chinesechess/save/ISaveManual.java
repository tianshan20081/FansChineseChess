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
package cn.fansunion.chinesechess.save;

import java.util.ArrayList;

/**
 * 保存棋谱接口,保存棋谱的界面需要实现该接口
 * 
 * @author 雷文
 * @since 2.0
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 */
public interface ISaveManual {
	/**
	 * 获得棋谱的游戏记录信息
	 * 
	 * @return 游戏记录
	 */
	public GameRecord getGameRecord();

	/**
	 * 获得棋谱的保存路径
	 * 
	 * @return 保存路径列表
	 */
	public ArrayList<String> getSavePaths();
}
