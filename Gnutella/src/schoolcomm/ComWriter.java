package schoolcomm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

public class ComWriter {

	private Socket echoSocket;
	private PrintWriter out;

	public ComWriter(String hostName, int portNumber) throws UnknownHostException, IOException {

		echoSocket = new Socket(hostName, portNumber);
		out = new PrintWriter(echoSocket.getOutputStream(), true);
	}

	@SuppressWarnings("unchecked")
	public void send(String key, String text) {
		JSONObject obj = new JSONObject();
		obj.put("key", key);
		obj.put("msg", text);
		out.println(obj);
	}

	public void close() {
		if (out != null) {
			out.close();
		}
	}

}
