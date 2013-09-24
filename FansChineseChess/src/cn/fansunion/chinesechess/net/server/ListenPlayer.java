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
package cn.fansunion.chinesechess.net.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cn.fansunion.chinesechess.ServerGUI;
import cn.fansunion.chinesechess.net.common.MsgPacket;
import cn.fansunion.chinesechess.net.common.Message.MessageType;
import cn.fansunion.chinesechess.net.common.MsgPacket.MsgType;


/**
 * 为每个玩家定义一个线程类来处理新的会话
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class ListenPlayer implements Runnable {

	private ObjectInputStream ois;// 输入流

	private ObjectOutputStream oos;// 输出流

	Socket socket;// 和某个玩家通信用的套接字

	ServerGUI parent;// 主线程的引用

	MsgPacket packet;//

	public ListenPlayer(ServerGUI parent, ObjectOutputStream oos,
			ObjectInputStream ois, Socket socket) {
		this.parent = parent;
		this.oos = oos;
		this.ois = ois;
		this.socket = socket;// 为了方便关闭套接字
	}

	/**
	 * 单独的线程，负责接收某个玩家的消息
	 */
	public void run() {
		try {
			while (true) {
				// 从玩家接收消息
				if (ois != null) {
					packet = (MsgPacket) ois.readObject();
				} else {
					System.out.println("ois = null");
				}

				MsgType status = packet.getMsgType();
				System.out.println("服务器收到了玩家消息，消息状态：" + status);

				// 有玩家创建了游戏
				if (status == MsgType.ADD_CREATOR) {
					parent.addCreator(packet);
				}
				// 玩家加入创建者创建的游戏
				else if (status == MsgType.PLAYER_JOIN_CREATED_GAME) {
					System.out.println("服务器成功收到了客户的加入消息");
					parent.addMemer(packet);
				}
				// 创建者离开创建的游戏
				else if (status == MsgType.CREATOR_EXIT_CREATED_GAME) {
					parent.subCreator(packet);
				}
				// 玩家退出创建者创建的游戏
				else if (status == MsgType.PLAYER_EXIT_CREATED_GAME) {
					parent.subMember(packet);
				}
				// 玩家退出
				else if (status == MsgType.BYE_BYE) {
					parent.subPlayer(packet);
					// close();
				}
				// 创建者开始了游戏
				else if (status == MsgType.CREATOR_START_CREATED_GAME) {
					parent.startGame(packet);
				}
				// 有玩家改变了角色
				else if (status == MsgType.CHANGE_ROLE) {
					parent.handleChangeRole(packet);
				}

				// 房间信息
				else if (status == MsgType.ROOM_MESSAGE) {
					MessageType flag = packet.getMessage().getStatus();
					if (flag == MessageType.TO_ALL) {
						parent.handleRoomMessage(packet);
						parent.notifyAllPlayers(oos, packet);
					} else {
						parent.notifyPlayersByName(packet.names, packet);
					}
				}
				// 组内信息
				else if (status == MsgType.GROUP_MESSAGE) {
					MessageType flag = packet.getMessage().getStatus();
					if (flag == MessageType.TO_ALL) {
						parent.handleRoomMessage(packet);
						parent.notifyAllPlayers(oos, packet);
					} else {
						parent.notifyPlayersByName(packet.names, packet);
					}
				} else if (status == MsgType.GAME_EXIT) {
					parent.handleDataPacket(packet);
				} else if (status == MsgType.GAME_MESSAGE) {
					parent.handleDataPacket(packet);
				} else if (status == MsgType.PIECE_MOVING
						|| status == MsgType.GAME_UNDO
						|| status == MsgType.GAME_BACK_YES
						|| status == MsgType.GAME_BACK_NO
						|| status == MsgType.PLAYER_GIVEIN
						|| status == MsgType.GAME_PAUSE
						|| status == MsgType.GAME_CONTINUE) {
					// 游戏进行中的信息
					parent.notifyGroupOtherPlayers(packet);
				}

			}
		} catch (IOException ex) {
			System.err.println(ex);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 及时关闭套接字和输出输入流
	 * 
	 */
	public void close() {
		System.out.println("正在关闭退出玩家的套接字！");
		try {
			if (oos != null) {
				oos.close();
				oos = null;
			}

			if (ois != null) {
				ois.close();
				ois = null;
			}

			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
