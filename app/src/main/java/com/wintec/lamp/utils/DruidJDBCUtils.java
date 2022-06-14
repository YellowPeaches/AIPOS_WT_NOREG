//package com.wintec.lamp.utils;
//import com.alibaba.druid.pool.DruidDataSource;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class DruidJDBCUtils {
//    private static DruidDataSource source = null;
//
//    static {
//        try {
//            String url = "jdbc:oracle:thin:@" + "118.31.114.228" + ":" + "1521" + ":" + "helowin";
//            DruidDataSource source = new DruidDataSource();
//            source.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//            source.setUsername("TESTONLINE");
//            source.setPassword("TESTONLINE");
////            source.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT");
//            source.setUrl(url);
//            source.setInitialSize(3);
//            source.setMaxActive(5);
//
////            DruidPooledConnection conn = source.getConnection();
////            System.out.println(conn);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    static Connection getConnection() throws Exception {
//        Connection conn = source.getConnection();
//        return conn;
//    }
//
//    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
//        if (resultSet != null) {
//            try {
//                resultSet.close();
//            } catch (SQLException throwables) {
//                throw new RuntimeException(throwables);
//            }
//        }
//        if (statement != null) {
//            try {
//                statement.close();
//            } catch (SQLException throwables) {
//                throw new RuntimeException(throwables);
//            }
//
//        }
//        if (connection != null) {
//            try {
//                connection.close();
//            } catch (SQLException throwables) {
//                throw new RuntimeException(throwables);
//            }
//        }
//    }
//}
//
