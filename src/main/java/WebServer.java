import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


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
                    System.out.println("REQUEST : " + request);
                    if (request == null || request.isBlank()) {
                        System.out.println("Empty request received!");
                        continue;
                    } else {

                        String header;

                        while ((header = reader.readLine()) != null && !header.isEmpty()) {
                            System.out.println("HEADER : " + header);

                        }
                        String requestLine = request;

                        String[] parts = requestLine.trim().split("\\s+");

                        String path = "";
                        String method = "";


                        String body;
                        int statusCode;
                        String statusText;
                        String contentType = "text/html; charset=UTF-8";


                        if (parts.length != 3) {
                            body = "400 Bad Request";
                            statusCode = 400;
                            statusText = "Bad Request";
                        } else {
                            method = parts[0];
                            path = parts[1];

                            if (!method.equals("GET")) {
                                body = "405 Method Not Allowed";
                                statusCode = 405;
                                statusText = "Method Not Allowed";
                            } else {


                                if (path.equals("/")) {
                                    body = Files.readString(
                                            Path.of("src/main/resources/static/index.html"),
                                            StandardCharsets.UTF_8
                                    );
                                    statusCode = 200;
                                    statusText = "OK";
                                } else if (path.equals("/about")) {
                                    body = Files.readString(
                                            Path.of("src/main/resources/static/about.html"),
                                            StandardCharsets.UTF_8

                                    );
                                    statusCode = 200;
                                    statusText = "OK";
                                } else if (path.equals("/contact")) {
                                    body = Files.readString(
                                            Path.of("src/main/resources/static/contact.html"),
                                            StandardCharsets.UTF_8
                                    );
                                    statusCode = 200;
                                    statusText = "OK";

                                } else if (path.equals("/style.css")) {
                                    body = Files.readString(
                                            Path.of("src/main/resources/static/style.css"),
                                            StandardCharsets.UTF_8);
                                    statusCode=200;
                                    statusText="OK";
                                    contentType = "text/css; charset=UTF-8";


                                } else {
                                    body = "404 Not Found";
                                    statusCode = 404;
                                    statusText = "Not Found";
                                }
                            }
                        }


                        String response =
                                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                                        "Content-Type: " + contentType + "\r\n" +
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
}

