package net.study;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

/**
 * @author wb-lw252418
 * 
 * this is a Server with UI. uses swing.
 */
public class ServerUI {

	public static void main(String[] args) {
		JTextFrame frame = new JTextFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}

class JTextFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -84676403575661096L;

	private MsgServer server;
	private MessageBox msgBox;
	private Deamon deamon;
	
	private JTextArea inputZone;
	private JTextArea out;
	private JButton start_btn;
	private JButton stop_btn;
	private JButton send_btn;
	
	JTextFrame() {
		msgBox = new MessageBox();
		inputZone = new JTextArea(10, 100);
		inputZone.setLineWrap(true);
		// bind key
		inputZone.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "sendMsg");
		Action sendAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				send();
			}
		};
		inputZone.getActionMap().put("sendMsg", sendAction);
		
		out = new JTextArea(10, 100);
		out.setLineWrap(true);
		out.setEditable(false);
		out.setBackground(Color.BLACK);
		out.setForeground(Color.GREEN);
		start_btn = new JButton("启动服务");
		stop_btn = new JButton("停止服务");
		send_btn = new JButton("发送");
		start_btn.addActionListener(new ClickListener());
		stop_btn.addActionListener(new ClickListener());
		send_btn.addActionListener(new ClickListener());
		stop_btn.setEnabled(false);
		send_btn.setEnabled(false);
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(1, 3));
		btnPanel.add(start_btn);
		btnPanel.add(stop_btn);
		btnPanel.add(send_btn);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(out, BorderLayout.NORTH);
		this.getContentPane().add(inputZone, BorderLayout.CENTER);
		this.getContentPane().add(btnPanel, BorderLayout.SOUTH);
	}
	
	private void send() {
		String msg = inputZone.getText();
		try {
			msgBox.put(msg);
			inputZone.setText("");
			String old = out.getText();
			out.setText(old + "\r\n" + "消息已发送！");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	private class ClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == start_btn) {
				System.out.println("start server.");
				if(null == server) {
					server = new MsgServer(msgBox);
				}
				deamon = new Deamon(server);
				try {
					deamon.execute();
					String old = out.getText();
					start_btn.setEnabled(false);
					stop_btn.setEnabled(true);
					send_btn.setEnabled(true);
					out.setText(old + "\r\n" + "服务启动成功！");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				return;
			}
			if(e.getSource() == stop_btn) {
				System.out.println("stop server.");
				deamon.cancel(false);
				if(null != server) {
					try {
						server.destroy();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				stop_btn.setEnabled(false);
				start_btn.setEnabled(true);
				send_btn.setEnabled(false);
				return;
			}
			if(e.getSource() == send_btn) {
				System.out.println("send message.");
				send();
				return;
			}
		}
	}
	
	private class Deamon extends SwingWorker<Boolean, Void> {

		private MsgServer server;
		
		Deamon(MsgServer server) {
			this.server = server;
		}
		
		@Override
		protected Boolean doInBackground() throws Exception {
			server.start();
			return true;
		}
		
	}
}