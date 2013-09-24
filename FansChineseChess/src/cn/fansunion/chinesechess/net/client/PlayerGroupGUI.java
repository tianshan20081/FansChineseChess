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

import java.applet.AudioClip;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.net.common.Creator;
import cn.fansunion.chinesechess.net.common.JTableUtil;
import cn.fansunion.chinesechess.net.common.Member;
import cn.fansunion.chinesechess.net.common.Message;
import cn.fansunion.chinesechess.net.common.MsgPacket;
import cn.fansunion.chinesechess.net.common.PlayerGroupModel;
import cn.fansunion.chinesechess.net.common.Message.MessageType;
import cn.fansunion.chinesechess.net.common.MsgPacket.MsgType;
import cn.fansunion.chinesechess.net.common.MsgPacket.PlayerRole;


/**
 * 玩家组
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class PlayerGroupGUI extends JFrame implements ActionListener,
		NAME {

	private static final long serialVersionUID = 1L;

	JTable groupTable = new JTable();

	// 开始游戏、退出游戏、发送消息对话框
	public JButton start, exit, send;

	// 小组成员组合框
	public JComboBox comboBox;

	// 小组成员列表，不包含本人
	public ArrayList<String> names = new ArrayList<String>();

	// 用户组合框
	private JComboBox userComboBox;

	// 消息组合框
	private JComboBox msgComboBox;

	// 游戏是否已经开始了， 主要用于关闭窗口时判断用的
	private boolean gameStart = false;

	// 组合框中的备选消息
	String[] initialMsgs = { "见到您真高兴啊", "快点吧，我等到花都谢了", "您的棋走得太好了", "下次再玩吧，我要走了" };

	// 组合框中的备选用户
	String[] initialUsers = { "所有人" };

	// 消息文本域
	public JTextArea msgArea;

	// 自己的角色
	public PlayerRole role = null;

	// 父窗口的引用
	RoomGUI parent;

	MatchGUI client = new MatchGUI(this);

	// 背景音乐
	URL url = ChessUtil.class.getResource("/sounds/back.mid");

	AudioClip bgSound = JApplet.newAudioClip(url);
	
	public static String SPACE = "    ";

	public PlayerGroupGUI(RoomGUI parent) {
		this.parent = parent;
		initButtons();

		setSize(650, 700);
		setTitle(PropertyReader.get("GAME_TITLE"));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);

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

		int[] widths = { 60, 60, 80, 32 };
		String[] columnNames = { "玩家名", "角色", "IP地址", "图标" };
		PlayerGroupModel model = new PlayerGroupModel(columnNames, 4);

		groupTable.setPreferredSize(new Dimension(400, 120));
		groupTable.setModel(model);
		groupTable.setGridColor(Color.BLUE);
		groupTable.setRowHeight(32);

		TableColumn sportColumn = groupTable.getColumnModel().getColumn(1);

		comboBox = new JComboBox();
		comboBox.addActionListener(this);
		comboBox.addItem("黑方");
		comboBox.addItem("观察者");
		comboBox.addItem("裁判");
		comboBox.setSelectedIndex(0);
		sportColumn.setCellEditor(new DefaultCellEditor(comboBox));
		groupTable.repaint();

		JTableUtil.fitTableColumns(groupTable, widths);

		JScrollPane scroll = new JScrollPane(groupTable);
		scroll.setPreferredSize(new Dimension(500, 150));

		JPanel playersPanel = new JPanel();
		playersPanel.setPreferredSize(new Dimension(500, 180));
		TitledBorder playersBorder = new TitledBorder("玩家信息");
		playersPanel.setBorder(playersBorder);
		playersPanel.add(scroll);

		JPanel msgPanel = new JPanel(new BorderLayout());
		TitledBorder msgBorder = new TitledBorder("玩家消息");
		msgPanel.setBorder(msgBorder);
		// 玩家消息
		msgArea = new JTextArea();
		msgArea.setRows(10);
		msgArea.setFont(new Font("宋体", Font.PLAIN, 16));
		msgArea.setEditable(false);
		JScrollPane displayScroll = new JScrollPane(msgArea);
		msgPanel.add(BorderLayout.CENTER, displayScroll);

		// 消息控制面板
		JPanel sendMsgPanel = new JPanel(new BorderLayout());

		// 发送消息
		msgComboBox = new JComboBox(initialMsgs);
		msgComboBox.setSelectedIndex(-1);
		msgComboBox.setPreferredSize(new Dimension(400, 30));
		// 可以自己编写消息发送
		msgComboBox.setEditable(true);
		msgComboBox.setToolTipText(PropertyReader.get("MSG_JCOMBOBOX_TOOLTIP"));
		msgComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 发送消息
		userComboBox = new JComboBox(initialUsers);
		// 默认发送给所有人
		userComboBox.setSelectedIndex(0);
		// userComboBox
		// 不能编辑用户列表
		userComboBox.setEditable(false);
		userComboBox.setPreferredSize(new Dimension(80, 30));
		userComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		userComboBox.setToolTipText(PropertyReader
				.get("USER_JCOMBOBOX_TOOLTIP"));

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(msgComboBox);
		panel.add(send);
		panel.add(userComboBox);
		sendMsgPanel.add(BorderLayout.NORTH, panel);
		msgPanel.add(BorderLayout.SOUTH, sendMsgPanel);

		JPanel controlPanel = new JPanel();
		controlPanel.add(start);
		controlPanel.add(exit);

		this.add(BorderLayout.NORTH, playersPanel);
		this.add(BorderLayout.CENTER, msgPanel);
		this.add(BorderLayout.SOUTH, controlPanel);
	}

	/**
	 * 初始化控制按钮
	 * 
	 */
	private void initButtons() {
		// 创建游戏、加入游戏、退出游戏按钮宽度一致
		ImageIcon ii = ChessUtil.getImageIcon("create.png");
		int width = ii.getIconWidth();
		int height = ii.getIconHeight();

		/**
		 * 创建图片按钮，图片完全占居按钮表面
		 */
		start = new JButton(ChessUtil.getImageIcon("start.png"));
		start.setPreferredSize(new Dimension(width, height));
		start.setToolTipText("开始游戏");
		start.setCursor(new Cursor(Cursor.HAND_CURSOR));
		start.addActionListener(this);

		exit = new JButton(ChessUtil.getImageIcon("exit.png"));
		exit.setPreferredSize(new Dimension(width, height));
		exit.setToolTipText("退出游戏");
		exit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		exit.addActionListener(this);

		send = new JButton(ChessUtil.getImageIcon("send.png"));
		send.setPreferredSize(new Dimension(width, height));
		send.setToolTipText("发送消息");
		send.setCursor(new Cursor(Cursor.HAND_CURSOR));
		send.addActionListener(this);
	}

	/**
	 * 更新成员组信息
	 * 
	 * @param serverPacket
	 */
	public void updateGroup(MsgPacket serverPacket) {
		Creator creator = serverPacket.getCreator();
		for (int k = 0; k < serverPacket.members.size(); k++) {
			System.out
					.println("成员组姓名：" + serverPacket.members.get(k).getName());
		}

		// 先删除所有数据
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				groupTable.setValueAt(null, i, j);
			}
		}

		// 更新玩家组表格第一行，创建者的信息
		groupTable.setValueAt(creator.getUser().getName(), 0, 0);
		groupTable.setValueAt("红方", 0, 1);
		groupTable.setValueAt(creator.getIp(), 0, 2);
		groupTable.setValueAt(ChessUtil.getImageIcon("hongshuai.gif"), 0, 3);

		System.out.println("创建者名字：" + creator.getUser().getName());
		System.out.println("成员组人数：" + serverPacket.members.size());
		System.out.println("创建者IP：" + creator.getIp());

		// 更新黑方、观察者、裁判的信息
		for (int index = 0; index < serverPacket.members.size(); index++) {
			Member member = serverPacket.members.get(index);
			PlayerRole role = member.getRole();
			groupTable.setValueAt(member.getName(), index + 1, 0);

			groupTable.setValueAt(member.getIp(), index + 1, 2);

			if (role == PlayerRole.ROLE_BLACK) {
				groupTable.setValueAt("黑方", index + 1, 1);
				groupTable.setValueAt(ChessUtil.getImageIcon("heijiang.gif"),
						index + 1, 3);
			} else if (role == PlayerRole.ROLE_OBSERVER) {
				groupTable.setValueAt("观察者", index + 1, 1);
				groupTable.setValueAt(ChessUtil.getImageIcon("observer.png"),
						index + 1, 3);
			} else if (role == PlayerRole.ROLE_JUDGMENT) {
				groupTable.setValueAt("裁判", index + 1, 1);
				groupTable.setValueAt(ChessUtil.getImageIcon("judgment.png"),
						index + 1, 3);
			}

		}

		// 更新客户端创建者的成员列表
		for (int m = 0; m < parent.creators.size(); m++) {
			List<Member> temp = new ArrayList<Member>();
			Creator creator2 = parent.creators.get(m);

			for (int n = 0; n < serverPacket.members.size(); n++) {
				Member member = serverPacket.members.get(n);

				if (member.getCid() == creator2.getCid()) {
					temp.add(member);
				}
			}
			// 用新的成员列表替换旧的
			creator.setMembers(temp);
		}
		// 更新客户端创建者列表的人数
		// 成员人数可能有变化，要及时更新
		parent.updateCreators();

		// 及时更新玩家列表
		updateNames();
	}

	/**
	 * 创建者开始了游戏
	 * 
	 * @param serverPacket
	 */
	public void handleCreatorStartGame() {
		// 更新消息接收者
		for (int i = 0; i < 4; i++) {
			String name = (String) groupTable.getValueAt(i, 0);
			if ((name != null)
					&& (!name.equals("") && (!name.equals(parent.userName)))) {
				names.add(name);
			}
		}
		updateNames();

		setTitle(PropertyReader.get("GAME_TITLE"));
		setSize(730, 700);
		setIconImage(ChessUtil.getAppIcon2());
		setJMenuBar(client.bar);
		setResizable(false);// 不允许修改界面的大小
		setLocationRelativeTo(null);
		setContentPane(client.getContentPane());
		client.board.userName = parent.userName;
		client.board.client = client;

		// 游戏开始了
		gameStart = true;
		System.out.println("参与者开始了游戏！");

		if (role == PlayerRole.ROLE_BLACK) {
			client.board.initChess(false);
		} else {
			// 观察者和裁判的界面和红方一致
			client.board.initChess(true);
			client.board.myTurn = false;
			// 观察者没有权限暂停、悔棋和认输
			client.pauseGame.setEnabled(false);
			client.pause.setEnabled(false);
			client.giveIn.setEnabled(false);
			client.undo.setEnabled(false);
		}

		// 播放开始声音
		ChessUtil.playSound("begin.wav");
		// 循环播放背景音乐
		bgSound.loop();
		validateAll();
	}

	/**
	 * 事件响应
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		// 开始游戏
		if (source == start) {
			int playerNum = 0;
			String role0 = (String) groupTable.getValueAt(0, 1);
			String role1 = (String) groupTable.getValueAt(1, 1);
			String role2 = (String) groupTable.getValueAt(2, 1);
			String role3 = (String) groupTable.getValueAt(3, 1);

			System.out.println("创建者角色：" + role0);
			System.out.println("玩家1角色：" + role1);
			System.out.println("玩家2角色：" + role2);
			System.out.println("玩家3角色：" + role3);

			if (role1 != null && role1.equals("黑方")) {
				playerNum++;
			}
			if (role2 != null && role2.equals("黑方")) {
				playerNum++;
			}
			if (role3 != null && role3.equals("黑方")) {
				playerNum++;
			}
			if (playerNum != 1) {
				if (playerNum == 0) {
					JOptionPane.showMessageDialog(this, "玩家不够", "玩家不够",
							JOptionPane.YES_OPTION);
					return;
				}
				if (playerNum == 2 || playerNum == 3) {
					JOptionPane.showMessageDialog(this, "玩家过多",
							"只能有2个玩家，可以有观察者", JOptionPane.YES_OPTION);
					return;
				}
			} else {
				// 有且只有2个玩家，创建者只能是玩家
				MsgPacket sp = new MsgPacket();
				sp.setMsgType(MsgType.CREATOR_START_CREATED_GAME);
				sp.setName(parent.userName);

				for (int i = 0; i < parent.creators.size(); i++) {
					String creatorName = (String) groupTable.getValueAt(0, 0);
					if (parent.creators.get(i).getUser().getName().equals(
							creatorName)) {
						sp.setCreator(parent.creators.get(i));
						break;
					}
				}
				parent.sendPacket(sp);
				updateNames();
				// 更新消息接收者

				setTitle(PropertyReader.get("GAME_TITLE"));
				setSize(730, 700);
				setJMenuBar(client.bar);
				setIconImage(ChessUtil.getAppIcon());
				setResizable(false);// 不允许修改界面的大小
				setLocationRelativeTo(null);
				setContentPane(client.getContentPane());

				client.board.initChess(true);
				client.board.client = client;
				client.board.userName = parent.userName;

				// 游戏开始了
				gameStart = true;
				System.out.println("创建者开始了游戏！");

				// 播放开始声音
				ChessUtil.playSound("begin.wav");
				// 循环播放背景音乐
				bgSound.loop();
				validateAll();
			}
		}
		// 退出游戏
		else if (source == exit) {
			handleExitGame();
		} else if (source == comboBox) {
			//
			String chooice = comboBox.getSelectedItem().toString();
			PlayerRole selectedRole =null;
			if(RED_NAME.equals(chooice)){
				selectedRole = PlayerRole.ROLE_RED;
			}else if(BLACK_NAME.equals(chooice)){
				selectedRole = PlayerRole.ROLE_BLACK;
			}else if("".equals(chooice)){
				
			}else if("".equals(chooice)){
				
			}
			// 玩家角色发生改变
			if (selectedRole != role) {
				role = selectedRole;
				updateRole(parent.userName, role);

				MsgPacket sp = new MsgPacket();
				sp.setMsgType(MsgType.CHANGE_ROLE);
				sp.setName(parent.userName);
				sp.setRole(role);
				parent.sendPacket(sp);
			}
			System.out.println("组合框");
			String s = (String) comboBox.getItemAt(comboBox.getSelectedIndex());
			System.out.println("玩家的最新角色：" + s);
		}

		// 发送消息
		else if (source == send) {
			String msg = (String) msgComboBox.getSelectedItem();
			if (msg != null && !msg.equals("")) {// 消息非空
				MsgPacket sp = new MsgPacket();
				// DataPacket dataPacket = new DataPacket();

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
				sp.setMsgType(MsgType.GROUP_MESSAGE);
				sp.setName(parent.userName);

				// 共用发消息的代码
				parent.sendPacket(sp);

				msgComboBox.setSelectedIndex(-1);
				msgArea.append(parent.userName + ":" + msg + "\n");
			}
		}
	}

	/**
	 * 处理玩家自己退出游戏
	 * 
	 */
	private void handleExitGame() {
		int result = JOptionPane.showConfirmDialog(this, "您确定要退出游戏么？",
				"您确定要退出游戏么?", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE);

		if (result == JOptionPane.NO_OPTION) {
			System.out.println("玩家没有退出游戏！");
			return;
		}
		System.out.println("玩家将要离开游戏！");
		MsgPacket packet = new MsgPacket();
		packet.setName(parent.userName);// 离开游戏的角色的名字
		packet.setRole(role);
		if (role == PlayerRole.ROLE_BLACK) {
			packet.setMsgType(MsgType.PLAYER_EXIT_CREATED_GAME);
			// 退出者的角色和所在组的信息
			int size = parent.creators.size();
			for (int index = 0; index < size; index++) {
				Creator creator = parent.creators.get(index);
				for (int m = 0; m < creator.getMembers().size(); m++) {
					// 找到了玩家所在的组的信息
					if (creator.getMembers().get(m).getName().equals(
							parent.userName)) {
						packet.setCreator(creator);
						System.out.println("找到了玩家所在的组的信息！");
						break;
					}
				}
			}

		} else if (role == PlayerRole.ROLE_RED) {
			packet.setMsgType(MsgType.CREATOR_EXIT_CREATED_GAME);
		}

		// 如果已经开始了游戏
		if (gameStart) {
			// packet.setStatus(DATA_PACKET);
			packet.setMsgType(MsgType.GAME_EXIT);

			System.out.println("试图关闭背景音乐！");
			bgSound.stop();
			client.board.getWinkThread().interrupt();

		}
		// 通知Room窗口自己已经没有加入或进行游戏
		parent.isJoin = false;
		// 恢复按钮和菜单功能
		parent.setMenuAndButtonEnabled(true);
		parent.sendPacket(packet);

		this.dispose();

	}

	/**
	 * 处理玩家发来的DataPacket消息
	 * 
	 * @param packet
	 *            数据包
	 */
	public void handleDataPacket(MsgPacket packet) {
		MsgType status = packet.getMsgType();
		// 棋盘的状态栏
		JLabel gameStatus = client.board.gameStatusContent;

		if (status == MsgType.GAME_UNDO) {
			MsgPacket sp = new MsgPacket();
			sp.setName(parent.userName);
			// 请求悔棋的玩家为接收者
			sp.names.add(packet.getName());
			// 是否同意
			int result = JOptionPane.showConfirmDialog(this, "对方请求悔棋，您是否同意？",
					"对方请求悔棋，您是否同意？", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// sp.setStatus(DATA_PACKET);
				sp.setMsgType(MsgType.GAME_BACK_YES);
				// 顺序变反
				client.board.myTurn = !client.board.myTurn;
				if (client.board.myTurn) {
					client.board.gameStatusContent.setText(SPACE
							+ "对方请求悔棋！现在轮到我走喽！");
				} else {
					client.board.gameStatusContent.setText(SPACE
							+ "对方请求悔棋！等待对方走棋！");
				}
				// 记录回滚
				client.board.chessManual.removeManualItem();
			} else {
				sp.setMsgType(MsgType.GAME_BACK_NO);
				// sp.getDataPacket().setStatus(GAME_BACK_NO);
			}
			// 无论是否同意悔棋都发送回答
			parent.sendPacket(sp);
		}
		// 对方同意悔棋
		else if (status == MsgType.GAME_BACK_YES) {
			int size = client.records.getManualItems().size();
			if (size > 0) {
				client.records.removeManualItem();
				client.board.myTurn = !client.board.myTurn;

				if (client.board.myTurn) {
					gameStatus.setText(SPACE + "现在轮到我走喽！");
				} else {
					gameStatus.setText(SPACE + "等待对方走棋！");
				}
			} else {
				gameStatus.setText(gameStatus.getText() + "不能再悔棋了啊！");
			}
			client.msgArea.append(SERVER_NAME + ":" + packet.getName()
					+ "同意了您的悔棋请求！\n");
		} else if (status == MsgType.GAME_BACK_NO) {
			client.msgArea.append(SERVER_NAME + ":" + packet.getName()
					+ "没有同意您的悔棋请求！\n");
		}

		// 收到棋子移动消息
		else if (status == MsgType.PIECE_MOVING) {
			try {
				client.board.handleMoveMessage(packet);
			} catch (Exception e) {
				System.out.println("接收移动消息失败啦！");
				e.printStackTrace();
			}
		}

		// 收到退出消息
		else if (status == MsgType.GAME_EXIT) {
			gameStatus.setText(SPACE + packet.getName() + "已经退出游戏！");
			String name = packet.getName();
			System.out.println(name + "退出了游戏");
			PlayerRole role = packet.getRole();

			if (role == PlayerRole.ROLE_OBSERVER
					|| role == PlayerRole.ROLE_JUDGMENT) {
				client.msgArea.append(SERVER_NAME + ":" + name + "已经退出了游戏！");
			} else if (role == PlayerRole.ROLE_RED
					|| role == PlayerRole.ROLE_BLACK) {
				client.msgArea.append(SERVER_NAME + ":" + name + "已经退出了游戏！");
				if (role == PlayerRole.ROLE_RED) {
					for (int index = 0; index < parent.creators.size(); index++) {
						Creator creator = parent.creators.get(index);
						if (creator.getUser().getName().equals(name)) {
							parent.creators.remove(index);
							parent.updateCreators();
							break;
						}
					}
				}
				client.board.myTurn = false;

				// 禁止某些按钮
				client.pauseGame.setEnabled(false);
				client.pause.setEnabled(false);
				client.giveIn.setEnabled(false);
				client.undo.setEnabled(false);
				// 更新客户端其他成员列表
				names.remove(name);
				if (names.size() == 0) {
					// 其他人都退出了，禁止发送消息
					client.send.setEnabled(false);
				}
				client.updateNames();
			}

		}

		// 收到了投降消息
		else if (status == MsgType.PLAYER_GIVEIN) {
			gameStatus.setText(SPACE + packet.getName() + "已经投降啦！仁兄果然有才啊！");
			client.board.myTurn = false;
			ChessUtil.playSound("gamewin.wav");
		}

		// 收到了对方的暂停消息
		else if (status == MsgType.GAME_PAUSE) {
			client.board.otherIsPause = true;
			if (client.board.isPause) {
				gameStatus.setText(SPACE + "您和对方都已经暂停了游戏！");
			} else {
				gameStatus.setText(SPACE + "对方暂停了游戏！");
			}
		}

		else if (status == MsgType.GAME_CONTINUE) {
			client.board.otherIsPause = false;
			if (client.board.isPause) {
				gameStatus.setText(SPACE + "我已经暂停了游戏！");
			} else {
				if (client.board.myTurn) {
					gameStatus.setText(SPACE + "轮到我走喽！");
				} else {
					gameStatus.setText(SPACE + "等待对方走棋！");
				}
			}
		}

		// 收到了聊天消息,播放声音
		else if (status == MsgType.GAME_MESSAGE) {
			ChessUtil.playSound("msg.wav");
			Message message = packet.getMessage();
			// String date = message.getDate();
			String content = message.getContent();

			String tempMsg = packet.getName() + " " + ChessUtil.getTime()
					+ "\n   " + content + "\n";

			client.addMsg(tempMsg);
			client.msgRecord += tempMsg;
			client.msgArea.setCaretPosition(msgArea.getText().length());
			validate();
			repaint();
		}

	}

	/**
	 * 更新某个玩家的角色
	 * 
	 * @param userName
	 *            玩家的名字
	 * @param role
	 *            最新角色
	 */
	public void updateRole(String userName, PlayerRole role) {
		// 及时更新图标
		for (int i = 0; i < 4; i++) {
			String name = (String) groupTable.getValueAt(i, 0);
			if (name != null && name.equals(userName)) {
				switch (role) {
				case ROLE_RED:
					groupTable.setValueAt("红方", i, 1);
					groupTable.setValueAt(ChessUtil
							.getImageIcon("hongshuai.gif"), i, 3);
					break;
				case ROLE_BLACK:
					groupTable.setValueAt("黑方", i, 1);
					groupTable.setValueAt(ChessUtil
							.getImageIcon("heijiang.gif"), i, 3);
					break;
				case ROLE_OBSERVER:
					groupTable.setValueAt("观察者", i, 1);
					groupTable.setValueAt(ChessUtil
							.getImageIcon("observer.png"), i, 3);
					break;
				case ROLE_JUDGMENT:
					groupTable.setValueAt("裁判", i, 1);
					groupTable.setValueAt(ChessUtil
							.getImageIcon("judgment.png"), i, 3);
					break;
				}
			}
		}
	}

	/**
	 * 更新接收消息的玩家列表
	 * 
	 */
	public void updateNames() {
		// 先清空
		names.clear();
		// 更新消息接收者
		for (int i = 0; i < 4; i++) {
			String name = (String) groupTable.getValueAt(i, 0);
			if ((name != null)
					&& (!name.equals("") && (!name.equals(parent.userName)))) {
				names.add(name);
			}
		}

		// 名字修改之后
		client.updateNames();

		userComboBox.removeAllItems();
		userComboBox.addItem("所有人");

		int size = names.size();
		for (int index = 0; index < size; index++) {
			String name = names.get(index);
			if (!name.equals(parent.userName)) {
				userComboBox.addItem(name);
			}
		}
	}

	/**
	 * 是否开启背景音乐
	 * 
	 * @param flag
	 *            true 开启背景音乐 false 关闭背景音乐
	 */
	public void switchBgsound(boolean flag) {
		if (flag) {
			if (bgSound != null) {
				bgSound.loop();
			}

		} else {
			if (bgSound != null) {
				bgSound.stop();
			}

		}
	}

	public void validateAll() {
		// 更新
		client.board.validate();
		client.board.repaint();
		client.validate();
		client.repaint();
		validate();
		repaint();
	}
}
