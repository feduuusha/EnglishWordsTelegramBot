package ru.itis.words.controllers.impl.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.itis.words.models.Card;
import ru.itis.words.models.User;
import ru.itis.words.services.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final Logger logger = Logger.getLogger(TelegramBot.class);
    private final Service service;
    private final TelegramClient telegramClient;
    private final HashMap<Long, HashMap<Integer, Card>> mapMessageToCard;

    public TelegramBot(Service service, String botToken) {
        this.service = service;
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.mapMessageToCard = new HashMap<>();
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().getText().equals("/start")) {
                mapMessageToCard.put(update.getMessage().getChatId(), new HashMap<>());
                service.addUser(update.getMessage().getFrom().getId());
                SendMessage message = new SendMessage(String.valueOf(update.getMessage().getChatId()), "Приветствую тебя новый пользователь!");
                message.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        .keyboardRow(new KeyboardRow("\uD83D\uDCD5 Новая карточка", "\uD83E\uDD16 Профиль"))
                        .resizeKeyboard(true)
                        .selective(true)
                        .build());
                sendMessage(message);
            } else if (update.getMessage().getText().equals("\uD83D\uDCD5 Новая карточка")) {
                long start = System.currentTimeMillis();
                if (!mapMessageToCard.containsKey(update.getMessage().getChatId())) {
                    mapMessageToCard.put(update.getMessage().getChatId(), new HashMap<>());
                }
                Card card = service.makeCard();
                service.incrementNumberOfCards(update.getMessage().getFrom().getId());
                SendMessage message = new SendMessage(String.valueOf(update.getMessage().getChatId()), "\uD83C\uDDEC\uD83C\uDDE7 Английское слово - <b>" + card.getEnglishWord() + "</b>\n\uD83C\uDFAF Выберите правильный перевод:");
                message.setReplyMarkup(ReplyKeyboardMarkup.builder().clearKeyboard().build());
                message.setParseMode("HTML");
                List<InlineKeyboardButton> firstRowButton = new ArrayList<>();
                List<InlineKeyboardButton> secondRowButton = new ArrayList<>();
                for (int i = 0; i < card.getRussianWords().size(); ++i) {
                    String word = card.getRussianWords().get(i);
                    InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton
                            .builder()
                            .text(word)
                            .build();

                    if (word.equals(card.getRussianWord())) {
                        inlineKeyboardButton.setCallbackData("rightAnswer");
                    } else {
                        inlineKeyboardButton.setCallbackData("wrongAnswer");
                    }
                    if (i < 2)
                        firstRowButton.add(inlineKeyboardButton);
                    else
                        secondRowButton.add(inlineKeyboardButton);
                }
                message.setReplyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(new InlineKeyboardRow(firstRowButton))
                        .keyboardRow(new InlineKeyboardRow(secondRowButton))
                        .build());
                logger.info("Полная генерация карточки заняла: " + (System.currentTimeMillis() - start));
                try {
                    int messageId = telegramClient.execute(message).getMessageId();
                    mapMessageToCard.get(update.getMessage().getChatId()).put(messageId, card);
                } catch (TelegramApiException e) {
                    logger.error(e.getMessage());
                    throw new IllegalStateException(e);
                }
            } else if (update.getMessage().getText().equals("\uD83E\uDD16 Профиль")) {
                User user = service.findUserByTgId(update.getMessage().getFrom().getId());
                String answer = "\uD83E\uDEAA <b>Ваш ID:</b> " + user.getTgId() + "\n\n"
                        + "\uD83D\uDCC8 <b>Статистика: </b>\n"
                        + "├ Всего карточек прорешено: " + user.getNumberOfCards() + "\n"
                        + "├ Прорешино правильно: " + user.getNumberOfCorrectCards() + "\n"
                        + "└ Прорешено неправильно: " + user.getNumberOfIncorrectCards();
                SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), answer);
                sendMessage.setParseMode("HTML");
                sendMessage(sendMessage);
            }
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("rightAnswer")) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                int messageId = update.getCallbackQuery().getMessage().getMessageId();
                service.incrementNumberOfCorrectCards(update.getCallbackQuery().getFrom().getId());
                DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);
                deleteMessage(deleteMessage);
                Card card = mapMessageToCard.get(chatId).get(messageId);
                mapMessageToCard.get(chatId).remove(messageId);
                StringBuilder answer = new StringBuilder("✅ Это <b>правильный</b> ответ!\n"
                        + "\uD83C\uDDEC\uD83C\uDDE7 <b>Английское слово:</b> " + card.getEnglishWord() + "\n"
                        + "\uD83C\uDDF7\uD83C\uDDFA <b>Перевод на русский язык:</b> " + card.getRussianWord() + "\n"
                        + "\uD83C\uDF10 <b>Определения:</b> \n");
                addDefinitionsToAnswer(chatId, card, answer);
            } else if (update.getCallbackQuery().getData().equals("wrongAnswer")) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                int messageId = update.getCallbackQuery().getMessage().getMessageId();
                service.incrementNumberOfIncorrectCards(update.getCallbackQuery().getFrom().getId());
                DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), update.getCallbackQuery().getMessage().getMessageId());
                deleteMessage(deleteMessage);
                Card card = mapMessageToCard.get(chatId).get(messageId);
                mapMessageToCard.get(chatId).remove(messageId);
                StringBuilder answer = new StringBuilder("\uD83D\uDEAB <b>Неправильный</b> ответ!\n"
                        + "\uD83C\uDDEC\uD83C\uDDE7 <b>Английское слово:</b> " + card.getEnglishWord() + "\n"
                        + "\uD83C\uDDF7\uD83C\uDDFA <b>Перевод на русский язык:</b> " + card.getRussianWord() + "\n"
                        + "\uD83C\uDF10 <b>Определения:</b>\n");
                addDefinitionsToAnswer(chatId, card, answer);
            }
        }
    }

    private void addDefinitionsToAnswer(long chatId, Card card, StringBuilder answer) {
        for (int i = 1; i <= card.getDefinitions().size(); ++i) {
            answer.append(i).append(") ").append(card.getDefinitions().get(i - 1).strip()).append("\n");
        }
        SendMessage message = new SendMessage(String.valueOf(chatId), answer.toString());
        message.setParseMode("HTML");
        sendMessage(message);
    }

    private void deleteMessage(DeleteMessage deleteMessage) {
        try {
            telegramClient.execute(deleteMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
