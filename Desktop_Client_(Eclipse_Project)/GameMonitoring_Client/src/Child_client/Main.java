package Child_client;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.Socket;
import java.awt.TrayIcon;
import java.awt.event.*;
import javax.swing.*;  
import java.awt.Cursor;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import java.awt.CardLayout;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.MatteBorder;

public class Main extends JFrame {
	int time, tot;
	int rtime=0, secSet=0 , secDur=0;
	int a,b,c;
	String gm;	
	TrayIcon trayIcon;
    SystemTray tray;
	private JPanel appUtama;
	private JTextField status;
	private JLayeredPane layeredPane;
	private JPanel halAwal;
	private JPanel halSetting;
	private JLabel user;
	private JLabel logout;
	private JTable table;
	private JButton addGame;
	private JFileChooser openFileChooser;
	private JScrollPane listGame;
	private JButton delGame;
	private JToggleButton swMonitor;
	private mySwingWorker swingWorker;
	private timSwingWorker timSwingWorker;
	private JLabel gameRun;
	private JLabel gameTimeRun;
	private JLabel timeRem;
	private JLabel timeLimit;
	private JLabel lname;
    private JSeparator separator1;
    private JSeparator separator2;
    private JPanel panel_1;
    private JLabel lblSwitch;
    private JSeparator separator_1;
	private JLabel timeTotal;
    
    String ServerAddress = "localhost";
	int ServerSocket = 8000;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
					//frame.setResizable(false);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		login callClass = new login();
		callClass.enterName();
	}

	public void printName(String receivedName) {
		String[] rN = receivedName.split("\\,");
		user.setText(rN[0]);
		lname.setText(rN[1]);
	}
	
	public void cls() {
	}
	
	public void switchPanels(JPanel panel) {
		layeredPane.removeAll();
		layeredPane.add(panel);
		layeredPane.repaint();
		layeredPane.revalidate();
	}
	
	public void HideToSystemTray() {
		tray=SystemTray.getSystemTray();
        Image image = new ImageIcon(this.getClass().getResource("/logo.png")).getImage();
       	PopupMenu popup=new PopupMenu();
       	MenuItem defaultItem=new MenuItem();
        defaultItem=new MenuItem("Open");
        defaultItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		setVisible(true);
        		setExtendedState(JFrame.NORMAL);
            }
    	});
    	popup.add(defaultItem);
    	trayIcon=new TrayIcon(image, "Monitoring Application v1.0", popup);
     	trayIcon.setImageAutoSize(true);
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState()==ICONIFIED){
                    try {
                        tray.add(trayIcon);
                        setVisible(false);
                    } catch (AWTException ex) {
                    	}
                }
                if(e.getNewState()==NORMAL){
                    tray.remove(trayIcon);
                    setVisible(true);
                }
            }
        });
    }
	
	private void ShowData() {
		String uss = user.getText();
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Game Name");
		model.addColumn("Run Time");
		try {
			Socket s = new Socket(ServerAddress,ServerSocket);
			PrintStream send = new PrintStream(s.getOutputStream(),true);
			send.println("getTable,"+uss);
		    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));	
		    String line;
		    while((line = in.readLine()) != null) {
		    	if(line.trim().equals(",,,done")) break;
				String[] mdl = line.split(",");
				
				int rt = Integer.valueOf(mdl[1]);
                int second = rt % 60;
			    int hour = rt / 60;
			    int minute = hour % 60;
			    hour = hour / 60;
			    String rtime = (hour+" : "+minute+" : "+second);
				
				model.addRow(new Object[] {mdl[0], rtime});
		    }
			table.setModel(model);
			table.setAutoResizeMode(0);
			table.getColumnModel().getColumn(0).setPreferredWidth(215);
			table.getColumnModel().getColumn(1).setPreferredWidth(116);	
			s.close();
		} catch(Exception e) {
			status.setText("ERROR");
			}
	}
	
	 /** Create the frame.
	 */
	public Main() {
		Image image = new ImageIcon(this.getClass().getResource("/logo.png")).getImage();
		setIconImage(image);
		setTitle("Application Monitoring v1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 150, 498, 656);
		appUtama = new JPanel();
		appUtama.setBackground(new Color(0, 0, 0));
		appUtama.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, new Color(0, 255, 255), null, new Color(153, 50, 204), null));
		setContentPane(appUtama);
		appUtama.setLayout(null);				
		
		JLabel lblNewLabel_3 = new JLabel("_________________________________________________________________");
		lblNewLabel_3.setBounds(13, 37, 455, 16);
		appUtama.add(lblNewLabel_3);
		
		JLabel judul = new JLabel("Logged in as");
		judul.setFont(new Font("Times New Roman", Font.BOLD, 19));
		judul.setBounds(150, 3, 112, 50);
		appUtama.add(judul);
		
		lname = new JLabel("");
		lname.setFont(new Font("Times New Roman", Font.BOLD, 19));
		lname.setBounds(259, 11, 183, 34);
		appUtama.add(lname);
		Font font = lname.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		lname.setFont(font.deriveFont(attributes));	
		HideToSystemTray();
		
		user = new JLabel();
		user.setVisible(false);
		user.setFont(new Font("Times New Roman", Font.BOLD, 19));
		user.setBounds(13, 0, 61, 16);
		appUtama.add(user);
		
		JLabel judul1 = new JLabel("STATUS :");
		judul1.setForeground(new Color(230, 230, 250));
		judul1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
		judul1.setBounds(31, 80, 94, 16);
		appUtama.add(judul1);		
		
		lblSwitch = new JLabel("SWITCH :");
		lblSwitch.setForeground(new Color(230, 230, 250));
		lblSwitch.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
		lblSwitch.setBounds(267, 80, 94, 16);
		appUtama.add(lblSwitch);
		
		status = new JTextField();
		status.setBackground(new Color(192, 192, 192));
		status.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 26));
		status.setForeground(new Color(255, 0, 0));
		status.setEditable(false);
		status.setHorizontalAlignment(SwingConstants.CENTER);
		status.setBounds(31, 107, 225, 121);
		status.setText("waiting");
		appUtama.add(status);
		status.setColumns(10);
		
		logout = new JLabel("");
		logout.setHorizontalAlignment(SwingConstants.CENTER);
		logout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {				
				status.setText("STOPPED");	
				JOptionPane.showMessageDialog(null, "Logged Out");
				login mbo = new login();
				mbo.clos(99);
				dispose();
				swingWorker.cancel(true);
				swingWorker = null;
				timSwingWorker.cancel(true);
				timSwingWorker = null;
			}
		});
		logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Image logot = new ImageIcon(this.getClass().getResource("/logout.png")).getImage().getScaledInstance(41,43,java.awt.Image.SCALE_SMOOTH);;
		logout.setIcon(new ImageIcon(logot));
		logout.setBounds(423, 552, 45, 55);
		appUtama.add(logout);
		
		JLabel settingMenu = new JLabel("");
		Image sett = new ImageIcon(this.getClass().getResource("/sett.png")).getImage().getScaledInstance(45,45,java.awt.Image.SCALE_SMOOTH);
		settingMenu.setIcon(new ImageIcon(sett));
		settingMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ShowData();
				switchPanels(halSetting);
			}
		});
		settingMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		settingMenu.setBounds(10, 553, 45, 50);
		appUtama.add(settingMenu);		
		
		JButton exitB = new JButton("Exit");
		exitB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				swingWorker.cancel(true);
				swingWorker = null;
				timSwingWorker.cancel(true);
				timSwingWorker = null;
			}
		});
		exitB.setBounds(268, 200, 183, 27);
		appUtama.add(exitB);
		
		swMonitor = new JToggleButton("START MONITORING");
		swMonitor.setBackground(new Color(255, 51, 51));
		swMonitor.setVerticalTextPosition(SwingConstants.BOTTOM);
		swMonitor.setHorizontalTextPosition(SwingConstants.CENTER);
		swMonitor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		swMonitor.setBounds(268, 107, 183, 84);
		Image swMon = new ImageIcon(this.getClass().getResource("/swMon.png")).getImage().getScaledInstance(42,42,java.awt.Image.SCALE_SMOOTH);
		Image swMonOff = new ImageIcon(this.getClass().getResource("/swMonOff.png")).getImage().getScaledInstance(42,42,java.awt.Image.SCALE_SMOOTH);
		swMonitor.setIcon(new ImageIcon(swMonOff));
		appUtama.add(swMonitor);
		swMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(swMonitor.isSelected() && user.getText()!="") {
					status.setText("MONITORING");
					ShowData();
					(swingWorker = new mySwingWorker()).execute();
					swMonitor.setText("STOP MONITOR");
					swMonitor.setIcon(new ImageIcon(swMon));
				}
				else {
					swingWorker.cancel(true);
					swingWorker = null;
					status.setText("STOPPED");
					swMonitor.setText("START MONITORING");
					swMonitor.setIcon(new ImageIcon(swMonOff));
					gameRun.setText("..........."); 
					gameTimeRun.setText("0 : 0 : 0");
					timeLimit.setText("0 : 0 : 0");
					timeRem.setText("0 : 0 : 0");	
					timeTotal.setText("0 : 0 : 0");
					timSwingWorker.cancel(true);
					timSwingWorker = null;	
					try{Thread.sleep(500);}catch(Exception s){}										
					b=0;
					
				}
			}
		});
		
		panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(192, 192, 192), 3, true));
		panel_1.setBackground(new Color(0, 255, 255));
		panel_1.setBounds(5, 5, 470, 55);
		appUtama.add(panel_1);
		
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(31, 241, 420, 309);
		appUtama.add(layeredPane);
		layeredPane.setLayout(new CardLayout(0, 0));
		
		halAwal = new JPanel();
		halAwal.setBorder(new MatteBorder(2, 5, 2, 5, (Color) Color.GREEN));
		layeredPane.add(halAwal, "name_47314774821100");
		halAwal.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Game Running");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel.setBounds(140, 15, 145, 31);
		halAwal.add(lblNewLabel);
		
		gameRun = new JLabel("<no game detected>");
		gameRun.setFont(new Font("Tahoma", Font.BOLD, 16));
		gameRun.setBounds(39, 75, 324, 31);
		halAwal.add(gameRun);
		
		gameTimeRun = new JLabel("0 : 0 : 0");
		gameTimeRun.setFont(new Font("Tahoma", Font.BOLD, 15));
		gameTimeRun.setBounds(204, 128, 159, 23);
		halAwal.add(gameTimeRun);
		
		timeRem = new JLabel("0 : 0 : 0");		
		timeRem.setFont(new Font("Tahoma", Font.BOLD, 15));
		timeRem.setBounds(204, 167, 159, 23);
		halAwal.add(timeRem);
		
		timeTotal = new JLabel("0 : 0 : 0");
		timeTotal.setFont(new Font("Tahoma", Font.BOLD, 15));
		timeTotal.setBounds(204, 225, 159, 23);
		halAwal.add(timeTotal);
		
		timeLimit = new JLabel("0 : 0 : 0");
		timeLimit.setFont(new Font("Tahoma", Font.BOLD, 15));
		timeLimit.setBounds(204, 264, 159, 23);
		halAwal.add(timeLimit);
		
		JLabel newLabel5 = new JLabel("Game Elapsed Time");
		newLabel5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		newLabel5.setBounds(39, 128, 145, 23);
		halAwal.add(newLabel5);
		
		JLabel newLabel8 = new JLabel("Time Remaining");
		newLabel8.setFont(new Font("Tahoma", Font.PLAIN, 15));
		newLabel8.setBounds(39, 167, 145, 23);
		halAwal.add(newLabel8);
		
		JLabel lblTotalTimeElapsed = new JLabel("Total Game Time");
		lblTotalTimeElapsed.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTotalTimeElapsed.setBounds(39, 225, 152, 23);
		halAwal.add(lblTotalTimeElapsed);
		
		JLabel newLabel6 = new JLabel("Limited for");
		newLabel6.setFont(new Font("Tahoma", Font.PLAIN, 15));
		newLabel6.setBounds(39, 264, 115, 23);
		halAwal.add(newLabel6);
		
		separator1 = new JSeparator();
		separator1.setBounds(12, 8, 396, 9);
		halAwal.add(separator1);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 300, 396, 2);
		halAwal.add(separator);
		
		separator_1 = new JSeparator();
		separator_1.setForeground(Color.BLUE);
		separator_1.setBackground(Color.RED);
		separator_1.setBounds(34, 112, 358, 9);
		halAwal.add(separator_1);
		
		halSetting = new JPanel();
		halSetting.setBorder(new MatteBorder(5, 2, 5, 2, (Color) new Color(51, 51, 204)));
		layeredPane.add(halSetting, "name_47348602866200");
		halSetting.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Setting");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel_1.setBounds(185, 16, 65, 23);
		halSetting.add(lblNewLabel_1);
		
		JLabel bck = new JLabel("Back");		
		Image bcki = new ImageIcon(this.getClass().getResource("/back.png")).getImage().getScaledInstance(22,22,java.awt.Image.SCALE_SMOOTH);
		bck.setIcon(new ImageIcon(bcki));
		bck.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switchPanels(halAwal);
			}
		});
		bck.setFont(new Font("Verdana", Font.PLAIN, 16));
		bck.setBounds(15, 17, 84, 33);
		bck.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		halSetting.add(bck);
		
		listGame = new JScrollPane();
		listGame.setBounds(45, 80, 334, 160);
		halSetting.add(listGame);
				
		table = new JTable();
		table.setFont(new Font("Tahoma", Font.PLAIN, 14));
		listGame.setViewportView(table);
		table.getSelectedRow();
				
		addGame = new JButton("Add  ");
		addGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openFileChooser = new JFileChooser();    
				openFileChooser.setFileFilter(new FileNameExtensionFilter("Game .EXE", "exe"));
				int returnValue = openFileChooser.showOpenDialog(null);    
				if(returnValue == JFileChooser.APPROVE_OPTION) { 
					String xx = user.getText();
					File f = openFileChooser.getSelectedFile();    
					String fname = f.getName(); 
					try {
					    Socket s = new Socket(ServerAddress,ServerSocket);
						PrintStream send = new PrintStream(s.getOutputStream(),true);
						send.println("insGame,"+xx+","+fname);
					    ShowData();
					    s.close();
					}
					catch (Exception exc) { }			        
				}			    
			}
		});
		addGame.setHorizontalTextPosition(SwingConstants.LEADING);
		Image add = new ImageIcon(this.getClass().getResource("/add.png")).getImage().getScaledInstance(23,23,java.awt.Image.SCALE_SMOOTH);
		addGame.setIcon(new ImageIcon(add));
		addGame.setBounds(271, 251, 100, 39);
		halSetting.add(addGame);
				
		delGame = new JButton("Delete");
		Image del = new ImageIcon(this.getClass().getResource("/del.png")).getImage().getScaledInstance(23,23,java.awt.Image.SCALE_SMOOTH);
		delGame.setIcon(new ImageIcon(del));
		delGame.setBounds(52, 251, 100, 39);
		halSetting.add(delGame);
		delGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String uss = user.getText();
				int tbl = table.getSelectedRow();
				TableModel model = table.getModel();
				String delf = model.getValueAt(tbl, 0).toString();
				try {
					Socket s = new Socket(ServerAddress,ServerSocket);
					PrintStream send = new PrintStream(s.getOutputStream(),true);
					send.println("delGame,"+uss+","+delf);
				    ShowData();
				    s.close();
				}
				catch (Exception exc) {	}		
			}
		});
				
		JLabel lblNewLabel_2 = new JLabel("Game Manager");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(162, 50, 113, 23);
		halSetting.add(lblNewLabel_2);
				
		separator2 = new JSeparator();
		separator2.setBounds(12, 12, 396, 9);
		halSetting.add(separator2);
				
		JLabel lblNewLabel_4 = new JLabel("");
		lblNewLabel_4.setBounds(350, 15, 50, 55);
		Image lstG = new ImageIcon(this.getClass().getResource("/gameList.png")).getImage().getScaledInstance(50,50,java.awt.Image.SCALE_SMOOTH);
		lblNewLabel_4.setIcon(new ImageIcon(lstG));
		halSetting.add(lblNewLabel_4);
		
	}
	
	private class mySwingWorker extends javax.swing.SwingWorker<ArrayList<Integer>, Integer> {
		@Override
		protected ArrayList<Integer> doInBackground() {
			ArrayList getGL = new ArrayList();
			for(int i = 0;i<table.getModel().getRowCount();i++) {
				getGL.add(table.getModel().getValueAt(i,0)); //table column0 ->array
				}
			String listGL = String.join(", ", getGL);
			String[] ary = listGL.split("\\s*,\\s*");
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (;;) {
				try {
					String line;					
					boolean how=false;				
					Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /fo csv /nh");
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						for (int z = 0; z < ary.length; z++) {
							if(line.contains(ary[z])) {
							    how = true;
							    gm = ary[z];	      }      }						    	 							    	 
					}							     						
					if(how) { 						
						Timer();
						gameRun.setText(gm);
						c=0;
						}
					else { 	
						for(;c<1;c++) {
							timSwingWorker.cancel(true);
							timSwingWorker = null;
						}
						b=0;
						gm="<no game detected>";
						gameRun.setText(gm); 
						gameTimeRun.setText("0 : 0 : 0");
						timeLimit.setText("0 : 0 : 0");
						timeRem.setText("0 : 0 : 0");
						timeTotal.setText("0 : 0 : 0");
						time=0;
						}		
					Thread.sleep(500);										
				} 
				catch (Exception e) {  }
				
				if (isCancelled()) {
					System.out.println("SwingWorker - Monitoring STOPPED");
					return list;
				}				
			}
		}
	}
	
	private class timSwingWorker extends javax.swing.SwingWorker<ArrayList<Integer>, Integer> {
		@Override
		protected ArrayList<Integer> doInBackground() {			
			ArrayList<Integer> tmr = new ArrayList<Integer>();
			System.out.println("Calculating Started");
			for(;;) {
				try {
					rtime = time + secDur; 
					int rtot = tot + time;
					
					if(rtot>=secSet && secSet>0) {						
			        	try {
			        		Runtime r = Runtime.getRuntime();
							r.exec("taskkill /IM "+gm);
			        	}catch (IOException ex) { }
			        	rtot=secSet;
			        }
					
					int second0 = rtot % 60;
			        int hour0 = rtot / 60;
			        int minute0 = hour0 % 60;
			        hour0 = hour0 / 60;
			        timeTotal.setText(hour0+ " : " +minute0+ " : " +second0);
					
					int second = rtime % 60;
			        int hour = rtime / 60;
			        int minute = hour % 60;
			        hour = hour / 60;
			        gameTimeRun.setText(hour+ " : " +minute+ " : " +second);			        
			
			        int rem = secSet - rtot;
			        if(rem<0) rem=0;
					int second1 = rem % 60;
			        int hour1 = rem / 60;
			        int minute1 = hour1 % 60;
			        hour1 = hour1 / 60;
			        timeRem.setText(hour1+ " : " +minute1+ " : " +second1);
			        if(rem<90) timeRem.setForeground(Color.RED); else timeRem.setForeground(Color.BLACK);
			        if(rtime>=secSet) timeRem.setText(timeRem.getText()+ "  -  Timeout");
			        
			        time++;
					Thread.sleep(1000);
					
				} catch (InterruptedException e) { }
			
				if (isCancelled()) {
					System.out.println("SwingWorker - TIMER STOPPED");
					return tmr;
				}
			}
		}
	}
	
	private void getTimer() {
		String uss = user.getText();
		try {		
			Socket s = new Socket(ServerAddress,ServerSocket);
			PrintStream send = new PrintStream(s.getOutputStream(),true);
			send.println("getTime,"+uss+","+gm);
		    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));					    
			String m = in.readLine();
			String[] getTimer = m.split("\\,");;
			secSet = Integer.valueOf(getTimer[0]);
			secDur = Integer.valueOf(getTimer[1]);
			
			int second = secSet % 60;
	        int hour = secSet / 60;
	        int minute = hour % 60;
	        hour = hour / 60;
	        timeLimit.setText(hour+ " : " +minute+ " : " +second+ "  -  "+getTimer[2]);
	        s.close();
		} 
		catch (Exception e) {  }		
		try {
			Socket s = new Socket(ServerAddress,ServerSocket);
			PrintStream send = new PrintStream(s.getOutputStream(),true);
			send.println("getTable,"+uss);
		    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));					    
		    String line;
		    tot=0;
		    while((line = in.readLine()) != null) {
		    	if(line.trim().equals(",,,done")) break;
				String[] mdl = line.split(",");
				tot += Integer.parseInt(mdl[1]);
		    }
		    s.close();
		} catch (Exception xx) {
			}
	}
	
	private void Timer() {
        for(;b<1;b++) {
        	getTimer();
        	(timSwingWorker = new timSwingWorker()).execute();
        }
        sendTimer();
	}
	
	private void sendTimer() {
		String uss = user.getText();
		try {			
			Socket s = new Socket(ServerAddress,ServerSocket);
			PrintStream send = new PrintStream(s.getOutputStream(),true);
		    send.println("setRT,"+uss+","+gm+","+rtime);
		    s.close();
			} catch (Exception e) {  
				}	
	}
}
