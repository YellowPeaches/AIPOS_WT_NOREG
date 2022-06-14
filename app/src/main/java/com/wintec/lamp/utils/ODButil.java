//package com.wintec.lamp.utils;
//
//import com.wintec.lamp.base.Const;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ODButil {
//    private static String IP = Const.getSettingValue(Const.KEY_GET_DATA_IP);
//    private static String PORT = Const.getSettingValue(Const.KEY_GET_DATA_PORT);  //数据库名
//    private static String DBName = Const.getSettingValue(Const.KEY_GET_DATA_DB_NAME);  //数据库名
//    private static String USER = Const.getSettingValue(Const.KEY_GET_DATA_USER);
//    private static String PWD = Const.getSettingValue(Const.KEY_GET_DATA_PWD);
//
//    /**
//     * 创建数据库对象
//     */
//    private static Connection getSQLConnection() throws SQLException, ClassNotFoundException {
//        Connection con = null;
//
//        Class.forName("oracle.jdbc.driver.OracleDriver");//加载数据驱动
//        String url="jdbc:oracle:thin:@"+IP+":"+PORT+":"+DBName;
//        String user="USER";
//        String password="PWD";
//        con = DriverManager.getConnection(url, user, password);// 连接数据库
//
//        return con;
//    }
//
//    public static List Query(String sql) throws SQLException, ClassNotFoundException {
//
//    }
//
//    public static void close(Connection conn, PreparedStatement ps, ResultSet rs){
//        try {
//            if(rs!=null){
//                rs.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if(ps!=null){
//                ps.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if(conn!=null){
//                conn.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}
