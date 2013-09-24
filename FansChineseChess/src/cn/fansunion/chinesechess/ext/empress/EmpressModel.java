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
import java.util.ArrayList;

/**
 * 求N皇后的所有合法布局
 * 
 * 3个约束条件：任何2个棋子都不能占居棋盘上的同一行、或者同一列、或者同一对角线
 * 
 * 棋盘状态的变化情况，可以看作一个N叉树。
 * 
 * 求所有合法布局的过程，即为在3个约束条件下先根遍历状态树的过程。
 * 
 * 遍历中访问结点的操作为，判断棋谱上是否已经得到一个完整的布局，如果是，则输出该布局；
 * 
 * 否则依次先根遍历满足约束条件的各棵子树，即首先判断该子树根的布局是否合法，若合法，则
 * 
 * 先根遍历该子树，否则减去该子树分支。
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class EmpressModel {

	private int num;

	private int[][] array;

	private ArrayList<ArrayList<Point>> lists = new ArrayList<ArrayList<Point>>();

	private ArrayList<int[][]> advancedLists = new ArrayList<int[][]>();

	public EmpressModel(int n) {
		this.num = n;
		array = new int[n + 1][n + 1];// 默认为0
	}

	// 初始化所有的布局
	public void initAllLayout() {
		trial(1);
		sort();
		initAdvancedLists();
	}

	/**
	 * 进入本函数时，在n*n棋盘前j-1列已经放置了满足3个条件的j-1个棋子
	 * 
	 * 现在从第j列起，继续为后续棋子选择合适的位置
	 * 
	 * 选择列优先，是为了保证在GUI显示布局的变化，看起来是，每一行的棋子都是从左向右移动的。
	 * 
	 * 行优先时，每列棋子，从上向下移动。
	 * 
	 * @param j
	 */
	private void trial(int j) {
		// 进入本函数时，在n*n棋盘前j-1列已经放置了满足3个条件的j-1个棋子
		// 现在从第i行起，继续为后续棋子选择合适的位置
		if (j > num) {
			// 求得一个合法布局，保存起来
			saveCurrentLayout();
		} else {
			for (int i = 1; i <= num; i++) {
				// 在第i行第j列放置一个棋子
				array[i][j] = 1;
				if (isLegal(i, j)) {
					trial(j + 1);
				}
				// 移走第i行第j列的棋子
				array[i][j] = 0;
			}
		}
	}

	/**
	 * 判断当前布局是否合法
	 * 
	 * @return
	 */
	private boolean isLegal(int row, int col) {
	
		for (int i = 1; i < array.length; i++) {
			int sumI = 0;// 第i行之和
			int sumJ = 0;// 第i列之和
			for (int j = 1; j < array[i].length; j++) {
				sumI += array[i][j];
				sumJ += array[j][i];
			}
			if (sumI >= 2 || sumJ >= 2) {
				return false;
			}
			sumI = 0;
			sumJ = 0;

		}
		
		// 左上到右下的对角线是否有棋子
		int i = row - 1;
		for (int j = col - 1; j >= 1; j--) {
			if (i >= 1 && array[i][j] == 1) {
				return false;
			}
			i--;
		}

		// 左下到右上的对角线是否有棋子
		i = row + 1;
		for (int j = col - 1; j >= 1; j--) {
			if (i <= this.num && array[i][j] == 1) {
				return false;
			}
			i++;
		}

		return true;
	}

	/**
	 * 保存当前的布局
	 * 
	 */
	private void saveCurrentLayout() {
		ArrayList<Point> list = new ArrayList<Point>();
		for (int i = 1; i < array.length; i++) {
			for (int j = 1; j < array[i].length; j++) {
				if (array[i][j] == 1) {
					list.add(new Point(i, j));
				}
			}
		}
		lists.add(list);
	}

	public void printAllLayout() {
		int size = lists.size();
		for (int i = 0; i < size; i++) {
			ArrayList<Point> arrayList = lists.get(i);
			int size2 = arrayList.size();
			System.out.println("第" + i + "种布局！");
			for (int j = 0; j < size2; j++) {
				Point point = arrayList.get(j);
				System.out.print("(" + (int) point.getX() + ","
						+ (int) point.getY() + ")\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public void printAddLayout2() {
		int size = advancedLists.size();
		for (int i = 0; i < size; i++) {
			int[][] arr = advancedLists.get(i);
			System.out.println("第" + i + "种布局！");
			for (int j = 1; j <= num; j++) {
				for (int k = 1; k <= num; k++) {
					System.out.print(arr[j][k] + "\t");
				}
				System.out.println();
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * 
	 * 排序是为了，减少抖动，即每次变换布局时，近可能少地换棋子
	 */
	public void sort() {
		sortEveryList();
	}

	private void sortEveryList() {
		/*
		 * System.out.println("冒泡排序之前："); printAllLayout();
		 */
		int size = lists.size();
		// 对lists中的每个链表，按照点的纵坐标排序
		for (int q = 0; q < size; q++) {

			ArrayList<Point> arraylist = lists.get(q);

			int size2 = arraylist.size();
			// 冒泡排序
			for (int r = 1; r < size2; r++) {
				// 纵坐标从小到大
				for (int s = 0; s < size2 - r; s++) {
					Point first = arraylist.get(s);
					double m = first.getY();

					Point second = arraylist.get(s + 1);
					double n = second.getY();

					if (m > n) {
						arraylist.set(s + 1, first);
						arraylist.set(s, second);
					}

				}
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EmpressModel ek4 = new EmpressModel(4);
		ek4.initAllLayout();
		// System.out.println("排序之前：");
		ek4.printAllLayout();

		ek4.initAdvancedLists();// 初始化高级保存需要的布局
		ek4.printAddLayout2();

		/*
		 * EightKing ek4 = new EightKing(4); ek4.trial(0);
		 * 
		 * EightKing ek8 = new EightKing(8); ek8.trial(0);
		 */
	}

	public ArrayList<int[][]> getAdvancedLists() {
		return advancedLists;
	}

	private void initAdvancedLists() {
		int size = lists.size();
		for (int index = 0; index < size; index++) {
			ArrayList<Point> list = lists.get(index);
			int[][] arr = new int[num + 1][num + 1];

			int len = list.size();
			for (int i = 0; i < len; i++) {
				int x = (int) list.get(i).getY();
				int y = (int) list.get(i).getX();
				arr[x][y] = 1;
			}

			advancedLists.add(arr);
		}
	}

	public int[][] getArray() {
		return array;
	}

	public int getNum() {
		return num;
	}

	public ArrayList<ArrayList<Point>> getLists() {
		return lists;
	}

}
