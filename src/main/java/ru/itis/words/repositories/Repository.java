package ru.itis.words.repositories;

import ru.itis.words.models.User;

public interface Repository {
    User findUserByTgId(long tgId);

    void incrementNumberOfCards(long tgId);

    void incrementNumberOfCorrectCards(long tgId);

    void incrementNumberOfIncorrectCards(long tgId);

    void addUser(long tgId);
}
