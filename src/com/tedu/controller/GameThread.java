package com.tedu.controller;

import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.tedu.element.ElementObj;
import com.tedu.element.Enemy;
import com.tedu.element.PaoPao;
import com.tedu.element.PaoPaoExplode;
import com.tedu.element.Player;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.manager.MusicPlayer;
import com.tedu.show.GameJFrame;

/**
 * author: 李熠烜
 *ElementManager em: 游戏元素管理器实例，用于管理游戏中的各种元素（如玩家、敌人、道具等）。
 * MusicPlayer bgm: 游戏背景音乐播放器实例。
 * int map: 当前选择的地图编号。
 * int vectory: 用于判定游戏胜利状态的变量。
 * boolean isOver: 标记游戏是否结束。
 * boolean isPause: 标记游戏是否暂停。
 * int mode: 游戏模式（单人或双人）。
 * long gameTime: 游戏运行时间计时器。
 */

public class GameThread extends Thread {

    // 联动元素管理器
    private ElementManager em;
    // 游戏进行时的背景音乐
    private MusicPlayer bgm;

    // 选择地图
    private int map = 0; // 默认值为0，即未选择地图

    public int getMap() {
        return this.map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    private int vectory = 1;// 设置是否胜利，0代表胜利

    // 游戏进程是否结束
    private boolean isOver = false;

    // 暂停判定符
    private boolean isPause;

    private int mode;


    public GameThread(int map, int mode) {
        this.mode = mode;
        this.map = map;
        em = ElementManager.getManager();
    }

    @Override
    public void run() {
        // super.run();

        // 扩展，可以将true变为一个变量用于游戏进程控制（例如：暂停）
        // while (true) {
        // 游戏开始前：读进度条，加载游戏资源（场景）
        gameLoad();

        // 游戏进行时
        gameRun();

        // 游戏场景结束时：游戏资源回收
        gameOver();
    }


    private void gameLoad() {
        // System.out.println("gameLoad");

        // 加载图片资源。注意必须在加载地图、人物之前
        GameLoad.loadImg();

        // 加载游戏音乐（包括音效和背景音乐）
        GameLoad.loadMusic();

        // 加载地图，10 可以设置成变量，切换关卡
        GameLoad.MapLoad(this.map);


        if (mode == 2) {
            GameLoad.loadPlayer("144,144,player1,65,87,68,83,32,1",
                    "528,480,player2,37,38,39,40,10,2");
        } else {
            GameLoad.loadPlayer("144,144,player1,65,87,68,83,32,1");
            GameLoad.loadEnemy("528,480,player2,37,38,39,40,10,2");
        }
        // GameLoad.loadPlayer("144,144,player1,37,38,39,40,17,1");

        // 加载NPC...

    }


    private long gameTime = 3L;

    private void gameRun() {

        // 开始循环播放背景音乐。暂时放这里，可能会改位置
        bgm = GameLoad.musicMap.get("bgm0").setLoop(true);
        // bgm.play();

        // 预留扩展，true可以改为变量，用于控制关卡结束等
        while (!isOver) {
            // System.out.println("gameRun");
            if (!isPause) {
                // 所有元素刷新移动
                Map<GameElement, List<ElementObj>> all = em.getGameElements();
                fclicked();
                moveAndUpdate(all);

                // 约定：第一个参数：碰撞的主动方；第二个参数：被碰撞的一方
                elementsCollide(GameElement.PLAYER, GameElement.MAPS); // Player和障碍物
                elementsCollide(GameElement.EXPLODE, GameElement.PLAYER); // 泡泡爆炸和Player
                elementsCollide(GameElement.EXPLODE, GameElement.MAPS); // 泡泡爆炸和地图
                elementsCollide(GameElement.EXPLODE, GameElement.ENEMY);
                elementsCollide(GameElement.PLAYER, GameElement.TOOL); // Player和道具
                elementsCollide(GameElement.ENEMY, GameElement.TOOL);
                elementsCollide(GameElement.EXPLODE, GameElement.PAOPAO); // 泡泡爆炸和泡泡
                elementsCollide(GameElement.PLAYER, GameElement.PAOPAO); // player和泡泡

                List<ElementObj> enemyList = em.getElementsByKey(GameElement.ENEMY);
                for (ElementObj obj : enemyList) {
                    if (obj instanceof Enemy) {
                        Enemy e = (Enemy) obj;
                        e.automate();
                    }
                }

                gameTime++;

                checkEnd(GameElement.PLAYER);
                checkEnd(GameElement.ENEMY);
            }
            try {
                sleep(35);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkEnd(GameElement ge) {
        List<ElementObj> playerList = ElementManager.getManager().getElementsByKey(ge);
        for (ElementObj player : playerList) {
            Player player1 = (Player) player;
            if (!player1.isLive()) {
                vectory--;
                if (vectory == 0) {
                    String msg = "，请按结束游戏";
                    if (player1.getPlayerNum() == 1) {
                        if (mode == 2) {
                            msg = "小黄胜利" + msg;
                        } else {
                            msg = "你输了" + msg;
                        }
                    } else if (player1.getPlayerNum() == 2) {
                        if (mode == 2) {
                            msg = "小红胜利" + msg;
                        } else {
                            msg = "你赢了" + msg;
                        }
                    }
                    if (player1.getPlayerNum() == 1) {
                        Object[] options = { "确定" };
                        JOptionPane.showMessageDialog(null,
                                msg, "提示", JOptionPane.INFORMATION_MESSAGE);
                        isOver = true;
                    }
                    if (player1.getPlayerNum() == 2) {
                        Object[] options = { "确定" };
                        JOptionPane.showMessageDialog(null,
                                msg, "提示", JOptionPane.INFORMATION_MESSAGE);
                        isOver = true;
                    }
                    vectory = 1;
                }
            }
        }
    }


    public void elementsCollide(GameElement eleA, GameElement eleB) {
        List<ElementObj> listA = em.getElementsByKey(eleA);
        List<ElementObj> listB = em.getElementsByKey(eleB);
        // 人物和道具之间的碰撞设置
        if (eleB == GameElement.TOOL) {
            for (ElementObj g1 : listA) {
                for (ElementObj g2 : listB) {
                    if (g2.collide(g1)) {
                        return;
                    }
                }
            }
        }
        // 泡泡爆炸和地图之间的碰撞设置
        if (eleA == GameElement.EXPLODE && eleB == GameElement.MAPS) {
            for (ElementObj g1 : listA) {
                for (ElementObj g2 : listB) {
                    if (g1.collide(g2)) {
                        g2.setLive(false);
                        return;
                    }
                }
            }
        }

        // 泡泡爆炸和泡泡之间的碰撞设置
        if (eleA == GameElement.EXPLODE && eleB == GameElement.PAOPAO) {
            for (ElementObj g1 : listA) {
                for (ElementObj g2 : listB) {
                    if (g1.collide(g2)) {
                        g2.setLive(false);
                        return;
                    }
                }
            }
        }

        // player和泡泡之间的碰撞设置
        if (eleA == GameElement.PLAYER && eleB == GameElement.PAOPAO) {
            for (ElementObj g1 : listA) {
                for (ElementObj g2 : listB) {
                    if (g1.collide(g2)) {
                    }
                }
            }
        }
        for (ElementObj a : listA) {
            for (ElementObj b : listB) {
                if (a.collide(b)) {

                    // 爆炸时碰撞，未爆炸时的不碰撞
                    if (eleA.equals(GameElement.EXPLODE)
                            && (eleB.equals(GameElement.PLAYER) ||
                            eleB.equals(GameElement.ENEMY))) {
                        b.die(gameTime);
                        // System.out.println(b);
                        if (b.isLive()) {
                            Player player = (Player) b;
                            player.setBoom(true);
                        }
                    }
                }
            }
        }

    }

    public void moveAndUpdate(Map<GameElement, List<ElementObj>> all) {
        // GameElement.values()是隐藏方法，无法点进去
        // 返回的数组的顺序时是枚举变量声明时的顺序

        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            // 操作集合不要使用迭代器foreach，修改数据会抛出异常
            // for (int i = 0; i < list.size(); i++) {
            for (int i = list.size() - 1; i >= 0; i--) {
                ElementObj obj = list.get(i);

                // 如果元素处于消亡状态，将它从元素管理器中移除
                if (!obj.isLive()) {
                    // list.remove(i--); // 回退

                    // 启动一个消亡方法，方法中可以做很多事情。例如：死亡动画、掉装备
                    //
                    obj.die(gameTime); // 调用死亡方法
                    em.removeElement(i, ge);
                    // list.remove(i);
                    continue;
                }
                obj.count(gameTime);
                obj.model(gameTime);
            }
        }
    }


    private void gameOver() {
        // 清空元素管理器里面的所有对象
        ElementManager.getManager().clearAll();

        GameJFrame.setJPanel("OverJPanel");
    }


    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }



    private int fclickedTime = 0; // 闪烁次数

    private void fclicked() {
        List<ElementObj> playerList = ElementManager.getManager().getElementsByKey(GameElement.PLAYER);
        for (ElementObj player : playerList) {
            Player player1 = (Player) player;
            if (player1.isBoom()) {
                if (fclickedTime < 48) {
                    if (player1.getFclickedY() == 48) {
                        player1.setFclickedY(0);
                    } else {
                        player1.setFclickedY(48);
                    }
                    fclickedTime++;
                } else {
                    fclickedTime = 0;
                    player1.setBoom(false);
                }
                player1.model(gameTime);
            }
        }
    }

    public MusicPlayer getBgm() {
        return bgm;
    }

    public boolean getIsOver() {
        return isOver;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }

}
