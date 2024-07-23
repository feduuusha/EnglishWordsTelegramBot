package ru.itis.words.services.dictionaries;

import java.util.List;

public interface Dictionary {
    List<String> findDefinitionsOfWord(String word);
}
