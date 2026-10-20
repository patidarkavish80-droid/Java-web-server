import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Server running at http://localhost:8080");

            while (true) {
                try (Socket client = server.accept()) {
                    System.out.println("Client connected!");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(client.getInputStream())
                    );
                     String request = reader.readLine();
                     System.out.println("REQUEST : "+request);

                     String header;

                     while((header=reader.readLine())!=null&&!header.isEmpty()){
                         System.out.println("HEADER : "+header);

                    }
                    String requestLine = request;

                    String[] parts = requestLine.split(" ");
                    String path = parts[1];
                     String body;
                     int statusCode ;
                     String statusText;

                    if (path.equals("/")) {
                        body = "Home Page";
                        statusCode = 200;
                        statusText="OK";
                    } else if (path.equals("/about")) {
                        body = "About Page";
                        statusCode = 200;
                        statusText = "OK";
                    } else if (path.equals("/contact")) {
                        body = "Contact Page";
                        statusCode = 200;
                        statusText = "OK";
                    } else {
                        body = "404 Not Found";
                        statusCode = 404;
                        statusText = "Not Found";
                    }

                    String response =
                            "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n" +
                                    body;

                    System.out.println("ROUTE: " + path
                            + " | STATUS: " + statusCode
                            + " | BODY: " + body);

                        OutputStream out = client.getOutputStream();
                         out.write(response.getBytes(StandardCharsets.UTF_8));
                         out.flush();
                    System.out.println("RESPONSE SENT");
                }
            }
        }
    }
}