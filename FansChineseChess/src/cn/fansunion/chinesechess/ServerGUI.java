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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.net.NetworkConstants;
import cn.fansunion.chinesechess.net.common.Creator;
import cn.fansunion.chinesechess.net.common.CreatorTableModel;
import cn.fansunion.chinesechess.net.common.JTableUtil;
import cn.fansunion.chinesechess.net.common.Member;
import cn.fansunion.chinesechess.net.common.Message;
import cn.fansunion.chinesechess.net.common.MsgPacket;
import cn.fansunion.chinesechess.net.common.Player;
import cn.fansunion.chinesechess.net.common.PlayerTableModel;
import cn.fansunion.chinesechess.net.common.Message.MessageType;
import cn.fansunion.chinesechess.net.common.MsgPacket.MsgType;
import cn.fansunion.chinesechess.net.common.MsgPacket.PlayerRole;
import cn.fansunion.chinesechess.net.server.ListenPlayer;
import cn.fansunion.chinesechess.net.server.User;
import cn.fansunion.chinesechess.net.server.UserStream;


/**
 * 中国象棋对战平台服务器
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class ServerGUI extends JFrame implements ActionListener, NAME {

	private static final long serialVersionUID = 1L;

	// 创建者id
	private int cid = 0;

	// 消息组合框
	private JComboBox msgComboBox;

	// 组合框中的备选消息
	private String[] initialMsgs = { "见到您真高兴啊", "快点吧，我等到花都快谢了", "您的棋走得太好了",
			"下次再玩吧，我要走了" };

	// 组合框中的备选用户
	private String[] initialUsers = { "所有人" };

	// 用户组合框
	private JComboBox userComboBox;

	// 发送消息按钮
	private JButton send;

	private JTextArea msgArea;

	private JTextArea inAndOutArea;

	private JTable leftTable, rightTable;

	private ServerSocket serverSocket;

	private final ExecutorService pool;

	/** 玩家列表 */
	private List<Player> players = Collections
			.synchronizedList(new ArrayList<Player>());

	// 保存用户和套接字的信息
	private List<UserStream> userStreams = Collections
			.synchronizedList(new ArrayList<UserStream>());

	/** 创建者列表 */
	public List<Creator> creators = Collections
			.synchronizedList(new ArrayList<Creator>());

	/**
	 * 服务器端应用程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * 首先设置GUI外观样式：本地系统样式
		 */
		String lookAndFeel = null;
		lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			System.out.println("设置本地外观出错！");
			e.printStackTrace();
		}

		ServerGUI serverFrame = new ServerGUI();
		serverFrame.setLocationRelativeTo(null);
		serverFrame.work();
	}

	/**
	 * 构造函数，初始化用户界面
	 */
	public ServerGUI() {
		pool = Executors.newFixedThreadPool(NetworkConstants.POOL_SIZE);
		initButtons();// 初始化按钮
		initMenu();// 初始化菜单
		initPanels();// 初始化面板即界面

		addWindowListener(new WindowAdapter() {
			// 响应默认的退出事件
			public void windowClosing(WindowEvent e) {
				handleExitGame();
			}

			// 消息组合框获取输入焦点
			public void windowActivated(WindowEvent e) {
				msgComboBox.requestFocusInWindow();
			};
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(1002, 700);
		setTitle("楚汉棋兵--服务器--雷文-http://FansUnion.cn");
		setVisible(true);
		// setIconImage(FansUtil.getAppIcon());

	}

	protected boolean handleExitGame() {
		shutdownAndAwaitTermination();
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
		return false;
	}

	/**
	 * 分两个阶段关闭 ExecutorService。第一阶段调用 shutdown 拒绝传入任务，然后调用
	 * shutdownNow（如有必要）取消所有遗留的任务
	 * 
	 * @param pool
	 */
	private void shutdownAndAwaitTermination() {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 初始化面板
	 * 
	 */
	private void initPanels() {
		JPanel leftPanel, rightPanel;

		int[] widths = { 8, 8, 2, 8 };
		// 初始化左边的面板
		leftTable = new JTable(new CreatorTableModel());
		leftTable.setGridColor(Color.BLUE);

		JTableUtil.fitTableColumns(leftTable, widths);

		TitledBorder gameBorder = new TitledBorder("房间信息");
		leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(gameBorder);
		leftPanel.setPreferredSize(new Dimension(500, 700));

		JScrollPane creatorScroll = new JScrollPane(leftTable);
		leftTable.setPreferredSize(new Dimension(470, 240));
		leftTable.setRowHeight(20);
		creatorScroll.setPreferredSize(new Dimension(474, 268));

		// 创建者面板，容纳创建滚动条，滚动条容纳创建者表格
		JPanel creatorPanel = new JPanel();
		creatorPanel.setPreferredSize(new Dimension(480, 300));
		TitledBorder creatorBorder = new TitledBorder("创建者信息");
		creatorPanel.setBorder(creatorBorder);
		creatorPanel.add(creatorScroll);

		// 消息面板
		JPanel msgPanel = new JPanel(new BorderLayout());
		TitledBorder msgBorder = new TitledBorder("玩家消息");
		msgPanel.setBorder(msgBorder);
		// 玩家消息文本域
		msgArea = new JTextArea();
		msgArea.setRows(10);
		msgArea.setFont(new Font("宋体", Font.PLAIN, 16));
		msgArea.setEditable(false);
		JScrollPane displayScroll = new JScrollPane(msgArea);

		// 消息控制面板
		JPanel sendMsgPanel = new JPanel(new BorderLayout());

		// 发送消息
		msgComboBox = new JComboBox(initialMsgs);
		msgComboBox.setSelectedIndex(-1);
		msgComboBox.setPreferredSize(new Dimension(290, 30));
		// 可以自己编写消息发送
		msgComboBox.setEditable(true);
		msgComboBox.setToolTipText(PropertyReader.get("MSG_JCOMBOBOX_TOOLTIP"));
		msgComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		msgComboBox.grabFocus();

		// 发送消息
		userComboBox = new JComboBox(initialUsers);
		// 默认发送给所有人
		userComboBox.setSelectedIndex(0);

		// 不能编辑用户列表
		userComboBox.setEditable(false);
		userComboBox.setPreferredSize(new Dimension(80, 30));
		userComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		userComboBox.setToolTipText(PropertyReader
				.get("USER_JCOMBOBOX_TOOLTIP"));

		// 消息编辑组合框 发送按钮 用户组合框所在的面板
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(msgComboBox);
		panel.add(send);
		panel.add(userComboBox);
		sendMsgPanel.add(BorderLayout.NORTH, panel);

		msgPanel.add(BorderLayout.CENTER, displayScroll);
		msgPanel.add(BorderLayout.SOUTH, sendMsgPanel);

		leftPanel.add(BorderLayout.NORTH, creatorPanel);
		leftPanel.add(BorderLayout.CENTER, msgPanel);

		// 初始化右边的面板
		rightTable = new JTable(new PlayerTableModel());
		rightTable.setGridColor(Color.BLUE);
		rightTable.setRowHeight(20);
		rightTable.setPreferredSize(new Dimension(450, 400));
		JTableUtil.fitTableColumns(rightTable, widths);

		rightPanel = new JPanel(new BorderLayout());
		JScrollPane playersScroll = new JScrollPane(rightTable);
		playersScroll.setPreferredSize(new Dimension(470, 430));
		rightPanel.add(BorderLayout.NORTH, playersScroll);
		TitledBorder playerBorder = new TitledBorder("玩家列表");
		rightPanel.setBorder(playerBorder);
		rightPanel.setPreferredSize(new Dimension(500, 700));

		JPanel inAndOutPanel = new JPanel();
		TitledBorder inAndOutBorder = new TitledBorder("重聚●离别");
		inAndOutPanel.setBorder(inAndOutBorder);

		inAndOutArea = new JTextArea();
		inAndOutArea.setRows(6);
		inAndOutArea.setFont(new Font("宋体", Font.PLAIN, 16));
		inAndOutArea.setEditable(false);
		inAndOutArea.setPreferredSize(new Dimension(440, 130));
		JScrollPane inAndOutAreaScroll = new JScrollPane(inAndOutArea);
		inAndOutAreaScroll.setPreferredSize(new Dimension(450, 150));
		inAndOutPanel.add(inAndOutAreaScroll);
		rightPanel.add(BorderLayout.CENTER, inAndOutPanel);

		// 左右分割条
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				leftPanel, rightPanel);
		split.setDividerSize(2);
		split.setDividerLocation(500);

		add(split, BorderLayout.CENTER);

	}

	/**
	 * 初始化菜单
	 * 
	 */
	private void initMenu() {

	}

	/**
	 * 初始化按钮
	 * 
	 */
	private void initButtons() {
		ImageIcon ii = ChessUtil.getImageIcon("send.png");
		int width = ii.getIconWidth();
		int height = ii.getIconHeight();
		send = new JButton(ChessUtil.getImageIcon("send.png"));
		send.addActionListener(this);
		send.setPreferredSize(new Dimension(width, height));
		send.setToolTipText(PropertyReader.get("SEND_JBUTTON_TOOLTIP"));
		send.setCursor(new Cursor(Cursor.HAND_CURSOR));

	}

	/** 主线程，负责接收新的连接请求 */
	private void work() {
		try {
			// 打开端口号PORT，使客户能够连接上
		 serverSocket = new ServerSocket(NetworkConstants.PORT);

			String time = ChessUtil.getTime();
			msgArea.append(SERVER_NAME + ":开始监听用户请求 --" + time + "\n");
			// 不间断地监听用户请求
			while (true) {
				// 等待客户进程发出连接请求
				Socket playerSocket = serverSocket.accept();

				ObjectOutputStream oos = new ObjectOutputStream(
						playerSocket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(
						playerSocket.getInputStream());

				// 获取玩家帐号和密码等登录信息
				MsgPacket packet = (MsgPacket) ois.readObject();
				System.out.println("成功获得玩家的名字：" + packet.getName());
				MsgType status = packet.getMsgType();

				String name = "fans";
				if (status == MsgType.PLAYER_LOGIN) {
					name = packet.getName();
				}
				String loginTime = ChessUtil.getTime();
				inAndOutArea.append(name + "进入了房间  --" + loginTime + "\n");

				String ipAddress = playerSocket.getInetAddress()
						.getHostAddress();
				String gameStatus = "空闲";

				User user = new User();
				user.setName(name);
				user.setIp(ipAddress);
				Player player = new Player(user, loginTime, ipAddress,
						gameStatus);
				players.add(player);
				// 更新服务器玩家列表
				updatePlayers();

				UserStream userStream = new UserStream(name, oos, ois,
						playerSocket);
				userStreams.add(userStream);

				System.out.println("服务器将要向新玩家发送创建者列表");
				MsgPacket serverPacket2 = new MsgPacket();
				serverPacket2.setMsgType(MsgType.CREATOR_LIST);
				serverPacket2.setCreators(creators);
				sendPacket(oos, serverPacket2);

				System.out.println("服务器将要向新玩家发送玩家列表");
				MsgPacket serverPacket = new MsgPacket();
				serverPacket.setMsgType(MsgType.PLAYER_LIST);
				serverPacket.setPlayers(players);
				oos.writeObject(serverPacket);

				System.out.println("服务器将要向所有玩家发出新玩家加入消息");
				// 向除新玩家之外的所有玩家发出新玩家加入消息
				MsgPacket serverPacket3 = new MsgPacket();
				serverPacket3.setMsgType(MsgType.ADD_PLAYER);
				serverPacket3.setPlayer(player);
				// 通知所有玩家，有新的玩家加入了游戏
				notifyAllPlayers(oos, serverPacket3);

				// 开始一个新的线程处理客户发来的请求
				ListenPlayer task = new ListenPlayer(this, oos, ois,
						playerSocket);
				// 线程池中增加一个新的任务
				pool.execute(task);

				// 回到等待状态，继续接收其他客户进程发来的请求
			}
		} catch (IOException ex) {
			System.err.println(ex);
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新服务器端玩家列表
	 */

	/**
	 * 从套接字的输出流发送数据
	 * 
	 * @param oos
	 *            输出流
	 * @param packet
	 *            数据包
	 */
	public void sendPacket(ObjectOutputStream oos, MsgPacket packet) {
		try {
			if (oos != null) {
				oos.writeObject(packet);
				oos.flush();
				System.out.println("服务器成功发出消息 " + "消息状态" + packet.getMsgType());
			} else {
				System.out.println("输出流为空，没有发送数据哟！");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 通知所有玩家新玩家的加入,不通知指定的输出流
	 * 
	 * @param oos
	 *            输出流
	 * @param packet
	 *            数据包
	 */
	public void notifyAllPlayers(ObjectOutputStream oos, MsgPacket packet) {
		int size = userStreams.size();
		System.out.println("当前有" + size + "个玩家");
		boolean isConnected = true;

		for (int i = 0; i < size; i++) {
			try {
				UserStream userStream = userStreams.get(i);
				Socket s = userStream.getSocket();
				ObjectOutputStream out = userStream.getOos();
				isConnected = s.isConnected() && !s.isClosed();
				if (isConnected) {
					if (out != oos) {
						out.writeObject(packet);
					}
				} else {
					System.out.println("套接字已经关闭了！");
				}
				System.out.println("服务器将要向玩家" + userStream.getName()
						+ "发送新玩家的信息");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通知所有玩家信息
	 * 
	 * @param packet
	 *            数据包
	 * 
	 */
	public void notifyAllPlayers(MsgPacket packet) {
		int size = userStreams.size();
		// System.out.println("当前有" + size + "个玩家");
		// 套接字是否连接着
		boolean isConnected = true;

		for (int i = 0; i < size; i++) {
			try {
				UserStream userStream = userStreams.get(i);
				Socket s = userStream.getSocket();
				isConnected = s.isConnected() && !s.isClosed();
				ObjectOutputStream out = userStream.getOos();
				if (isConnected) {
					if (out != null) {
						out.writeObject(packet);
						System.out.println("服务器将要向玩家" + userStream.getName()
								+ "发送信息 消息状态" + packet.getMsgType());
					} else {
						System.out.println("输出流为空，没能发送数据！");
					}
				} else {
					System.out.println("套接字是关闭着的！");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 减去一个成员
	 * 
	 * @param packet
	 */
	public void subMember(MsgPacket packet) {
		Creator creator = packet.getCreator();
		String name = packet.getName();
		Creator tempCreator = null;
		for (int i = 0; i < creators.size(); i++) {
			tempCreator = creators.get(i);

			if ((creator != null) && (tempCreator.getCid() == creator.getCid())) {
				System.out.println("id为" + creator.getCid() + "的创建者列表有成员退出了！"
						+ name);

				for (int j = 0; j < tempCreator.getMembers().size(); j++) {
					if (tempCreator.getMembers().get(j).getName().equals(name)) {
						tempCreator.getMembers().remove(j);
						break;
					}
				}
			}
		}
		updateCreators();

		/**
		 * 通知所有玩家(不必通知刚刚已经退出的玩家)有玩家退出了已经加入的游戏
		 */
		MsgPacket sp = new MsgPacket();
		sp.setMsgType(MsgType.PLAYER_EXIT_CREATED_GAME);
		sp.setCreator(creator);
		sp.setName(packet.getName());
		if (tempCreator != null) {
			for (int i = 0; i < tempCreator.getMembers().size(); i++) {
				sp.members.add(tempCreator.getMembers().get(i));
			}
		}
		notifyAllPlayers(sp);
	}

	/**
	 * 更新创建者列表
	 */
	public void updateCreators() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				leftTable.setValueAt("", i, j);
			}
		}

		int size = creators.size();
		System.out.println("现在有" + size + "个创建者");

		for (int index = 0; index < size; index++) {
			Creator creator = creators.get(index);
			leftTable.setValueAt(creator.getUser().getName(), index, 0);
			leftTable.setValueAt(creator.getDate(), index, 1);
			leftTable.setValueAt(1 + creator.getMembers().size(), index, 2);
			leftTable.setValueAt(creator.getGameStatus(), index, 3);
		}
	}

	/**
	 * 通知所有玩家，有新的成员加入了
	 * 
	 * 如果加入的是玩家所在的组，还要更新成员信息’
	 * 
	 * @param packet
	 */
	public void addMemer(MsgPacket packet) {
		Member member = packet.getMember();
		int cid = member.getCid();
		MsgPacket membersPacket = new MsgPacket();
		// 加入的玩家的名字
		membersPacket.setName(packet.getName());
		System.out.println("玩家组id为" + cid);
		Creator creator;
		for (int index = 0; index < creators.size(); index++) {
			creator = creators.get(index);
			if (creator.getCid() == cid) {
				System.out.println("一个玩家成功加入成员列表中！");
				creator.getMembers().add(member);
				membersPacket.setMsgType(MsgType.PLAYER_JOIN_CREATED_GAME);
				membersPacket.setCreator(creator);

				for (int m = 0; m < creator.getMembers().size(); m++) {
					membersPacket.members.add(creator.getMembers().get(m));
				}
				break;
			}
		}
		// 更新玩家人数
		updateCreators();
		// 向所有玩家发送新玩家加入游戏消息
		notifyAllPlayers(membersPacket);
	}

	/**
	 * 服务器先给创建者++个id
	 * 
	 * 然后向创建者列表顶部中加入一个创建者
	 * 
	 * 然后将修改之后的创建者信息发给创建者
	 * 
	 * 服务器给创建者发送了2个消息，为了同步2个界面中的信息
	 * 
	 * @param serverPacket
	 */
	public void addCreator(MsgPacket serverPacket) {

		Creator creator = serverPacket.getCreator();
		creator.setCid(cid++);// 创建者id

		creators.add(0, creator);
		updateCreators();

		for (int m = 0; m < creators.size(); m++) {
			serverPacket.getCreators().add(creators.get(m));
		}
		serverPacket.setCreator(creator);
		notifyAllPlayers(serverPacket);

		/**
		 * 服务器向创建者发送修改之后的创建者和成员信息，实际上此时没有其他玩家 如果不重新分配一个数据包，经常遇到状态字段没有改变的问题
		 */
		MsgPacket sp = new MsgPacket();

		sp.setCreator(creator);

		sp.setMsgType(MsgType.CREATOR_AND_MEMBER);
		int num = userStreams.size();
		for (int index = 0; index < num; index++) {
			UserStream userStream = userStreams.get(index);
			if (userStream.getName().equals(creator.getUser().getName())) {
				System.out.println("服务器将向创建者发送创建者和成员信息");
				sendPacket(userStream.getOos(), sp);
				break;
			}
		}

	}

	/**
	 * 从服务器端直接发送更新后的创建者列表，
	 * 
	 * 总是容易出问题(服务器端发送0个创建者，客户端却能收到1个)，原因不知 创建者人数减少了，
	 * 
	 * 通知所有玩家更新创建者列表
	 * 
	 * @param packet
	 */
	public void subCreator(MsgPacket packet) {
		String name = packet.getName();

		int size = creators.size();
		for (int index = 0; index < size; index++) {
			Creator creator = creators.get(index);
			if (creator.getUser().getName().equals(name)) {
				System.out.println("删除了一个创建者");
				creators.remove(index);
				break;
			}
		}
		updateCreators();

		// 通知所有玩家更新创建者列表
		MsgPacket sp = new MsgPacket();
		sp.setMsgType(MsgType.SUB_CREATOR);
		sp.setName(name);
		System.out.println("通知客户端减少一个创建者：");
		notifyAllPlayers(sp);

		// 通知所有玩家创建者退出
		MsgPacket spExit = new MsgPacket();
		spExit.setMsgType(MsgType.CREATOR_EXIT_CREATED_GAME);
		spExit.setName(name);
		notifyAllPlayers(spExit);
	}

	/**
	 * 除去一个玩家的信息,直接发送更新之后的玩家列表，也有问题
	 * 
	 * 只能发送退出游戏的玩家的信息
	 * 
	 * @param packet
	 */
	public void subPlayer(MsgPacket packet) {
		String name = packet.getName();
		inAndOutArea.append(name + "离开了房间  --" + ChessUtil.getTime() + "\n");
		int size = players.size();
		for (int i = 0; i < size; i++) {
			Player player = players.get(i);
			if (player.getUser().getName().equals(name)) {
				System.out.println("服务器玩家列表删除了" + name);
				players.remove(i);
				break;
			}
		}
		updatePlayers();// 更新玩家列表

		// 通知其他玩家有玩家退出，不通知退出的玩家
		int uSize = userStreams.size();
		for (int i = 0; i < uSize; i++) {
			if (userStreams.get(i).getName().equals(name)) {
				MsgPacket sp = new MsgPacket();
				sp.setMsgType(MsgType.SUB_PLAYER);
				sp.setName(name);
				notifyAllPlayers(userStreams.get(i).getOos(), sp);
				break;
			}
		}

		// 通知其他玩家有玩家退出，不通知退出的玩家，然后才能更新套接字
		int userSize = userStreams.size();
		for (int index = 0; index < userSize; index++) {
			if (userStreams.get(index).getName().equals(name)) {
				userStreams.remove(index);
				break;
			}
		}
	}

	/**
	 * 更新玩家列表界面
	 */
	private void updatePlayers() {
		// 先清除所有玩家信息
		for (int i = 0; i < NetworkConstants.PLAYER_MAX_SIZE; i++) {
			for (int j = 0; j < 4; j++) {
				rightTable.setValueAt("", i, j);
			}
		}
		// 更新玩家组合框选项
		userComboBox.removeAllItems();
		userComboBox.addItem("所有人");

		// 增加玩家信息
		int size = players.size();
		for (int i = 0; i < size; i++) {
			Player p = players.get(i);
			// 更新玩家列表
			rightTable.setValueAt(p.getUser().getName(), i, 0);
			rightTable.setValueAt(p.getIpAddress(), i, 1);
			rightTable.setValueAt(p.getLoginTime(), i, 2);
			rightTable.setValueAt(p.getGameStatus(), i, 3);
			// 更新玩家组合框
			userComboBox.addItem(p.getUser().getName());
		}

	}

	/**
	 * 创建者开始了游戏
	 * 
	 * @param packet
	 */
	public void startGame(MsgPacket packet) {
		String name = packet.getName();
		Creator creator = new Creator();
		int size = creators.size();
		for (int index = 0; index < size; index++) {
			creator = creators.get(index);
			if (creator.getUser().getName().equals(name)) {
				// 更新游戏状态
				creator.setGameStatus(CREATOR_PK);
				break;
			}
		}

		int mSize = creator.getMembers().size();
		for (int i = 0; i < mSize; i++) {
			String n = creator.getMembers().get(i).getName();

			int userSize = userStreams.size();
			for (int index = 0; index < userSize; index++) {
				if (userStreams.get(index).getName().equals(n)) {
					sendPacket(userStreams.get(index).getOos(), packet);
					System.out.println("正在向玩家" + n + "发送开始消息！");
				}
			}

			int playersSize = players.size();
			for (int m = 0; m < playersSize; m++) {
				String name2 = players.get(m).getUser().getName();
				if (name2.equals(n)
						|| name2.equals(creator.getUser().getName())) {
					players.get(m).setGameStatus(CREATOR_PK);
				}
			}
		}

		updateCreators();// 更新创建者
		updatePlayers();// 更新玩家

		// 通知所有玩家更新创建者的游戏状态和玩家的游戏的状态
		MsgPacket sp = new MsgPacket();
		sp.setMsgType(MsgType.CHANGE_GAME_STATUS);
		sp.setCreator(creator);
		notifyAllPlayers(sp);

	}

	/**
	 * 负责处理玩家发来的棋局信息
	 * 
	 * 向其他玩家发送棋局的最新信息
	 * 
	 * @param packet
	 */
	public void handleDataPacket(MsgPacket packet) {
		MsgType status = packet.getMsgType();

		// 有人退出了游戏
		if (status == MsgType.GAME_EXIT) {
			// 通知组内其他成员，有人退出了游戏
			notifyGroupOtherPlayers(packet);

			PlayerRole role = packet.getRole();
			String name = packet.getName();
			// 判断其身份 创建者、玩家还是观察者等
			if (role == PlayerRole.ROLE_OBSERVER) {
				System.out.println("观察者" + "退出了游戏");
			} else if (role == PlayerRole.ROLE_BLACK) {

				System.out.println("玩家" + "退出了游戏");
			} else if (role == PlayerRole.ROLE_RED) {
				int size = creators.size();
				for (int index = 0; index < size; index++) {
					Creator creator = creators.get(index);
					if (creator.getUser().getName().equals(name)) {
						// 删除再更新创建者列表
						creators.remove(index);
						updateCreators();
						break;
					}
				}
				// 创建者退出时，通知所有玩家
				MsgPacket sp = new MsgPacket();
				sp.setMsgType(MsgType.SUB_CREATOR);
				sp.setName(packet.getName());
				notifyAllPlayers(sp);
				System.out.println("创建者" + "退出了游戏");
			}

		}

		// 游戏进行时的组内消息
		else if (status == MsgType.GAME_MESSAGE) {
			System.out.println("服务器收到了游戏消息");
			MessageType state = packet.getMessage().getStatus();
			if (state == MessageType.TO_ALL) {
				System.out.println("服务器正在向组内的其他成员发送消息");
				notifyGroupOtherPlayers(packet);
			} else if (state == MessageType.TO_SOME) {
				System.out.println("服务器正在向组内的一个成员发送消息");
				notifyPlayersByName(packet.names, packet);
			}
		} else {

		}

	}

	/**
	 * 处理玩家的角色变换信息
	 * 
	 * @param packet
	 */
	public void handleChangeRole(MsgPacket packet) {
		System.out.println("服务器正在处理改变角色消息！");
		notifyGroupOtherPlayers(packet);
	}

	/**
	 * 给发送消息的玩家所在组的其他成员发送消息
	 * 
	 * @param sp
	 */
	public void notifyGroupOtherPlayers(MsgPacket sp) {
		// 发送消息的人
		String name = sp.getName();
		// 消息的接收者，如果不是玩家退出消息，则最多发送给3个人
		ArrayList<String> dest = new ArrayList<String>();
		Creator creator = new Creator();
		// 查找玩家所在的组

		int size = creators.size();
		for (int index = 0; index < size; index++) {
			creator = creators.get(index);
			// 创建者发出了消息，应该通知其所有成员
			if (creator.getUser().getName().equals(name)) {
				int memberSize = creator.getMembers().size();
				for (int i = 0; i < memberSize; i++) {
					String n = creator.getMembers().get(i).getName();
					dest.add(n);
				}
				break;
			}
			// 是否是其成员发送的
			boolean flag = false;
			int mSize = creator.getMembers().size();
			for (int i = 0; i < mSize; i++) {
				String n = creator.getMembers().get(i).getName();
				if (n.equals(name)) {
					flag = true;
				}
			}

			// 是其成员发送的
			if (flag) {
				dest.add(creator.getUser().getName());
				for (int i = 0; i < creator.getMembers().size(); i++) {
					String n = creator.getMembers().get(i).getName();
					if (!n.equals(name)) {
						dest.add(n);
					}
				}
			}
		}
		this.notifyPlayersByName(dest, sp);
	}

	/**
	 * 根据名字发送消息
	 * 
	 * @param names
	 *            接收消息的玩家的名字
	 * @param sp
	 *            数据包
	 */

	public void notifyPlayersByName(ArrayList<String> names, MsgPacket sp) {
		for (int i = 0; i < names.size(); i++) {
			String n = names.get(i);

			// 查找套接字，发送数据包
			for (int j = 0; j < userStreams.size(); j++) {
				UserStream userStream = userStreams.get(j);
				if (userStream.getName().equals(n)) {
					sendPacket(userStream.getOos(), sp);
				}
			}
		}
	}

	/**
	 * 处理房间消息
	 * 
	 * @param packet
	 */
	public void handleRoomMessage(MsgPacket packet) {
		Message m = packet.getMessage();
		msgArea.append(packet.getName() + ":" + m.getContent() + "\n");
	}

	/**
	 * 服务器端事件响应
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == send) {
			// 在房间内发送消息
			send();
		}
	}

	/**
	 * 服务器端，发送聊天消息
	 */
	private void send() {
		String msg = (String) msgComboBox.getSelectedItem();

		if (msg == null || msg.equals("")) {
			JOptionPane.showMessageDialog(null, "聊天内容不能为空。");
			return;
		}

		// 消息非空
		Message message = new Message();
		message.setContent(msg);
		message.setDate(ChessUtil.getDateAndTime());
		message.setStatus(MessageType.SYSTEM);

		// 构造数据包
		MsgPacket sp = new MsgPacket();
		sp.setMessage(message);
		sp.setMsgType(MsgType.ROOM_MESSAGE);
		sp.setName(SERVER_NAME);

		// 消息的接收者，一个人或所有人
		String dest = (String) userComboBox.getSelectedItem();

		ArrayList<String> names = new ArrayList<String>();
		names.add(dest);

		if (dest.equals("所有人")) {
			// 共用发消息的代码
			notifyAllPlayers(sp);
		} else {
			notifyPlayersByName(names, sp);
		}

		msgComboBox.setSelectedIndex(-1);
		msgArea.append(SERVER_NAME + ":" + msg + "\n");

	}
}
