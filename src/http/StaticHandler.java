package http;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

public class StaticHandler implements HttpHandler
{
    private final String pathToRoot;

    public StaticHandler(String pathToRoot) {
        this.pathToRoot = pathToRoot.endsWith("/") ? pathToRoot : pathToRoot + "/";

        File base = new File(pathToRoot);
        if (! base.exists() || ! base.isDirectory())
            throw new IllegalStateException("Couldn't find webroot: "+pathToRoot);
    }

    private static class Asset {
        public final byte[] data;

        public Asset(byte[] data) {
            this.data = data;
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        try {
            path = path.substring(1);
            path = path.replaceAll("//", "/");
            if (path.length() == 0)
                path = "index.html";

            File file = new File(pathToRoot + path);
            InputStream in = new FileInputStream(file);
            if (in == null) {
                String toAsset = pathToRoot + path + ".html";
                file = new File(toAsset);
            }

            if (path.endsWith(".js"))
                httpExchange.getResponseHeaders().set("Content-Type", "text/javascript");
            else if (path.endsWith(".html"))
                httpExchange.getResponseHeaders().set("Content-Type", "text/html");
            else if (path.endsWith(".css"))
                httpExchange.getResponseHeaders().set("Content-Type", "text/css");
            else if (path.endsWith(".json"))
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            else if (path.endsWith(".svg"))
                httpExchange.getResponseHeaders().set("Content-Type", "image/svg+xml");
            if (httpExchange.getRequestMethod().equals("HEAD")) {
                httpExchange.getResponseHeaders().set("Content-Length", "" + file.length());
                httpExchange.sendResponseHeaders(200, -1);
                return;
            }

            httpExchange.sendResponseHeaders(200, file.length());
            OutputStream body = httpExchange.getResponseBody();
            byte[] tmp = new byte[4096];
            int r;
            while ((r=in.read(tmp)) >= 0)
                body.write(tmp, 0, r);
            body.flush();
            body.close();
        } catch (NullPointerException t) {
            System.err.println("Error retrieving: " + path);
            t.printStackTrace();
	    httpExchange.sendResponseHeaders(404, 0);
            httpExchange.getResponseBody().close();
        } catch (Throwable t) {
            System.err.println("Error retrieving: " + path);
            t.printStackTrace();
        }
    }
}
