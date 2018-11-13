package com.apabi.flow.book.util;

import com.apabi.shuyuan.book.model.SCmfMeta;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.sql.*;

/**
 * @author guanpp
 * @date 2018/9/13 9:46
 * @description
 */
public class SqlServerJdbc {
    //数据库连接对象
    private static Connection conn = null;

    //驱动
    private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    //连接字符串
    private static String url = "jdbc:sqlserver://R710VM03:1433;DatabaseName=Dlib_2";

    //用户名
    private static String username = "gpp";

    //密码
    private static String password = "Founder123";

    //数据库游标
    //private static PreparedStatement pstmt;

    // 获得连接对象
    public static synchronized Connection getConn() {
        if (conn == null) {
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    //转换结果集为实体对象
    private static Object poplulate(ResultSet resultSet, Class clazz) throws SQLException, IllegalAccessException, InstantiationException {
        if (resultSet != null) {
            //结果集 中列的名称和类型的信息
            ResultSetMetaData rsm = resultSet.getMetaData();
            int colNumber = rsm.getColumnCount();
            //取第一个对象
            while (resultSet.next()) {
                Field[] fields = clazz.getDeclaredFields();
                //实例化对象
                Object obj = clazz.newInstance();
                //取出每一个字段进行赋值
                for (int i = 1; i <= colNumber; i++) {
                    try {
                        Object value = resultSet.getObject(i);
                        //匹配实体类中对应的属性
                        for (int j = 0; j < fields.length; j++) {
                            Field f = fields[j];
                            String fName = f.getName().toUpperCase();
                            String cName = rsm.getColumnName(i).toUpperCase();
                            if (fName.equals(cName)) {
                                boolean flag = f.isAccessible();
                                f.setAccessible(true);
                                if (value != null) {
                                    f.set(obj, value);
                                } else {
                                    f.set(obj, value);
                                }
                                f.setAccessible(flag);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return obj;
            }
        }
        return null;
    }

    //获取图书元数据
    public static SCmfMeta queryBookMeta(String sql) throws SQLException {
        if (sql != null && sql.length() > 0) {
            PreparedStatement pstmt = null;
            //建立一个结果集，用来保存查询出来的结果
            ResultSet rs = null;
            try {
                pstmt = getConn().prepareStatement(sql);
                //建立一个结果集，用来保存查询出来的结果
                rs = pstmt.executeQuery();
                //获取实体对象
                SCmfMeta bookMeta = (SCmfMeta) poplulate(rs, SCmfMeta.class);
                return bookMeta;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        }
        return null;
    }

    //获取最大drid
    public static String queryMaxDrid(String sql) throws SQLException {
        if (sql != null && sql.length() > 0) {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = getConn().prepareStatement(sql);
                //建立一个结果集，用来保存查询出来的结果
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String drid = rs.getString("maxDrid");
                    rs.close();
                    pstmt.close();
                    return drid;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        }
        return null;
    }

    //获取drid
    public static String queryDrid(String sql) throws SQLException {
        if (sql != null && sql.length() > 0) {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = getConn().prepareStatement(sql);
                //建立一个结果集，用来保存查询出来的结果
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String drid = rs.getString("Title");
                    rs.close();
                    pstmt.close();
                    return drid;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        }
        return null;
    }

    //获取fileId
    public static String queryFileId(String sql) throws SQLException {
        if (sql != null && sql.length() > 0) {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = getConn().prepareStatement(sql);
                //建立一个结果集，用来保存查询出来的结果
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String fileId = rs.getString("FILEID");
                    rs.close();
                    pstmt.close();
                    return fileId;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        }
        return null;
    }

    //获取fileUrl
    public static String queryFileUrl(String sql) throws SQLException {
        if (sql != null && sql.length() > 0) {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = getConn().prepareStatement(sql);
                //建立一个结果集，用来保存查询出来的结果
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String fileUrl = rs.getString("URLFILEPATH");
                    rs.close();
                    pstmt.close();
                    return fileUrl;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        }
        return null;
    }

    //执行查询语句
    public static void query(String sql) {
        PreparedStatement pstmt;
        try {
            pstmt = getConn().prepareStatement(sql);
            //建立一个结果集，用来保存查询出来的结果
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("title");
                System.out.println(name);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //关闭连接
    public static void close() {
        try {
            getConn().close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getLastLine(String log) throws IOException {
        if (!StringUtils.isEmpty(log)) {
            RandomAccessFile rf;
            rf = new RandomAccessFile(log, "r");
            long len = rf.length();
            long start = rf.getFilePointer();
            long nextEnd = start + len - 1;
            String line;
            rf.seek(nextEnd);
            int c;
            while (nextEnd > start) {
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (StringUtils.isEmpty(line)) {
                        continue;
                    } else {
                        return new String(line.getBytes("UTF-8"), "UTF-8");
                    }
                }
                nextEnd--;
                rf.seek(nextEnd);
                if (nextEnd == 0) {
                    // 当文件指针退至文件开始处，输出第一行
                    return rf.readLine();
                }
            }

        }
        return null;
    }

    public static void main(String[] args) throws SQLException, IOException {
        getConn();
        /*SCmfMeta bookMeta = queryBookMeta("SELECT * FROM CMF_META_0001 WHERE DRID = '34'");
        String res = queryDrid("SELECT * FROM CMF_META_0001 WHERE DRID = '22'");
        System.out.println(res);*/
        //System.out.println(ResourceUtils.getURL("log").getPath());
        System.out.println(getLastLine("F:\\ideaWorkspace\\flowAdmin\\log\\shuyuan\\2018-10-31.log"));
        close();
    }
}
