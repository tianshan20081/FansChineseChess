/**
 * ��Ŀ����: FansChineseChess
 * �汾�ţ�2.0
 * ���֣�����
 * ����: http://FansUnion.cn
 * CSDN:http://blog.csdn.net/FansUnion
 * ����: leiwen@FansUnion.cn
 * QQ��240-370-818
 * ��Ȩ����: 2011-2013,leiwen
 */
package cn.fansunion.chinesechess.save;

import java.util.ArrayList;
import java.util.Vector;

import cn.fansunion.chinesechess.core.ManualItem;
import cn.fansunion.chinesechess.print.part.Position;


/**
 * ��Ϸ��¼��
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class GameRecord {
	/**
	 * ��������
	 * 
	 * @author ����
	 * 
	 */
	public enum ManualType {
		NETWORK_RED, NETWORK_BLACK, NETWORK_OBSERVER, NETWORK_JUDGEMENT, PRINT_PARTIAL, PRINT_WHOLE, MAN_MACHINE
	};

	private String desc;// ��ֵ�������Ϣ

	private ArrayList<ManualItem> records;// �ƶ���¼

	private ManualType flag;// ��������

	private String dateAndTime;// ���ں�ʱ��

	private Vector<String> descs;// ��������

	private ArrayList<Position> initLocations;// ���ӵĳ�ʼλ��

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