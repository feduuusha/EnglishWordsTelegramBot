package ru.itis.words.models;

public class User {
    private final long tgId;
    private final int numberOfCards;
    private final int numberOfCorrectCards;
    private final int numberOfIncorrectCards;

    public User(long tgId, int numberOfCards, int numberOfCorrectCards, int numberOfIncorrectCards) {
        this.tgId = tgId;
        this.numberOfCards = numberOfCards;
        this.numberOfCorrectCards = numberOfCorrectCards;
        this.numberOfIncorrectCards = numberOfIncorrectCards;
    }

    public long getTgId() {
        return tgId;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }

    public int getNumberOfCorrectCards() {
        return numberOfCorrectCards;
    }

    public int getNumberOfIncorrectCards() {
        return numberOfIncorrectCards;
    }

    @Override
    public String toString() {
        return "User{" +
                "tgId=" + tgId +
                ", numberOfCards=" + numberOfCards +
                ", numberOfCorrectCards=" + numberOfCorrectCards +
                ", numberOfIncorrectCards=" + numberOfIncorrectCards +
                '}';
    }
}
