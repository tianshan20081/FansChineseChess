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
package cn.fansunion.chinesechess.save;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.fansunion.chinesechess.ChessUtil;
import cn.fansunion.chinesechess.net.client.MatchGUI;


/**
 * 玩家聊天记录对话框
 * 
 * @author leiwen@fansunion.cn,http://FansUnion.cn,
 *         http://blog.csdn.net/FansUnion
 * @since 2.0
 */
public class MsgRecordDialog extends JFrame implements ActionListener {

	private static final long serialVersionUID = 102569L;

	MatchGUI client;

	private JButton saveMsgAs;

	private Cursor handCursor;

	public MsgRecordDialog(MatchGUI client) {
		handCursor = new Cursor(Cursor.HAND_CURSOR);
		this.client = client;
		initButtons();
		initPanel();
		set();
	}

	private void set() {
		setTitle("聊天记录");
		setIconImage(ChessUtil.getAppIcon());
		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			// 响应默认的退出事件
			public void windowClosing(WindowEvent e) {
				handleExitGame();
			}

		});
	}

	private void initPanel() {
		JTextArea record = new JTextArea();
		record.setText(client.msgRecord);
		record.setFont(new Font("宋体", Font.PLAIN, 16));
		record.setPreferredSize(new Dimension(360, 350));
		JScrollPane recordScroll = new JScrollPane(record);

		JPanel recordPanel = new JPanel();
		recordPanel.add(recordScroll);
		recordScroll.setPreferredSize(new Dimension(380, 380));
		add(BorderLayout.CENTER, recordPanel);

		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(saveMsgAs);
		add(BorderLayout.SOUTH, controlPanel);
	}

	private void initButtons() {
		saveMsgAs = new JButton("聊天记录另存为");
		saveMsgAs.addActionListener(this);
		saveMsgAs.setToolTipText("聊天记录另存为");
		saveMsgAs.setCursor(handCursor);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == saveMsgAs) {
			client.saveMsgAs();
		}

	}

	private void handleExitGame() {
		dispose();
	}
}
