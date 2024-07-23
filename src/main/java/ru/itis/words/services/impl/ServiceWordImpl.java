package ru.itis.words.services.impl;

import org.apache.log4j.Logger;
import ru.itis.words.models.Card;
import ru.itis.words.models.User;
import ru.itis.words.repositories.Repository;
import ru.itis.words.services.Service;
import ru.itis.words.services.dictionaries.Dictionary;
import ru.itis.words.services.generators.Generator;
import ru.itis.words.services.translators.Translator;

import java.util.Collections;
import java.util.List;

public class ServiceWordImpl implements Service {
    private final Logger logger = Logger.getLogger(ServiceWordImpl.class);
    private final Dictionary dictionary;
    private final Generator generator;
    private final Translator translator;
    private final Repository repository;

    public ServiceWordImpl(Dictionary dictionary, Generator generator, Translator translator, Repository repository) {
        this.dictionary = dictionary;
        this.generator = generator;
        this.translator = translator;
        this.repository = repository;
    }

    @Override
    public Card makeCard() {
        long start = System.currentTimeMillis();
        List<String> englishWords = generator.generateWords(4);
        logger.info("Генерация слов заняла: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        String englishWord = englishWords.get(3);
        List<String> russianWords = translator.translateAll(englishWords);
        String russianWord = russianWords.get(3);
        Collections.sort(russianWords);
        logger.info("Перевод слов занял: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        List<String> definitions = dictionary.findDefinitionsOfWord(englishWord);
        logger.info("Получение определений заняло: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        definitions = translator.translateAll(definitions);
        logger.info("Перевод определений занял:  " + (System.currentTimeMillis() - start));
        return new Card(englishWord, russianWord, russianWords, definitions);
    }

    @Override
    public User findUserByTgId(long tgId) {
        return repository.findUserByTgId(tgId);
    }

    @Override
    public void incrementNumberOfCards(long tgId) {
        repository.incrementNumberOfCards(tgId);
    }

    @Override
    public void incrementNumberOfCorrectCards(long tgId) {
        repository.incrementNumberOfCorrectCards(tgId);
    }

    @Override
    public void incrementNumberOfIncorrectCards(long tgId) {
        repository.incrementNumberOfIncorrectCards(tgId);
    }

    @Override
    public void addUser(long tgId) {
        repository.addUser(tgId);
    }
}
