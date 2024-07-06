package com.tedu.element;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;

public class Enemy extends Player {
    private final static int maxW = 15;
    private final static int maxH = 14;
    private final static int eW = 48;
    private final static int eH = 48;
    private GameElement[][] prmaps = new GameElement[maxW][maxH];
    private GameElement[][] maps = new GameElement[maxW][maxH];
    private ElementManager em = ElementManager.getManager();

    private int[] dx = { -1, 0, 1, 0, 0 };
    private int[] dy = { 0, -1, 0, 1, 0 };
    public String[] ds = { "left", "up", "right", "down", "stay" };// 调试用

    private int imgX = 0;
    private int imgY = 0; // 0下 1左 2右 3上
    private int fclickedY = 48; // 闪烁用，改变人物右下角坐标来实现闪烁功能
    private long imgTime;

    // @Override
    // public void showElement(Graphics g) {
    //// g.drawImage(this.getIcon().getImage(),
    //// this.getX(), this.getY(),
    //// this.getW(), this.getH(), null);
    // // 图片分割
    // g.drawImage(this.getIcon().getImage(),
    // // Player在面板中显示的区域。一个Player占据一个格子的大小（48*48）
    // this.getX(), this.getY(), // 左上角坐标
    // this.getX() + 48, this.getY() + fclickedY, // 右下角坐标
    // // Player在图片素材中的位置区域
    // 24 + (imgX*100), 34 + (imgY*100), // 左上角坐标
    // 74 + (imgX*100), 100 + (imgY*100), // 右下角坐标
    // null);
    // }
    //
    // @Override
    // protected void updateImage(long gameTime) {
    // // 如果当前时间和上次换装的时间的间隔大于3，才换一次装
    // if (gameTime - imgTime > 3) {
    // // 更新上一次的换装时间
    // imgTime = gameTime;
    // // 通过更改偏移量，来修改换的装
    // imgX++;
    //// System.out.println(imgX);
    // // 循环换装
    // if (imgX > 3) {
    // imgX = 0;
    // }
    // }
    // }

    private boolean outBoundary(int x, int y) {
        return x < 0 || y < 0 || x >= maxW || y >= maxH;
    }

    private void getBubbleThreads(int x, int y, int r, GameElement ge) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 1; j <= r; ++j) {// j=1;
                int ax = x + dx[i] * j;
                int ay = y + dy[i] * j;
                if (outBoundary(ax, ay)) {
                    continue;
                }
                if (maps[ax][ay].equals(GameElement.FLOOR) ||
                        maps[ax][ay].equals(GameElement.TOOL) ||
                        maps[ax][ay].equals(GameElement.PLAYER)) {
                    maps[ax][ay] = ge;
                    // System.out.println("(" + ax + "," + ay + ")" + " " + ge);
                }
            }
        }
    }

    private void getMapElement(GameElement ge) {
        List<ElementObj> list = em.getElementsByKey(ge);
        // System.out.println(ge + " has " + list.size());
        for (ElementObj obj : list) {
            int mx = obj.getX() / eW;
            int my = obj.getY() / eH;
            GameElement type = null;
            if (ge.equals(GameElement.MAPS)) {
                Map map = (Map) obj;
                if (map.getType() == 1) {
                    type = GameElement.WEAKMAPS;
                } else if (map.getType() == 0) {
                    type = GameElement.FLOOR;
                } else {
                    type = ge;
                }
            } else if (ge.equals(GameElement.PAOPAO)) {
                // System.out.println("PAOPAO");
                type = GameElement.PAOPAO;
                PaoPao bubble = (PaoPao) obj;
                getBubbleThreads(mx, my, bubble.getPower(), GameElement.EXPLODE);
            } else if (ge.equals(GameElement.EXPLODE)) {
                // System.out.println("EXPLODE");
                type = GameElement.MAPS;
                PaoPaoExplode bubble = (PaoPaoExplode) obj;
                getBubbleThreads(mx, my, bubble.getPower(), GameElement.MAPS);
            } else {
                type = ge;
            }
            maps[mx][my] = type;
        }
    }

    public void printMaps() {
        for (int j = 0; j < maxH; ++j) {
            for (int i = 0; i < maxW; ++i) {
                if (maps[i][j] != null) {
                    System.out.print(String.format("%8s", maps[i][j]));
                } else {
                    System.out.print(String.format("%8s", "NULL"));
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private int[][] dist = new int[maxW][maxH]; // 最短步数
    private int[][] prev = new int[maxW][maxH]; // 当前这一步可以从上一步怎么走来
    private final static int infinity = maxW * maxH;

    private class node {
        public int x = 0;
        public int y = 0;
        public int d = 0;

        public node(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
        }
    }

    private static Comparator<node> cmp = new Comparator<Enemy.node>() {
        public int compare(node lhs, node rhs) {
            if (lhs.d == rhs.d) {
                return Math.random() < 0.5 ? 1 : -1;
            }
            return lhs.d - rhs.d;
        }
    };

    Queue<node> attackTarget = new PriorityQueue<>(cmp);

    private void BFS() {
        int ax = getX() / eW;
        int ay = getY() / eH;
        for (int i = 0; i < maxW; ++i) {
            for (int j = 0; j < maxH; ++j) {
                dist[i][j] = infinity;
                prev[i][j] = -1;// 不可达
            }
        }
        Queue<node> q = new PriorityQueue<>(cmp);
        q.add(new node(ax, ay, 0));
        dist[ax][ay] = 0;
        while (!q.isEmpty()) {
            node p = q.peek();
            q.poll();
            int bx = p.x;
            int by = p.y;
            for (int i = 0; i < 4; ++i) {
                int cx = bx + dx[i];
                int cy = by + dy[i];
                if (outBoundary(cx, cy) || dist[cx][cy] != infinity) {
                    continue;
                }
                prev[cx][cy] = i;
                if (maps[cx][cy].equals(GameElement.WEAKMAPS)) {
                    attackTarget.add(new node(cx, cy, 2 * infinity + p.d + 1));
                } else if (maps[cx][cy].equals(GameElement.TOOL)) {
                    attackTarget.add(new node(cx, cy, 1 * infinity + p.d + 1));
                } else if (maps[cx][cy].equals(GameElement.PLAYER)) {
                    attackTarget.add(new node(cx, cy, 0 * infinity + p.d + 1));
                }
                if (maps[cx][cy].equals(GameElement.MAPS)
                        // || maps[cx][cy].equals(GameElement.EXPLODE)
                        || maps[cx][cy].equals(GameElement.WEAKMAPS)) {
                    dist[cx][cy] = infinity - 1;
                    continue;
                }
                if (maps[cx][cy].equals(GameElement.PAOPAO) && !(cx == ax && cy == ay)) {
                    dist[cx][cy] = infinity - 1;
                    continue;
                }
                dist[cx][cy] = p.d + 1;
                q.add(new node(cx, cy, dist[cx][cy]));
            }
        }
    }

    public void printDist() {
        for (int j = 0; j < maxH; ++j) {
            for (int i = 0; i < maxW; ++i) {
                System.out.print(String.format("%9d", dist[i][j]));
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printPrev() {
        for (int j = 0; j < maxH; ++j) {
            for (int i = 0; i < maxW; ++i) {
                if (prev[i][j] != -1) {
                    System.out.print(String.format("%9s", ds[prev[i][j]]));
                } else {
                    System.out.print(String.format("%9s", "block"));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printGrid() {
        for (int j = 0; j < maxH; ++j) {
            for (int i = 0; i < maxW; ++i) {
                System.out.print(String.format("  (%2d,%2d)", i, j));
            }
            System.out.println();
        }
        System.out.println();
    }

    private void fillInTheBlank() {
        for (int i = 0; i < maxW; ++i) {
            for (int j = 0; j < maxH; ++j) {
                if (maps[i][j] == null) {
                    maps[i][j] = GameElement.FLOOR;
                }
            }
        }
    }

    private int stopped = 0;

    private void learnFromPrev() {
        boolean warn = false;
        tag: for (int i = 0; i < maxW; ++i) {
            for (int j = 0; j < maxH; ++j) {
                if (prmaps[i][j] == null) {
                    continue;
                }
                if (prmaps[i][j].equals(GameElement.PAOPAO) &&
                        !maps[i][j].equals(GameElement.PAOPAO)) {
                    warn = true;
                    break tag;
                }
            }
        }
        if (warn) {
            stopped = 15;
            System.out.println("fflush");
        }
        if (stopped >= 0 &&
                !maps[getX() / eW][getY() / eH].equals(GameElement.PAOPAO)) {
            for (int i = 0; i < maxW; ++i) {
                for (int j = 0; j < maxH; ++j) {
                    maps[i][j] = GameElement.MAPS;
                }
            }
        }
        for (int i = 0; i < maxW; ++i) {
            for (int j = 0; j < maxH; ++j) {
                prmaps[i][j] = maps[i][j];
            }
        }
        --stopped;
    }

    private void learnCurrentMap() {
        GameElement[] geReq = { GameElement.MAPS, GameElement.TOOL,
                GameElement.PLAYER, GameElement.PAOPAO, GameElement.EXPLODE };
        for (GameElement ge : geReq) {
            getMapElement(ge);
        }
        fillInTheBlank();
        learnFromPrev();
        BFS();
    }

    public void printAttacks() {
        Queue<node> q = attackTarget;
        System.out.println("Attacks:");
        while (!q.isEmpty()) {
            node p = q.peek();
            q.poll();
            System.out.println("" + p.x + "," + p.y + "," + p.d + "," + maps[p.x][p.y]);
        }
    }

    private int moveLeft = 0;// 还差几次移动才能完成
    private int direction = 0;
    // 可能现在不需要了，保险起见还是用着吧
    private int lockX = 0;
    private int lockY = 0;
    // 残差
    private int remainX = 0;
    private int remainY = 0;

    private void walk() {
        setX(lockX);
        setY(lockY);
        int speed = getMoveSpeed();
        // System.out.println("before-" + moveLeft + "," + getX() + "," + getY() +
        // ",speed=" + speed);
        int newx = getX() + dx[direction] * speed;
        int newy = getY() + dy[direction] * speed;
        if (moveLeft == 1) {// 磕了加速可能除不尽，特判一下
            newx = remainX;
            newy = remainY;
        }
        lockX = newx;
        lockY = newy;
        setX(newx);
        setY(newy);
        --moveLeft;
        // System.out.println("after-" + moveLeft + "," + getX() + "," + getY() +
        // ",speed=" + speed);
        // if (moveLeft == 0) {
        // printMaps();
        // printGrid();
        // System.out.println("walked " + getX() + "," + getY()
        // + " (" + getX() / eW + "," + getY() / eH + ") "
        // + maps[getX() / eW][getY() / eH]);
        // }
    }

    private boolean checkSafe(int d) {
        int cx = getX() / eW;
        int cy = getY() / eH;
        int nx = cx + dx[d];
        int ny = cy + dy[d];
        if (maps[nx][ny].equals(GameElement.EXPLODE)) {
            return false;
        }
        return true;
    }

    @Override
    protected void move() {
    }

    private void moveout(int d, boolean safely) {
        // System.out.println("plans to move " + (d != -1 ? ds[d] : "into bug"));
        if (d >= 0 && d < 4) {
            if (safely && !checkSafe(d)) {
                return;
            }
            int cx = getX() / eW;
            int cy = getY() / eH;
            int nx = cx + dx[d];
            int ny = cy + dy[d];
            if (maps[nx][ny].equals(GameElement.MAPS) ||
                    maps[nx][ny].equals(GameElement.WEAKMAPS) ||
                    maps[nx][ny].equals(GameElement.PAOPAO)) {
                return;
            }
            // System.out.println("moving");
            int speed = getMoveSpeed();
            moveLeft = (eW + speed - 1) / speed;// 上取整
            remainX = getX() + dx[d] * eW;
            remainY = getY() + dy[d] * eH;
            direction = d;
            if (direction == 0)
                this.imgY = 3;
            else if (direction == 3)
                this.imgY = 0;
            else
                this.imgY = direction;
            lockX = getX();
            lockY = getY();
            // System.out.println(getX() + "," + getY());
            walk();
        }
    }

    private void moveout(int d) {
        moveout(d, true);
    }

    private int lastNum = getBubbleNum();// 上一回合炸弹数

    private boolean checkSuicide() {
        GameElement[][] news = new GameElement[maxW][maxH];
        int safe = 0;
        for (int i = 0; i < maxW; ++i) {
            for (int j = 0; j < maxH; ++j) {
                if (dist[i][j] >= infinity - 1) {
                    news[i][j] = GameElement.MAPS;
                } else {
                    news[i][j] = maps[i][j];
                }
                if (news[i][j].equals(GameElement.TOOL) ||
                        news[i][j].equals(GameElement.PLAYER) ||
                        news[i][j].equals(GameElement.FLOOR)) {
                    ++safe;
                }
            }
        }
        return safe == 0;
    }

    private void bomb() {
        if (lastNum == 0) {
            // System.out.println("wait a moment");
            lastNum = getBubbleNum();
            return;// 不要反复鞭尸炸残骸
        }
        if (checkSuicide()) {// 放了炸弹无路可走
            lastNum = getBubbleNum();
            return;
        }
        // System.out.println("put a bomb at " + getX() / eW + "," + getY() / eH);
        setPkType(true);
        add();// 有炸弹就炸死他，没有就等到有为止
        lastNum = getBubbleNum();
    }

    private int neighbor(int ax, int ay, int bx, int by) {
        for (int i = 0; i < 4; ++i) {
            if (bx + dx[i] == ax && by + dy[i] == ay) {
                return i;
            }
        }
        return (ax == ay && bx == by) ? 4 : -1;
    }

    private boolean bombable(int ax, int ay, int bx, int by) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 1; j <= getPower(); ++j) {
                int cx = bx + dx[i] * j;
                int cy = by + dy[i] * j;
                if (outBoundary(cx, cy)) {
                    continue;
                }
                if (cx == ax && cy == ay && (maps[cx][cy].equals(GameElement.WEAKMAPS)
                        || maps[cx][cy].equals(GameElement.PLAYER))) {
                    // System.out.println("from (" + bx + "," + by + ") reach (" + cx + "," + cy +
                    // ")");
                    return true;
                }
                if (maps[cx][cy].equals(GameElement.MAPS) ||
                        maps[cx][cy].equals(GameElement.WEAKMAPS)) {
                    break;
                }
            }
        }
        return false;
    }

    private void negative() {
        // System.out.println("negative");
        int minDist = infinity;
        int tx = 0;
        int ty = 0;
        int cx = getX() / eW;
        int cy = getY() / eH;
        for (int i = 0; i < maxW; ++i) {
            for (int j = 0; j < maxH; ++j) {
                if (cx == i && cy == j) {
                    continue;
                }
                if (!maps[i][j].equals(GameElement.EXPLODE) &&
                        !maps[i][j].equals(GameElement.PAOPAO)
                        && dist[i][j] < minDist) {
                    minDist = dist[i][j];
                    tx = i;
                    ty = j;
                }
            }
        }
        // System.out.println("walkaway " + tx + ", " + ty + ",d=" + minDist);

        while (true) {
            if (-1 != neighbor(tx, ty, cx, cy)) {
                break;
            }
            int nx = tx - dx[prev[tx][ty]];
            int ny = ty - dy[prev[tx][ty]];
            // System.out.println("(" + tx + "," + ty + ")=>(" + nx + "," + ny + ")");
            tx = nx;
            ty = ny;
        }
        moveout(neighbor(tx, ty, cx, cy), false);
    }

    private void positive(node target) {
        // System.out.println("positive");
        int tx = target.x;
        int ty = target.y;
        int ox = tx;
        int oy = ty;
        int cx = getX() / eW;
        int cy = getY() / eH;
        while (true) {
            if (neighbor(tx, ty, cx, cy) != -1) {
                break;
            }
            if (!maps[ox][oy].equals(GameElement.TOOL) &&
                    bombable(tx, ty, cx, cy)) {
                break;
            }
            if (prev[tx][ty] == -1) {// unknown bug
                break;
            }
            int nx = tx - dx[prev[tx][ty]];
            int ny = ty - dy[prev[tx][ty]];
            tx = nx;
            ty = ny;
        }
        if (maps[ox][oy].equals(GameElement.TOOL)) {
            moveout(neighbor(tx, ty, cx, cy));
        } else {
            if (bombable(ox, oy, cx, cy)) {
                bomb();
            } else {
                moveout(neighbor(tx, ty, cx, cy));
            }
        }
    }

    private void decide() {
        if (maps[getX() / eW][getY() / eH].equals(GameElement.EXPLODE) ||
                maps[getX() / eW][getY() / eH].equals(GameElement.PAOPAO)) {
            negative();
            return;
        }
        if (!attackTarget.isEmpty()) {
            node target = attackTarget.peek();
            attackTarget.poll();
            positive(target);
        }
    }

    public void automate() {
        if (moveLeft != 0) {
            walk();
            return;
        }
        learnCurrentMap();
        // printMaps();
        // printDist();
        // printPrev();
        // printGrid();
        decide();
    }
}
