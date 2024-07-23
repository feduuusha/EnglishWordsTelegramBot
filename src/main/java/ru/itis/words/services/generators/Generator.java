package ru.itis.words.services.generators;

import java.util.List;

public interface Generator {
    List<String> generateWords(int countOfWords);
}
