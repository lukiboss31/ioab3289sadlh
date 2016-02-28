package schoolcomm;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
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
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -5258975096863902697L;
	private JPanel contentPane;
	private List<User> users = new ArrayList<User>();
	private Map<String, ComFrame> frames = new HashMap<String, ComFrame>();
	private final JList<String> list = new JList<String>();
	private DefaultListModel<String> model;
	private ComListener server;
	private User self = new User();

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the frame.
	 */
	public MainFrame(int myPort, String myUsername) {

		self.port = myPort;
		self.username = myUsername;

		server = new ComListener(self.port, this);
		server.start();

		setTitle(self.username + ": " + self.port);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {

					if (list.getSelectedValue() != null) {
						String selectedUsername = list.getSelectedValue().toString();
						// open chat with user
						getOrCreateFrameForUsername(selectedUsername);
					}
				}
			}
		});

		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.insets = new Insets(0, 0, 5, 5);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 1;
		gbc_list.gridy = 1;
		contentPane.add(list, gbc_list);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshList();
			}
		});
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRefresh.insets = new Insets(0, 0, 5, 5);
		gbc_btnRefresh.gridx = 1;
		gbc_btnRefresh.gridy = 3;
		contentPane.add(btnRefresh, gbc_btnRefresh);

		writeMyFile();
		readUsers();

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
		File dir = new File("config");

		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalStateException("Config does not exists!");
		}
		String[] list = dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String fileName) {
				if (fileName.endsWith(".txt")) {
					return true;
				}
				return false;
			}
		});

		for (String filename : list) {
			User u = readUser(filename);

			if (u != null && !u.username.equals(self.username)) {
				users.add(u);
			}

		}

		// sort:
		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.username.compareTo(o2.username);
			}
		});

	}

	private User readUser(String filename) {

		// System.out.println(filename);
		File f = new File("config", filename);

		try {
			BufferedReader r = new BufferedReader(new FileReader(f));

			String readLine = r.readLine();
			r.close();

			// System.out.println("line = " + readLine);

			String[] split = readLine.split(":");

			// System.out.println("Split : " + split[0]);

			User u = new User();

			u.hostName = split[0];
			u.ipAddr = split[1];
			u.remotePort = Integer.parseInt(split[2]);
			u.username = filename.split(".txt")[0];
			System.out.println(u);
			return u;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void refreshList() {
		// 1: read users
		readUsers();
		// refreshed list of users is in users.

		// 2: add users to JList
		model = new DefaultListModel<String>();

		for (User u : users) {
			model.addElement(u.username);
		}
		list.setModel(model);

	}

	public void writeMyFile() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ipAddr = addr.getHostAddress();
			String hostName = addr.getHostName();

			File config = new File("config");
			if (!config.exists()) {
				config.mkdir();
			}
			File file = new File("config", self.username + ".txt");
			boolean firstTime = !file.exists();
			FileWriter wri = null;

			wri = new FileWriter(file, true);
			if (firstTime) {
				wri.write(hostName + ":" + ipAddr + ":" + self.port);
			}
			file.deleteOnExit();

			if (wri != null) {

				wri.close();
			}

		} catch (IOException ignore) {
			System.out.println(ignore);
		}

	}

	public void messageReceived(String text) {
		// text = "ocko: blabla"

		// username = "ocko"
		String username = getUsername(text);

		ComFrame commFrame = getOrCreateFrameForUsername(username);
		commFrame.setText(text);
	}

	private ComFrame getOrCreateFrameForUsername(String username) {
		ComFrame commFrame = frames.get(username);

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

	private ComFrame createComFrame(User user) {
		ComFrame commFrame = null;
		try {
			commFrame = new ComFrame(user);
			commFrame.setVisible(true);
			commFrame.toFront();
			frames.put(user.username, commFrame);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return commFrame;
	}

	public String getUsername(String text) {
		// "ocko: blabla"
		String[] split = text.split(":");
		String username = split[0];
		return username;
	}

}
