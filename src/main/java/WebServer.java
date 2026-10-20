
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class WebServer {

    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server running at http://localhost:8080");

        while (true) {
            try (Socket client = server.accept()) {

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );

                String request = reader.readLine();
                System.out.println("REQUEST: " + request);

                if (request == null || request.isBlank()) {
                    continue;
                }

                String header;
                while ((header = reader.readLine()) != null && !header.isEmpty()) {
                    System.out.println("HEADER: " + header);
                }

                String[] parts = request.trim().split("\\s+");

                String path = "";
                String body;
                int statusCode;
                String statusText;
                String contentType = "text/html; charset=UTF-8";

                if (parts.length != 3) {
                    body = "400 Bad Request";
                    statusCode = 400;
                    statusText = "Bad Request";

                } else {
                    String method = parts[0];
                    path = parts[1];

                    if (!method.equals("GET")) {
                        body = "405 Method Not Allowed";
                        statusCode = 405;
                        statusText = "Method Not Allowed";

                    } else {
                        String fileName = null;

                        if (path.equals("/")) {
                            fileName = "index.html";
                        } else if (path.equals("/about")) {
                            fileName = "about.html";
                        } else if (path.equals("/contact")) {
                            fileName = "contact.html";
                        } else if (path.equals("/style.css")) {
                            fileName = "style.css";
                            contentType = "text/css; charset=UTF-8";
                        } else if (path.equals("/script.js")) {
                            fileName = "script.js";
                            contentType = "text/javascript; charset=UTF-8";
                        }

                        if (fileName == null) {
                            body = "404 Not Found";
                            statusCode = 404;
                            statusText = "Not Found";

                        } else {
                            Path filePath = Path.of(
                                    "src/main/resources/static/" + fileName
                            );

                            try {
                                if (Files.exists(filePath)) {
                                    body = Files.readString(filePath, StandardCharsets.UTF_8);
                                    statusCode = 200;
                                    statusText = "OK";
                                } else {
                                    body = "404 Not Found";
                                    statusCode = 404;
                                    statusText = "Not Found";
                                }

                            } catch (IOException e) {
                                body = "500 Internal Server Error";
                                statusCode = 500;
                                statusText = "Internal Server Error";

                                System.out.println(
                                        "File reading failed: " + e.getMessage()
                                );
                            }
                        }
                    }
                }

                String response =
                        "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                                "Content-Type: " + contentType + "\r\n" +
                                "Content-Length: " +
                                body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n" +
                                body;

                OutputStream out = client.getOutputStream();
                out.write(response.getBytes(StandardCharsets.UTF_8));
                out.flush();

                System.out.println(
                        "ROUTE: " + path +
                                " | STATUS: " + statusCode + " " + statusText
                );
            }
        }
    }
}
