package com.tedu.show;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayer;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.tedu.controller.GameThread;
import com.tedu.element.ElementObj;
import com.tedu.game.GameStart;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.MusicPlayer;


public class GameMainJPanel extends JPanel implements Runnable {

    // ����Ԫ�ع�����
    private ElementManager em;

    //	private JPanel btnPanel;
    private JButton bgmBtn;	// BGM�İ�ť
    private boolean bgmStatus = false; // BGM���Ƿ���

    private JButton runBtn;

    public GameMainJPanel() {
        init();
    }

    protected void init() {
        // �õ�Ԫ�ع������ĵ���
        em = ElementManager.getManager();

//		this.setLayout(null);
//		JButton bgmBtn = new JButton("Click Me");
//		bgmBtn.setBounds(300, 200, 100,50);
//		this.add(bgmBtn);

        // ����Ƕ�ס�BorderLayout.SOUTH��FlowLayout.CENTER)ʵ�ְ�ť��ײ�����
        this.setLayout(new BorderLayout());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);	// ���������Ϊ͸���������ɫ�����ڵ���ͼ
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bgmBtn = new JButton("����BGM");
        runBtn = new JButton("��ͣ��Ϸ");
        JButton overBtn = new JButton("������Ϸ");

        bgmBtn.addActionListener(e -> {
            // ��ȡ��ǰ��Ϸ�̵߳�BGM
            MusicPlayer bgm = ((GameThread) GameJFrame.gj.getThread()).getBgm();

            if (bgmStatus) { // ����Ѿ����ˣ��͹ص�
                bgm.over();
                bgmBtn.setText("����BGM");
            } else { // ������ڹرգ��Ϳ���
                bgm.play();
                bgmBtn.setText("�ر�BGM");
            }
            bgmStatus = !bgmStatus; // ״̬ȡ��

            // ע��ȫ�ּ������������������壬���������
            // �����ť�󣬽��������˰�ť�ϣ�������Ҫ���»�ý��㣬��ť�����Ż���Ч
            GameJFrame.gj.requestFocus();
//				GameMainJPanel.this.requestFocus();
        });

        runBtn.addActionListener(e -> {
            // ��ȡ��Ϸ�߳�
            GameThread thread = (GameThread) GameJFrame.gj.getThread();
            // ��ȡ��ǰ��Ϸ�̵߳�BGM
            MusicPlayer bgm = thread.getBgm();
            if (thread.isPause()) { // ����Ѿ���ͣ���������Ϸ
                thread.setPause(false);
                runBtn.setText("��ͣ��Ϸ");
                if (bgmStatus) { // ����Ѿ�����BGM���͹ص�
                    bgm.play();
                }
                GameJFrame.gj.requestFocus();

            } else {
                thread.setPause(true);
                runBtn.setText("������Ϸ");
                if (bgmStatus) {
                    bgm.over();
                }
                // ע��
                // ��ͣ�ڼ��ô���ʧȥ���㣬�ü��̼���ʧЧ
                // ������ͣ�ڼ䣬��������л�����
            }

        });

//		jp = new OverJPanel(gj);
        // ������Ϸ�İ�ť�ĵ���¼�
        overBtn.addActionListener(e -> {
            GameThread th = (GameThread) GameJFrame.gj.getThread();
            MusicPlayer bgm = th.getBgm();
            if (bgmStatus) {
                bgm.over();
                bgmBtn.setText("����BGM");
            }

            th.setIsOver(true);

//				GameJFrame.setJPanel("OverJPanel");
//				gj.setjPanel(jp);
//				gj.setThread(null, 1);
//				gj.start();
            // ע��ȫ�ּ������������������壬���������
            // �����ť�󣬽��������˰�ť�ϣ�������Ҫ���»�ý��㣬��ť�����Ż���Ч
            GameJFrame.gj.requestFocus();
        });

        btnPanel.add(bgmBtn);
        btnPanel.add(runBtn);
        btnPanel.add(overBtn);
        this.add(btnPanel, BorderLayout.SOUTH);
    }

    // ��д�滭����

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        // ����Ԫ�ص���ʾ
        Map<GameElement, List<ElementObj>> all = em.getGameElements();
        // GameElement.values()�����ط������޷����ȥ
        // ���ص������˳��ʱ��ö�ٱ�������ʱ��˳��
        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
//			if(ge.equals(GameElement.MAPS)) {
////			if(ge == GameElement.MAPS) {
//				Collections.sort(list);
//			}
            for (int i = 0; i < list.size(); i++) {
                ElementObj obj = list.get(i);
                obj.showElement(g);
            }
        }

        // ���»��������������paint֮����ڵ�ס���
        super.paintChildren(g);

//		Set<GameElement> keySet = all.keySet();
//		for (GameElement key : keySet) {
//			List<ElementObj> list = all.get(key);
//			for (int i = 0; i < list.size(); i++) {
//				ElementObj obj = list.get(i);
//				obj.showElement(g);
//			}
//		}

    }

    @Override
    public void run() {
        while (true) {
            this.repaint();
            // һ������£�ͨ�������������ٶ�
            try {
                // 50 ms ˢ��һ��
                // ����1��ˢ��20��
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }





}
