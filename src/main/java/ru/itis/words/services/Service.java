package ru.itis.words.services;

import ru.itis.words.models.Card;
import ru.itis.words.models.User;

public interface Service {
    Card makeCard();

    User findUserByTgId(long tgId);

    void incrementNumberOfCards(long tgId);

    void incrementNumberOfCorrectCards(long tgId);

    void incrementNumberOfIncorrectCards(long tgId);

    void addUser(long tgId);
}
