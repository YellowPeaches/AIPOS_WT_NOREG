package com.wintec.lamp.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wintec.lamp.ScaleActivityUI;
import com.wintec.lamp.base.Const;

import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
    private static String IP = Const.getSettingValue(Const.KEY_GET_DATA_IP);
    private static String PORT = Const.getSettingValue(Const.KEY_GET_DATA_PORT);  //数据库名
    private static String DBName = Const.getSettingValue(Const.KEY_GET_DATA_DB_NAME);  //数据库名
    private static String USER = Const.getSettingValue(Const.KEY_GET_DATA_USER);
    private static String PWD = Const.getSettingValue(Const.KEY_GET_DATA_PWD);
    private static Handler handler;
    public static Connection con = null;
    public static long timeFlag = System.currentTimeMillis();

    public static void init(Handler handler1) {
        handler = handler1;
    }

    /**
     * 创建数据库对象
     */
    private static Connection getSQLConnection() throws SQLException, ClassNotFoundException {
        if (con == null) {
//            if (System.currentTimeMillis()-timeFlag>60000){
                if ("oracle".equals(Const.getSettingValue(Const.KEY_GET_DATA_DB))) {
                    String url = "jdbc:oracle:thin:@" + IP + ":" + PORT + ":" + DBName;
                    String user = USER;
                    String password = PWD;
                    try {
                        Class.forName("oracle.jdbc.driver.OracleDriver");//加载数据驱动
                        DriverManager.setLoginTimeout(1);
                        logWriteData("开始DriverManager.getConnection(url, user, password)");
                        con = DriverManager.getConnection(url, user, password);// 连接数据库
                        logWriteData("getSQLConnection()完成");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        Log.d("TAG", "加载数据库驱动失败");
                        logWriteData("加载数据库驱动失败" + e.getMessage());
                    } catch (Exception e) {
                        timeFlag=System.currentTimeMillis();
                        e.printStackTrace();
                        Log.d("TAG", "连接数据库失败");
                        logWriteData("连接数据库失败-" + e.getMessage());
                    }
                } else {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    //加上 useunicode=true;characterEncoding=UTF-8 防止中文乱码
                    DriverManager.setLoginTimeout(3);
                    con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DBName + ";useunicode=true;characterEncoding=UTF-8;connectTimeout=1000&socketTimeout=60000", USER, PWD);
                }
//            }
        }
        return con;
    }

    /**
     * 返回的List的[1]数据时List<Map>
     *
     * @param sql
     * @return
     * @throws
     */
    //region 传入sql,返回转换成List(查询)
    public static List Query(String sql) {
        List result = null;
        try {
            result = new ArrayList();
            ResultSet rs = null;
            Connection conn = getSQLConnection();
            if (conn == null) {

            }
//            Connection conn = DruidJDBCUtils.getConnection();
            Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(1);
            rs = stmt.executeQuery(sql);
            result = convertList(rs);
            rs.close();
            stmt.close();
//            conn.close();
        } catch (SQLException e) {
            logWriteData("连接异常1" + e.getMessage());
            Message msg = handler.obtainMessage();
            msg.what = ScaleActivityUI.SHOW_FAIL;
            msg.obj = "查询数据异常1";
            handler.sendMessage(msg);
        } catch (ClassNotFoundException e) {
            logWriteData("连接异常2" + e.getMessage());
            Message msg = handler.obtainMessage();
            msg.what = ScaleActivityUI.SHOW_FAIL;
            msg.obj = "查询数据异常2";
            handler.sendMessage(msg);
        } catch (Exception e) {
            logWriteData("数据库连接异常" + e.getMessage());
            Message msg = handler.obtainMessage();
            msg.what = ScaleActivityUI.SHOW_FAIL;
            msg.obj = "数据库 连接异常";
            handler.sendMessage(msg);
        }
        if (result == null) {
            logWriteData("没有数据");
        }
        return result;
    }

    private static String tostrings(String[] arr) {
        StringBuilder str5 = new StringBuilder();
        for (String s : arr) {
            str5.append(s);
            str5.append(",");
        }
        return str5.toString();
    }

    /**jihu
     * 更新数据，新增，修改，删除
     * 不成功则回滚
     */
    //region 更新数据，新增，修改，删除 返回int
    public static int exeList(String[] sql) throws SQLException {
        int rs = 0;
        int index = 0;
        Connection conn = null;
        try {

            conn = getSQLConnection();
            conn.setAutoCommit(false);  //不自动提交（默认执行一条成功则提交，现在都执行完成功才手动提交）
            Statement stmt = conn.createStatement();//
            for (String item : sql) {
                rs += stmt.executeUpdate(item);
                index++;
            }
            conn.commit();
            stmt.close();
            conn.close();//提交
        } catch (SQLException e) {
            //  LogUtil.e("TestDButil", e.getMessage() + ","+tostrings(sql) ); //本机打印日志
            String res = "查询数据异常3" + e.getMessage();
            e.printStackTrace();
            conn.rollback();
            return 0;
        } catch (Exception e) {
            // LogUtil.e("TestDButil", e.getMessage() + "," ); //本机打印日志
            if (conn != null) {
                conn.rollback();
            }
            return 0;
        }

        return rs;
    }
    //endregion


    //返回list,ResultSet转List<map>
    public static List convertList(ResultSet rs) throws SQLException {
        List all = new ArrayList();
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名
        int columnCount = md.getColumnCount();//获取行的数量
        String res = "ok";


        all.add(res);
        int coun = 0;
        while (rs.next()) {
            Map rowData = new HashMap();//声明Map
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
            }
            list.add(rowData);
            coun++;
        }
        if (coun < 1) {
            all.set(0, "nodate");
        }
        all.add(list);

        return all;
    }
    //endregion


    /**
     * 更新数据，新增，修改，删除
     */
    //region 更新数据，新增，修改，删除 返回int
    public static int exesqlint(String sql) throws SQLException {
        int rs = 0;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getSQLConnection();
            stmt = conn.createStatement();
            rs = stmt.executeUpdate(sql);
            logWriteData("改价成功  " + sql);
        } catch (SQLException e) {
            //      LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志

            String res = "查询数据异常4 改价上报异常" + e.getMessage();
            logWriteData(res);
            e.printStackTrace();

            return 0;
        } catch (Exception e) {
            // LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志
            logWriteData("改价上报异常2" + e.getMessage() + "-" + sql);

            return 0;
        } finally {
            stmt.close();
            conn.close();
        }

        return rs;
    }
    //endregion

    //region 更新数据，新增，修改，删除 返回LIST数据
    public static List exesql(String sql) {
        List result = new ArrayList();
        int rs = 0;
        try {
            String ress = "";
            Connection conn = getSQLConnection();

            Statement stmt = conn.createStatement();//
            rs = stmt.executeUpdate(sql);
            if (rs > 0) {
                ress = "ok";
            } else {
                ress = "nodate";
            }
            result.add(ress);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            String res = "请联系管理员，异常" + e.getMessage();
            // LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志
            e.printStackTrace();
            result.add(res);
            return result;
        } catch (Exception e) {
            String res = "无网络";
            //  LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志

            result.add(res);
            return result;
        }
        return result;
    }


    //endregion


    /**
     * 查询，有无该条数据
     *
     * @param sql
     * @return
     * @throws
     */
    //region 查询，又多少条行数
    public static int hasrows(String sql) {
        int result = 0;

        try {
            Connection conn = getSQLConnection();

            Statement stmt = conn.createStatement();//
            ResultSet ss = stmt.executeQuery(sql);
            if (!ss.next()) {
                result = 0;
            } else {
                result = 1;
            }
            ss.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            String res = "查询数据异常5" + e.getMessage();
            //        LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志

            return -1;
        } catch (Exception e) {
            String res = "无网络";
            //     LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志

            return -1;

        }
        return result;
    }
    //endregion


    //region 传入sql,返回转换成List(查询)
    public static <T> List QueryT(String sql, T t) {
        List result = new ArrayList();

        ResultSet rs = null;
        try {
            Connection conn = getSQLConnection();

            Statement stmt = conn.createStatement();//
            rs = stmt.executeQuery(sql);
            result = util(t, rs);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            //  LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志

//            String res = "查询数据异常" + e.getMessage();
//            e.printStackTrace();
            String res = "nodate";
            result.add(res);
            return result;
        } catch (Exception e) {
            // LogUtil.e("TestDButil", e.getMessage() + "," + sql); //本机打印日志

            String res = "无网络";
            result.add(res);
            return result;
        }

        return result;
    }


    /**
     * ResultSet转List<T>
     *
     * @param t
     * @param rs
     * @return
     * @throws
     */
    public static <T> List util(T t, ResultSet rs) throws Exception {
        // 创建一个对应的空的泛型集合
        List<T> list = new ArrayList<T>();
        List ALL = new ArrayList();
        // 反射出类类型（方便后续做操作）
        Class c = t.getClass();
        // 获得该类所有自己声明的字段，不问访问权限.所有。所有。所有
        Field[] fs = c.getDeclaredFields();
        int count = 0;
        // 大家熟悉的操作，不用多说
        ALL.add("nodate");
        int ros = rs.getRow();
        if (rs != null) {
            while (rs.next()) {
                count++;
                if (count == 1) {
                    ALL.set(0, "ok");
                }
                // 创建实例
                t = (T) c.newInstance();
                // 赋值
                for (int i = 0; i < fs.length; i++) {
                    /*
                     * fs[i].getName()：获得字段名
                     *
                     * f:获得的字段信息
                     */
                    Field f = t.getClass().getDeclaredField(fs[i].getName());
                    // 参数true 可跨越访问权限进行操作
                    f.setAccessible(true);
                    /*
                     * f.getType().getName()：获得字段类型的名字
                     */
                    // 判断其类型进行赋值操作
                    if (f.getType().getName().equals(String.class.getName())) {
                        f.set(t, rs.getString(fs[i].getName()));
                    } else if (f.getType().getName().equals(int.class.getName())) {
                        f.set(t, rs.getInt(fs[i].getName()));
                    }
                }

                list.add(t);
            }
        }

        ALL.add((list));
        // 返回结果
        return ALL;
    }

    //endregion

    /**
     * List<Map<String, Object>>转List<T>
     *
     * @param list
     * @param clazz
     * @return
     * @throws
     */

    public static <T> List<T> castMapToBean(List<Map<String, Object>> list, Class<T> clazz) throws Exception {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<T> tList = new ArrayList<T>();
        // 获取类中声明的所有字段
        Field[] fields = clazz.getDeclaredFields();

        T t;
        for (Map<String, Object> map : list) {
            // 每次都先初始化一遍,然后再设置值
            t = clazz.newInstance();
            for (Field field : fields) {
                // 把序列化的字段去除掉
                if (!"serialVersionUID".equals(field.getName())) {
                    // 由于Field都是私有属性，所有需要允许修改
                    field.setAccessible(true);

                    // 设置值, 类型要和vo对应好,不然会报类型转换错误
                    field.set(t, map.get(field.getName()));
                }
            }
            tList.add(t);
        }

        return tList;
    }


    public static void logWriteData(String log) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateNow = dateFormat.format(date);

        String filePath = "/sdcard/LAMP/logs/";
        String fileName = dateNow.substring(0, 8) + "log.txt";

        writeTxtToFile(log, filePath, fileName);
    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String dateNow = dateFormat.format(date);

        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = dateNow + " - " + strcontent + "\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

}

