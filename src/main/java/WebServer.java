
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class WebServer {
    private static String readHttpLine(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;

        while ((b = input.read()) != -1) {
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                buffer.write(b);
            }
        }

        if (b == -1 && buffer.size() == 0) {
            return null;
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }
    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server running at http://localhost:8080");

        while (true) {
            try (Socket client = server.accept()) {

                InputStream input = client.getInputStream();

                String request = readHttpLine(input);
                System.out.println("REQUEST: " + request);

                if (request == null || request.isBlank()) {
                    continue;
                }

                int contentLength = 0;
                String header;

                while ((header = readHttpLine(input)) != null && !header.isEmpty()) {
                    System.out.println("HEADER: " + header);

                    if (header.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(
                                header.substring(header.indexOf(":") + 1).trim()
                        );
                    }
                }

                System.out.println("Content Length: " + contentLength);
                byte[] bodyBytes = input.readNBytes(contentLength);
                String requestBody = new String(bodyBytes, StandardCharsets.UTF_8);

                System.out.println("POST BODY: " + requestBody);

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
                    if (method.equals("POST") && path.equals("/submit")) {
                        body = "Form submitted successfully!";
                        statusCode = 200;
                        statusText = "OK";

                    } else if (!method.equals("GET")) {
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
