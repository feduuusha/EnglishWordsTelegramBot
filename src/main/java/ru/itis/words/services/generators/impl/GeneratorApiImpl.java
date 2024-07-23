package ru.itis.words.services.generators.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ru.itis.words.services.generators.Generator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeneratorApiImpl implements Generator {
    private final Logger logger = Logger.getLogger(GeneratorApiImpl.class);
    private static final String QUERY = "https://random-word-api.vercel.app/api?words=";
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public List<String> generateWords(int countOfWords) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(QUERY + countOfWords))
                .GET()
                .header("accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<String> words = new ArrayList<>();
                JsonNode node = mapper.readTree(response.body());
                Iterator<JsonNode> iterator = node.elements();
                while (iterator.hasNext()) {
                    words.add(iterator.next().asText());
                }
                return words;
            } else {
                logger.error("Incorrect response code - " + response.statusCode());
                throw new IllegalStateException();
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
