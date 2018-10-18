package com.apabi.flow;

import java.sql.*;

public class OracleJdbcTest {
	//数据库连接对象
	private static Connection conn = null;

	private static String driver = "oracle.jdbc.driver.OracleDriver"; //驱动

	private static String url = "jdbc:oracle:thin:@//172.18.89.115/orcl.cndplab.com"; //连接字符串

	private static String username = "system"; //用户名

	private static String password = "Founder123"; //密码


	// 获得连接对象
	private static synchronized Connection getConn(){
		if(conn == null){
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, username, password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	//执行查询语句
	public static void query(String sql, boolean isSelect) throws SQLException{
		PreparedStatement pstmt;

		try {
			pstmt = getConn().prepareStatement(sql);
			//建立一个结果集，用来保存查询出来的结果
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				System.out.println(name);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void query(String sql) throws SQLException{
		PreparedStatement pstmt;
		pstmt = getConn().prepareStatement(sql);
		pstmt.execute();
		pstmt.close();
	}


	//关闭连接
	public static void close(){
		try {
			getConn().close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException {
		OracleJdbcTest.getConn();
		OracleJdbcTest.query("select title from Book.BOOKLIST", false );
		OracleJdbcTest.close();
	}
}