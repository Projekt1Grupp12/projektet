package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
	
	public boolean tableExists(String database, String table) throws SQLException {
		useDatabase("game");
		resultSet = execute("SELECT * FROM information_schema.tables WHERE table_schema = '"+database+"' AND table_name = '"+table+"' LIMIT 1");
		return resultSet.next();
	}
	
	public ResultSet execute(String s) throws SQLException {
		statement = connection.createStatement();
		return statement.executeQuery(s);
	}
	
	public int executeUpdate(String s) throws SQLException {
		statement = connection.createStatement();
		return statement.executeUpdate(s);
	}
	
	public void useDatabase(String name) throws SQLException {
		execute("USE " + name);
	}
	
	public void createDatabase(String name) throws SQLException {
		executeUpdate("CREATE DATABASE " + name + ";");
	}
	
	public int getInt(String selection, String column) throws SQLException {
		resultSet = execute(selection);
		return (resultSet.next()) ? resultSet.getInt(column) : -1;
	}
	
	public String getString(String selection, String column) throws SQLException {
		resultSet = execute(selection);
		return (resultSet.next()) ? resultSet.getString(column) : "?";
	}
	
	public void insert(String table, String[] values) throws SQLException {
		String columnsTotal = "";
		String valuesTotal = "";
		
		resultSet = execute("SELECT * FROM " + table);
		ResultSetMetaData m = resultSet.getMetaData();
		String[] columns = new String[m.getColumnCount()];
		
		for(int i = 1; i <= m.getColumnCount(); i++)
			columns[i-1] = m.getColumnName(i); 
		
		for(int i = 0; i < values.length; i++) {
			columnsTotal += "`" + columns[i] + "`" + ((i < values.length-1) ? "," : "");
			valuesTotal += "\'" + values[i] + "\'" + ((i < values.length-1) ? "," : "");
		}
		
		executeUpdate("INSERT INTO `" + table + "` ("+columnsTotal+") VALUES ("+valuesTotal+")");
	}
	
	public void close() throws SQLException {
		connection.close();
		resultSet.close();
		statement.close();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Database d = new Database("game", "sqluser", "sqluserpw");
		d.insert("Highscore", new String[]{"1", "200", "tes"});
		System.out.println(d.getInt("SELECT * FROM Highscore ORDER BY id DESC LIMIT 1;", "score"));
		System.out.println(d.tableExists("game", "Hidghscore"));
	}
}
