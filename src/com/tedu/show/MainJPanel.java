package com.tedu.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.tedu.controller.GameListener;
import com.tedu.controller.GameThread;
import com.tedu.game.GameStart;
import com.tedu.manager.GameLoad;


public class MainJPanel extends JPanel{

    public JButton jb1;
    public JButton jb2;
    public JButton jb3;

//	public static GameMainJPanel jp;
//	public static SelectJPanel jp;

    public MainJPanel() {

        GameLoad.loadImg();
        ImageIcon icon = GameLoad.imgMap.get("single");
        ImageIcon icon2 = GameLoad.imgMap.get("double");
        ImageIcon icon3 = GameLoad.imgMap.get("shuoming");
        this.setLayout(null);

        jb1 = new JButton(icon);
        jb1.setBorderPainted(false);
        jb1.setContentAreaFilled(false);
        jb1.setBounds(250, 200, icon.getIconWidth(), icon.getIconHeight());

        jb2 = new JButton(icon2);
        jb2.setBorderPainted(false);
        jb2.setContentAreaFilled(false);
        jb2.setBounds(250, 300, icon2.getIconWidth(), icon2.getIconHeight());

        jb3 = new JButton(icon3);
        jb3.setBorderPainted(false);
        jb3.setContentAreaFilled(false);
        jb3.setBounds(250, 400, icon2.getIconWidth(), icon2.getIconHeight());

//		jp = new SelectJPanel(gj);
//		jp = new GameMainJPanel();
//		ʵ��������
//		GameListener listener = new GameListener();
//		ʵ�������߳�
//		GameThread th = new GameThread();

        jb1.addActionListener(e -> {
            GameJFrame.setJPanel("SelectJPanel",1);
//				Object[] options = { "ȷ��" };
//				JOptionPane.showOptionDialog(null, "����ģʽ��δ�����������ڴ�", "��ʾ",
//				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
//				null, options, null);
        });
        jb2.addActionListener(e -> {
            GameJFrame.setJPanel("SelectJPanel",2);
//				ע��
//				gj.setjPanel(jp);
//				gj.setKeyListener(listener);
//				gj.setThread(th);
//				gj.start();
        });
        jb3.addActionListener(e -> {
            Object[] options = { "ȷ��" };
            JOptionPane.showOptionDialog(null, "<html><body><tr>���1 �����ΪWASD ����Ϊ�ո�</tr>"
                            + "<tr>���2 �����Ϊ�������� ����Ϊ�س�</tr>"
                            + "<tr>�𿨣�������������</tr>"
                            + "<tr>ҩˮ���������ݵ�����</tr>"
                            + "<tr>���ݣ��������ݵķ�������</tr>"
                            + "<tr>���ͷ�������ƶ��ٶ�</tr></body></html>", "��Ϸ˵��",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, null);
        });
        this.add(jb1);
        this.add(jb2);
        this.add(jb3);
//		gj.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        ImageIcon icon = GameLoad.imgMap.get("ground");
        g.drawImage(icon.getImage(), 0, 0, GameJFrame.jp1.getWidth(), GameJFrame.jp1.getHeight(),null);
    }

}
