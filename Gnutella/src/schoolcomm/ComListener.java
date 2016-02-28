package schoolcomm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ComListener extends Thread {

	private ServerSocket serverSocket;
	private MainFrame hauptfenster;

	public ComListener(int port, MainFrame hauptfenster) {

		this.hauptfenster = hauptfenster;
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void run() {

		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				String name = clientSocket.getInetAddress().getHostName();
				System.out.println("connection established to: " + name);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				//while (!in.ready()){}
				String readLine = in.readLine();
				System.out.println(readLine);
				hauptfenster.messageReceived(readLine);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
