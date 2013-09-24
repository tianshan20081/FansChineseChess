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

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * N皇后工具类，主要用于保存N皇后的所有布局。
 * 
 * 一种以最简的形式保存，令一种以完整的形式保存。
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public final class EmpressUtil {
	private EmpressUtil() {

	}

	public static void advancedSave(String path,
			ArrayList<int[][]> advancedLists) {
		File file = new File(path);

		try {
			FileWriter writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			int size = advancedLists.size();
			int n = advancedLists.get(0).length;
			bw.write("3个约束条件：任何2个棋子都不能占居棋盘上的同一行、或者同一列、或者同一对角线.");
			bw.newLine();
			bw.write((n - 1) + "皇后共有" + size + "个布局！");
			bw.newLine();
			for (int index = 0; index < size; index++) {
				bw.write("第" + (index + 1) + "个布局:");
				bw.newLine();
				int[][] array = advancedLists.get(index);

				for (int i = 1; i <= array.length - 1; i++) {
					for (int j = 1; j <= array.length - 1; j++) {
						bw.write(array[i][j] + "  ");
					}

					bw.newLine();
				}

				bw.newLine();
				bw.newLine();

			}

			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save(String path, ArrayList<ArrayList<Point>> lists) {
		File file = new File(path);

		try {
			FileWriter writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			int size = lists.size();
			int n = lists.get(0).size();
			bw.write("3个约束条件：任何2个棋子都不能占居棋盘上的同一行、或者同一列、或者同一对角线.");
			bw.newLine();
			bw.write((n) + "皇后共有" + size + "个布局！");
			bw.newLine();
			for (int index = 0; index < size; index++) {
				bw.write("第" + (index + 1) + "个布局:");
				bw.newLine();
				ArrayList<Point> array = lists.get(index);

				for (int i = 0; i <= array.size() - 1; i++) {
					Point p = array.get(i);
					int x = (int) p.getX();
					int y = (int) p.getY();
					bw.write("(" + x + "," + y + ")  ");
				}
				bw.newLine();
				bw.newLine();

			}

			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
