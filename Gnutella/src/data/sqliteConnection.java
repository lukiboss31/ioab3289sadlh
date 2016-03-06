package data;

import java.sql.*;

import javax.swing.*;

public class sqliteConnection {
	private static String path1 = System.getProperty("user.dir");
	public static Connection dbConnection(){
		try{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path1 +"/DB.db");
//			JOptionPane.showMessageDialog(null, "Connected");
			if (conn.isReadOnly()){
				JOptionPane.showMessageDialog(null, "db is read only");
			}
	
			return conn;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
}
