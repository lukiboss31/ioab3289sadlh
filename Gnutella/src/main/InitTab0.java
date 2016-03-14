package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.channels.SelectableChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import data.User;
import schoolcomm.MainFrame;

public class InitTab0 {

	private static String selectedUsername;

	public static JPanel InitTab0(final MainFrame mf) {
		
		
		final JList<String> list = mf.list;
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 177, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 308, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.insets = new Insets(0, 0, 5, 5);
		gbc_list.gridx = 1;
		gbc_list.gridy = 1;
		panel.add(list, gbc_list);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {

					if (list.getSelectedValue() != null) {
						mf.selectedUsername = list.getSelectedValue().toString();
						getUser(mf);
						mf.tabbedPane.setEnabledAt(1, true);
						mf.tabbedPane.setSelectedIndex(1);
					}
				}
			}
		});

		JButton btnRefresh = new JButton("Refresh");
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.insets = new Insets(0, 0, 5, 5);
		gbc_btnRefresh.fill = GridBagConstraints.BOTH;
		gbc_btnRefresh.gridx = 1;
		gbc_btnRefresh.gridy = 3;
		panel.add(btnRefresh, gbc_btnRefresh);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mf.refreshList();
			}
		});
		return panel;
	}

	public static void getUser(MainFrame mf) {
		try {
			String querry = "select * from users where username='" + mf.selectedUsername + "'";
			PreparedStatement pst = mf.connection.prepareStatement(querry);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				User u = new User();
				u.hostName = rs.getString("hostname");
				u.ipAddr = rs.getString("ip-Addr");
				u.remotePort = rs.getInt("port");
				u.username = rs.getString("username");
				mf.selectedUser = u;
			}
			rs.close();
			pst.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static String getselectedUsername() {
		return selectedUsername;
	}
}
