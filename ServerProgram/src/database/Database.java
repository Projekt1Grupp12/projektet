package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private Connection connection;
	
	private ResultSet resultSet;
	
	private Statement statement;
	
	public Database(String database, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost/" + database + "?" + "user="+user+"&password="+password);
	}
	
	public void setupGame() {
		
	}
	
	public void endGame() {
		
	}
	
	public void updateGame() {
		
	}
	
	public ResultSet execute(String s) throws SQLException {
		statement = connection.createStatement();
		return statement.executeQuery(s);
	}
	
	public void useDatabase(String name) throws SQLException {
		execute("USE " + name);
	}
	
	public void createDatabase(String name) throws SQLException {
		execute("CREATE DATABASE " + name + ";");
	}
	
	public int getInt(String selection, String column) throws SQLException {
		resultSet = execute(selection);
		return resultSet.getInt(column);
	}
	
	public String getString(String selection, String column) throws SQLException {
		resultSet = execute(selection);
		return resultSet.getString(column);
	}
	
	public void close() throws SQLException {
		connection.close();
		resultSet.close();
		statement.close();
	}
}
