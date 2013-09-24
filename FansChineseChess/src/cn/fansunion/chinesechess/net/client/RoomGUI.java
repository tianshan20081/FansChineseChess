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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.load.ChessLoadingGUI;
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
import cn.fansunion.chinesechess.net.server.User;


/**
 * 楚汉棋兵客户端
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class RoomGUI extends JFrame implements ActionListener, NAME {

	private static final long serialVersionUID = 101L;

	public boolean isJoin = false;

	JComboBox msgComboBox;

	// 组合框中的备选消息
	String[] initialMsgs = { "见到您真高兴啊", "快点吧，我等到花都谢了", "您的棋走得太好了", "下次再玩吧，我要走了" };

	public JTextArea msgArea;// 便于访问

	// 组合框中的备选用户
	String[] initialUsers = { "所有人" };

	// 用户组合框
	private JComboBox userComboBox;

	List<Player> players = Collections
			.synchronizedList(new ArrayList<Player>());

	/** 创建者列表 */
	List<Creator> creators = Collections
			.synchronizedList(new ArrayList<Creator>());

	PlayerGroupGUI group;

	// 创建游戏、加入游戏、装载棋谱、退出游戏、发送消息按钮
	public JButton create, join, load, exit, send;

	public JMenuBar bar = new JMenuBar();;

	public JMenu fileMenu, helpMenu;

	public JMenuItem welcome, aboutGame, helpContent;

	public JMenuItem createGame, exitGame, loadManual;

	private Socket roomSocket;

	public ObjectOutputStream toServer;

	public ObjectInputStream fromServer;

	public String userName;

	public JTextArea inAndOutArea;

	JTable leftTable, rightTable;

	public RoomGUI(String name) {
		this.userName = name;
		initButtons();
		initMenu();
		initPanels();
		handleKeyEvent();

		setSize(1002, 700);
		setTitle(PropertyReader.get("ROOM_TITLE"));
		setIconImage(ChessUtil.getAppIcon());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// 响应默认的退出事件
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				handleExitGame();
			}

			// 消息组合框获取输入焦点
			public void windowActivated(WindowEvent e) {
				msgComboBox.requestFocusInWindow();
			};

		});
	}

	/**
	 * 初始化左右面板
	 * 
	 */
	private void initPanels() {
		JPanel leftPanel = new JPanel(new BorderLayout());

		JPanel rightPanel = new JPanel();

		int[] widths = { 8, 8, 8, 8 };
		// 初始化左边的面板
		leftTable = new JTable(new CreatorTableModel());
		leftTable.setGridColor(Color.BLUE);

		JTableUtil.fitTableColumns(leftTable, widths);
		TitledBorder gameBorder = new TitledBorder("房间信息");
		leftPanel.setBorder(gameBorder);
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(500, 700));
		leftPanel.setLayout(new BorderLayout());

		JScrollPane creatorScroll = new JScrollPane(leftTable);
		leftTable.setPreferredSize(new Dimension(470, 240));
		leftTable.setRowHeight(20);
		creatorScroll.setPreferredSize(new Dimension(474, 268));

		JPanel creatorPanel = new JPanel();
		creatorPanel.setPreferredSize(new Dimension(480, 300));
		TitledBorder creatorBorder = new TitledBorder("创建者信息");
		creatorPanel.setBorder(creatorBorder);
		creatorPanel.add(creatorScroll);

		JPanel msgPanel = new JPanel(new BorderLayout());
		TitledBorder msgBorder = new TitledBorder("玩家消息");
		msgPanel.setBorder(msgBorder);
		// 玩家消息
		msgArea = new JTextArea();
		msgArea.setRows(8);
		msgArea.setFont(new Font("宋体", Font.PLAIN, 16));
		msgArea.setEditable(false);
		JScrollPane displayScroll = new JScrollPane(msgArea);

		// 消息控制面板
		JPanel sendMsgPanel = new JPanel(new BorderLayout());

		// 发送消息
		msgComboBox = new JComboBox(initialMsgs);
		msgComboBox.setSelectedIndex(-1);
		msgComboBox.setPreferredSize(new Dimension(290, 30));
		msgComboBox.setEditable(true);
		msgComboBox.setToolTipText(PropertyReader.get("MSG_JCOMBOBOX_TOOLTIP"));
		msgComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 发送消息
		userComboBox = new JComboBox(initialUsers);
		// 默认发送给所有人
		userComboBox.setSelectedIndex(0);
		userComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		userComboBox.setToolTipText(PropertyReader
				.get("USER_JCOMBOBOX_TOOLTIP"));
		// 不能编辑用户列表
		userComboBox.setEditable(false);
		userComboBox.setPreferredSize(new Dimension(80, 30));
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(msgComboBox);
		panel.add(send);
		panel.add(userComboBox);
		sendMsgPanel.add(BorderLayout.NORTH, panel);
		msgPanel.add(BorderLayout.CENTER, displayScroll);
		msgPanel.add(BorderLayout.SOUTH, sendMsgPanel);

		JPanel controlPanel = new JPanel();
		controlPanel.add(create);
		controlPanel.add(join);
		controlPanel.add(load);
		controlPanel.add(exit);

		leftPanel.add(BorderLayout.NORTH, creatorPanel);
		leftPanel.add(BorderLayout.CENTER, msgPanel);
		leftPanel.add(BorderLayout.SOUTH, controlPanel);

		// 初始化右边的面板
		rightTable = new JTable(new PlayerTableModel());
		rightTable.setGridColor(Color.BLUE);
		rightTable.setRowHeight(20);
		JTableUtil.fitTableColumns(rightTable, widths);

		rightPanel = new JPanel(new BorderLayout());
		JScrollPane playersScroll = new JScrollPane(rightTable);
		playersScroll.setPreferredSize(new Dimension(470, 430));
		rightPanel.add(BorderLayout.NORTH, playersScroll);
		TitledBorder playerBorder = new TitledBorder("玩家列表");
		rightPanel.setBorder(playerBorder);
		rightPanel.setPreferredSize(new Dimension(500, 700));

		// 玩家进出房间的信息
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

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				leftPanel, rightPanel);
		split.setDividerSize(2);
		split.setDividerLocation(500);

		add(split, BorderLayout.CENTER);

	}

	/**
	 * 初始化控制按钮
	 * 
	 */
	private void initButtons() {
		// 创建游戏、加入游戏、装载游戏、退出游戏按钮宽度一致
		ImageIcon ii = ChessUtil.getImageIcon("create.png");
		int width = ii.getIconWidth();
		int height = ii.getIconHeight();

		/**
		 * 创建图片按钮，图片完全占居按钮表面
		 */
		create = new JButton(ChessUtil.getImageIcon("create.png"));
		create.setPreferredSize(new Dimension(width, height));
		create.setToolTipText(PropertyReader
				.get("CREAGE_GAME__JBUTTON_TOOLTIP"));
		create.setCursor(new Cursor(Cursor.HAND_CURSOR));
		create.addActionListener(this);

		join = new JButton(ChessUtil.getImageIcon("join.png"));
		join.setPreferredSize(new Dimension(width, height));
		join.setToolTipText(PropertyReader.get("JOIN_GAME_JBUTTON_TOOLTIP"));
		join.setCursor(new Cursor(Cursor.HAND_CURSOR));
		join.addActionListener(this);

		load = new JButton(ChessUtil.getImageIcon("load.png"));
		load.setPreferredSize(new Dimension(width, height));
		load.setToolTipText(PropertyReader.get("LOAD_GAME_JBUTTON_TOOLTIP"));
		load.setCursor(new Cursor(Cursor.HAND_CURSOR));
		load.addActionListener(this);

		exit = new JButton(ChessUtil.getImageIcon("exit.png"));
		exit.setPreferredSize(new Dimension(width, height));
		exit.setToolTipText(PropertyReader.get("EXIT_GAME_JBUTTON_TOOLTIP"));
		exit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		exit.addActionListener(this);

		send = new JButton(ChessUtil.getImageIcon("send.png"));
		send.setPreferredSize(new Dimension(width, height));
		send.setToolTipText(PropertyReader.get("SEND_JBUTTON_TOOLTIP"));
		send.setCursor(new Cursor(Cursor.HAND_CURSOR));
		send.addActionListener(this);

	}

	/**
	 * 初始化菜单、注册监听器、添加快捷键
	 * 
	 */
	private void initMenu() {

		fileMenu = new JMenu("楚汉棋兵(C)");

		helpMenu = new JMenu("帮助(H)");

		welcome = new JMenuItem("欢迎", ChessUtil.getImageIcon("welcome.gif"));

		helpContent = new JMenuItem("帮助内容", ChessUtil.getImageIcon("help.gif"));

		aboutGame = new JMenuItem("关于楚汉棋兵", ChessUtil.getImageIcon("info.gif"));

		createGame = new JMenuItem("创建游戏");

		exitGame = new JMenuItem("退出游戏", ChessUtil.getImageIcon("exit.gif"));

		loadManual = new JMenuItem("装载游戏");

		fileMenu.add(createGame);
		fileMenu.add(loadManual);
		fileMenu.add(exitGame);

		fileMenu.setMnemonic(KeyEvent.VK_C);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		helpMenu.add(welcome);
		helpMenu.addSeparator();
		helpMenu.add(helpContent);
		helpMenu.addSeparator();
		helpMenu.add(aboutGame);

		createGame.addActionListener(this);
		loadManual.addActionListener(this);

		welcome.addActionListener(this);
		exitGame.addActionListener(this);
		aboutGame.addActionListener(this);
		helpContent.addActionListener(this);
		bar.add(fileMenu);
		bar.add(helpMenu);
		setJMenuBar(bar);

		// 设置快捷键
		createGame.setAccelerator(KeyStroke.getKeyStroke('C',
				InputEvent.CTRL_DOWN_MASK));
		loadManual.setAccelerator(KeyStroke.getKeyStroke('L',
				InputEvent.CTRL_DOWN_MASK));

		exitGame.setAccelerator(KeyStroke.getKeyStroke('E',
				InputEvent.CTRL_DOWN_MASK));
		welcome.setAccelerator(KeyStroke.getKeyStroke('W',
				InputEvent.CTRL_DOWN_MASK));
		helpContent.setAccelerator(KeyStroke.getKeyStroke('H',
				InputEvent.CTRL_DOWN_MASK));
		aboutGame.setAccelerator(KeyStroke.getKeyStroke('A',
				InputEvent.CTRL_DOWN_MASK));

	}

	/**
	 * 连接到服务器
	 * 
	 * 连接成功返回true，并且初始化输入输出流
	 * 
	 * 失败返回false
	 */
	public boolean connectToServer(String host) {
		boolean flag = false;
		try {
			// 创建一个客户端套接字来连接到服务器端
			roomSocket = new Socket(host, NetworkConstants.PORT);
			msgArea.append(SERVER_NAME + ":" + SYSTEM_WELCOME + "--"
					+ ChessUtil.getTime() + "\n");

			try {
				toServer = new ObjectOutputStream(roomSocket.getOutputStream());
				fromServer = new ObjectInputStream(roomSocket.getInputStream());

				if (fromServer == null) {
					System.out.println("输出流构造失败");
				} else {
					MsgPacket packet = new MsgPacket();
					packet.setMsgType(MsgType.PLAYER_LOGIN);
					packet.setName(userName);
					sendPacket(packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			flag = true;

		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
			System.err.println(ex);
		}
		return flag;

	}

	/**
	 * 响应创建、加入游戏等事件
	 */
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// 创建游戏
		if (source == create || source == createGame) {
			if (creators.size() >= 10) {
				JOptionPane.showMessageDialog(this, "创建者人数已经达到上限啦！",
						"创建者人数已经达到上限啦！", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			// 红帥作为图标
			group = new PlayerGroupGUI(this);
			// 保存自己的角色
			group.role = PlayerRole.ROLE_RED;
			isJoin = true;

			System.out.println("我的角色是创建者:" + PlayerRole.ROLE_RED);
			MsgPacket packet = new MsgPacket();
			packet.setMsgType(MsgType.ADD_CREATOR);
			Creator creator = new Creator();
			User user = new User();
			user.setName(userName);
			creator.setUser(user);
			creator.setDate(ChessUtil.getTime());
			creator.setGameStatus("等待");
			creator.setIp(roomSocket.getLocalAddress().getHostAddress());
			packet.setCreator(creator);
			sendPacket(packet);

			group.setIconImage(ChessUtil.getAppIcon());
			group.setVisible(true);

			setMenuAndButtonEnabled(false);

		}

		// 加入游戏
		else if (source == join) {
			int selectedRow = leftTable.getSelectedRow();
			if (selectedRow == -1) {
				return;
			}
			String name = (String) leftTable.getValueAt(selectedRow, 0);
			int num = (Integer) leftTable.getValueAt(selectedRow, 2);
			String gameStatus = (String) leftTable.getValueAt(selectedRow, 3);
			// 游戏正在进行，禁止加入
			if (gameStatus == CREATOR_PK) {
				JOptionPane.showMessageDialog(this, "游戏正在进行！", "游戏正在进行！",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			// 游戏人已经满了，禁止加入
			if (num >= 4) {
				JOptionPane.showMessageDialog(this, "游戏人数已经达到上限啦！",
						"游戏人数已经达到上限啦！", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			// 红帥作为图标
			group = new PlayerGroupGUI(this);
			// 默认为玩家
			group.role = PlayerRole.ROLE_BLACK;

			isJoin = true;
			group.start.setEnabled(false);
			System.out.println("我的角色是玩家:" + PlayerRole.ROLE_BLACK);

			if ((name != null) && (!name.equals(""))) {
				System.out.println("客户将要发出加入消息");
				MsgPacket packet = new MsgPacket();
				packet.setName(userName);
				packet.setMsgType(MsgType.PLAYER_JOIN_CREATED_GAME);
				Member member = new Member();
				member.setName(userName);
				member.setRole(group.role);
				member.setIp(roomSocket.getInetAddress().getHostAddress());
				for (int i = 0; i < creators.size(); i++) {
					Creator creator = creators.get(i);
					if (creator.getUser().getName().equals(name)) {
						member.setCid(creator.getCid());
						break;
					}
				}
				packet.setMember(member);
				sendPacket(packet);

				group.setVisible(true);
				// 黑將作为图标
				group.setIconImage(ChessUtil.getAppIcon());

				setMenuAndButtonEnabled(false);

			}
		} else if (source == exit || source == exitGame) {
			handleExitGame();
		} else if (source == send) {
			String msg = (String) msgComboBox.getSelectedItem();
			if (msg != null && !msg.equals("")) {// 消息非空
				MsgPacket sp = new MsgPacket();
				// DataPacket dataPacket = new DataPacket();
				sp.setMsgType(MsgType.ROOM_MESSAGE);
				Message message = new Message();
				message.setContent(msg);
				message.setDate(ChessUtil.getDateAndTime());
				// 发送给多个人(今后可能实现)
				String dest = (String) userComboBox.getSelectedItem();
				ArrayList<String> names = new ArrayList<String>();
				names.add(dest);

				if (dest.equals("所有人")) {
					// 共用发消息的代码
					message.setStatus(MessageType.TO_ALL);
				} else {
					sp.names = names;
					message.setStatus(MessageType.TO_SOME);
				}
				sp.setMessage(message);
				// sp.setDataPacket(dataPacket);
				sp.setMsgType(MsgType.ROOM_MESSAGE);
				sp.setName(userName);

				// 共用发消息的代码
				sendPacket(sp);

				msgComboBox.setSelectedIndex(-1);
				msgArea.append(userName + ":" + msg + "\n");
			}
		}
		// 装载棋谱
		else if (source == load || source == loadManual) {
			ChessLoadingGUI test = new ChessLoadingGUI();
			test.setVisible(true);
		}

		// 关于游戏
		else if (source == aboutGame) {
			ChessUtil.showAboutDialog();

		}

		// 游戏规则
		else if (source == helpContent) {
			ChessUtil.showHelpDialog();
		}
		// 欢迎对话框
		else if (source == welcome) {
			JOptionPane.showMessageDialog(this, "欢迎使用楚汉棋兵", "欢迎使用楚汉棋兵",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	/**
	 * 屏蔽或恢复按钮和菜单功能
	 * 
	 * @param isEnable
	 */
	public void setMenuAndButtonEnabled(boolean isEnable) {
		if (isEnable) {
			// 恢复按钮和菜单功能
			create.setEnabled(true);
			join.setEnabled(true);
			load.setEnabled(true);
			exit.setEnabled(true);

			createGame.setEnabled(true);
			loadManual.setEnabled(true);
			exitGame.setEnabled(true);
		} else {
			// 屏蔽按钮和菜单功能
			create.setEnabled(false);
			join.setEnabled(false);
			load.setEnabled(false);
			exit.setEnabled(false);

			createGame.setEnabled(false);
			loadManual.setEnabled(false);
			exitGame.setEnabled(false);
		}
	}

	@SuppressWarnings("unused")
	private void closeSocket() {
		try {
			toServer.close();
			fromServer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** 向服务器发送数据 */
	public void sendPacket(MsgPacket packet) {
		System.out.println("正在向服务器端发送数据！" + "消息状态" + packet.getMsgType());
		try {
			toServer.writeObject(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 响应键盘事件
	 * 
	 */
	private void handleKeyEvent() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			public void eventDispatched(AWTEvent event) {
				if (((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED) {
					int code = ((KeyEvent) event).getKeyCode();
					if (code == KeyEvent.VK_F1) {
						ChessUtil.showHelpDialog();
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

	}

	/** 客户端开始工作 */
	public void work() {
		// 在一个单独的线程中控制游戏
		Thread thread = new Thread(new RoomThread());
		thread.start();
	}

	/**
	 * 核心线程，负责接收服务器的消息
	 * 
	 * @author 火影fans
	 * 
	 */
	private class RoomThread implements Runnable {
		public void run() {
			try {
				/* 继续游戏！ 不断发送和接收消息 */
				while (true) {
					// if (running == RUNNING) {
					receiveInfoFromServer();
					/*
					 * } *else if (running == GAME_EXIT) { //
					 * System.out.println("程序将要退出！"); Thread.sleep(1000); } else
					 * if (running == GAME_PAUSE) { Thread.sleep(1000); }
					 */
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** 从服务器接收消息 */
	private void receiveInfoFromServer() throws Exception {
		MsgPacket serverPacket = new MsgPacket();
		if (fromServer != null) {
			serverPacket = (MsgPacket) fromServer.readObject();
		}
		MsgType status = serverPacket.getMsgType();
		System.out.println("客户端成功收到了服务器的消息" + " 消息状态:" + status);

		switch (status) {

		// 收到了创建者列表数据包
		case CREATOR_LIST:
			System.out.println(userName + "收到创建者列表");
			addCreators(serverPacket);
			break;

		// 收到了玩家列表数据包
		case PLAYER_LIST:
			System.out.println("收到" + serverPacket.getPlayers().size()
					+ "个玩家信息:");
			addPlayers(serverPacket);
			break;

		// 收到了新增玩家数据报
		case ADD_PLAYER:
			addPlayer(serverPacket);
			break;

		// 收到了玩家离开数据包
		case SUB_PLAYER:
			subPlayer(serverPacket);
			break;

		// 有新的玩家创建了游戏
		case ADD_CREATOR:
			System.out.println("客户端成功收到了增加创建者消息");
			addCreator(serverPacket);
			break;

		// 创建者减少了一个
		case SUB_CREATOR:
			subCreator(serverPacket);
			break;

		// 收到了创建者及其成员信息
		case CREATOR_AND_MEMBER:
			System.out.println("客户端成功收到了成员组信息,成员人数为:"
					+ serverPacket.members.size());
			// 更新创建者所在的所有成员的信息
			group.updateGroup(serverPacket);
			break;

		// 玩家加入了创建者创建的游戏
		case PLAYER_JOIN_CREATED_GAME:
			handleJoinGame(serverPacket);
			break;
		// 玩家离开了创建者创建的游戏
		case PLAYER_EXIT_CREATED_GAME:
			handlePlayerExitGame(serverPacket);
			break;

		// 创建者离开了创建的游戏
		case CREATOR_EXIT_CREATED_GAME:
			handleCreatorExitGame(serverPacket);
			break;
		// 创建者开始了创建的游戏
		case CREATOR_START_CREATED_GAME:
			group.handleCreatorStartGame();
			break;

		case CHANGE_GAME_STATUS:
			handleChangeGameStatus(serverPacket);
			break;
		// 有玩家改变了角色
		case CHANGE_ROLE:
			changeRole(serverPacket);
			break;

		// 房间信息
		case ROOM_MESSAGE:
			handleRoomMessage(serverPacket);
			break;

		// 游戏开始之前玩家组内信息
		case GROUP_MESSAGE:
			handleGroupMessage(serverPacket);
			break;

		// 游戏开始之后棋局信息
		case GAME_MESSAGE:
		case PIECE_MOVING:
		case GAME_UNDO:
		case GAME_BACK_YES:
		case GAME_BACK_NO:
		case PLAYER_GIVEIN:
		case GAME_PAUSE:
		case GAME_CONTINUE:
		case GAME_EXIT:
			group.handleDataPacket(serverPacket);
			break;

		default:
			break;

		}
	}

	/**
	 * 更新创建者的游戏状态
	 * 
	 * @param serverPacket
	 */
	private void handleChangeGameStatus(MsgPacket serverPacket) {

		Creator creator = serverPacket.getCreator();
		String name = creator.getUser().getName();

		// 更改创建者列表中某个创建者的游戏状态
		for (int index = 0; index < creators.size(); index++) {
			Creator c = creators.get(index);
			if (c.getUser().getName().equals(name)) {
				c.setGameStatus(CREATOR_PK);
				break;
			}
		}

		// 更改玩家列表中的游戏状态
		for (int i = 0; i < creator.getMembers().size(); i++) {
			String n = creator.getMembers().get(i).getName();

			int playersSize = players.size();
			for (int m = 0; m < playersSize; m++) {
				String name2 = players.get(m).getUser().getName();
				if (name2.equals(n) || name2.equals(name)) {
					players.get(m).setGameStatus(CREATOR_PK);
					break;
				}
			}
		}

	}

	/**
	 * 处理游戏开始之前，小组内的消息
	 * 
	 * @param serverPacket
	 */
	private void handleGroupMessage(MsgPacket serverPacket) {
		// DataPacket dp = serverPacket.getDataPacket();
		Message m = serverPacket.getMessage();
		MessageType flag = m.getStatus();
		if (flag == MessageType.TO_ALL) {
			group.msgArea.append(serverPacket.getName() + ":" + m.getContent()
					+ "\n");
		} else if (flag == MessageType.TO_SOME) {
			group.msgArea.append("[私聊]" + serverPacket.getName() + ":"
					+ m.getContent() + "\n");
		}
	}

	/**
	 * 处理房间消息
	 * 
	 * @param serverPacket
	 */
	private void handleRoomMessage(MsgPacket serverPacket) {
		// DataPacket dp = serverPacket.getDataPacket();
		Message m = serverPacket.getMessage();
		MessageType flag = m.getStatus();
		if (flag == MessageType.TO_ALL) {
			msgArea
					.append(serverPacket.getName() + ":" + m.getContent()
							+ "\n");

		} else if (flag == MessageType.TO_SOME) {
			msgArea.append("私聊:" + serverPacket.getName() + ":"
					+ m.getContent() + "\n");

		} else if (flag == MessageType.SYSTEM) {
			msgArea.append("系统:" + m.getContent() + "\n");

		}

	}

	/**
	 * 有玩家改变了角色，创建者不会改变角色
	 * 
	 */
	private void changeRole(MsgPacket sp) {

		String name = sp.getName();
		PlayerRole role = sp.getRole();
		group.updateRole(name, role);
		System.out.println("有玩家改变了角色，最新角色为：" + "role =" + role);
	}

	/**
	 * 删除一个玩家，并更新玩家列表
	 * 
	 * @param serverPacket
	 */
	private void subPlayer(MsgPacket serverPacket) {
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			String name = serverPacket.getName();
			if (player.getUser().getName().equals(name)) {
				inAndOutArea.append(name + "离开了房间  --" + ChessUtil.getTime()
						+ "\n");
				players.remove(i);
				break;
			}
		}
		updatePlayers();
	}

	private void subCreator(MsgPacket serverPacket) {
		int size = creators.size();
		for (int index = 0; index < size; index++) {
			Creator creator = creators.get(index);
			if (creator.getUser().getName().equals(serverPacket.getName())) {
				System.out.println("删除了一个创建者");
				creators.remove(index);
				break;
			}
		}
		updateCreators();

	}

	/**
	 * 处理创建者退出游戏，提示玩家退出游戏
	 * 
	 * @param serverPacket
	 */
	private void handleCreatorExitGame(MsgPacket serverPacket) {
		String name = serverPacket.getName();
		String n = (String) group.groupTable.getValueAt(0, 0);

		if (userName.equals(n)) {
			// 创建者不需要再提示
			return;
		}
		if (name.equals(n)) {
			group.msgArea.append(name + "退出了游戏");
			// 如果已经退出了，不必再提示
			if (isJoin) {
				JOptionPane.showConfirmDialog(group, "创建者已经退出游戏！您即将退出", "退出",
						JOptionPane.YES_OPTION);
			}
			group.setVisible(false);
			isJoin = false;
			setMenuAndButtonEnabled(true);
		}

	}

	/**
	 * 处理玩家退出游戏
	 * 
	 * @param serverPacket
	 */
	private void handlePlayerExitGame(MsgPacket serverPacket) {
		group.msgArea.append(serverPacket.getName() + "退出了游戏");

		Creator creator = serverPacket.getCreator();

		int size = creators.size();
		for (int i = 0; i < size; i++) {
			if (creators.get(i).getCid() == creator.getCid()) {
				creators.get(i).setMembers(serverPacket.members);
				updateCreators();
			}
		}

		// 如果创建者或成员中有自己，则更新
		// 判断是否需要更新创建者_成员信息
		if (creator.getUser().getName().equals(userName)) {
			System.out.println("有玩家退出，更新成员列表");
			group.updateGroup(serverPacket);
			return;
		}

		// 判断是否需要更新创建者_成员信息
		for (int k = 0; k < serverPacket.members.size(); k++) {
			if (serverPacket.members.get(k).getName().equals(userName)) {
				System.out.println("有玩家退出，更新成员列表");
				group.updateGroup(serverPacket);
				return;
			}
		}

	}

	/**
	 * 处理玩家加入游戏
	 * 
	 * @param serverPacket
	 */
	private void handleJoinGame(MsgPacket serverPacket) {
		group.msgArea.append(serverPacket.getName() + "加入了游戏\n");
		Creator creator = serverPacket.getCreator();

		int size = creators.size();
		for (int i = 0; i < size; i++) {
			if (creators.get(i).getCid() == creator.getCid()) {
				creators.get(i).setMembers(serverPacket.members);
				updateCreators();
			}
		}

		// 如果创建者或成员中有自己，则更新
		// 判断是否需要更新创建者_成员信息
		if (creator.getUser().getName().equals(userName)) {
			group.updateGroup(serverPacket);
			return;
		}

		// 判断是否需要更新创建者_成员信息
		for (int k = 0; k < serverPacket.members.size(); k++) {
			if (serverPacket.members.get(k).getName().equals(userName)) {
				group.updateGroup(serverPacket);
				return;
			}
		}
	}

	/**
	 * 更新客户端界面中的创建者列表信息，
	 * 
	 * 可能有新的创建者
	 * 
	 * 也有可能有新的成员加入，要更改人数
	 * 
	 */
	public void updateCreators() {
		// 先清除创建者列表
		for (int i = 0; i < NetworkConstants.CREATOR_MAX_SIZE; i++) {
			for (int j = 0; j < 4; j++) {
				leftTable.setValueAt("", i, j);
			}
		}
		int size = creators.size();
		for (int index = 0; index < size; index++) {
			System.out.println("玩家收到了新的创建者列表： 人数" + size);
			// 按顺序更新创建者列表
			Creator creator = creators.get(index);
			leftTable.setValueAt(creator.getUser().getName(), index, 0);
			leftTable.setValueAt(creator.getDate(), index, 1);
			leftTable.setValueAt(1 + creator.getMembers().size(), index, 2);
			leftTable.setValueAt(creator.getGameStatus(), index, 3);
		}
	}

	/**
	 * 更新创建者列表
	 * 
	 * @param serverPacket
	 */
	private void addCreators(MsgPacket serverPacket) {
		System.out.println("创建者人数：" + serverPacket.getCreators().size());
		creators = serverPacket.getCreators();
		updateCreators();
	}

	/**
	 * 向创建者列表顶部中加入一个创建者
	 * 
	 * @param serverPacket
	 */
	private void addCreator(MsgPacket serverPacket) {

		Creator creator = serverPacket.getCreator();
		// 将创建者添加到最前面
		creators.add(0, creator);
		updateCreators();
	}

	/**
	 * 向玩家列表中加入一个玩家
	 * 
	 * @param serverPacket
	 */
	private void addPlayer(MsgPacket serverPacket) {
		Player player = serverPacket.getPlayer();
		players.add(player);
		updatePlayers();
		String name = player.getUser().getName();
		String loginTime = ChessUtil.getTime();
		inAndOutArea.append(name + "进入了房间  --" + loginTime + "\n");
	}

	/**
	 * 用数据包中的内容更新玩家列表
	 * 
	 * @param serverPacket
	 */
	private void addPlayers(MsgPacket serverPacket) {
		players = serverPacket.getPlayers();
		updatePlayers();
	}

	/**
	 * 更新玩家列表
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
		System.out.println("现在有" + size + "个玩家");
		for (int i = 0; i < size; i++) {
			Player player = players.get(i);
			rightTable.setValueAt(player.getUser().getName(), i, 0);
			rightTable.setValueAt(player.getIpAddress(), i, 1);
			rightTable.setValueAt(player.getLoginTime(), i, 2);
			rightTable.setValueAt(player.getGameStatus(), i, 3);

			// 更新玩家组合框
			String name = player.getUser().getName();
			if (!name.equals(userName)) {// 没有必要给自己发送信息
				userComboBox.addItem(name);
				System.out.println("试图更新玩家列表:" + player.getIpAddress());
			}
		}

	}

	/**
	 * 处理退出消息
	 * 
	 * 被exit、exitGame和默认的关闭事件所共用
	 * 
	 */
	private void handleExitGame() {
		boolean exit = false;
		int result = JOptionPane.showConfirmDialog(this, "您确定要退出游戏么？",
				"您确定要退出游戏么?", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			if (isJoin) {
				JOptionPane.showMessageDialog(this, "请先退出已经加入的游戏",
						"请先退出已经加入的游戏?", JOptionPane.YES_OPTION);
				return;
			}
			MsgPacket sp = new MsgPacket();
			sp.setMsgType(MsgType.BYE_BYE);
			sp.setName(userName);
			sendPacket(sp);
			// closeSocket();
			System.out.println(userName + "即将退出游戏！");
			exit = true;
		}
		if (exit) {
			System.exit(0);
		}
	}
}
