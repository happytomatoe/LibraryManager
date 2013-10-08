import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
	Connection c;
	Statement st;
	ResultSet rs;
	String DbName;
	String select = "SELECT * FROM ";

	private static final String createTableBooks = "CREATE TABLE IF NOT EXISTS `Books` ("
			+ "`id` INTEGER PRIMARY KEY,"
			+ "`title` varchar(25) NOT NULL,"
			+ "`author` varchar(25) NOT NULL,"
			+ "`priority` int(11) NOT NULL,"
			+ "`weight` double NOT NULL," + "`numOfPages` int(11) NOT NULL)";

	private static final String createTableDiscs = "CREATE TABLE IF NOT EXISTS `discs` ("
			+ "`id` INTEGER PRIMARY KEY,"
			+ "`title` varchar(20) NOT NULL,"
			+ "`type` varchar(10) NOT NULL,"
			+ "`description` text NOT NULL"
			+ ")";

	private static final String createTableFilms = "CREATE TABLE IF NOT EXISTS `films` ("
			+ "`id` INTEGER PRIMARY KEY,"
			+ "`title` varchar(10) NOT NULL,"
			+ "`genre` varchar(25) NOT NULL,"
			+ "`priority` int(11) NOT NULL,"
			+ "`country` varchar(25) NOT NULL,"
			+ "`director` varchar(25) NOT NULL" + ")";

	private static final String createTableMusic = "CREATE TABLE IF NOT EXISTS `music` ("
			+ "`id` INTEGER PRIMARY KEY,"
			+ "`title` varchar(25) NOT NULL,"
			+ "`author` varchar(15) NOT NULL,"
			+ "`priority` int(11) NOT NULL,"
			+ "`length` varchar(8) NOT NULL,"
			+ "`genre` varchar(20) NOT NULL"
			+ ") ";

	private static final String createTableVideoGames = "CREATE TABLE IF NOT EXISTS `videogames` ("
			+ "`id` INTEGER PRIMARY KEY,"
			+ "`title` varchar(25) NOT NULL,"
			+ "`author` varchar(15) NOT NULL,"
			+ "`dateOfPublishing` int(11) NOT NULL,"
			+ "`genre` varchar(25) NOT NULL,"
			+ "`platform` varchar(15) NOT NULL,"
			+ "`site` varchar(25) NOT NULL" + ")";



	
	public DBHelper(String DbName) {
		this.DbName = DbName;
	}

	
	
	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:" + DbName);
		st = c.createStatement();
		st.setQueryTimeout(30);

	}

	public void createAllTables() {

		if (c == null)
			try {
				connect();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		try {
			dropTables();

			createTables();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void dropTables() throws SQLException {
		Tables[] tableNames = Tables.values();
		for (int i = 0; i < tableNames.length; i++)
			st.executeUpdate("DROP TABLE IF EXISTS " + tableNames[i]);
	}
	
	private void createTables() throws SQLException {
		st.executeUpdate(createTableBooks);
		st.executeUpdate(createTableMusic);
		st.executeUpdate(createTableVideoGames);
		st.executeUpdate(createTableFilms);
		st.executeUpdate(createTableDiscs);
	}

	
	
	


	
	public void insertIntoBook(String title, String author, int priority,
			double weight, int numOfPages) throws SQLException,
			ClassNotFoundException {
		if (c == null)
			connect();

		String sql = "INSERT INTO `Books` VALUES (NULL,'" + title + "','"
				+ author + "'," + priority + "," + weight + "," + numOfPages
				+ ")";
		st.executeUpdate(sql);
	}

	public void removeRow(Tables table, int id) {
		String sql = "DELETE FROM " + table + " WHERE `id`="
				+ String.valueOf(id);
		if (c == null)
			try {
				connect();

			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		try {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public ResultSet select(Tables table) throws SQLException {
		return st.executeQuery(select + table);
	}

	public ResultSet selectAll() throws SQLException {
		String sql = select;
		Tables[] tableNames = Tables.values();
		for (int i = 0; i < tableNames.length; i++) {
			sql += tableNames[i];
			if (i < tableNames.length - 1)
				sql += ",";
		}
		System.out.println(sql);
		return st.executeQuery(sql);
	}


}
