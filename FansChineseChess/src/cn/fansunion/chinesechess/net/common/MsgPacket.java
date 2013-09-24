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
package cn.fansunion.chinesechess.net.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.fansunion.chinesechess.core.ManualItem;


/**
 * 客户端和服务器之间通信用的数据包
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class MsgPacket implements Serializable {

	private static final long serialVersionUID = 1010L;

	public static enum MsgType {
		GAME_EXIT, GAME_PAUSE, GAME_CONTINUE,
		PIECE_MOVING, GAME_UNDO, GAME_BACK_YES, 
		GAME_BACK_NO, PLAYER_GIVEIN, ROOM_MESSAGE,
		GAME_MESSAGE, GROUP_MESSAGE, PLAYER_CREATE_GAME,
		PLAYER_JOIN_CREATED_GAME, CREATOR_EXIT_CREATED_GAME,
		PLAYER_EXIT_CREATED_GAME, CREATOR_START_CREATED_GAME, 
		PLAYER_LIST, CREATOR_LIST, ADD_PLAYER, SUB_PLAYER, 
		ADD_CREATOR, SUB_CREATOR, PLAYER_LOGIN, CREATOR_AND_MEMBER,
		CHANGE_ROLE, CHANGE_GAME_STATUS, BYE_BYE,
	};

	public enum PlayerRole {
		ROLE_OBSERVER, ROLE_BLACK, ROLE_RED, ROLE_JUDGMENT
	};

	// 消息标志
	private MsgType msgType;

	//
	private List<Player> players;

	//
	private List<Creator> creators;

	//
	private Player player;

	//
	private Creator creator;

	//
	public ArrayList<Member> members;

	//
	public ArrayList<String> names;

	//
	private Member member;

	// 用户名
	private String name;

	// 聊天消息
	private Message message;//

	// 玩家的角色
	private PlayerRole role;

	// 红方还是蓝方
	private String playerName;

	// 棋局记录
	public ManualItem chessRecord;

	public PlayerRole getRole() {
		return role;
	}

	public void setRole(PlayerRole role) {
		this.role = role;
	}

	public MsgPacket() {
		creators = new ArrayList<Creator>();
		members = new ArrayList<Member>();
		names = new ArrayList<String>();

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Creator> getCreators() {
		return creators;
	}

	public void setCreators(List<Creator> creators) {
		this.creators = creators;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public MsgType getMsgType() {
		return msgType;
	}

	public void setMsgType(MsgType status) {
		this.msgType = status;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

}
