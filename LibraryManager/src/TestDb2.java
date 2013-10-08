import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
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
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;



public class TestDb2 extends JFrame {
DBHelper dbHelp;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestDb2 frame = new TestDb2();
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
	public TestDb2() {
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
		
		table = new JTable();
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
		table.setAutoCreateRowSorter(true);
		String values[]=new String[Tables.values().length+1];
		values[0]="All";
		Object tbValues[]=Tables.values();
		for(int i=0;i<tbValues.length;i++)
			values[i+1]=tbValues[i].toString();
		comboBox = new JComboBox(values);
		contentPane.add(comboBox, BorderLayout.NORTH);
		
		initDB();
		
		processTable();
	}

	protected void addItem() {
		String types[]=new String[]{"Book","Film","Disc","Video Game"};
		String answ=(String)JOptionPane.showInputDialog(this,"Choose type:","Add", JOptionPane.INFORMATION_MESSAGE, null, types,types[0]);
		System.out.println(answ);
		if(answ!=null){
			
			
			
		}
	}

	protected void removeRow() {
		System.out.println(table.getSelectedRow());
		int index=table.getSelectedRow();
		if(index==-1)return;
		DefaultTableModel tableModel = ((DefaultTableModel)table.getModel());
		dbHelp.removeRow(Tables.Books,(int)tableModel.getValueAt(index, 0));
		tableModel.removeRow(index);
		table.validate();
		
		
	}

	private void initDB() {
		dbHelp=new DBHelper("catalog.db");
		try {
			dbHelp.connect();
			dbHelp.createAllTables();
			dbHelp.selectAll();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void processTable() {
		
		
		
		try {
//			dbHelp.createTableBook();
			dbHelp.insertIntoBook("A", "Dickenson",0, 12, 178);
			dbHelp.insertIntoBook("B", "Cickenson",1, 12, 32);
			dbHelp.insertIntoBook("C", "Emely",2, 5, 20);
			table.setModel(buildTableModel(dbHelp.select(Tables.Books)));
			 table.getColumnModel().getColumn(0).setMinWidth(0);
			   table.getColumnModel().getColumn(0).setMaxWidth(0);
			   table.getColumnModel().getColumn(0).setWidth(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void showAbout() {
		JOptionPane.showMessageDialog(this,
			    "CatalogManager.\nAuthor Roman Lukash","About",JOptionPane.PLAIN_MESSAGE);
	}


	private JTable table;
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
