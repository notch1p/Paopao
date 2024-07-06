package com.tedu.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tedu.element.ElementObj;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.show.GameJFrame;

/**
 * author: 李熠烜
 * 玩家控制的坦克可以移动和射击。
 * 敌方坦克自动移动，并与玩家坦克对战。
 * 游戏包含多个关卡，每个关卡有不同数量的敌方坦克。
 * 玩家需要消灭所有敌方坦克以进入下一关。
 */

public class GameListener implements KeyListener {
    private ElementManager em = ElementManager.getManager();

    // 记录按下的方向键
    private Set<Integer> set = new HashSet<>();
    //记录当前暂停状态
    private boolean isPause=false;
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }


    @Override
    public void keyPressed(KeyEvent e) {
//		System.out.println("keyPressed: " + e.getKeyCode());
        int key = e.getKeyCode();
        if (set.contains(key)) {
            // 防止按住一个方向键不放，连续多次修改状态
            // 之所以用set集合，是因为可能不只有4个方向键，可能还有其他键
            return;
        }
        set.add(key);

        // 拿到玩家集合
        List<ElementObj> playList = em.getElementsByKey(GameElement.PLAYER);
        for (ElementObj obj : playList) {
            obj.keyClick(true, key);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // 禁用debug输出降低延迟
        System.out.println("keyReleased: " + e.getKeyCode());

        int key = e.getKeyCode();
        if (!set.contains(key)) { // 不  存在
            return;
        }
        set.remove(key);

        List<ElementObj> playList = em.getElementsByKey(GameElement.PLAYER);
        for (ElementObj obj : playList) {
            obj.keyClick(false, key);
        }
        if(key==80)
        {
            isPause=!isPause;
            ((GameThread) GameJFrame.gj.getThread()).setPause(isPause);
        }
    }

}
