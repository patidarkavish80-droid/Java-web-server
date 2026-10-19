import java.io.IOException;
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

                    String response =
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                                    "Content-Length: 12\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n" +
                                    "Hello World!";

                    OutputStream out = client.getOutputStream();
                    out.write(response.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
            }
        }
    }
}