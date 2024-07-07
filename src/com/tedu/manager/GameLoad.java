package com.tedu.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;

import com.tedu.element.ElementObj;


public class GameLoad {
    //	得到资源管理器
    private static ElementManager em = ElementManager.getManager();

    //	图片集合  使用map来进行存储     枚举类型配合移动(扩展)
    public static Map<String, ImageIcon> imgMap = new HashMap<>();
    public static Map<String, MusicPlayer> musicMap = new HashMap<>();


//	public static Map<String, List<ImageIcon>> imgMaps;

    //	用户读取文件的类
    private static Properties pro = new Properties();


    public static void playMusic(String key) {
        MusicPlayer musicPlayer = musicMap.get(key);
        if (musicPlayer != null) {
            musicPlayer.play();
        }
    }


    public static void MapLoad(int mapId) {
        loadObj();
//		得到啦我们的文件路径
        String mapName="com/tedu/text/"+mapId+".map";
//		使用io流来获取文件对象   得到类加载器
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream maps = classLoader.getResourceAsStream(mapName);
        if(maps ==null) {
            System.out.println("配置文件读取异常,请重新安装");
            return;
        }
        try {
//			以后用的 都是 xml 和 json
            pro.clear();
            pro.load(maps);
//			可以直接动态的获取所有的key，有key就可以获取 value
//			java学习中最好的老师 是 java的API文档。
            Enumeration<?> names = pro.propertyNames();
            while(names.hasMoreElements()) {//获取是无序的
//				这样的迭代都有一个问题：一次迭代一个元素。
                String key=names.nextElement().toString();
//				就可以自动的创建和加载 我们的地图啦
                String [] arrs=pro.getProperty(key).split(";");
                for(int i=0;i<arrs.length;i++) {
                    ElementObj obj= getObj("Map");
                    ElementObj element = null;
                    if (obj != null) {
                        element = obj.createElement(key+","+arrs[i]);
                    }
                    em.addElement(element, GameElement.MAPS);
                }
            }

            // 对地图的List进行排序
            Collections.sort(em.getElementsByKey(GameElement.MAPS));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void loadImg() {// 可以带参数，因为不同的关也可能需要不一样的图片资源
        String texturl = "com/tedu/text/ImageData.pro";// 文件的命名可以更加有规律
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream texts = classLoader.getResourceAsStream(texturl);
//		imgMap用于存放数据
        pro.clear();
        try {
//			System.out.println(texts);
            pro.load(texts);
            Set<Object> set = pro.keySet();// 是一个set集合
            for (Object o : set) {
                String url = pro.getProperty(o.toString());
                imgMap.put(o.toString(), new ImageIcon(url));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void loadMusic() {
        String texturl = "com/tedu/text/MusicData.pro";
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream texts = classLoader.getResourceAsStream(texturl);
        // 由于pro是公用的，先清空上次数据
        pro.clear();
        try {
            pro.load(texts);
            Set<Object> set = pro.keySet();// 是一个set集合
            for (Object o : set) {
                String url = pro.getProperty(o.toString());
                musicMap.put(o.toString(), new MusicPlayer(url));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void loadPlayer(String... playerStrArr) {
        loadObj();

        for (int i = 0; i < playerStrArr.length; i++) {
            // 通过反射获取Player的实例。传入的参数对应obj.pro里面的key
            ElementObj obj = getObj("Player");
            ElementObj player = null;
            if (obj != null) {
                player = obj.createElement(playerStrArr[i]);
            }

            // 将Player的实例放入元素管理器
            em.addElement(player, GameElement.PLAYER);
        }
    }

    public static void loadEnemy(String... enemyStrings) {
        loadObj();
        for (int i = 0; i < enemyStrings.length; i++) {
            // 通过反射获取Player的实例。传入的参数对应obj.pro里面的key
            ElementObj obj = getObj("Enemy");
            ElementObj player = null;
            if (obj != null) {
                player = obj.createElement(enemyStrings[i]);
            }

            // 将Player的实例放入元素管理器
            em.addElement(player, GameElement.ENEMY);
        }
    }

    public static void loadPlayFile(String str) {
        loadObj();
        // String playStr="300,300,playfile";
        ElementObj obj = getObj("playfile");
        ElementObj play = null;
        if (obj != null) {
            play = obj.createElement(str);
        }
//		ElementObj play = new Play().createElement(playStr);
//		解耦,降低代码和代码之间的耦合度 可以直接通过 接口或者是抽象父类就可以获取到实体对象
        em.addElement(play, GameElement.PAOPAO);
    }

    public static ElementObj getObj(String str) {
        try {
            Class<?> class1 = objMap.get(str);
            Object newInstance = class1.newInstance();
            if (newInstance instanceof ElementObj) {
                return (ElementObj) newInstance; // 这个对象就和 new Play()等价
//				新建立啦一个叫  GamePlay的类
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static final Map<String, Class<?>> objMap = new HashMap<>();


    public static void loadObj() {
        String texturl = "com/tedu/text/obj.pro";// 文件的命名可以更加有规律
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream texts = classLoader.getResourceAsStream(texturl);
        pro.clear();
        try {
            pro.load(texts);
            Set<Object> set = pro.keySet();// 是一个set集合
            for (Object o : set) {
                String classUrl = pro.getProperty(o.toString());
//				使用反射的方式直接将 类进行获取
                Class<?> forName = Class.forName(classUrl);
                objMap.put(o.toString(), forName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

//	用于测试
//	public static void main(String[] args) {
//		MapLoad(5);
//	}
//

}
