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
package cn.fansunion.chinesechess.net.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.fansunion.chinesechess.config.MyPropertyReader;


/**
 * 观察者工具类
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class ObserverUtil {
	/**
	 * 屏蔽一个字符串中的部分词语
	 * 
	 * @param message
	 * @return 过滤后的字符串
	 */
	public static List<String> filterStrings = new ArrayList<String>();

	private static HashMap<String, String> properties;

	static {
		MyPropertyReader reader = new MyPropertyReader("config/filter.ini");
		properties = reader.getProperties();

	}

	public static String get(String key) {
		return properties.get(key);
	}

	/**
	 * 过滤一个字符串中的一些内容
	 * 
	 * @param message
	 * @return
	 */
	public static String filter(String message) {
		for (Map.Entry<String, String> property : properties.entrySet()) {
			String filter = property.getValue();
			if (message.contains(filter)) {
				message = message.replaceAll(filter, "*");
			}
		}

		return message;
	}

	public static void main(String[] args) {
		display(filterStrings);
		String msg = "馬八进七，快走啊，車八进一";
		System.out.println(filter(msg));
	}

	public static void display(List<String> strings) {
		if (strings == null) {
			return;
		}
		int size = strings.size();
		for (int index = 0; index < size; index++) {
			System.out.print(strings.get(index) + "\t");
		}
		System.out.println();
	}
}
