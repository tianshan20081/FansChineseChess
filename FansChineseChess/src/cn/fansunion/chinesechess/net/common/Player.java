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

import cn.fansunion.chinesechess.net.server.User;


/**
 * 玩家类
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class Player implements Serializable {

	private static final long serialVersionUID = 2581L;

	// 玩家基本信息
	private User user;

	// 玩家登录时间
	private String loginTime;

	// 玩家ip地址
	private String ipAddress;

	//游戏状态
	private String gameStatus;
	
	public Player() {

	}

	public Player(User user, String loginTime, String ipAddress,String gameStatus) {
		this.user = user;
		this.loginTime = loginTime;
		this.ipAddress = ipAddress;
		this.gameStatus = gameStatus;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}

}
