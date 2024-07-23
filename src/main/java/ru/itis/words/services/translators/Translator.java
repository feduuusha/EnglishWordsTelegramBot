package ru.itis.words.services.translators;

import java.util.List;

public interface Translator {
    List<String> translateAll(List<String> words);
}
