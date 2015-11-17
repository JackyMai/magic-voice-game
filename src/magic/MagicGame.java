package magic;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MagicGame extends JFrame {
	private static final long serialVersionUID = 1L;
	final JFrame mainFrame = this;
	final JPanel mainMenuPanel = new JPanel();
	private Dimension defaultDim = new Dimension(440, 270);
	private JPanel cardLayoutPanel;
	private JTextField registerTextField;
	private JTextField loginTextField;
	private static String currentUser;
	private JScrollPane scrollPanel;
	private JTable scoreBoard;
	private DefaultTableModel tableModel;
	private static HashMap<String, StatsWorker> festivalCalls;
	
	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MagicGame frame = new MagicGame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// This method will take the user back to the main menu with a festival greeting
	public void backButton() {
		setSize(defaultDim);
		changePanel(mainMenuPanel);
	}
	
	// This method will change the current panel to the JPanel specified
	public void changePanel(JPanel p) {
		cardLayoutPanel.removeAll();
		cardLayoutPanel.add(p);
		cardLayoutPanel.repaint();
		cardLayoutPanel.revalidate();
	}
	
	// This method will create the game file and directory necessary to run the game
	// The files and directories will only be create if it doesn't already exist
	public void createGameFiles() {
		File mvgUserList = new File(".mvg_users.txt");
		File mvgGameDir = new File(".magicgame");
		
		try {
			mvgUserList.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		mvgGameDir.mkdirs();
	}
	
	// This method will create a GreetingsWorker and play a specific message
	// depending on what "mode" it is in
	public void welcomeMsg(String mode) {
		GreetingsWorker gw = new GreetingsWorker(mode, currentUser);
		gw.execute();
	}
	
	// This method will iterate through the user list file line by line and compare
	// to see if the user is already registered or not, regardless of case.
	public String userExists(String username) {
		String line = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(".mvg_users.txt"));
			
			while((line = br.readLine()) != null) {
				if(line.equalsIgnoreCase(username)) {
					return line;
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	// This method will add the username in the argument to the user list file
	// as well as creating a stats file for that new user
	private void addUser(String username) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(".mvg_users.txt", true));
			writer.append(username+'\n');
			writer.close();
			
			File mvgUserStats = new File(".magicgame"+File.separator+username+".txt");
			mvgUserStats.createNewFile();
			BufferedWriter writer2= new BufferedWriter(new FileWriter(mvgUserStats, false));
			writer2.write("0");
			writer2.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	// This method will instantiating a new PlayWorker to play the corresponding
	// message depending on whether the user guessed correctly or not and returns
	// that result
	private boolean playResult(int guess, int diceRoll) {
		PlayWorker fw = new PlayWorker(currentUser, guess, diceRoll);
		fw.execute();
		
		if(guess == diceRoll) {
			return true;
		} else {
			return false;
		}
	}
	
	// This method will generate a random integer between 1 to 6 inclusive
	private int diceRoll() {
		Random r = new Random();
		int diceroll = r.nextInt(6)+1;
		
		return diceroll;
	}
	
	// This method will iterate through the user list file and count how many users
	// there are currently registered in the system
	public int totalUser() {
		int total = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(".mvg_users.txt"));
			
			while((br.readLine()) != null) {
				total++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return total;
	}
	
	// This method will iterate through the user list, and for each user find out how many times
	// that user has guess correctly and store it inside a string array
	public String[][] getScoreBoard() {
		int i = 0;
		int total = totalUser();
		String[][] scoreBoardArray = new String[total][2];
		
		try {
			String username;
			BufferedReader br = new BufferedReader(new FileReader(".mvg_users.txt"));
			
			// Iterating through all of the registered users
			while((username = br.readLine()) != null) {
				BufferedReader br2 = new BufferedReader(new FileReader(".magicgame"+File.separator+username+".txt"));
				
				// For each user, find out how many times he guessed correctly
				int gamesWon = Integer.parseInt(br2.readLine());
				
				scoreBoardArray[i][0] = username;
				scoreBoardArray[i][1] = Integer.toString(gamesWon);
				i++;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return scoreBoardArray;
	}
	
	// This method will update the score board of the game, mainly used for refreshing the JTable data
	@SuppressWarnings("serial")
	public void updateScoreBoard() {
		String[][] scoreBoardArray = getScoreBoard();
		Arrays.sort(scoreBoardArray, new Comparator<String[]>() {
			public int compare(String[] score1, String[] score2) {
				return -Integer.valueOf(score1[1]).compareTo(Integer.valueOf(score2[1]));
			}
		});
		
		tableModel = new DefaultTableModel(scoreBoardArray, new String[]{"Player", "Correct Guesses"}) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		scoreBoard = new JTable(tableModel);
		scoreBoard.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPanel.setViewportView(scoreBoard);
	}
	
	// This method is the hack that will return the PID of a UNIX process
	public int getPID(Process process) {
		if(process.getClass().getName().equals("java.lang.UNIXProcess")) {
			Field f = null;
			int pid = 0;

			try {
				f = process.getClass().getDeclaredField("pid");
			} catch (NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}

			f.setAccessible(true);
			try {
				pid = f.getInt(process);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
			return pid;
		}
		
		return -1;
	}
	
	// This method will instantiate a new StatsWorker and report the username, score and position
	// of the user selected in the JTable score board
	public void reportScore(int position) {
		String username = (String)scoreBoard.getModel().getValueAt(position, 0);
		String score = (String)scoreBoard.getModel().getValueAt(position, 1);
		int total = totalUser();
		
		StatsWorker sw = new StatsWorker(username, score, position, total);
		sw.execute();
		
		// Store the instance of StatsWorker with the key being the username
		festivalCalls.put(username, sw);
	}
	
	// This method will kill the festival process for a particular user (if it's there and still running)
	// by retrieving the StatsWorker instance from the hashmap, using the hack to find out it's PID,
	// then killing the "aplay" process responsible for the audio playback
	public void killFestival(int position) {
		String username = (String)scoreBoard.getModel().getValueAt(position, 0);
		
		if(festivalCalls.containsKey(username)) {
			String cmd = "pstree -p " + getPID(festivalCalls.get(username).getProcess());
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			
			Process process;
			try {
				process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				String line = br.readLine();
				
				if(line != null) {
					int aplayIndex = line.indexOf("play(") + 5;
					String aplayPID = line.substring(aplayIndex, line.indexOf(")", aplayIndex));
					
					String killcmd = "kill " + aplayPID;
					ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", killcmd);
					builder2.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Create the frame.
	 */
	public MagicGame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 440, 270);
		cardLayoutPanel = new JPanel();
		cardLayoutPanel.setBorder(null);
		setContentPane(cardLayoutPanel);
		cardLayoutPanel.setLayout(new CardLayout());
		
		mainMenuPanel.setBackground(Color.WHITE);
		cardLayoutPanel.add(mainMenuPanel, "name_4481302797771");
		mainMenuPanel.setLayout(null);

		createGameFiles();
		
		welcomeMsg("m");
		
		festivalCalls = new HashMap<String, StatsWorker>();
		
		/*
		 * Login Panel
		 */
		final JPanel loginPanel = new JPanel();
		loginPanel.setBackground(Color.WHITE);
		cardLayoutPanel.add(loginPanel, "name_5121406481340");
		loginPanel.setLayout(null);
		
		JLabel lblLoginUser = new JLabel("Login User");
		lblLoginUser.setFont(new Font("Ubuntu", Font.PLAIN, 20));
		lblLoginUser.setBounds(10, 0, 200, 40);
		loginPanel.add(lblLoginUser);
		
		JButton btnNewButton = new JButton("Back");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton();
			}
		});
		btnNewButton.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		btnNewButton.setBounds(10, 40, 65, 25);
		loginPanel.add(btnNewButton);
		
		JLabel lblEnterYourUsername = new JLabel("Enter your username:");
		lblEnterYourUsername.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		lblEnterYourUsername.setBounds(30, 110, 145, 27);
		loginPanel.add(lblEnterYourUsername);
		
		ActionListener loginAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String loginName = loginTextField.getText();
				String username;
				
				if(loginName.isEmpty()) {
					JOptionPane.showMessageDialog(loginPanel, "The username cannot be empty!");
				} else if((username = userExists(loginName)) != null) {
					currentUser=username;
					JOptionPane.showMessageDialog(loginPanel, "Successfullly logged in as \"" + username + "\"!");
					changePanel(mainMenuPanel);
					welcomeMsg("l");
				} else {
					JOptionPane.showMessageDialog(loginPanel, "The username \"" + loginName + "\" is not registered yet!");
				}
			}
		};
		
		loginTextField = new JTextField();
		loginTextField.addActionListener(loginAction);
		loginTextField.setFont(new Font("Ubuntu", Font.PLAIN, 12));
		loginTextField.setColumns(10);
		loginTextField.setBounds(180, 110, 200, 27);
		loginPanel.add(loginTextField);
		
		JButton loginConfirm = new JButton("OK");
		loginConfirm.addActionListener(loginAction);
		loginConfirm.setFont(new Font("Ubuntu", Font.PLAIN, 12));
		loginConfirm.setBounds(350, 222, 70, 25);
		loginPanel.add(loginConfirm);
		
		/*
		 * Register Panel
		 */
		final JPanel registerPanel = new JPanel();
		registerPanel.setBackground(Color.WHITE);
		cardLayoutPanel.add(registerPanel, "name_6619170124275");
		registerPanel.setLayout(null);
		
		final JLabel lblNewLabel = new JLabel("Register User");
		lblNewLabel.setFont(new Font("Ubuntu", Font.PLAIN, 20));
		lblNewLabel.setBounds(10, 0, 200, 40);
		registerPanel.add(lblNewLabel);
		
		JLabel lblPleaseEnter = new JLabel("Enter new username:");
		lblPleaseEnter.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		lblPleaseEnter.setBounds(30, 110, 145, 27);
		registerPanel.add(lblPleaseEnter);
		
		ActionListener registerAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String registerName = registerTextField.getText();
				
				if(registerName.isEmpty()) {
					JOptionPane.showMessageDialog(registerPanel, "The username cannot be empty!");
				} else if(!registerName.matches("[a-zA-Z]+")) {
					JOptionPane.showMessageDialog(registerPanel, "The username may only contain alphabets [a-z][A-Z]!");
				} else if(userExists(registerName) != null) {
					JOptionPane.showMessageDialog(registerPanel, "The username \"" + registerName + "\" already exists!");
				} else {
					addUser(registerName);
					JOptionPane.showMessageDialog(registerPanel, "Successfully registered as \"" + registerName + "\"!");
					backButton();
				}
			}
		};
		
		registerTextField = new JTextField();
		registerTextField.addActionListener(registerAction);
		registerTextField.setFont(new Font("Ubuntu", Font.PLAIN, 12));
		registerTextField.setBounds(180, 110, 200, 27);
		registerPanel.add(registerTextField);
		registerTextField.setColumns(10);
		
		JButton button_4 = new JButton("Back");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton();
			}
		});
		button_4.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		button_4.setBounds(10, 40, 65, 25);
		registerPanel.add(button_4);
		
		JButton registerConfirm = new JButton("OK");
		registerConfirm.addActionListener(registerAction);
		registerConfirm.setFont(new Font("Ubuntu", Font.PLAIN, 12));
		registerConfirm.setBounds(350, 222, 70, 25);
		registerPanel.add(registerConfirm);
		
		/*
		 * Play Panel
		 */
		final JPanel playPanel = new JPanel();
		playPanel.setBackground(Color.WHITE);
		cardLayoutPanel.add(playPanel, "name_6624417195551");
		playPanel.setLayout(null);
		
		JLabel lblPlayGame = new JLabel("Play Magic Game");
		lblPlayGame.setFont(new Font("Ubuntu", Font.PLAIN, 20));
		lblPlayGame.setBounds(10, 0, 200, 40);
		playPanel.add(lblPlayGame);
		
		JButton button_5 = new JButton("Back");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton();
			}
		});
		button_5.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		button_5.setBounds(10, 40, 65, 25);
		playPanel.add(button_5);
		
		JLabel lblSelectANumber = new JLabel("Select a number between 1 and 6 from above");
		lblSelectANumber.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		lblSelectANumber.setBounds(20, 170, 460, 33);
		playPanel.add(lblSelectANumber);
		
		final JLabel playResultLabel = new JLabel("Can you guess the right number?");
		playResultLabel.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		playResultLabel.setBounds(20, 205, 460, 33);
		playPanel.add(playResultLabel);
		
		ActionListener playerGuessed = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				int guess = Integer.parseInt(button.getText());
				int actual = diceRoll();
				
				if(playResult(guess, actual)) {
					playResultLabel.setText("Well done " + currentUser + " , you guessed correctly!");
				} else {
					playResultLabel.setText("Sorry " + currentUser + " , it was a " + actual);
				}
			}
		};
		
		JButton oneButton = new JButton("1");
		oneButton.addActionListener(playerGuessed);
		oneButton.setFont(new Font("Ubuntu", Font.PLAIN, 18));
		oneButton.setBounds(20, 100, 60, 50);
		playPanel.add(oneButton);
		
		JButton twoButton = new JButton("2");
		twoButton.addActionListener(playerGuessed);
		twoButton.setFont(new Font("Ubuntu", Font.PLAIN, 18));
		twoButton.setBounds(100, 100, 60, 50);
		playPanel.add(twoButton);
		
		JButton threeButton = new JButton("3");
		threeButton.addActionListener(playerGuessed);
		threeButton.setFont(new Font("Ubuntu", Font.PLAIN, 18));
		threeButton.setBounds(180, 100, 60, 50);
		playPanel.add(threeButton);
		
		JButton fourButton = new JButton("4");
		fourButton.addActionListener(playerGuessed);
		fourButton.setFont(new Font("Ubuntu", Font.PLAIN, 18));
		fourButton.setBounds(260, 100, 60, 50);
		playPanel.add(fourButton);
		
		JButton fiveButton = new JButton("5");
		fiveButton.addActionListener(playerGuessed);
		fiveButton.setFont(new Font("Ubuntu", Font.PLAIN, 18));
		fiveButton.setBounds(340, 100, 60, 50);
		playPanel.add(fiveButton);
		
		JButton sixButton = new JButton("6");
		sixButton.addActionListener(playerGuessed);
		sixButton.setFont(new Font("Ubuntu", Font.PLAIN, 18));
		sixButton.setBounds(420, 100, 60, 50);
		playPanel.add(sixButton);
		
		/*
		 * Stats Panel
		 */
		final JPanel statsPanel = new JPanel();
		statsPanel.setBackground(Color.WHITE);
		cardLayoutPanel.add(statsPanel, "name_6627686937725");
		statsPanel.setLayout(null);
		
		JLabel lblStats = new JLabel("Player Stats");
		lblStats.setFont(new Font("Ubuntu", Font.PLAIN, 20));
		lblStats.setBounds(10, 0, 200, 40);
		statsPanel.add(lblStats);
		
		JButton button_6 = new JButton("Back");
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton();
			}
		});
		button_6.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		button_6.setBounds(10, 40, 65, 25);
		statsPanel.add(button_6);
		
		scrollPanel = new JScrollPane();
		scrollPanel.setBounds(10, 75, 420, 315);
		statsPanel.add(scrollPanel);
		
		JButton festButton = new JButton("Festival");
		festButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = scoreBoard.getSelectedRow();
				reportScore(selectedRow);
			}
		});
		festButton.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		festButton.setBounds(260, 40, 90, 25);
		statsPanel.add(festButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = scoreBoard.getSelectedRow();
				killFestival(selectedRow);
			}
		});
		stopButton.setFont(new Font("Ubuntu", Font.PLAIN, 14));
		stopButton.setBounds(360, 40, 70, 25);
		statsPanel.add(stopButton);
		
		/*
		 * Main Menu Panel
		 */
		JLabel magicGameLabel = new JLabel("The Magic Game");
		magicGameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		magicGameLabel.setFont(new Font("Ubuntu", Font.PLAIN, 40));
		magicGameLabel.setBounds(0, 0, 450, 120);
		mainMenuPanel.add(magicGameLabel);
		
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentUser == null) {
					changePanel(loginPanel);
				} else {
					JOptionPane.showMessageDialog(mainMenuPanel, "You cannot login again if you're currently logged in!");
				}
			}
		});
		loginButton.setFont(new Font("Ubuntu", Font.PLAIN, 16));
		loginButton.setBounds(90, 130, 100, 40);
		mainMenuPanel.add(loginButton);
		
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentUser == null) {
					changePanel(registerPanel);
				} else {
					JOptionPane.showMessageDialog(mainMenuPanel, "You cannot register if you're currently logged in!");
				}
			}
		});
		registerButton.setFont(new Font("Ubuntu", Font.PLAIN, 16));
		registerButton.setBounds(250, 130, 100, 40);
		mainMenuPanel.add(registerButton);
		
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentUser != null) {
					mainFrame.setSize(500,270);
					changePanel(playPanel);
				} else {
					JOptionPane.showMessageDialog(mainMenuPanel, "You cannot play game unless you're logged in!");
				}
			}
		});
		playButton.setFont(new Font("Ubuntu", Font.PLAIN, 16));
		playButton.setBounds(90, 190, 100, 40);
		mainMenuPanel.add(playButton);
		
		JButton statsButton = new JButton("Stats");
		statsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					mainFrame.setSize(440,400);
					updateScoreBoard();
					changePanel(statsPanel);
			}
		});
		statsButton.setFont(new Font("Ubuntu", Font.PLAIN, 16));
		statsButton.setBounds(250, 190, 100, 40);
		mainMenuPanel.add(statsButton);
	}
}
