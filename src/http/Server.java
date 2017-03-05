package http;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) throws IOException {
		HttpServer httpServer = HttpServer.create();
		httpServer.createContext("/", new StaticHandler(".", false, false));
		httpServer.bind(new InetSocketAddress("localhost", 8000), 100);
		httpServer.start();
	}
}
