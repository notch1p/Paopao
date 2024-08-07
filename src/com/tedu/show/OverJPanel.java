package com.tedu.show;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.tedu.game.GameStart;
import com.tedu.manager.GameLoad;

public class OverJPanel extends JPanel{

    public JButton jb;

//	public static MainJPanel jp;

    public OverJPanel() {
        this.setLayout(null);
//		jp = GameStart.jp;
        ImageIcon icon = GameLoad.imgMap.get("regame");
        jb = new JButton(icon);
        jb.setBorderPainted(false);
        jb.setContentAreaFilled(false);
        jb.setBounds(250, 200, icon.getIconWidth(), icon.getIconHeight());
        jb.addActionListener(e -> {
            GameJFrame.setJPanel("MainJPanel");
//				注入
//				gj.setjPanel(jp);
//				gj.start();
        });
        this.add(jb);
    }

    @Override
    protected void paintComponent(Graphics g) {
        ImageIcon icon = GameLoad.imgMap.get("over");
        g.drawImage(icon.getImage(), 0, 0, GameJFrame.jp4.getWidth(), GameJFrame.jp4.getHeight(),null);
    }

}
