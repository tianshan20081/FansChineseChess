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
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.config.NAME;
import cn.fansunion.chinesechess.config.PropertyReader;
import cn.fansunion.chinesechess.core.ChessManual;
import cn.fansunion.chinesechess.net.common.Message;
import cn.fansunion.chinesechess.net.common.MsgPacket;
import cn.fansunion.chinesechess.net.common.Message.MessageType;
import cn.fansunion.chinesechess.net.common.MsgPacket.MsgType;
import cn.fansunion.chinesechess.save.GameRecord;
import cn.fansunion.chinesechess.save.ISaveManual;
import cn.fansunion.chinesechess.save.MsgRecordDialog;
import cn.fansunion.chinesechess.save.SaveAsDialog;
import cn.fansunion.chinesechess.save.SaveDialog;
import cn.fansunion.chinesechess.save.GameRecord.ManualType;


/**
 * 楚汉棋兵客户端
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 1.0
 */
public class MatchGUI extends JFrame implements ActionListener, ISaveManual,
		NAME {

	private static final long serialVersionUID = 101L;

	// 控制面板，用来存放控制按钮
	private JPanel control, gameStatusPanel;

	// 暂停、悔棋、认输、发送消息按钮
	public JButton exit, undo, pause, save, giveIn, qiuhe, send;

	// 棋盘
	public NetworkBoard board = null;

	public ChessManual records = null;

	JComboBox msgComboBox;

	// 菜单及其选项
	public JMenuBar bar;

	JMenu fileMenu, settingMenu, chattingMenu, helpMenu;

	JMenuItem saveManual, saveManualAs, pauseGame, exitGame;

	JMenuItem setting, helpContent, aboutGame, saveMsgAs, clearMsg, recoverMsg,
			msgHistory, welcome;

	JCheckBoxMenuItem bgSound;

	public String msgRecord = "";// 保存消息历史记录

	private Cursor handCursor;

	// 游戏状态
	JLabel gameStatusContent, gameStatusIcon;

	// 保留父窗口的引用
	public PlayerGroupGUI parent;

	// 组合框中的备选消息
	String[] initialMsg = { "见到您真高兴啊", "快点吧，我等到花都谢了", "您的棋走得太好了", "下次再玩吧，我要走了" };

	public JTextArea msgArea;// 便于访问

	// 组合框中的备选用户
	String[] initialUsers = { "所有人" };

	// 用户组合框
	private JComboBox userComboBox;

	public static String SPACE = "    ";

	/**
	 * 构造函数
	 * 
	 * @param parent
	 *            父窗口的引用
	 */
	public MatchGUI(PlayerGroupGUI parent) {
		this.parent = parent;
		handCursor = new Cursor(Cursor.HAND_CURSOR);

		// 初始化有顺序
		initButtons();// 初始化按钮
		initMenus();// 初始化菜单

		board = new NetworkBoard();
		records = board.chessManual;

		initPanels();// 初始化面板
		// 响应默认的退出事件
		addWindowListener(new WindowAdapter() {
			// 消息组合框获取输入焦点
			public void windowActivated(WindowEvent e) {
				msgComboBox.requestFocusInWindow();
			};
		});
		handleKeyEvent();
	}

	/**
	 * 响应键盘事件
	 * 
	 */
	private void handleKeyEvent() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			public void eventDispatched(AWTEvent event) {

				KeyEvent keyEvent = (KeyEvent) event;
				int id = keyEvent.getID();

				if (id == KeyEvent.KEY_PRESSED) {
					int code = (keyEvent).getKeyCode();
					if (code == KeyEvent.VK_F1) {
						ChessUtil.showHelpDialog();
					}
				}

				// 发送消息
				else if (id == (KeyEvent.VK_ENTER & KeyEvent.CTRL_MASK)) {

				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

	}

	/**
	 * 初始化面板
	 * 
	 */
	private void initPanels() {
		// 构造右边的面板
		JPanel rightPanel = new JPanel(new BorderLayout());

		// 棋局信息面板
		JPanel recordsPanel = new JPanel(new BorderLayout());

		TitledBorder recordsBorder = new TitledBorder(PropertyReader
				.get("CHESS_MESSAGE_TOOLTIP"));
		recordsPanel.setBorder(recordsBorder);
		recordsPanel.setPreferredSize(new Dimension(240, 200));
		recordsPanel.add(BorderLayout.CENTER, records);

		// 玩家消息面板
		BorderLayout msgLayout = new BorderLayout();
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(msgLayout);
		TitledBorder msgBorder = new TitledBorder(PropertyReader
				.get("PLAYER_MESSAGE_TOOLTIP"));
		msgPanel.setBorder(msgBorder);

		msgArea = new JTextArea();
		msgArea.setToolTipText(PropertyReader.get("PLAYER_MESSAGE_TOOLTIP"));
		msgArea.setRows(7);
		msgArea.setFont(new Font("宋体", Font.PLAIN, 16));
		msgArea.setEditable(false);
		msgArea.setWrapStyleWord(true);
		msgArea.setLineWrap(true);
		JScrollPane displayScroll = new JScrollPane(msgArea);

		// board.msgArea = msgArea;
		// 发送消息
		msgComboBox = new JComboBox(initialMsg);
		msgComboBox.setSelectedIndex(-1);
		msgComboBox.setEditable(true);
		msgComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		msgComboBox.setToolTipText(PropertyReader.get("MSG_JCOMBOBOX_TOOLTIP"));

		// 发送消息
		userComboBox = new JComboBox(initialUsers);
		// 默认发送给所有人
		userComboBox.setSelectedIndex(0);
		userComboBox.setCursor(handCursor);
		userComboBox.setToolTipText(PropertyReader
				.get("USER_JCOMBOBOX_TOOLTIP"));
		// 不能编辑用户列表
		userComboBox.setEditable(false);
		userComboBox.setPreferredSize(new Dimension(80, 30));

		// 消息控制面板
		JPanel sendMsgPanel = new JPanel(new BorderLayout());
		sendMsgPanel.add(BorderLayout.NORTH, msgComboBox);

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(send);
		panel.add(userComboBox);
		sendMsgPanel.add(BorderLayout.CENTER, panel);

		msgPanel.add(BorderLayout.CENTER, displayScroll);
		msgPanel.add(BorderLayout.SOUTH, sendMsgPanel);

		// 在右边的面板中加入记录面板和消息面板
		rightPanel.add(BorderLayout.NORTH, recordsPanel);
		rightPanel.add(BorderLayout.CENTER, msgPanel);

		// 分割栏
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				board, rightPanel);
		split.setDividerSize(5);
		split.setDividerLocation(460);
		add(split, BorderLayout.CENTER);

		// 控制面板
		control = new JPanel();
		BorderLayout layout = new BorderLayout();
		control.setLayout(layout);

		JPanel controlPanel = new JPanel();

		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		controlPanel.setLayout(flowLayout);

		TitledBorder controlBorder = new TitledBorder("游戏控制");

		controlPanel.setBorder(controlBorder);

		controlPanel.add(pause);
		controlPanel.add(undo);
		controlPanel.add(giveIn);
		controlPanel.add(qiuhe);
		controlPanel.add(exit);
		controlPanel.add(save);

		control.add(BorderLayout.CENTER, controlPanel);

		// 游戏状态面板
		gameStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		gameStatusPanel.setPreferredSize(new Dimension(660, 80));
		gameStatusIcon = new JLabel(ChessUtil.getImageIcon("hongshuai.png"));
		// 游戏状态栏
		gameStatusContent = new JLabel("红方先走棋");
		gameStatusContent.setFont(new Font("宋体", Font.PLAIN, 16));
		// gameStatus.setForeground(Color.BLACK);

		TitledBorder gameStatusBorder = new TitledBorder("游戏状态");
		gameStatusBorder.setTitleColor(Color.RED);
		gameStatusBorder.setTitleFont(new Font("宋体", Font.PLAIN, 16));
		gameStatusPanel.setToolTipText("游戏状态");
		gameStatusPanel.setBorder(gameStatusBorder);

		gameStatusPanel.add(gameStatusIcon);
		gameStatusPanel.add(gameStatusContent);

		control.add(BorderLayout.SOUTH, gameStatusPanel);

		add(BorderLayout.SOUTH, control);
		board.gameStatusContent = gameStatusContent;

	}

	/**
	 * 初始化控制按钮
	 * 
	 */
	private void initButtons() {
		// 暂停、悔棋、认输、退出、保存按钮宽度一致

		ImageIcon ii = ChessUtil.getImageIcon("save.png");
		int width = ii.getIconWidth();
		int height = ii.getIconHeight();

		/**
		 * 创建图片按钮，图片完全占居按钮表面
		 * 
		 * 设置按钮的图标，大小，提示和鼠标样式
		 */
		save = new JButton(ChessUtil.getImageIcon("save.png"));
		save.setPreferredSize(new Dimension(width, height));
		save.setToolTipText(PropertyReader.get("SAVE_JBUTTON_TOOLTIP"));
		save.setCursor(handCursor);
		save.addActionListener(this);

		pause = new JButton(ChessUtil.getImageIcon("pause.png"));
		pause.setPreferredSize(new Dimension(width, height));
		pause.setToolTipText(PropertyReader.get("PAUSE_JBUTTON_TOOLTIP"));
		pause.setCursor(handCursor);
		pause.addActionListener(this);

		undo = new JButton(ChessUtil.getImageIcon("undo.png"));
		undo.setPreferredSize(new Dimension(width, height));
		undo.setToolTipText(PropertyReader.get("UNDO_JBUTTON_TOOLTIP"));
		undo.setCursor(handCursor);
		undo.addActionListener(this);

		giveIn = new JButton(ChessUtil.getImageIcon("giveIn.png"));
		giveIn.setPreferredSize(new Dimension(width, height));
		giveIn.setToolTipText(PropertyReader.get("GIVEIN_JBUTTON_TOOLTIP"));
		giveIn.setCursor(handCursor);
		giveIn.addActionListener(this);

		qiuhe = new JButton(ChessUtil.getImageIcon("qiuhe.png"));
		qiuhe.setPreferredSize(new Dimension(width, height));
		qiuhe.setToolTipText(PropertyReader.get("qiuhe"));
		qiuhe.setCursor(handCursor);
		qiuhe.addActionListener(this);

		exit = new JButton(ChessUtil.getImageIcon("exit2.png"));
		exit.setPreferredSize(new Dimension(width, height));
		exit.setToolTipText(PropertyReader.get("EXIT_GAME_JBUTTON_TOOLTIP"));
		exit.setCursor(handCursor);
		exit.addActionListener(this);

		ImageIcon sendIcon = ChessUtil.getImageIcon("send.png");
		send = new JButton(sendIcon);
		send.setToolTipText(PropertyReader.get("SEND_JBUTTON_TOOLTIP"));
		send.setCursor(handCursor);
		send.setPreferredSize(new Dimension(sendIcon.getIconWidth(), sendIcon
				.getIconHeight()));
		send.addActionListener(this);

	}

	/**
	 * 初始化菜单
	 * 
	 */
	private void initMenus() {
		// 构造菜单
		bar = new JMenuBar();
		fileMenu = new JMenu("楚汉棋兵(G)");

		pauseGame = new JMenuItem("暂停游戏");
		saveManual = new JMenuItem("保存棋谱", ChessUtil.getImageIcon("save.gif"));
		saveManualAs = new JMenuItem("另存棋谱", ChessUtil
				.getImageIcon("saveas.gif"));
		exitGame = new JMenuItem("退出游戏", ChessUtil.getImageIcon("exit.gif"));

		fileMenu.add(saveManual);
		fileMenu.add(saveManualAs);
		fileMenu.add(pauseGame);
		fileMenu.add(exitGame);
		bar.add(fileMenu);

		chattingMenu = new JMenu("聊天(C)");
		msgHistory = new JMenuItem("聊天记录");
		saveMsgAs = new JMenuItem("聊天记录另存为", ChessUtil
				.getImageIcon("saveas.gif"));
		clearMsg = new JMenuItem("清空界面信息");
		recoverMsg = new JMenuItem("恢复界面信息");

		chattingMenu.add(msgHistory);
		chattingMenu.add(saveMsgAs);
		chattingMenu.add(clearMsg);
		chattingMenu.add(recoverMsg);

		bar.add(chattingMenu);

		helpMenu = new JMenu("帮助(H)");
		welcome = new JMenuItem("欢迎", ChessUtil.getImageIcon("welcome.gif"));
		helpContent = new JMenuItem("帮助内容", ChessUtil.getImageIcon("help.gif"));
		aboutGame = new JMenuItem("关于楚汉棋兵", ChessUtil.getImageIcon("info.gif"));

		settingMenu = new JMenu("设置(S)");
		bgSound = new JCheckBoxMenuItem("背景音乐");
		bgSound.setSelected(true);
		setting = new JMenuItem("常用设置");
		settingMenu.add(bgSound);
		settingMenu.add(setting);
		bar.add(settingMenu);

		helpMenu.add(welcome);
		helpMenu.add(helpContent);
		helpMenu.add(helpContent);
		helpMenu.add(helpContent);
		helpMenu.add(aboutGame);
		bar.add(helpMenu);

		setJMenuBar(bar);

		// 设置快捷键
		saveManual.setAccelerator(KeyStroke.getKeyStroke('S',
				InputEvent.CTRL_DOWN_MASK));
		saveManualAs.setAccelerator(KeyStroke.getKeyStroke('U',
				InputEvent.CTRL_DOWN_MASK));
		pauseGame.setAccelerator(KeyStroke.getKeyStroke('P',
				InputEvent.CTRL_DOWN_MASK));
		exitGame.setAccelerator(KeyStroke.getKeyStroke('E',
				InputEvent.CTRL_DOWN_MASK));

		welcome.setAccelerator(KeyStroke.getKeyStroke('W',
				InputEvent.CTRL_DOWN_MASK));
		helpContent.setAccelerator(KeyStroke.getKeyStroke('H',
				InputEvent.CTRL_DOWN_MASK));
		aboutGame.setAccelerator(KeyStroke.getKeyStroke('A',
				InputEvent.CTRL_DOWN_MASK));

		msgHistory.setAccelerator(KeyStroke.getKeyStroke('C',
				InputEvent.CTRL_DOWN_MASK));
		saveMsgAs.setAccelerator(KeyStroke.getKeyStroke('M',
				InputEvent.CTRL_DOWN_MASK));
		clearMsg.setAccelerator(KeyStroke.getKeyStroke('Q',
				InputEvent.CTRL_DOWN_MASK));
		recoverMsg.setAccelerator(KeyStroke.getKeyStroke('H',
				InputEvent.CTRL_DOWN_MASK));

		bgSound.setAccelerator(KeyStroke.getKeyStroke('B',
				InputEvent.CTRL_DOWN_MASK));
		setting.setAccelerator(KeyStroke.getKeyStroke('T',
				InputEvent.CTRL_DOWN_MASK));

		// 设置助记符
		fileMenu.setMnemonic(KeyEvent.VK_G);
		chattingMenu.setMnemonic(KeyEvent.VK_C);
		settingMenu.setMnemonic(KeyEvent.VK_S);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		// 注册监听器
		saveManual.addActionListener(this);
		saveManualAs.addActionListener(this);
		pauseGame.addActionListener(this);
		exitGame.addActionListener(this);

		setting.addActionListener(this);
		bgSound.addActionListener(this);

		welcome.addActionListener(this);
		helpContent.addActionListener(this);
		aboutGame.addActionListener(this);

		msgHistory.addActionListener(this);
		saveMsgAs.addActionListener(this);
		clearMsg.addActionListener(this);
		recoverMsg.addActionListener(this);

	}

	/**
	 * 响应菜单事件和按钮事件
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		MsgPacket sp = new MsgPacket();
		// 是否应该发送消息,默认不发送
		boolean isSend = false;

		// 退出游戏
		if ((source == exitGame) || (source == exit)) {
			int result = JOptionPane.showConfirmDialog(parent, "您确定要退出么？",
					"退出么？", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// 通知Room窗口自己已经没有加入或进行游戏
				parent.parent.isJoin = false;
				sp.setMsgType(MsgType.GAME_EXIT);
				parent.parent.setMenuAndButtonEnabled(true);
				isSend = true;
				// 然后发送退出消息，如果是创建者，服务器会向所有玩家
				// 发送减少创建者消息，现在不必更新创建者列表
			} else {
				isSend = false;
			}
		}

		else if ((source == pause) || (source == pauseGame)) {
			isSend = true;
			// 继续游戏
			if (board.isPause) {
				board.isPause = false;

				pause.setIcon(ChessUtil.getImageIcon("pause.png"));
				pauseGame.setText(PropertyReader.get("PAUSE_JBUTTON_TOOLTIP"));
				pause.setToolTipText(PropertyReader
						.get("PAUSE_JBUTTON_TOOLTIP"));
				sp.setMsgType(MsgType.GAME_CONTINUE);

				if (board.otherIsPause) {
					gameStatusContent.setText(SPACE + "对方已经暂停了游戏！请耐心等待！");
				} else {
					if (board.myTurn) {
						gameStatusContent.setText(SPACE + "轮到我走喽！");
					} else {
						gameStatusContent.setText(SPACE + "等待对方走棋！");
					}
				}
			}
			// 暂停游戏
			else {
				board.isPause = true;
				pause.setIcon(ChessUtil.getImageIcon("continue.png"));
				pauseGame.setText(PropertyReader
						.get("CONTINUE_JBUTTON_TOOLTIP"));
				pause.setToolTipText(PropertyReader
						.get("CONTINUE_JBUTTON_TOOLTIP"));
				sp.setMsgType(MsgType.GAME_PAUSE);
			}

			if (board.otherIsPause) {
				gameStatusContent.setText(SPACE + "您和对方都已经暂停了游戏！");
			} else {
				gameStatusContent.setText(SPACE + "您已经暂停了游戏！");
			}

		}

		else if (source == giveIn) {
			int result = JOptionPane.showConfirmDialog(this, "您确定投降么？");
			if (result == JOptionPane.YES_OPTION) {
				isSend = true;
				sp.setMsgType(MsgType.PLAYER_GIVEIN);
				gameStatusContent.setText(SPACE + "胜败乃兵家常事！继续努力啊！");
				ChessUtil.playSound("gameover.wav");
				board.myTurn = false;
			} else {
				isSend = false;
			}
		}

		// 处理悔棋
		else if (source == undo) {
			isSend = true;
			// 通知对方悔棋
			sp.setMsgType(MsgType.GAME_UNDO);
			// msgArea.append("正在请求悔棋\n");
			updateGameStatus(RED_TURN_GAME_STATUS, "正在请求悔棋");
		}

		else if (source == qiuhe) {
			isSend = true;
		}
		// 准备消息内容
		else if (source == send) {
			String msg = (String) msgComboBox.getSelectedItem();
			if (msg != null && !msg.equals("")) {// 消息非空
				isSend = true;

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
					sp.names = names;// 接收消息的人
					message.setStatus(MessageType.TO_SOME);
				}
				sp.setMessage(message);
				sp.setMsgType(MsgType.GAME_MESSAGE);
				sp.setName(board.userName);

				// 组合框不选中任何选现象并请求获得焦点
				msgComboBox.setSelectedIndex(-1);
				msgComboBox.requestFocusInWindow();
				// 消息文本域添加新的消息并滚动到最底
				String tempMsg = board.userName + " " + ChessUtil.getTime()
						+ "\n   " + msg + "\n";
				msgArea.append(tempMsg);
				msgRecord = msgRecord + tempMsg;
				msgArea.setCaretPosition(msgArea.getText().length());
			}
		} else {
			isSend = false;
		}

		// 发送消息
		if (isSend) {
			sp.setName(board.userName);
			sp.setRole(parent.role);
			sendPacket(sp);
			// 通知对方和服务器自己退出之后再退出
			if (sp.getMsgType() == MsgType.GAME_EXIT) {
				System.out.println("试图关闭背景音乐！");
				parent.switchBgsound(false);
				if (board.getWinkThread() != null) {
					board.getWinkThread().interrupt();
				}
				this.dispose();
				parent.dispose();
			}
		}

		// 保存棋谱
		if ((source == save) || (source == saveManual)) {
			SaveDialog dialog = new SaveDialog(this);
			dialog.setVisible(true);
		} else if (source == saveManualAs) {
			SaveAsDialog dialog = new SaveAsDialog(this);
			dialog.setVisible(true);
		} else if (source == clearMsg) {
			msgArea.setText("");
		} else if (source == recoverMsg) {
			msgArea.setText(msgRecord);
		} else if (source == msgHistory) {
			MsgRecordDialog recordDialog = new MsgRecordDialog(this);
			recordDialog.setVisible(true);
		} else if (source == saveMsgAs) {
			saveMsgAs();
		} else if (source == aboutGame) {
			ChessUtil.showAboutDialog();
		} else if (source == helpContent) {
			ChessUtil.showHelpDialog();
		} else if (source == bgSound) {
			parent.switchBgsound(bgSound.getState());
		} else if (source == setting) {
			JOptionPane.showMessageDialog(this, "暂时还没有选项可以设置", "暂时还没有选项可以设置",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (source == welcome) {
			ChessUtil.showWelcomeDialog();
		}

	}

	/**
	 * 弹出一个对话框，选择路径之后，保存聊天信息
	 * 
	 */
	public void saveMsgAs() {
		JFileChooser fileChooser = new JFileChooser();

		int state = fileChooser.showSaveDialog(null);
		File saveFile = fileChooser.getSelectedFile();
		if (saveFile != null && state == JFileChooser.APPROVE_OPTION) {
			String path = saveFile.getPath() + ".txt";
			System.out.println("聊天记录的路径:" + path);
			ChessUtil.writeStringToFile(msgRecord, path);
		}

	}

	/**
	 * 发送消息
	 * 
	 * @param sp
	 */
	public void sendPacket(MsgPacket sp) {
		System.out.println("客户正在发送消息" + sp.getMsgType());
		parent.parent.sendPacket(sp);
	}

	/**
	 * 更新接收消息的玩家列表
	 * 
	 * @param names
	 */
	public void updateNames() {
		userComboBox.removeAllItems();
		userComboBox.addItem("所有人");

		int size = parent.names.size();
		for (int index = 0; index < size; index++) {
			String name = parent.names.get(index);
			if (!name.equals(board.userName)) {
				userComboBox.addItem(name);
			}
		}
	}

	/**
	 * 更新游戏状态
	 * 
	 * @param state
	 *            图标标识
	 * @param content
	 *            游戏状态文字描述
	 */
	public void updateGameStatus(int state, String content) {

		switch (state) {
		case RED_TURN_GAME_STATUS:
			gameStatusIcon.setIcon(ChessUtil.getImageIcon("hongshuai.png"));
			break;
		case BLACK_TURN_GAME_STATUS:
			gameStatusIcon.setIcon(ChessUtil.getImageIcon("heijiang.png"));
			break;
		default:
			break;
		}

		if (content != null && !content.equals("")) {
			gameStatusContent.setText(content);
		}
	}

	public void addMsg(String msg) {
		msgArea.append(msg);
	}

	@Override
	public GameRecord getGameRecord() {
		ManualType manualType = null;
		switch (parent.role) {
		case ROLE_RED:
			manualType = ManualType.NETWORK_RED;
			break;
		case ROLE_BLACK:
			manualType = ManualType.NETWORK_BLACK;
			break;
		case ROLE_OBSERVER:
			manualType = ManualType.NETWORK_OBSERVER;
			break;
		case ROLE_JUDGMENT:
			manualType = ManualType.NETWORK_JUDGEMENT;
			break;
		default:
			break;
		}
		GameRecord gameRecord = new GameRecord(manualType, ChessUtil
				.getDateAndTime(), "", board.chessManual.getManualItems(),
				board.chessManual.descs, null);
		return gameRecord;
	}

	@Override
	public ArrayList<String> getSavePaths() {
		ArrayList<String> paths = new ArrayList<String>();
		String path = "manuals/network/";
		String path2 = "manuals/network/";

		paths.add(path);
		paths.add(path2);
		return paths;
	}

}
