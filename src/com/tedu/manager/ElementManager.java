package com.tedu.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tedu.element.ElementObj;


public class ElementManager {


    private Map<GameElement, List<ElementObj>> gameElements;

    public Map<GameElement, List<ElementObj>> getGameElements() {
        return gameElements;
    }

    // 添加元素，多半由加载器调用
    public void addElement(ElementObj obj, GameElement ge) {
        gameElements.get(ge).add(obj);
    }

    // 删除元素，多半由加载器调用
    public void removeElement(int index, GameElement ge) {
        ElementObj obj = gameElements.get(ge).remove(index);
//		System.out.println("移除：" + obj);
    }

    // 根据key，取出对应的集合
    public List<ElementObj> getElementsByKey(GameElement ge) {
        return gameElements.get(ge);
    }


    private static ElementManager EM = null;	// 单例的引用

    public static synchronized ElementManager getManager() {
        if (EM == null) {
            EM = new ElementManager();	// 饱汉模式
        }
        return EM;
    }



    // 私有化构造方法
    private ElementManager() {
        init();
    }


    protected void init() {
        gameElements = new HashMap<>();

        for (GameElement ge : GameElement.values()) {
            gameElements.put(ge, new ArrayList<>());
        }

//		gameElements.put(GameElement.PLAY, new ArrayList<ElementObj>());
//		gameElements.put(GameElement.MAPS, new ArrayList<ElementObj>());
//		gameElements.put(GameElement.ENEMY, new ArrayList<ElementObj>());
//		gameElements.put(GameElement.BOSS, new ArrayList<ElementObj>());

        // 道具、子弹、爆炸效果...
    }


    public void clearAll() {
        Set<GameElement> keySet = gameElements.keySet();
        for (GameElement ge : keySet) {
            gameElements.get(ge).clear();
        }
    }



}
