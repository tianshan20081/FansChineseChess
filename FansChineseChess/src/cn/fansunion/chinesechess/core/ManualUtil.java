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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.print.part.Position;
import cn.fansunion.chinesechess.save.GameRecord;
import cn.fansunion.chinesechess.save.GameRecord.ManualType;


/**
 * 棋谱工具类
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public final class ManualUtil implements NAME {
	private ManualUtil() {

	}

	/**
	 * 保存棋谱，也保存了当前的时间
	 * 
	 * @param filePath
	 *            棋谱文件的路径
	 * @param descPath
	 *            棋谱文本的存储路径
	 * @param gameRecord
	 *            游戏记录
	 * @return 保存成功返回true，失败返回false
	 */
	public static boolean saveManual(String filePath, String descPath,
			GameRecord gameRecord) {

		/*
		 * 保存棋谱--文本格式--换行 Windows下换行符是\r\n
		 */
		String allDescs = "";
		for (int i = 0; i < gameRecord.getDescs().size(); i++) {
			allDescs += gameRecord.getDescs().get(i) + "\r\n\r\n";
		}
		ChessUtil.writeStringToFile(descPath, allDescs);

		// 保存棋谱文件
		try {
			FileOutputStream outOne = new FileOutputStream(filePath);
			ObjectOutputStream outTwo = new ObjectOutputStream(outOne);

			outTwo.writeObject(gameRecord.getFlag());// 棋谱类型
			outTwo.writeObject(gameRecord.getDateAndTime());// 当前日期和时间
			outTwo.writeObject(gameRecord.getDesc());// 棋谱描述
			outTwo.writeObject(gameRecord.getRecords());// 棋谱记录
			outTwo.writeObject(gameRecord.getDescs());// 棋谱
			outTwo.writeObject(gameRecord.getInitLocations());// 棋子初始位置

			outOne.close();
			outTwo.close();

		} catch (NotSerializableException nse) {
			nse.printStackTrace();
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 从指定的文件中读取游戏记录
	 * 
	 * @param file
	 *            需要读取的文件
	 * @return 从文件中读取的对象
	 */
	@SuppressWarnings("unchecked")
	/**
	 * 
	 */
	public static GameRecord readManual(File file) {
		GameRecord record = null;
		try {
			FileInputStream inOne = new FileInputStream(file);
			ObjectInputStream inTwo = new ObjectInputStream(inOne);
			ManualType flag = null;

			// 根据保存棋谱的格式读数据
			flag = (ManualType) inTwo.readObject();
			String dateAndTime = (String) inTwo.readObject();
			String desc = (String) inTwo.readObject();
			ArrayList<ManualItem> records = (ArrayList<ManualItem>) inTwo
					.readObject();
			Vector<String> descs = (Vector<String>) inTwo.readObject();
			ArrayList<Position> initLocations = null;
			if (flag == ManualType.PRINT_PARTIAL) {
				initLocations = (ArrayList<Position>) inTwo.readObject();
			}
			record = new GameRecord(flag, dateAndTime, desc, records, descs,
					initLocations);

			// 及时关闭流
			inOne.close();
			inTwo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return record;

	}
}
