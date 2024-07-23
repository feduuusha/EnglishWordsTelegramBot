package ru.itis.words.services.translators.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ru.itis.words.services.impl.ServiceWordImpl;
import ru.itis.words.services.translators.Translator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TranslatorApiImpl implements Translator {
    final static Logger logger = Logger.getLogger(ServiceWordImpl.class);
    private static final String QUERY_BASIS = "https://translate.googleapis.com/translate_a/single?client=gtx&dt=t&sl=en&tl=ru&q=";
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public List<String> translateAll(List<String> words) {
        StringBuilder query = new StringBuilder(QUERY_BASIS);
        if (words.isEmpty()) return new ArrayList<>();
        words = words.stream().map((word) -> word.replace(";", ".").strip()).collect(Collectors.toList());
        for (int i = 0; i < words.size(); ++i) {
            if (i == 0 || query.length() + words.get(i).length() <= 200 + QUERY_BASIS.length()) {
                query.append(words.get(i).strip()).append(";");
            } else {
                break;
            }
        }
        query.deleteCharAt(query.length() - 1);
        logger.debug(query);
        HttpRequest request = HttpRequest.newBuilder(URI.create((query).toString()
                        .replace(" ", "%20")
                        .replace("\"", "")))
                .GET()
                .header("accept", "application/json")
                .header("User-Agent", "PostmanRuntime/7.39.0")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response.body());
                String[] strings = node.get(0).get(0).get(0).asText().split(";");
                logger.debug(Arrays.toString(strings));
                return new ArrayList<>(Arrays.asList(strings));
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
