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
import java.util.Vector;

import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.print.part.Position;


/**
 * 游戏记录类
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class GameRecord {
	/**
	 * 棋谱类型
	 * 
	 * @author 雷文
	 * 
	 */
	public enum ManualType {
		NETWORK_RED, NETWORK_BLACK, NETWORK_OBSERVER, NETWORK_JUDGEMENT, PRINT_PARTIAL, PRINT_WHOLE, MAN_MACHINE
	};

	private String desc;// 棋局的描述信息

	private ArrayList<ManualItem> records;// 移动记录

	private ManualType flag;// 棋谱类型

	private String dateAndTime;// 日期和时间

	private Vector<String> descs;// 棋谱描述

	private ArrayList<Position> initLocations;// 棋子的初始位置

	public GameRecord(ManualType flag, String dateAndTime, String desc,
			ArrayList<ManualItem> records, Vector<String> descs,
			ArrayList<Position> initLocations) {

		this.flag = flag;
		this.dateAndTime = dateAndTime;
		this.records = records;
		this.descs = descs;
		this.desc = desc;
		this.initLocations = initLocations;
	}

	public String getDateAndTime() {
		return dateAndTime;
	}

	public String getDesc() {
		return desc;
	}

	public Vector<String> getDescs() {
		return descs;
	}

	public ManualType getFlag() {
		return flag;
	}

	public ArrayList<Position> getInitLocations() {
		return initLocations;
	}

	public ArrayList<ManualItem> getRecords() {
		return records;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setDescs(Vector<String> descs) {
		this.descs = descs;
	}

	public void setFlag(ManualType flag) {
		this.flag = flag;
	}

	public void setInitLocations(ArrayList<Position> initLocations) {
		this.initLocations = initLocations;
	}

	public void setRecords(ArrayList<ManualItem> records) {
		this.records = records;
	}
}