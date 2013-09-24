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

import java.util.ArrayList;

import cn.fansunion.chinesechess.core.ChessPiece;
import cn.fansunion.chinesechess.core.ChessPoint;
import cn.fansunion.chinesechess.core.ChessRule;


/**
 * 回溯法--迷宫求解算法模型
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class HorseMazeModel implements Runnable {
	private HorseMazeBoard board;

	// 迷宫数组
	private IntPoint[][] myPoints = new IntPoint[10][11];

	// 当前探索路径
	private ArrayList<Road> roads = new ArrayList<Road>();

	// 起始位置
	private IntPoint startPos = null;

	// 终点位置
	private IntPoint endPos = null;

	// 当前位置
	private IntPoint curPos;

	// 起始路径
	private Road startRoad = new Road(startPos);

	// 所有可通路径
	private ArrayList<ArrayList<Road>> allRoads = new ArrayList<ArrayList<Road>>();

	// 是否想走已经走过的路径
	private boolean isBackRoad = false;

	private ChessPiece movePiece;

	private static int TIME = 1500;

	private String tips;

	public HorseMazeModel(HorseMazeBoard board, IntPoint startPos,
			IntPoint endPos) {
		this.board = board;
		this.startPos = startPos;
		this.endPos = endPos;

		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 10; j++) {
				myPoints[i][j] = new IntPoint(i, j);
			}
		}
		curPos = startPos;
		ChessPoint point = board.chessPoints[startPos.i][startPos.j];
		if (point.hasPiece()) {
			movePiece = point.getPiece();
		}
	}

	public boolean hasMazePath() {
		Road road = new Road();
		boolean flag = false;

		do {
			if (curPos != null) {
				System.out
						.println("\n当前位置：(" + curPos.i + "," + curPos.j + ")");
			}
			if (curPos != null) {
				if (isBackRoad) {// 除了走回头路，还有别的路可以选么
					if (road.hasDirection()) {
						System.out.println("回头路 下一条路径！");

						nextPosition(road);

						if (!isBackRoad) {
							road = new Road(curPos);
							roads.add(road);
							showTips("向当前位置的下一个位置探索！");
							System.out.println("(" + road.seat.i + ","
									+ road.seat.j + ")加入到路径中！");
						}
					} else {
						Road temp = roads.remove(roads.size() - 1);// 走回头路删掉当前路径
						showTips("退回到前一个位置！");
						System.out.println("(" + temp.seat.i + ","
								+ temp.seat.j + ")被删除！");
						road = roads.get(roads.size() - 1);
						curPos = road.seat;
						board.movePiece(movePiece, temp.seat.i, temp.seat.j,
								curPos.i, curPos.j);
						sleep(TIME);

						isBackRoad = false;
					}
				} else {
					road = new Road(curPos);
					if (road.hasDirection()) {
						roads.add(road);
						showTips("向当前位置的下一个位置探索");
						System.out.println("(" + road.seat.i + ","
								+ road.seat.j + ")加入到路径中！");
					}// if (isBackRoad)结束
				}// if (curPos != null && curPos.go)结束

				System.out.println("road(" + road.seat.i + "," + road.seat.j
						+ ")");

				// 当前位置是起点，且没有路径可以走了
				if (curPos.i == startPos.i && curPos.j == startPos.j && isEnd()) {
					// 当前路径没有可以走的位置了
					System.out.println("(" + road.seat.i + "," + road.seat.j
							+ ")当前路径没有可以走的位置了");
					return allRoads.size() != 0;
				}

				if (curPos.i == endPos.i && curPos.j == endPos.j) {
					// 如果终点没有加入到路径中，则加入终点
					boolean found = false;
					for (int i = 0; i < roads.size(); i++) {
						IntPoint myPoint = roads.get(i).seat;
						if (myPoint.i == endPos.i && myPoint.j == endPos.j) {
							found = true;
							System.out.println("found = true");
						}
					}// for循环结束

					if (!found) {
						roads.add(new Road(endPos));
					}

					ArrayList<Road> oneRoad = new ArrayList<Road>();
					oneRoad.addAll(roads);
					allRoads.add(oneRoad);
					showTips("找到了一条路径！");
					sleep(1000);

					flag = true;
					System.out.println("找到了一条路径！");
					// 回退
					showTips("回退到当前位置的上一个位置");
					Road endRoad = roads.remove(roads.size() - 1);// 删除终点
					road = roads.get(roads.size() - 1);// 获取最后一个

					board.movePiece(movePiece, endRoad.seat.i, endRoad.seat.j,
							road.seat.i, road.seat.j);
					sleep(TIME);

					nextPosition(road);

				}// if语句结束
				else {// 当前位置不是终点，获得下一个位置
					System.out.println("获得下一个路径！");
					nextPosition(road);
				}
			} else {
				if (!roads.isEmpty()) {
					while ((!road.hasDirection()) && (!roads.isEmpty())) {
						showTips("回退到当前位置的上一个位置");
						Road temp = roads.remove(roads.size() - 1);
						System.out.println("(" + temp.seat.i + ","
								+ temp.seat.j + ")被删除了！");
						if (!roads.isEmpty()) {
							road = roads.get(roads.size() - 1);
							board.movePiece(movePiece, temp.seat.i,
									temp.seat.j, road.seat.i, road.seat.j);
							sleep(TIME);
						}
					}
					if (road.hasDirection()) {
						System.out.print("获得下一个路径22！：");
						nextPosition(road);
					}
				}
			}
		} while (!roads.isEmpty());// 栈非空

		System.out.println("结束了！" + flag);
		return flag;
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 寻找当前路径的下一个路径
	 * 
	 * @param curRoad
	 *            当前路径
	 */
	private void nextPosition(Road curRoad) {
		isBackRoad = false;
		curPos = curRoad.seat;
		int i = curPos.i;
		int j = curPos.j;
		System.out.print("(" + i + "," + j + ")的下一条路径为：");

		int iSize = 9;
		int jSize = 10;
		boolean[] directions = curRoad.directions;
		IntPoint nextPos = null;

		// 右边的4个方向
		if (directions[0]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[0] = false;
				System.out.println("起点0");
			}

			i++;
			j = j - 2;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("0走过了！");
			}
			curRoad.directions[0] = false;
		} else if (directions[1]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[1] = false;
				System.out.println("起点1");
			}

			i = i + 2;
			j--;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("1走过了！");
			}
			curRoad.directions[1] = false;
		} else if (directions[2]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[2] = false;
				System.out.println("起点2");
			}

			i = i + 2;
			j++;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("3走过了！");
			}
			curRoad.directions[2] = false;
		} else if (directions[3]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[3] = false;
				System.out.println("起点3");
			}
			i++;
			j = j + 2;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("3走过了！");
			}
			curRoad.directions[3] = false;

		}

		// 左边的4个方向
		else if (directions[4]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[4] = false;
				System.out.println("起点4");
			}

			i--;
			j = j + 2;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("4走过了！");
			}
			curRoad.directions[4] = false;
		} else if (directions[5]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[5] = false;
				System.out.println("起点5");
			}

			i = i - 2;
			j++;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("5走过了！");
			}
			curRoad.directions[5] = false;
		} else if (directions[6]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[6] = false;
				System.out.println("起点6");
			}

			i = i - 2;
			j--;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("6走过了！");
			}
			curRoad.directions[6] = false;
		} else if (directions[7]) {
			if (i == startPos.i && j == startPos.j) {
				startRoad.directions[7] = false;
				System.out.println("起点7");
			}
			i--;
			j = j - 2;
			if (i >= 1 && j >= 1 && i <= iSize && j <= jSize) {
				nextPos = myPoints[i][j];
				System.out.print("7走过了！");
			}
			curRoad.directions[7] = false;

		}

		System.out.println("(" + i + "," + j + ")");

		int size = roads.size();

		for (int m = 0; m < size; m++) {
			Road r = roads.get(m);
			if (r.seat.i == i && r.seat.j == j) {
				System.out.println("想走回头路!");
				isBackRoad = true;
				break;
			}
		}
		if (!isBackRoad) {
			if (nextPos == null) {
				System.out.println("nextPos == null!");
				// return;
			}
			boolean rule = ChessRule.allRule(movePiece, curPos.i, curPos.j,
					i, j, board.chessPoints);

			// 寻找下一个位置时，必须符合游戏规则，并且不能吃子，
			// 如果可以吃子，第一次探路时，如果吃掉了对方的棋子，那么后面几次探路时，
			// 就少了一些棋子，解决方案有2种，1，不允许吃子；2，吃子后，回退时需要，重新生成被吃的棋子
			if (rule && !board.chessPoints[i][j].hasPiece()) {
				board.movePiece(movePiece, curPos.i, curPos.j, i, j);
				curPos = nextPos;
				sleep(TIME);

			} else {
				isBackRoad = true;//
			}
		}

	}

	/**
	 * 探索是否结束
	 * 
	 * 如果起始路径的4个方向都走过了，则结束
	 */
	private boolean isEnd() {

		boolean left0 = startRoad.directions[4];
		boolean left1 = startRoad.directions[5];
		boolean left2 = startRoad.directions[6];
		boolean left3 = startRoad.directions[7];

		boolean right0 = startRoad.directions[0];
		boolean right1 = startRoad.directions[1];
		boolean right2 = startRoad.directions[2];
		boolean right3 = startRoad.directions[3];

		// 4个方向不能走，或者已经通过,则返回true
		if ((left0 || left1 || left2 || left3 || right0 || right1 || right2 || right3)) {
			return false;
		}
		return true;
	}

	/**
	 * 在控制台界面中，打印所有的路径
	 * 
	 */
	public void printAllRoads() {
		String str = "";
		for (int m = 0; m < allRoads.size(); m++) {
			str += "第" + m + "条路径:";
			System.out.println("第" + m + "条路径！");
			ArrayList<Road> oneRoad = allRoads.get(m);
			int num = oneRoad.size();
			for (int i = 0; i < num; i++) {
				Road road = oneRoad.get(i);
				System.out.print("id:" + i + " (" + road.seat.i + ","
						+ road.seat.j + ")\t");
				str += "(" + road.seat.i + "," + road.seat.j + ")→";
			}
			str += "\r\n";
			System.out.println();
		}
		board.horseMazePath.showTips(str);
		board.horseMazePath.mt = null;

	}

	private void showTips(String tips) {
		this.tips = tips;
		board.horseMazePath.showTips(tips);
	}

	public static void main(String[] args) {
		// String movePath = "8,10,9,8";
		String movePath = "8,10,1,6";
		movePath = movePath.trim();
		String[] path = movePath.split(",");
		if (path.length != 4) {
			System.out.println("坐标点不正确！");
			return;
		}

		int[] startToEnd = new int[4];
		for (int i = 0; i < path.length; i++) {
			startToEnd[i] = Integer.valueOf(path[i]);
		}
		IntPoint start = new IntPoint(startToEnd[0], startToEnd[1]);
		IntPoint end = new IntPoint(startToEnd[2], startToEnd[3]);

		HorseMazeBoard board = new HorseMazeBoard();
		board.initChess(true);

		HorseMazeModel maze = new HorseMazeModel(board, start, end);
		boolean has = maze.hasMazePath();
		if (has) {
			maze.printAllRoads();
		}
	}

	public void run() {
		boolean has = hasMazePath();

		if (has) {
			printAllRoads();
		}
	}

}
