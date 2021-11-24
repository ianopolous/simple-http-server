package http;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) throws IOException {
		if (args.length < 2 || args[0].equals("-help") || args[0].equals("--help")) {
			System.out.println("Usage: java -jar HttpServer.jar $web_root $port");
			return;
		}
	    HttpServer httpServer = HttpServer.create();
	    httpServer.createContext("/", new StaticHandler(args[0]));
	    int port = Integer.parseInt(args[1]);
	    httpServer.bind(new InetSocketAddress("localhost", port), 100);
	    httpServer.start();
	}
}
