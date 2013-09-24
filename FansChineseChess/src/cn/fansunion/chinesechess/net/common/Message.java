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

/**
 * 聊天消息类，房间、玩家组聊天用到
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 124L;

	public enum MessageType {
		TO_ALL, TO_SOME,SYSTEM
	};

	// 聊天消息内容
	private String content;

	// 聊天消息时间
	private String date;

	// 接收者的标志，一个人还是所有人
	private MessageType status;

	public MessageType getStatus() {
		return status;
	}

	public void setStatus(MessageType status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
