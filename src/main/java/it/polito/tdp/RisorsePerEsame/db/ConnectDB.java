package it.polito.tdp.RisorsePerEsame.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	public static Connection getConnection() {
		String url= "jdbc:mysql://127.0.0.1/iscritticorsi?user=root&password=root";
		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println("Errore durante la connesione con il Database");
			e.printStackTrace();
			return null;
		}
	}
}
