import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;


public class MainWindow extends JFrame {
	DBHelper dbHelp;
	private JPanel contentPane;
	private AbstractHyperlinkAction<Object> simpleAction;
	private DefaultTableRenderer renderer;
	static File logFile=new File("log.txt");
	static File errFile=new File("err.txt");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setOut(new PrintStream(new FileOutputStream(logFile),true));
					System.setErr(new PrintStream(new FileOutputStream(errFile),true));
					
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmLoad = new JMenuItem("Load");
		mnFile.add(mntmLoad);

		mntmCreate = new JMenuItem("Create");
		mnFile.add(mntmCreate);

		mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);

		mntmExport = new JMenuItem("Export");
		mnFile.add(mntmExport);

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		mnFile.add(mntmExit);

		mnAbout = new JMenu("Help");
		menuBar.add(mnAbout);

		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAbout();

			}
		});
		mnAbout.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		table = new JXTable();
		
		scrollPane.setViewportView(table);

		panel = new JPanel();
		contentPane.add(panel, BorderLayout.EAST);

		add = new JButton("Add");
		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addItem();

			}
		});
		add.setAlignmentX(Component.CENTER_ALIGNMENT);

		edit = new JButton("Edit");
		edit.setAlignmentX(Component.CENTER_ALIGNMENT);

		delete = new JButton("Delete");
		delete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeRow();
			}
		});
		delete.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(add);
		panel.add(edit);
		panel.add(delete);
		
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		table.setAutoCreateRowSorter(true);
		String values[] = new String[Tables.values().length + 1];
		values[0] = "All";
		Object tbValues[] = Tables.values();
		for (int i = 0; i < tbValues.length; i++)
			values[i + 1] = tbValues[i].toString();
		comboBox = new JComboBox(values);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedTable=comboBox.getSelectedItem().toString();
				try {
				if(selectedTable.equalsIgnoreCase("All"))showAllValues();
				else{
				
						showValues(selectedTable);
					
			}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(comboBox, BorderLayout.NORTH);

		initDB();

		processTable();
		
	simpleAction = new AbstractHyperlinkAction<Object>(null) {

		    public void actionPerformed(ActionEvent e) {
		    	 try {
		 			java.awt.Desktop.getDesktop().browse(java.net.URI.create((String)getTarget()));
		 		} catch (IOException ex) {
		 			// TODO Auto-generated catch block
		 			ex.printStackTrace();
		 		}
		    }

		};
		 renderer = new DefaultTableRenderer(
			    new HyperlinkProvider(simpleAction));
		
	}


	protected void addItem() {
		String types[] = new String[] { "Book", "Film", "Disc", "Video Game" };
		String answ = (String) JOptionPane.showInputDialog(this,
				"Choose type:", "Add", JOptionPane.INFORMATION_MESSAGE, null,
				types, types[0]);
		System.out.println(answ);
		if (answ != null) {
				new AddDialog(this).setVisible(true);;
		}
	}

	protected void removeRow() {
		System.out.println(table.getSelectedRow());
		int index = table.getSelectedRow();
		if (index == -1)
			return;
		DefaultTableModel tableModel = ((DefaultTableModel) table.getModel());
		dbHelp.removeRow(Tables.Books, (int) tableModel.getValueAt(index, 0));
		tableModel.removeRow(index);
		table.validate();

	}

	private void initDB() {
		dbHelp = new DBHelper("catalog.db");
		try {
			dbHelp.connect();
			dbHelp.createAllTables();

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processTable() {

		try {
			dbHelp.insertIntoBooks("A", "Dickenson", 0, 12, 178);
			dbHelp.insertIntoBooks("B", "Cickenson", 1, 12, 32);
			dbHelp.insertIntoBooks("C", "Emely", 2, 5, 20);

			dbHelp.insertIntoDiscs("Windows7", 0, "CD",
					"Windows 7 Service Pack2");
			dbHelp.insertIntoDiscs("Quake3", 2, "DVD", "Cool FPS");
			dbHelp.insertIntoDiscs("LinuxUbuntu", 4, "Blu-ray",
					"Linux Ubuntu 12.04");

			dbHelp.insertIntoFilms("Гравитация", 0,
					"http://www.kinopoisk.ru/film/468466/",
					"фантастика, триллер, драма", "США, Великобритания",
					"Альфонсо Куарон", new java.sql.Date(2013, 10, 3));
			dbHelp.insertIntoFilms("Va-Bank", 1,
					"http://www.kinopoisk.ru/film/468465", "фантастика",
					"Україна", "Рікардо Лопес", new java.sql.Date(2012, 9, 3));
			dbHelp.insertIntoFilms("Star Wars", 3,
					"http://www.kinopoisk.ru/film/468364", "фантастика",
					"USA", "S.Spielberg", new java.sql.Date(2001, 3, 23));
			
			dbHelp.insertIntoMusic("I love you", "St.Jobs", 0, "http://www.youtube.com/watch?v=Q7aOWIFgIZQ&list=PL8556EB79CCF6A5A7", "02:01", "hip-hop");
			dbHelp.insertIntoMusic("I hate you", "Ne-Yo", 2, "http://www.youtube.com/watch?v=Q7aOWIFgIZQ&list=PL8556EB79CCF6A5A7", "03:45", "Rap");
			dbHelp.insertIntoMusic("Merry Christmas", "J.Michael", 3, "http://www.youtube.com/watch?v=Q7aOWIFgIZQ&list=PL8556EB79CCF6A5A7", "01:23", "Rock");
			
			dbHelp.insertIntoVideoGames("Monogame", "I", 0,  new java.sql.Date(2002, 3, 23), "platform", "PC");
			dbHelp.insertIntoVideoGames("KingdomRush", "DesignStudios", 3,  new java.sql.Date(2012, 3, 23), "launcher", "Mac");
			dbHelp.insertIntoVideoGames("Counter-Strike", "Valve", 2,  new java.sql.Date(2005, 3, 23), "arcade", "Android");

			
			showAllValues();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showValues(Tables tableName) throws SQLException {
		table.setModel(buildTableModel(dbHelp.select(tableName)));
		if(table.getColumnExt("id")!=null)table.getColumnExt("id").setVisible(false);
if(table.getColumnExt("link")!=null){
	table.getColumnExt("link").setEditable(false);
	table.getColumnExt("link").setCellRenderer(renderer);
	
}
	}
	private void showAllValues() throws SQLException {
		table.setModel(buildTableModel(dbHelp.selectAll()));
		if(table.getColumnExt("id")!=null)table.getColumnExt("id").setVisible(false);
if(table.getColumnExt("link")!=null){
	table.getColumnExt("link").setEditable(false);
	table.getColumnExt("link").setCellRenderer(renderer);
	
}
	}
	
	private void showValues(String tableName) throws SQLException {
		table.setModel(buildTableModel(dbHelp.select(tableName)));
		if(table.getColumnExt("id")!=null)table.getColumnExt("id").setVisible(false);
if(table.getColumnExt("link")!=null){
	table.getColumnExt("link").setEditable(false);
	table.getColumnExt("link").setCellRenderer(renderer);
	
}
	}
	private void showAbout() {
		JOptionPane.showMessageDialog(this,
				"CatalogManager.\nAuthor Roman Lukash", "About",
				JOptionPane.PLAIN_MESSAGE);
	}

	private JXTable table;
	private JPanel panel;
	private JButton add;
	private JButton edit;
	private JButton delete;
	private JComboBox comboBox;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnAbout;
	private JMenuItem mntmExport;
	private JMenuItem mntmAbout;
	private JMenuItem mntmExit;
	private JMenuItem mntmSave;
	private JMenuItem mntmLoad;
	private JMenuItem mntmCreate;

	public static DefaultTableModel buildTableModel(ResultSet rs)
			throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
		}

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex));
			}
			data.add(vector);
		}

		return new DefaultTableModel(data, columnNames);

	}

}
