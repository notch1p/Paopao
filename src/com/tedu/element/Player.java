package com.tedu.element;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.rowset.spi.SyncResolver;
import javax.swing.ImageIcon;

import com.tedu.game.GameStart;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;
import com.tedu.show.SelectJPanel;


public class Player extends ElementObj {


    private boolean[] dirFlag = new boolean[] {false, false, false, false};


    private int[] keys = new int[5];

    // Player当前面向的方向，默认为向上
    private int curDir = 1;
    // Player移动速度
    private int moveSpeed = 6;

    private int hp = 1; 		//定义私有属性血量（等价于有多少条命）
    //定义私有属性最大可放置泡泡个数
    private AtomicInteger bubbleNum=new AtomicInteger(1);
    private int playerNum;		//玩家编号
    private int power=1;		//炮弹威力
    private int fclickedY=48;	//闪烁用，改变人物右下角坐标来实现闪烁功能
    private boolean isBoom=false;//闪烁用，检测人物是否被炸


    private int imgX = 0;
    private int imgY = 0; // 0下 1左 2右 3上

    // 上一次的换装时间。用于控制Player的换装速度
    private long imgTime;
    // 死亡动画的开始时间
    private long dieAnimateStartTime = -1;

    // 攻击状态。true：攻击；false：停止
    private boolean pkType = false;


    @Override
    public ElementObj createElement(String str) {
        //System.out.println(str);

        String[] split = str.split(",");
        this.setX(Integer.parseInt(split[0]));
        this.setY(Integer.parseInt(split[1]));
        ImageIcon icon = GameLoad.imgMap.get(split[2]);
        this.setIcon(icon);

        // 注意这里设置的是Player显示区域的宽高（一个格子），而并非素材图片的宽高
        // Player的图片素材是一张需要切割的图片
//		this.setW(icon.getIconWidth());
//		this.setH(icon.getIconHeight());
        this.setW(48);
        this.setH(48);
        this.setPlayerNum(Integer.parseInt(split[split.length-1]));  //最后一个参数记录了玩家编号
        for (int i = 3; i < split.length-1; i++) {
            keys[i - 3] = Integer.parseInt(split[i]);
        }

        // 基类中返回this
        return super.createElement(str);
    }

    public void setPkType(boolean pkType) {
        this.pkType = pkType;
    }

    @Override
    public void showElement(Graphics g) {
//		g.drawImage(this.getIcon().getImage(),
//						this.getX(), this.getY(),
//						this.getW(), this.getH(), null);

        if (dieAnimateStartTime == -1) {
            // 图片分割
            g.drawImage(this.getIcon().getImage(),
                    // Player在面板中显示的区域。一个Player占据一个格子的大小（48*48）
                    this.getX(), this.getY(),  // 左上角坐标
                    this.getX() + 48, this.getY() + fclickedY,   // 右下角坐标
                    // Player在图片素材中的位置区域
                    24 + (imgX*100), 34 + (imgY*100),  // 左上角坐标
                    74 + (imgX*100), 100 + (imgY*100),  // 右下角坐标
                    null);
        } else {
            g.drawImage(GameLoad.imgMap.get("SurroundedByBubbles").getImage(),
                    // Player在面板中显示的区域。一个Player占据一个格子的大小（48*48）
                    this.getX(), this.getY(),  // 左上角坐标
                    this.getX() + 48, this.getY() + 48,   // 右下角坐标
                    // Player在图片素材中的位置区域
                    16 + (imgX*100), 18,  // 左上角坐标
                    88 + (imgX*100), 102,  // 右下角坐标
                    null);
//			System.out.println(imgX);
        }
    }



    @Override
    protected void updateImage(long gameTime) {
        // 动画结束判定
        if (dieAnimateStartTime != -1 && gameTime - dieAnimateStartTime > 70) {
            // 死亡动画进行期间，人物不算死亡
            // 当死亡动画结束，将该Player的live置为False
            this.setLive(false);
            return;
        }

        // 如果当前时间和上次换装的时间的间隔大于3，才换一次装
        if (gameTime - imgTime > 3) {
            // 更新上一次的换装时间
            imgTime = gameTime;
            // 通过更改偏移量，来修改换的装
            imgX++;
//			System.out.println(imgX);
            // 循环换装
            if (imgX > 3) {
                imgX = 0;
            }
        }
    }

    int correctedX=-1;
    int correctedY=-1;
    @Override
    protected void move() {
        // System.out.println("I the fuck moving!");
        // 死亡动画执行期间，禁止移动
        if (dieAnimateStartTime != -1) {
            return;
        }

//		System.out.println(this.isPressed);
//		if (correctedX!=-1 && correctedY!=-1)
//		System.out.println("correctedX=" + correctedX + ";correctedY=" + correctedY);

        // 位置纠正。当方向键松开时，一定确保Player“顺滑”至下一格
        // 谜之代码，不要看，没有任何参考意义
        correctPosition2();

        // 左
        if (dirFlag[0] && this.getX() > 0) {
            this.setX(this.getX() - moveSpeed);
        }

        // 上
        if (dirFlag[1] && this.getY() > 0) {
            this.setY(this.getY() - moveSpeed);

        }

        // 地图大小：720*624
        // 最后需要将面板大小精准控制为地图大小
        // 右
        if (dirFlag[2] && this.getX() < GameJFrame.jp3.getWidth() - 48) {
            this.setX(this.getX() + moveSpeed);
        }

        // 下
        if (dirFlag[3] && this.getY() < GameJFrame.jp3.getHeight() - 48 - 4) {
            // 减4是为了修正误差，误差原因未知
            this.setY(this.getY() + moveSpeed);
        }
    }


    @Override
    protected void add() {
        // 死亡动画执行期间，禁止放泡泡
        if (dieAnimateStartTime != -1) {
            return;
        }
        if(!this.pkType) { //如果是不发射状态，就直接return
            return;
        }
        if (getBubbleNum()<1) {
            return;
        }
        System.out.println("PUTTED");
        pkType=false;

        //传递一个固定格式x:3,y:5,playerNum:1,power:2} json格式
        ElementObj element=new PaoPao().createElement(this.toString());
        GameLoad.playMusic("layBubble");

        //装入到集合中
        ElementManager.getManager().addElement(element,GameElement.PAOPAO);
        setBubbleNum(getBubbleNum()-1);

    }

    @Override
    public String toString() {
        //{x:3,y:5,playerNum:1,power:2}json格式
        int x=this.getX();
        int y=this.getY();
        if (curDir == 0 || curDir == 2) { // 水平方向
            if (x % 48 != 0) {
                int leftX = x / 48 * 48;
                int rightX = leftX + 48;
                x = (x - leftX <= rightX - x) ? leftX : rightX;
            }
        } else if (curDir == 1 || curDir == 3) { // 垂直方向
            if (y % 48 != 0) {
                int upY = y / 48 * 48;
                int downY = upY + 48;
                y = (y - upY <= downY - y) ? upY : downY;
            }
        }
        return "x:"+x+",y:"+y+",playerNum:"+getPlayerNum()+",power:"+getPower()
                +",ttl:72";
    }



    private void correctPosition2() {
        if (!isPressed) {
            if (curDir == 0 || curDir == 2) {
                if (correctedX != -1 && Math.abs(correctedX-this.getX()) < moveSpeed) {
                    this.setX(correctedX);
                    correctedX = -1;
                    Arrays.fill(dirFlag, false);
                }

                if (this.getX() % 48 != 0) {
                    dirFlag[curDir] = true;
                    if (curDir == 0) {
                        correctedX = this.getX()/48*48;
                    } else if (curDir == 2) {
                        correctedX = (this.getX()/48+1)*48;
                    }
                }
            }

            if (curDir == 1 || curDir == 3) {
                if (correctedY != -1 && Math.abs(correctedY-this.getY()) < moveSpeed) {
                    this.setY(correctedY);
                    correctedY = -1;
                    dirFlag[curDir] = false;
                }

                if (this.getY() % 48 != 0) {
                    dirFlag[curDir] = true;
                    if (curDir == 1) {
                        correctedY = this.getY()/48*48;
                    } else if (curDir == 3) {
                        correctedY = (this.getY()/48+1)*48;
                    }
                }
            }
        }
    }


    private void correctPosition() {
        if (curDir == 0 || curDir == 2) { // 水平方向
            int x = this.getX();
            if (x % 48 != 0) {
                int leftX = x / 48 * 48;
                int rightX = leftX + 48;
                int correctedX = (x - leftX <= rightX - x) ? leftX : rightX;
                this.setX(correctedX);
            }
        } else if (curDir == 1 || curDir == 3) { // 垂直方向
            int y = this.getY();
            if (y % 48 != 0) {
                int upY = y / 48 * 48;
                int downY = upY + 48;
                int correctedY = (y - upY <= downY - y) ? upY : downY;
                this.setY(correctedY);
            }
        }
    }


    private boolean isPressed = false;

    @Override
    public void keyClick(boolean isPressed, int key) {

//		this.isPressed = isPressed;
        // 修正，移动过程中放置泡泡后人物会移动不了
        if (key != keys[4]) {
            this.isPressed = isPressed;
        }

//		System.out.println("correctedX=" + correctedX + ";correctedY=" + correctedY);
//		if ((correctedX == -1 && correctedY != -1)
//				|| (correctedY == -1 && correctedX != -1)) {
//			return;
//		}


        // 之所以不用switch是因为case后面的参数必须是常量
        if (isPressed) { // 按下
            if (key == keys[0]) {
                // 向左
                updateDirStatus(0);	this.imgY = 1;
            } else if (key == keys[1]) {
                // 向上
                updateDirStatus(1);	this.imgY = 3;
            } else if (key == keys[2]) {
                // 向右
                updateDirStatus(2);	this.imgY = 2;
            } else if (key == keys[3]) {
                // 向下
                updateDirStatus(3);	this.imgY = 0;
            } else if (key == keys[4]) {
                // 放泡泡
                this.pkType = true; // 开启攻击状态
            }
        } else { // 松开
            // 修正Player的坐标，使Player停下的那一刻，必定在48*48的一格中
            //correctPosition(this.getX(), this.getY());
            if (key == keys[0]) {
                dirFlag[0] = false;
            } else if (key == keys[1]) {
                dirFlag[1] = false;
            } else if (key == keys[2]) {
                dirFlag[2] = false;
            } else if (key == keys[3]) {
                dirFlag[3] = false;
            } else if (key == keys[4]) {
                this.pkType = false; // 关闭攻击状态
            }
        }
    }


    private void updateDirStatus(int curDir) {
        for (int i = 0; i < dirFlag.length; i++) {
            if (i != curDir) {
                // 将其余3个方向标识false。避免45°行走
                dirFlag[i] = false;
            } else {
                dirFlag[i] = true;
            }
        }
        // 更新当前朝向
        this.curDir = curDir;
    }


    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }


//	public void injured(int harm) {
//		if (hp - harm > 0) {
//			hp -=  harm;
//		} else {
//			// 血量为0，触发死亡方法
//			//die();
//		}
// 	}

    //	设置参数用于人物闪烁和闪烁次数
    private int flickerTime=0;
    @Override
    public void die(long gameTime) {
        if (hp >= 1 && flickerTime==0) {
            flickerTime++;
            hp--;
            return;
        }
        if(flickerTime>0 && flickerTime<14)
        {
            flickerTime++;
        }
        else {
            flickerTime=0;
        }
        if(hp<1)
        {
            // 记录死亡动画的开始时间
            dieAnimateStartTime = gameTime;
            // 在死亡动画素材图片的初始偏移量
            imgX = 0;
        }
    }



    @Override
    public boolean collide(ElementObj obj) {
//		System.out.println(obj);
        if (obj instanceof Map) {
            Map map = (Map) obj;
            if (map.getType() == 0) { // 地板不碰撞
                return false;
            }
        }

        // 是否发生碰撞
        boolean isCollided = super.collide(obj);
//		System.out.println(isCollided);
        // 玩家碰到的是墙（不能穿过）
        if (obj instanceof Map) {
            if (isCollided) {
                // 当前方向停止移动
                dirFlag[curDir] = false;
                correctPosition();
            }
        }


        if (obj instanceof PaoPao) {
//			System.out.println(isCollided);
            PaoPao paopao = (PaoPao) obj;
            if (isCollided) {
                if (paopao.isFirst()) { // 第一次
//						System.out.println("11");
                    return false; // 纠正，为false
                } else {
//					if (!paopao.isFirst()) {
//						System.out.println("22");
                    dirFlag[curDir] = false;
                    correctPosition();
                    return true;
                }
            } else {
                if (paopao.getPlayerNum() == this.playerNum) {
                    paopao.setFirst(false);
                }
            }
        }

        return isCollided;
    }
    //泡泡个数、血量和玩家编号的get和set方法
    @Override
    public int compareTo(ElementObj o) {
        return 0;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getBubbleNum() {
        return bubbleNum.get();
    }

    public void setBubbleNum(int bubbleNum) {
        this.bubbleNum.set(bubbleNum);
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getFclickedY() {
        return fclickedY;
    }

    public void setFclickedY(int fclickedY) {
        this.fclickedY = fclickedY;
    }

    public boolean isBoom() {
        return isBoom;
    }

    public void setBoom(boolean isBoom) {
        this.isBoom = isBoom;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

}
