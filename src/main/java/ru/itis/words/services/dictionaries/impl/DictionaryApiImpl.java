package ru.itis.words.services.dictionaries.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ru.itis.words.services.dictionaries.Dictionary;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DictionaryApiImpl implements Dictionary {
    private final Logger logger = Logger.getLogger(DictionaryApiImpl.class);
    private final static String QUERY_BASIS = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public List<String> findDefinitionsOfWord(String word) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(QUERY_BASIS + word))
                .GET()
                .header("accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response.body());
                return node.findValues("definition")
                        .stream()
                        .map(JsonNode::asText)
                        .collect(Collectors.toList());
            } else if (response.statusCode() == 404) {
                logger.info("No Definitions Found for word - " + word);
                return new ArrayList<>();
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
