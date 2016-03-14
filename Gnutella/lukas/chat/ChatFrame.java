package chat;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import data.User;
import schoolcomm.ComWriter;

public class ChatFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8111448944014540364L;
	private JPanel contentPane;
	private JTextField inputField;
	private JTextArea textArea = new JTextArea();
	private JButton sendButton = new JButton(">");
	private String myUsername;

	private ComWriter comWriter;
	private String hostName;
	private int remotePort;
	private String ipAddr;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final int myPort = (args.length > 0) ? Integer.parseInt(args[0]) : 5555;
		final int remotePort = (args.length > 1) ? Integer.parseInt(args[1]) : 5555;

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					User u = new User();
					u.remotePort = remotePort;
					u.port = myPort;
					ChatFrame frame = new ChatFrame(u);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */

	public ChatFrame(User user) throws IOException {

		if (user.hostName == null) {
			InetAddress addr = InetAddress.getLocalHost();

			user.ipAddr = addr.getHostAddress();
			user.username = System.getProperty("user.name");
			user.hostName = addr.getHostName();
		}
		this.ipAddr = user.ipAddr;
		this.remotePort = user.remotePort;
		this.hostName = user.hostName;
		setTitle("Chating to " + user.username + " on host: " + user.hostName);

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);
		textArea.setBackground(new Color(255, 255, 255));

		textArea.setEditable(false);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 3;
		gbc_textArea.insets = new Insets(0, 0, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 1;
		gbc_textArea.gridy = 1;

		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(scrollPane, gbc_textArea);

		inputField = new JTextField();
		inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send();
			}
		});
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 3;
		contentPane.add(inputField, gbc_textField);
		inputField.setColumns(10);

		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send();
			}
		});
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 3;
		gbc_button.gridy = 3;
		contentPane.add(sendButton, gbc_button);

		System.out.println("Ip Adresse: " + ipAddr);
		System.out.println("Hostname  : " + hostName);

		// server = new ComListener(myPort, textArea);
		// server.start();
	}

	public void send() {
		String newText = inputField.getText();
		if (newText.isEmpty()) {
			return;
		}
		inputField.setText("");
		String newLine = myUsername + ": " + newText;

		setText(newLine);
		try {
			comWriter = new ComWriter(hostName, remotePort);
			// comWriter.send("test!!!");

			comWriter.send("ch", newLine);
		} catch (Exception e) {
			setText("error: " + e.getMessage());
		}
		comWriter.close();

	}

	public void setText(String newLine) {
		String text = textArea.getText();

		text = text + "\n" + newLine;

		textArea.setText(text);

	}

}
