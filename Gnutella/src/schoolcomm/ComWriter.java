package schoolcomm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ComWriter {

	private Socket echoSocket;
	private PrintWriter out;

	public ComWriter(String hostName, int portNumber) throws UnknownHostException, IOException {

		echoSocket = new Socket(hostName, portNumber);
		out = new PrintWriter(echoSocket.getOutputStream(), true);
	}

	public void send(String text) {
		out.println(text);

	}

	public void close() {
		if (out != null) {
			out.close();
		}
	}

}
