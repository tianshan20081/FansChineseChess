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

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * 中国象棋工具类
 * 
 * 不允许被继承
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public final class ChessUtil {
	/**
	 * 不允许实例化。
	 */
	private ChessUtil() {

	}

	/**
	 * 获取当前的日期和时间:年月日+时分秒
	 * 
	 * @return 当前的日期和时间
	 */
	public static String getDateAndTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取当前的时间:时分秒
	 * 
	 * @return 当前时间
	 * 
	 */
	public static String getTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获得今日日期和星期:2010年3月4日 星期四
	 * 
	 * @return 当前时间
	 */
	public static String getDateAndDay() {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		String today = dateFormat.format(calendar.getTime());

		return today;
	}

	/**
	 * 根据名字获取图标对象
	 * 
	 * @param name
	 *            图片的名字
	 * @return ImageIcon对象或null
	 */
	public static ImageIcon getImageIcon(String name) {
		String path = "img/" + name;
		URL imgURL = ChessUtil.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}
		return null;
	}

	/**
	 * 获取Image对象
	 * 
	 * @param name
	 *            图片的名字
	 * @return Image 对象或null
	 */
	public static Image getImage(String name) {
		String path = "img/" + name;
		URL imgURL = ChessUtil.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		}
		return null;
	}

	/**
	 * 返回应用程序图标所用到的图像
	 * 
	 * @return 图标对象
	 */
	public static Image getAppIcon() {
		String path = "img/piece/" + "shuai.png";
		URL imgURL = ChessUtil.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		}
		return null;
	}

	/**
	 * 返回应用程序图标所用到的图像
	 * 
	 * @return 图标对象
	 */
	public static Image getAppIcon2() {
		String path = "img/piece/" + "jiang.png";
		URL imgURL = ChessUtil.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		}
		return null;
	}

	/**
	 * 根据名字，播放声音目录下的声音文件
	 * 
	 * @param name
	 *            声音文件的名字(带后缀)
	 */
	public static void playSound(String name) {
		String path = "";
		path = "sounds/" + name;
		File file = new File(path);
		InputStream is;
		try {
			is = new FileInputStream(file.getAbsolutePath());
			AudioStream as = new AudioStream(is);
			AudioPlayer.player.start(as);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据名字返回一个URL对象
	 * 
	 * @param name
	 *            帮助文件的名字
	 * @return URL对象或null
	 */
	public static URL getHelpsUrl(String name) {
		String path = "helps/" + name;
		URL url = null;
		try {
			url = ChessUtil.class.getClassLoader().getResource(path);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return url;
	}

	/**
	 * 显示中国象棋帮助对话框
	 * 
	 */
	public static void showHelpDialog() {
		HelpDialog help = new HelpDialog();
		help.setVisible(true);
	}

	/**
	 * 将阿拉伯数字转换成汉字
	 * 
	 * @param num
	 *            阿拉伯数字 1到9
	 * @return 转换之后的汉字
	 */
	public static String numToZi(int num) {
		if (num <= 0 || num >= 10) {
			System.out.println("参数不合法！");
			return "";
		}
		String zi = "";

		switch (num) {
		case 1:
			zi = "一";
			break;
		case 2:
			zi = "二";
			break;
		case 3:
			zi = "三";
			break;
		case 4:
			zi = "四";
			break;
		case 5:
			zi = "五";
			break;
		case 6:
			zi = "六";
			break;
		case 7:
			zi = "七";
			break;
		case 8:
			zi = "八";
			break;
		case 9:
			zi = "九";
			break;
		default:
			System.out.println("参数不合法！");
			break;
		}

		return zi;
	}

	/**
	 * 将数字汉字转换成对应的阿拉伯数字
	 * 
	 * @param zi
	 * @return
	 */
	public static int ziToNum(String zi) {
		int num = 0;

		if (zi.equals("一") || zi.equals("1")) {
			num = 1;
		} else if (zi.equals("二") || zi.equals("2")) {
			num = 2;
		} else if (zi.equals("三") || zi.equals("3")) {
			num = 3;
		} else if (zi.equals("四") || zi.equals("4")) {
			num = 4;
		} else if (zi.equals("五") || zi.equals("5")) {
			num = 5;
		} else if (zi.equals("六") || zi.equals("6")) {
			num = 6;
		} else if (zi.equals("七") || zi.equals("7")) {
			num = 7;
		} else if (zi.equals("八") || zi.equals("8")) {
			num = 8;
		} else if (zi.equals("九") || zi.equals("9")) {
			num = 9;
		}

		return num;
	}

	/**
	 * 将文本内容写入到文本文件中
	 * 
	 * @param path
	 *            文件的路径
	 * @param content
	 *            文本内容
	 */
	public static void writeStringToFile(String path, String content) {
		File file = new File(path);

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			// 如果没有调用flush(),也没有关闭输出流，不会将字符串写入文件中
			writer.close();// 关闭流

			/*
			 * // 测试用的 FileReader reader = new FileReader(file); String record =
			 * ""; String oneLine = ""; BufferedReader bufferedReader = new
			 * BufferedReader(reader); while ((oneLine =
			 * bufferedReader.readLine()) != null) { record += oneLine + "\n"; }
			 * bufferedReader.close();// 关闭流 System.out.println("从文件中读到的内容为:\n"
			 * + record);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示关于对话框
	 * 
	 */
	public static void showAboutDialog() {
		JOptionPane
				.showMessageDialog(
						null,
						"作者:雷文\nQQ:240370818\n\n邮箱:leiwen@fansunion.cn\n日期:2011年10月",
						"关于楚汉棋兵", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 显示欢迎对话框
	 * 
	 */
	public static void showWelcomeDialog() {
		JOptionPane.showMessageDialog(null, "欢迎使用fans象棋对战平台", "欢迎使用fans象棋对战平台",
				JOptionPane.INFORMATION_MESSAGE);

	}

	/**
	 * 测试工具类
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// ChessUtil.playSound("back.mid");
		// File file = new File("sound/back.mid");
		// System.out.println(file.getAbsolutePath());
		// 背景音乐
		URL url = ChessUtil.class.getResource("/sounds/back.mid");
		ChessUtil.playSound("back.mid");
		System.out.println(url);
		// AudioClip bgSound = JApplet.newAudioClip(url);

	}

}
