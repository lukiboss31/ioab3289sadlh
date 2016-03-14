package schoolcomm;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import chat.ChatFrame;
import data.User;
import data.sqliteConnection;
import main.InitTab0;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -5258975096863902697L;
	private JPanel contentPane;
	public List<User> users = new ArrayList<User>();
	private Map<String, ChatFrame> frames = new HashMap<String, ChatFrame>();
	public final JList<String> list = new JList<String>();
	private DefaultListModel<String> model;
	private ComListener listener;
	public User self = new User();
	public String selectedUsername;
	public Connection connection = null;
	public JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	public User selectedUser;

	public static void main(String[] args) {
		final int myPort = (args.length > 0) ? Integer.parseInt(args[0]) : 5555;
		final String myUsername = (args.length > 1) ? args[1] : System.getProperty("user.name");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame(myPort, myUsername);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainFrame(int myPort, String myUsername) {

		initSelf(myPort, myUsername);

		listener = new ComListener(self.port, this);
		listener.start();
		connection = sqliteConnection.dbConnection();

		initMainFrame();

		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 5);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 1;
		gbc_tabbedPane.gridy = 1;
		contentPane.add(tabbedPane, gbc_tabbedPane);

		JPanel panel = InitTab0.InitTab0(this);

		JPanel panel_1 = new JPanel();
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JButton btnChat = new JButton("chat");
		btnChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getOrCreateFrameForUsername(selectedUsername);
			}
		});
		GridBagConstraints gbc_btnChat = new GridBagConstraints();
		gbc_btnChat.gridx = 1;
		gbc_btnChat.gridy = 1;
		panel_1.add(btnChat, gbc_btnChat);

		tabbedPane.addTab("Connection", null, panel, null);
		tabbedPane.addTab("Mode", null, panel_1, null);

		tabbedPane.setEnabledAt(1, false);
		
		writeMyFile();
		readUsers();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					System.out.println("closing");
					String querry = "delete from users where \"ip-Addr\"='" + self.ipAddr + "' and username='"
							+ self.username + "'";
					PreparedStatement pst = connection.prepareStatement(querry);
					pst.execute();
					pst.close();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
	}

	private void initMainFrame() {
		setTitle(self.username + ": " + self.port);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);
	}

	private void initSelf(int myPort, String myUsername) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}

		self.port = myPort;
		self.username = myUsername;
		self.hostName = addr.getHostName();
		self.ipAddr = addr.getHostAddress();
	}

	private User findUser(String gesuchteUsername) {
		for (User user : users) {
			if (user.username.equals(gesuchteUsername)) {
				System.out.println("user:  " + user);
				return user;
			}
		}
		return null;
	}

	public void readUsers() {
		users.clear();
		try {
			String querry = "select * from users";
			PreparedStatement pst = connection.prepareStatement(querry);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {

				User u = new User();
				u.hostName = rs.getString("hostname");
				u.ipAddr = rs.getString("ip-Addr");
				u.remotePort = rs.getInt("port");
				u.username = rs.getString("username");
				System.out.println(u);

				if (u != null && !u.username.equals(self.username)) {
					users.add(u);
				}
			}

			rs.close();
			pst.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// sort:
		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.username.compareTo(o2.username);
			}
		});

	}

	public void refreshList() {
		readUsers();
		model = new DefaultListModel<String>();

		for (User u : users) {
			model.addElement(u.username);
		}
		list.setModel(model);

	}

	public void writeMyFile() {
		try {

			String querry = "INSERT INTO users (hostname, \"ip-Addr\", port, username) VALUES (?, ?, ?, ?);";
			PreparedStatement pst = connection.prepareStatement(querry);
			pst.setString(1, self.hostName);
			pst.setString(2, self.ipAddr);
			pst.setInt(3, self.port);
			pst.setString(4, self.username);

			pst.execute();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void messageReceived(String text) {

		JSONParser parser = new JSONParser();
		String key = "";
		String msg = "";
		try {
			Object parsed = parser.parse(text);
			JSONObject obj = (JSONObject) parsed;

			key = "" + obj.get("key");
			msg = "" + obj.get("msg");

		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (key == "ch") {
			String username = getUsername(msg);

			ChatFrame commFrame = getOrCreateFrameForUsername(username);
			commFrame.setText(msg);
		}
	}

	private ChatFrame getOrCreateFrameForUsername(String username) {
		ChatFrame commFrame = frames.get(username);

		if (commFrame == null) {
			User user = findUserWithRefresh(username);
			commFrame = createComFrame(user);
		}
		return commFrame;
	}

	private User findUserWithRefresh(String username) {
		User user = findUser(username);
		if (user == null) {
			// refresh list & retry:
			refreshList();

			user = findUser(username);
			if (user == null) {
				System.out.println("user " + username + " not found...");
				return null; // oder exception?
			}
		}
		return user;
	}

	private ChatFrame createComFrame(User user) {
		ChatFrame commFrame = null;
		try {
			commFrame = new ChatFrame(user);
			commFrame.setVisible(true);
			commFrame.toFront();
			frames.put(user.username, commFrame);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return commFrame;
	}

	public String getUsername(String text) {
		String[] split = text.split(":");
		String username = split[0];
		return username;
	}

}
