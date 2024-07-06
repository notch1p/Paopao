package com.tedu.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.naming.InitialContext;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tedu.controller.GameListener;
import com.tedu.controller.GameThread;
import com.tedu.game.GameStart;
import com.tedu.manager.GameLoad;



public class SelectJPanel extends JPanel{

    public JButton jb0;
    public JButton jb1;
    public JButton jb2;

    public JLabel jl;

    public static int map = 0;//Ñ¡Ôñ¹Ø¿¨


    public SelectJPanel(int mode) {
        init(mode);
    }

    public void init(int mode) {

        this.setLayout(null);

        Font font = new Font("Î¢ÈíÑÅºÚ",Font.BOLD,25);
        Font font_small	= new Font("Î¢ÈíÑÅºÚ",Font.BOLD,18);

        jb0 = new JButton("·µ»Ø");
        jb0.setBackground(new Color(34,139,34));
        jb0.setFont(font_small);
        jb0.setOpaque(true);
        jb0.setBorderPainted(false);
        jb0.setBounds(100, 50, 80,50);

        jl = new JLabel("¹Ø¿¨Ñ¡Ôñ");
        jl.setFont(font);
        jl.setForeground(new Color(34,139,34));
        jl.setBounds(300, 100, 100,50);

        jb1 = new JButton("1");
        jb1.setBackground(new Color(34,139,34));
        jb1.setOpaque(true);
        jb1.setBorderPainted(false);
        jb1.setBounds(300, 200, 100,50);

        jb2 = new JButton("2");
        jb2.setBackground(new Color(34,139,34));
        jb2.setOpaque(true);
        jb2.setBorderPainted(false);
        jb2.setBounds(300, 300, 100,50);


        jb0.addActionListener(e -> GameJFrame.setJPanel("MainJPanel"));


        jb1.addActionListener(e -> {
            map = 1;
            GameJFrame.setJPanel("GameMainJPanel", mode);
        });


        jb2.addActionListener(e -> {
            map = 2;
            GameJFrame.setJPanel("GameMainJPanel", mode);
        });
        this.add(jb0);
        this.add(jl);
        this.add(jb1);
        this.add(jb2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        ImageIcon icon = GameLoad.imgMap.get("ground");
        g.drawImage(icon.getImage(), 0, 0, GameJFrame.jp2.getWidth(), GameJFrame.jp2.getHeight(),null);
    }

}
