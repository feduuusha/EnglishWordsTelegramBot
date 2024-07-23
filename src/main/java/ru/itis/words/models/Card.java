package ru.itis.words.models;

import java.util.List;

public class Card {
    private final String englishWord;
    private final String russianWord;
    private final List<String> russianWords;
    private final List<String> definitions;

    public Card(String englishWord, String russianWord, List<String> russianWords, List<String> definitions) {
        this.englishWord = englishWord;
        this.russianWord = russianWord;
        this.russianWords = russianWords;
        this.definitions = definitions;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public String getRussianWord() {
        return russianWord;
    }

    public List<String> getRussianWords() {
        return russianWords;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    @Override
    public String toString() {
        return "Card{" +
                "englishWord='" + englishWord + '\'' +
                ", russianWord='" + russianWord + '\'' +
                ", russianWords=" + russianWords +
                ", definitions=" + definitions +
                '}';
    }
}
