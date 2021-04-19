import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class WebInterfaceHandler implements HttpHandler {
    static Logger logger;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger = new CustomLogger(WebInterfaceHandler.class.getName());

        String method = httpExchange.getRequestMethod();
        OutputStream responseStream = httpExchange.getResponseBody();
        Headers responseHeaders = httpExchange.getResponseHeaders();
        logger.info("Received HTTP Request with method " + method);

        JSONObject responseBody = new JSONObject();
        responseBody.put("Host", httpExchange.getRequestHeaders().getFirst("Host"));
        responseBody.put("User-Agent", httpExchange.getRequestHeaders().getFirst("User-Agent"));
        String responseString = responseBody.toJSONString();

        switch (method) {
            case "GET":
                logger.fine("Generated response body: " + responseString);

                responseHeaders.add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseString.length());

                responseStream.write(responseString.getBytes(StandardCharsets.UTF_8));
                responseStream.flush();
                responseStream.close();
                break;
            default:
                break;
        }
    }
}