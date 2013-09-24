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
package cn.fansunion.chinesechess.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * 定义自己的属性读取文件
 * 
 * JDK自带的Properties方法必须读取ISO-8859-1编码的字符，这样编辑汉字不方便
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class MyPropertyReader {
	private HashMap<String, String> properties;

	private String fileName = "";

	public void setFileName(String name) {
		fileName = name;
	}

	public MyPropertyReader() {

	}

	public MyPropertyReader(String fileName) {
		this.fileName = fileName;
		init();
	}

	private void init() {
		properties = new HashMap<String, String>();
		try {
			FileReader reader = new FileReader(new File(fileName));

			String oneLine = "";
			BufferedReader bufferedReader = new BufferedReader(reader);
			while ((oneLine = bufferedReader.readLine()) != null) {
				// System.out.println(oneLine);
				String[] str = oneLine.split("=");
				System.out.println(str.length);
				if (str != null && str.length == 2) {
					// 必须调用trim防止前后都空格字符
					properties.put(str[0].trim(), str[1].trim());
					// System.out.println("str[0]:" + str[0] + " str[1]:" +
					// str[1]);
				}

			}

			bufferedReader.close();// 关闭流

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String get(String key) {
		return properties.get(key);
	}

	public static void main(String[] args) {
		MyPropertyReader reader = new MyPropertyReader("/config/StringConstants.properties");
		String value =reader.get("1");
		System.out.println(value);
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}
}
